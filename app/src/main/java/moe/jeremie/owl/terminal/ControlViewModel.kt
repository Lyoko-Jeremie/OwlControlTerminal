package moe.jeremie.owl.terminal

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel

class ControlViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "ControlViewModel"

    private var serverIp: InetAddress
    private var serverCmdPort: Int
    private var serverSocketAddress: InetSocketAddress

        private var udpSocket: DatagramChannel
//    private var udpSocket: DatagramSocket

    // https://stackoverflow.com/questions/47515997/observing-livedata-from-viewmodel
    val cmdEvent: MutableLiveData<CmdEvent> = MutableLiveData<CmdEvent>()
//    val cmdEventFlow: MutableStateFlow<CmdEvent> = MutableStateFlow<CmdEvent>()
//    val cmdEventLiveData = cmdEventFlow.asLiveData(viewModelScope.coroutineContext)

    private val _popupMsg: MutableLiveData<String> = MutableLiveData<String>("")
    val popupMsg: LiveData<String>
        get() = _popupMsg

    init {
        val app = getApplication<Application>()
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
                } catch (e: java.lang.Exception) {
                    _popupMsg.postValue(e.toString())
//                    throw e
                }
            }
        }
    }
}