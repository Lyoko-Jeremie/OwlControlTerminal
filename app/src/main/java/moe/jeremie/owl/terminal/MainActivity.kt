package moe.jeremie.owl.terminal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import moe.jeremie.owl.terminal.ImageProtocol.ImageProtocolKotlin.ImageProtocol.ImageRequest
import moe.jeremie.owl.terminal.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    private lateinit var binding: ActivityMainBinding

    override fun onDestroy() {
        super.onDestroy()
        Log.v(TAG, "onDestroy")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.v(TAG, "onCreate")

//        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        supportActionBar?.hide()


//        binding.bBack.background.alpha = 128
//        binding.bFront.background.alpha = 128
//        binding.bLeft.background.alpha = 128
//        binding.bRight.background.alpha = 128
//        binding.bUp.background.alpha = 128
//        binding.bDown.background.alpha = 128
//        binding.bCw.background.alpha = 128
//        binding.bCcw.background.alpha = 128

//        var a = ImageRequest.newBuilder()
//            .setCameraId(1)
//            .build();
//        a.toByteArray();
    }
}