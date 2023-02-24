package moe.jeremie.owl.terminal

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import moe.jeremie.owl.terminal.ImageProtocol.ImageProtocolKotlin.ImageProtocol
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.*
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.net.URL
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel
import java.nio.charset.Charset
import java.util.concurrent.atomic.AtomicBoolean


class ControlViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "ControlViewModel"

    private var serverIp: InetAddress
    private var serverCmdPort: Int
    private var serverSocketAddress: InetSocketAddress

    private var portImageHttp: Int
    private var portImageTcp: Int

    private var udpSocket: DatagramChannel
//    private var udpSocket: DatagramSocket


    private var imageHttpUrl1: URL
    private var imageHttpUrl2: URL


    // https://stackoverflow.com/questions/47515997/observing-livedata-from-viewmodel
    val cmdEvent: MutableLiveData<CmdEvent> = MutableLiveData<CmdEvent>()
//    val cmdEventFlow: MutableStateFlow<CmdEvent> = MutableStateFlow<CmdEvent>()
//    val cmdEventLiveData = cmdEventFlow.asLiveData(viewModelScope.coroutineContext)

    private val _popupMsg: MutableLiveData<String> = MutableLiveData<String>()
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

//        _popupMsg.value = "connect to: ${serverSocketAddress.address}:${serverSocketAddress.port}"

        Log.v(TAG, "connect to: ${ip}:${serverCmdPort}")
        Log.v(TAG, "connect to: ${serverSocketAddress.address}:${serverSocketAddress.port}")

//        udpSocket = DatagramSocket()
        udpSocket = DatagramChannel.open()
        Log.v(TAG, "isBlocking " + udpSocket.isBlocking)

