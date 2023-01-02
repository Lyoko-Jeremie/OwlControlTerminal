package moe.jeremie.owl.terminal

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import moe.jeremie.owl.terminal.ImageProtocol.ImageProtocolKotlin.ImageProtocol
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.URL
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel
import java.util.concurrent.atomic.AtomicBoolean

class ControlViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "ControlViewModel"

    private var serverIp: InetAddress
    private var serverCmdPort: Int
    private var serverSocketAddress: InetSocketAddress

    private var udpSocket: DatagramChannel
//    private var udpSocket: DatagramSocket


    private var imageHttpUrl1: URL
    private var imageHttpUrl2: URL


    // https://stackoverflow.com/questions/47515997/observing-livedata-from-viewmodel
    val cmdEvent: MutableLiveData<CmdEvent> = MutableLiveData<CmdEvent>()
//    val cmdEventFlow: MutableStateFlow<CmdEvent> = MutableStateFlow<CmdEvent>()
//    val cmdEventLiveData = cmdEventFlow.asLiveData(viewModelScope.coroutineContext)

    private val _popupMsg: MutableLiveData<String> = MutableLiveData<String>("")
    val popupMsg: LiveData<String>
        get() = _popupMsg

    val enableBmp1 = AtomicBoolean(true)
    val enableBmp2 = AtomicBoolean(true)

    private val _bmp1: MutableLiveData<Bitmap> = MutableLiveData<Bitmap>()
    val bmp1: LiveData<Bitmap>
        get() = _bmp1

    private val _bmp2: MutableLiveData<Bitmap> = MutableLiveData<Bitmap>()
    val bmp2: LiveData<Bitmap>
        get() = _bmp2

    init {
        val app = application
        val sharedPref = app.getSharedPreferences(
            app.resources.getString(R.string.config_preference_file_key),
            Context.MODE_PRIVATE
        )
        val ip = sharedPref.getString(
            app.resources.getString(R.string.config_name_AirplaneIp),
            app.resources.getString(R.string.config_default_AirplaneIp)
        ) ?: app.resources.getString(R.string.config_default_AirplaneIp)

        serverCmdPort = sharedPref.getInt(
            app.resources.getString(R.string.config_name_PortCmd),
            app.resources.getInteger(R.integer.config_default_PortCmd)
        )
        serverCmdPort = if (serverCmdPort != 0) serverCmdPort
        else app.resources.getInteger(R.integer.config_default_PortCmd)

        serverIp = InetAddress.getByName(ip)

        serverSocketAddress = InetSocketAddress(serverIp, serverCmdPort)

        _popupMsg.value = "connect to: ${serverSocketAddress.address}:${serverSocketAddress.port}"

        Log.v(TAG, "connect to: ${ip}:${serverCmdPort}")
        Log.v(TAG, "connect to: ${serverSocketAddress.address}:${serverSocketAddress.port}")

//        udpSocket = DatagramSocket()
        udpSocket = DatagramChannel.open()
        Log.v(TAG, "isBlocking " + udpSocket.isBlocking)

//        udpSocket.configureBlocking(false)

        val portImageHttp = sharedPref.getInt(
            app.resources.getString(R.string.config_name_PortImageHttp),
            app.resources.getInteger(R.integer.config_default_PortImageHttp)
        ) ?: app.resources.getInteger(R.integer.config_default_PortImageHttp)

        val portImageTcp = sharedPref.getInt(
            app.resources.getString(R.string.config_name_PortImageTcp),
            app.resources.getInteger(R.integer.config_default_PortImageTcp)
        ) ?: app.resources.getInteger(R.integer.config_default_PortImageTcp)

        imageHttpUrl1 = URL("http", serverIp.hostAddress, portImageHttp, "/1")
        imageHttpUrl2 = URL("http", serverIp.hostAddress, portImageHttp, "/2")
//        imageHttpUrl2 = URL("http://${serverIp}:${portImageHttp}/2")

        Log.v(TAG, "imageHttpUrl1 $imageHttpUrl1")
        Log.v(TAG, "imageHttpUrl2 $imageHttpUrl2")

        runCmdUdp(app)
    }

    private fun runCmdUdp(app: Application) {

        viewModelScope.launch(Dispatchers.IO) {

            Log.v(TAG, "isBlocking " + udpSocket.isBlocking)

            udpSocket.connect(serverSocketAddress)

            udpSocket.configureBlocking(true)
            Log.v(TAG, "isBlocking " + udpSocket.isBlocking)
            Log.v(TAG, "isConnected " + udpSocket.isConnected)

            cmdEvent.postValue(CmdEvent(Cmd.PING))

            // https://stackoverflow.com/questions/47515997/observing-livedata-from-viewmodel
            cmdEvent.asFlow().flowOn(Dispatchers.IO).collect {
                // UDP cmd
                var dataJson = JSONObject()
                dataJson.put("packageId", it.id)

                when (it.cmd) {
                    Cmd.PING -> dataJson.put("cmdId", 0)
                    Cmd.BREAK -> dataJson.put("cmdId", 10)
                    Cmd.TAKEOFF -> dataJson.put("cmdId", 11)
                    Cmd.LAND -> dataJson.put("cmdId", 12)
                    Cmd.MOVE -> {
                        dataJson.put("cmdId", 13)
                        dataJson.put("distance", it.moveDistance)
                        when (it.move) {
                            CmdMove.UP -> dataJson.put("forward", 1)
                            CmdMove.DOWN -> dataJson.put("forward", 2)
                            CmdMove.LEFT -> dataJson.put("forward", 3)
                            CmdMove.RIGHT -> dataJson.put("forward", 4)
                            CmdMove.FORWARD -> dataJson.put("forward", 5)
                            CmdMove.BACK -> dataJson.put("forward", 6)
                            CmdMove.IGNORE -> dataJson.put("forward", 0)
                        }
                    }
                    Cmd.ROTATE -> {
                        dataJson.put("cmdId", 14)
                        dataJson.put("rote", it.rotateRote)
                        when (it.rotate) {
                            CmdRotate.IGNORE -> dataJson.put("rotate", 0)
                            CmdRotate.CW -> dataJson.put("rotate", 1)
                            CmdRotate.CCW -> dataJson.put("rotate", 2)
                        }
                    }
                }

                val message: String = dataJson.toString()

                Log.v(TAG, "message " + message)

                val messageB = message.toByteArray()
                try {
                    udpSocket.write(ByteBuffer.wrap(messageB))

//                    // https://stackoverflow.com/questions/19540715/send-and-receive-data-on-udp-socket-java-android
//                    var udpPackage: DatagramPacket = DatagramPacket(
//                        messageB, messageB.size,
//                        serverIp, serverCmdPort
//                    )
//                    udpSocket.send(udpPackage)
                } catch (e: java.net.PortUnreachableException) {
                    _popupMsg.postValue(app.resources.getString(R.string.PortUnreachableException))
                } catch (e: java.lang.Exception) {
                    _popupMsg.postValue(e.toString())
//                    throw e
                }
            }
        }
    }

    fun runImageTcp(app: Application) {
        var a = ImageProtocol.ImageRequest.newBuilder()
            .setCameraId(1)
            .build();
        a.toByteArray();

    }

    fun runImageHttp(app: Application) {
        //        Log.v(TAG, "runImageHttp")
        fun getI(num: Int): Boolean {
//            Log.v(TAG, "getI $num")
            val urlConnection: HttpURLConnection =
                when (num) {
                    1 -> imageHttpUrl1.openConnection() as HttpURLConnection
                    2 -> imageHttpUrl2.openConnection() as HttpURLConnection
                    else -> throw Exception("runImageHttp getI $num")
                };

            urlConnection.connectTimeout = 1 * 1000;
            urlConnection.readTimeout = 1 * 1000;

            try {
//                Log.v(TAG, "connect 1")
                urlConnection.connect();
                Log.v(TAG, "connect 2")

                if (urlConnection.responseCode != 200) {
                    _popupMsg.postValue("responseCode " + urlConnection.responseCode + " on " + num)
                    Log.v(TAG, "responseCode " + urlConnection.responseCode + " on " + num)
                    return@getI false
                }
                Log.v(TAG, "urlConnection.responseCode" + urlConnection.responseCode)

                val inS: InputStream = BufferedInputStream(urlConnection.inputStream)
                // same as inS.readBytes()
                val buffer = ByteArrayOutputStream(maxOf(inS.available(), 1024 * 1024))
                inS.copyTo(buffer)
                val b = buffer.toByteArray()

                // https://stackoverflow.com/questions/13854742/byte-array-of-image-into-imageview
                val bmp = BitmapFactory.decodeByteArray(b, 0, b.size)

                Log.v(TAG, "postValue")
                when (num) {
                    1 -> _bmp1.postValue(bmp)
                    2 -> _bmp2.postValue(bmp)
                    else -> throw Exception("runImageHttp getI $num")
                };

                // next read
            } catch (e: Exception) {
                _popupMsg.postValue(e.toString())
            } finally {
                urlConnection.disconnect()
            }
            return@getI true
        }

        var c1LastOk = true
        var c2LastOk = true
        while (true) {
//            Log.v(TAG, "while (true)")
            if (enableBmp1.get()) {
                if (c1LastOk) {
//                    Log.v(TAG, "(enableBmp1.get())")
                    c1LastOk = getI(1)
                }
            } else {
                c1LastOk = true
            }
            if (enableBmp2.get()) {
                if (c2LastOk) {
//                    Log.v(TAG, "(enableBmp2.get())")
                    c2LastOk = getI(2)
                }
            } else {
                c2LastOk = true
            }
            Thread.sleep(0)
        }

    }

}