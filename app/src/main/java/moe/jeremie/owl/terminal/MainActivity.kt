package moe.jeremie.owl.terminal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import moe.jeremie.owl.terminal.ImageProtocol.ImageProtocolKotlin.ImageProtocol.ImageRequest
import moe.jeremie.owl.terminal.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    private lateinit var binding: ActivityMainBinding

    private val controlViewModel: ControlViewModel by viewModels<ControlViewModel>()

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

        binding.lifecycleOwner = this

        controlViewModel.popupMsg.observe(this, Observer {
            Snackbar.make(
                binding.rootConstraintLayout,
                it,
                Snackbar.LENGTH_SHORT
            ).show()
        })

        binding.bUp.setOnClickListener {
            controlViewModel.cmdEvent.value = CmdEvent(Cmd.MOVE, CmdMove.UP)
        }
        binding.bDown.setOnClickListener {
            controlViewModel.cmdEvent.value = CmdEvent(Cmd.MOVE, CmdMove.DOWN)
        }
        binding.bLeft.setOnClickListener {
            controlViewModel.cmdEvent.value = CmdEvent(Cmd.MOVE, CmdMove.LEFT)
        }
        binding.bRight.setOnClickListener {
            controlViewModel.cmdEvent.value = CmdEvent(Cmd.MOVE, CmdMove.RIGHT)
        }
        binding.bFront.setOnClickListener {
            controlViewModel.cmdEvent.value = CmdEvent(Cmd.MOVE, CmdMove.FORWARD)
        }
        binding.bBack.setOnClickListener {
            controlViewModel.cmdEvent.value = CmdEvent(Cmd.MOVE, CmdMove.BACK)
        }
        binding.bCw.setOnClickListener {
            controlViewModel.cmdEvent.value = CmdEvent(Cmd.ROTATE, CmdMove.IGNORE, CmdRotate.CW)
        }
        binding.bCcw.setOnClickListener {
            controlViewModel.cmdEvent.value = CmdEvent(Cmd.ROTATE, CmdMove.IGNORE, CmdRotate.CCW)
        }

        binding.buttonTakeoff.setOnClickListener {
            controlViewModel.cmdEvent.value = CmdEvent(Cmd.TAKEOFF)
        }
        binding.buttonLand.setOnClickListener {
            controlViewModel.cmdEvent.value = CmdEvent(Cmd.LAND)
        }

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