//        udpSocket.configureBlocking(false)

        portImageHttp = sharedPref.getInt(
            app.resources.getString(R.string.config_name_PortImageHttp),
            app.resources.getInteger(R.integer.config_default_PortImageHttp)
        ) ?: app.resources.getInteger(R.integer.config_default_PortImageHttp)

        portImageTcp = sharedPref.getInt(
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
                    Cmd.CALIBRATE -> dataJson.put("cmdId", 90)
                    Cmd.PING -> dataJson.put("cmdId", 0)
                    Cmd.BREAK -> dataJson.put("cmdId", 10)
                    Cmd.TAKEOFF -> {
                        dataJson.put("distance", it.moveDistance)
                        dataJson.put("cmdId", 11)
                    }
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
                    Cmd.UNLOCK -> dataJson.put("cmdId", 92)
                    Cmd.EmergencyStop -> dataJson.put("cmdId", 120)
                    Cmd.JoyCon -> {
                        dataJson.put("cmdId", 60)
                        requireNotNull(it.joyCon) { "Cmd.JoyCon requireNotNull(it.joyCon)" }
                        var dataJoyCon = JSONObject()
                        dataJoyCon.put("leftRockerX", it.joyCon.leftRockerX)
                        dataJoyCon.put("leftRockerY", it.joyCon.leftRockerY)
                        dataJoyCon.put("rightRockerX", it.joyCon.rightRockerX)
                        dataJoyCon.put("rightRockerY", it.joyCon.rightRockerY)
                        dataJoyCon.put("leftBackTop", it.joyCon.leftBackTop)
                        dataJoyCon.put("leftBackBottom", it.joyCon.leftBackBottom)
                        dataJoyCon.put("rightBackTop", it.joyCon.rightBackTop)
                        dataJoyCon.put("rightBackBottom", it.joyCon.rightBackBottom)
                        dataJoyCon.put("CrossUp", it.joyCon.CrossUp)
                        dataJoyCon.put("CrossDown", it.joyCon.CrossDown)
                        dataJoyCon.put("CrossLeft", it.joyCon.CrossLeft)
                        dataJoyCon.put("CrossRight", it.joyCon.CrossRight)
                        dataJoyCon.put("X", it.joyCon.A)
                        dataJoyCon.put("B", it.joyCon.B)
                        dataJoyCon.put("X", it.joyCon.X)
                        dataJoyCon.put("Y", it.joyCon.Y)
                        dataJoyCon.put("buttonAdd", it.joyCon.buttonAdd)
                        dataJoyCon.put("buttonReduce", it.joyCon.buttonReduce)
                        dataJson.put("JoyCon", dataJoyCon)
                    }
                    Cmd.JoyConSimple -> {
                        dataJson.put("cmdId", 61)
                        requireNotNull(it.joyCon) { "Cmd.JoyConSimple requireNotNull(it.joyCon)" }
                        var dataJoyConSimple = JSONObject()
                        dataJoyConSimple.put("leftRockerX", it.joyCon.leftRockerX)
                        dataJoyConSimple.put("leftRockerY", it.joyCon.leftRockerY)
                        dataJoyConSimple.put("rightRockerX", it.joyCon.rightRockerX)
                        dataJoyConSimple.put("rightRockerY", it.joyCon.rightRockerY)
                        dataJoyConSimple.put("buttonAdd", it.joyCon.buttonAdd)
                        dataJoyConSimple.put("buttonReduce", it.joyCon.buttonReduce)
                        dataJson.put("JoyConSimple", dataJoyConSimple)
                    }
                    Cmd.JoyConGyro -> {
                        dataJson.put("cmdId", 62)
                        requireNotNull(it.joyConGyro) { "Cmd.JoyConGyro requireNotNull(it.joyConGyro)" }
                        var dataJoyConGyro = JSONObject()
                        dataJoyConGyro.put("x", it.joyConGyro.x)
                        dataJoyConGyro.put("y", it.joyConGyro.y)
                        dataJoyConGyro.put("z", it.joyConGyro.z)
                        dataJoyConGyro.put("a", it.joyConGyro.a)
                        dataJoyConGyro.put("b", it.joyConGyro.b)
                        dataJoyConGyro.put("c", it.joyConGyro.c)
                        dataJoyConGyro.put("d", it.joyConGyro.d)
                        dataJson.put("JoyConGyro", dataJoyConGyro)
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
        Log.v(TAG, "runImageTcp")

        // https://stackoverflow.com/questions/1936857/convert-integer-into-byte-array-java
        fun toBytes(i: Int): ByteArray {
            val result = ByteArray(4)
            result[0] = (i shr 24).toByte()
            result[1] = (i shr 16).toByte()
            result[2] = (i shr 8).toByte()
            result[3] = i /*>> 0*/.toByte()
            return result
        }

        // https://stackoverflow.com/questions/1936857/convert-integer-into-byte-array-java
        fun toChars(i: Int): CharArray {
            val result = CharArray(4)
            result[0] = (i shr 24).toChar()
            result[1] = (i shr 16).toChar()
            result[2] = (i shr 8).toChar()
            result[3] = i /*>> 0*/.toChar()
            return result
        }

        fun getI(num: Int, sOut: DataOutputStream, sIn: DataInputStream): Boolean {
            val a = ImageProtocol.ImageRequest.newBuilder()
                .setCameraId(num)
                .build()
            val ap = a.toByteArray()

            // https://stackoverflow.com/questions/1436942/sending-int-through-socket-in-java
            sOut.writeInt(ap.size)
            sOut.write(ap)

            sOut.flush()
            Log.v(TAG, "mBufferOut")

            // https://stackoverflow.com/questions/35002458/parsing-int-from-the-start-of-a-byte-from-a-socket
            val size = sIn.readInt()
            val len = Integer.reverseBytes(size)
            assert(len < 1 shl 24)
            Log.v(TAG, "size $size")

            if (len == 0) {
                return@getI false
            }

            val b = ByteArray(len)
            sIn.readFully(b)

            // https://stackoverflow.com/questions/13854742/byte-array-of-image-into-imageview
            val bmp = BitmapFactory.decodeByteArray(b, 0, b.size)

            Log.v(TAG, "postValue")
            when (num) {
                1 -> _bmp1.postValue(bmp)
                2 -> _bmp2.postValue(bmp)
                else -> throw Exception("runImageHttp getI $num")
            }

            return@getI true
        }

        try {

            Log.v(TAG, "Socket(serverIp, portImageTcp).use 1")
            Socket(serverIp, portImageTcp).use { socket ->

                Log.v(TAG, "Socket(serverIp, portImageTcp).use 2")

                val sOut = DataOutputStream(socket.getOutputStream())

                val sIn = DataInputStream(socket.getInputStream())

                Log.v(TAG, "socket ${socket.isConnected}")

//                while (true) {
//                    if (enableBmp1.get()) {
//                        getI(1, sOut, sIn)
//                    }
//                    if (enableBmp2.get()) {
//                        getI(2, sOut, sIn)
//                    }
//                    Thread.sleep(0)
//                }
                var c1LastOk = true
                var c2LastOk = true
                while (true) {
                    if (enableBmp1.get()) {
                        if (c1LastOk) {
                            c1LastOk = getI(1, sOut, sIn)
                        }
                    } else {
                        c1LastOk = true
                    }
                    if (enableBmp2.get()) {
                        if (c2LastOk) {
                            c2LastOk = getI(2, sOut, sIn)
                        }
                    } else {
                        c2LastOk = true
                    }
                    Thread.sleep(0)
                }

            }
        } catch (e: java.lang.Exception) {
            _popupMsg.postValue(e.toString())
            Log.v(TAG, e.toString())
            return
//                throw e
        }


    }

    fun runImageHttp(app: Application) {

        val client = OkHttpClient()

        fun getI(num: Int): Boolean {

            val request: Request = Request.Builder()
                .url(
                    when (num) {
                        1 -> imageHttpUrl1.toString()
                        2 -> imageHttpUrl2.toString()
                        else -> throw Exception("runImageHttp getI $num")
                    }
                ).build()

            Log.v(TAG, "build " + request.url.toString())

            try {
                client.newCall(request).execute().use { r ->
                    Log.v(TAG, "execute")

                    if (r.code != 200) {
                        _popupMsg.postValue("responseCode " + r.code + " on " + num)
                        Log.v(TAG, "responseCode " + r.code + " on " + num)
                        return@getI false
                    }
                    Log.v(TAG, "r.code" + r.code)

                    val b = r.body!!.bytes()

                    // https://stackoverflow.com/questions/13854742/byte-array-of-image-into-imageview
                    val bmp = BitmapFactory.decodeByteArray(b, 0, b.size)

                    Log.v(TAG, "postValue")
                    when (num) {
                        1 -> _bmp1.postValue(bmp)
                        2 -> _bmp2.postValue(bmp)
                        else -> throw Exception("runImageHttp getI $num")
                    }

                }
                // next read
            } catch (e: Exception) {
                _popupMsg.postValue(e.toString())
                Log.v(TAG, e.toString())
//                throw e
                return@getI false
            }
            return@getI true
        }

        var c1LastOk = true
        var c2LastOk = true
        while (true) {
            if (enableBmp1.get()) {
                if (c1LastOk) {
                    c1LastOk = getI(1)
                }
            } else {
                c1LastOk = true
            }
            if (enableBmp2.get()) {
                if (c2LastOk) {
                    c2LastOk = getI(2)
                }
            } else {
                c2LastOk = true
            }
            Thread.sleep(0)
        }

    }

}