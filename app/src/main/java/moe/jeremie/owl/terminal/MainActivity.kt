package moe.jeremie.owl.terminal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import moe.jeremie.owl.terminal.ImageProtocol.ImageProtocolKotlin.ImageProtocol.ImageRequest

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

//        var a = ImageRequest.newBuilder()
//            .setCameraId(1)
//            .build();
//        a.toByteArray();
    }
}