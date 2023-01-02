package moe.jeremie.owl.terminal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import moe.jeremie.owl.terminal.ImageProtocol.ImageProtocolKotlin.ImageProtocol.ImageRequest

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    override fun onDestroy() {
        super.onDestroy()
        Log.v(TAG, "onDestroy")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.v(TAG, "onCreate")

        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

//        var a = ImageRequest.newBuilder()
//            .setCameraId(1)
//            .build();
//        a.toByteArray();
    }
}