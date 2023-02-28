package moe.jeremie.owl.terminal

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moe.jeremie.owl.terminal.databinding.ActivityMainUsBinding

class MainUSActivity : AppCompatActivity() {
    private val TAG = "MainUSActivity"

    private lateinit var binding: ActivityMainUsBinding

    private val controlViewModel: ControlViewModel by viewModels<ControlViewModel>()

    override fun onDestroy() {
        super.onDestroy()
        Log.v(TAG, "onDestroy")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.v(TAG, "onCreate")

//        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main_us)

        supportActionBar?.hide()

        binding.lifecycleOwner = this

        controlViewModel.popupMsg.observe(this, Observer {
            Log.v(TAG, "popupMsg $it")
            Snackbar.make(
                binding.rootConstraintLayout,
                it,
                Snackbar.LENGTH_SHORT
            ).show()
        })

        val sharedPref = application.getSharedPreferences(
            application.resources.getString(R.string.config_preference_file_key),
            Context.MODE_PRIVATE
        )
        val moveOffset = getIntR(
            application, sharedPref,
            R.string.config_name_MoveOffset,
            R.integer.config_default_MoveOffset,
        )

        with(binding) {

            bCalibrate.setOnClickListener {
                controlViewModel.cmdEvent.value =
                    CmdEvent(Cmd.CALIBRATE)
            }
            bEmergencyStop.setOnClickListener {
                controlViewModel.cmdEvent.value =
                    CmdEvent(Cmd.EmergencyStop)
            }
            bUp.setOnClickListener {
                controlViewModel.cmdEvent.value =
                    CmdEvent(Cmd.MOVE, CmdMove.UP, moveDistance = 20)
            }
            bDown.setOnClickListener {
                controlViewModel.cmdEvent.value =
                    CmdEvent(Cmd.MOVE, CmdMove.DOWN, moveDistance = 20)
            }
            bLeft.setOnClickListener {
                controlViewModel.cmdEvent.value =
                    CmdEvent(Cmd.MOVE, CmdMove.LEFT, moveDistance = moveOffset)
            }
            bRight.setOnClickListener {
                controlViewModel.cmdEvent.value =
                    CmdEvent(Cmd.MOVE, CmdMove.RIGHT, moveDistance = moveOffset)
            }
            bFront.setOnClickListener {
                controlViewModel.cmdEvent.value =
                    CmdEvent(Cmd.MOVE, CmdMove.FORWARD, moveDistance = moveOffset)
            }
            bBack.setOnClickListener {
                controlViewModel.cmdEvent.value =
                    CmdEvent(Cmd.MOVE, CmdMove.BACK, moveDistance = moveOffset)
            }
            bCw.setOnClickListener {
                controlViewModel.cmdEvent.value =
                    CmdEvent(Cmd.ROTATE, CmdMove.IGNORE, CmdRotate.CW, rotateRote = 15)
            }
            bCcw.setOnClickListener {
                controlViewModel.cmdEvent.value =
                    CmdEvent(Cmd.ROTATE, CmdMove.IGNORE, CmdRotate.CCW, rotateRote = 15)
            }

            buttonTakeoff.setOnClickListener {
                controlViewModel.cmdEvent.value = CmdEvent(Cmd.TAKEOFF, moveDistance = 100)
            }
            buttonLand.setOnClickListener {
                controlViewModel.cmdEvent.value = CmdEvent(Cmd.LAND)
            }

            buttonCamera1.setOnClickListener {
                val o = controlViewModel.enableBmp1.get()
                controlViewModel.enableBmp1.set(!o)
                if (o) {
                    binding.cameraImageView1.setImageResource(android.R.color.transparent)
                }
            }
            buttonCamera2.setOnClickListener {
                val o = controlViewModel.enableBmp2.get()
                controlViewModel.enableBmp2.set(!o)
                if (o) {
                    binding.cameraImageView2.setImageResource(android.R.color.transparent)
                }
            }
        }

//        binding.bBack.background.alpha = 128
//        binding.bFront.background.alpha = 128
//        binding.bLeft.background.alpha = 128
//        binding.bRight.background.alpha = 128
//        binding.bUp.background.alpha = 128
//        binding.bDown.background.alpha = 128
//        binding.bCw.background.alpha = 128
//        binding.bCcw.background.alpha = 128

        controlViewModel.bmp1.observe(this, Observer {
            Log.v(TAG, "bmp1")
            binding.cameraImageView1.setImageBitmap(it)
        })
        controlViewModel.bmp2.observe(this, Observer {
            Log.v(TAG, "bmp2")
            binding.cameraImageView2.setImageBitmap(it)
        })



        lifecycleScope.launch(Dispatchers.IO) {

            val app = application
            val sharedPref = app.getSharedPreferences(
                app.resources.getString(R.string.config_preference_file_key),
                Context.MODE_PRIVATE
            )

            val useImageModeHttp = sharedPref.getBoolean(
                app.resources.getString(R.string.config_name_UseImageModeHttp),
                app.resources.getBoolean(R.bool.config_default_UseImageModeHttp)
            ) ?: app.resources.getBoolean(R.bool.config_default_UseImageModeHttp)

            if (useImageModeHttp) {
                //
                Log.v(TAG, "(useImageModeHttp) $useImageModeHttp")
                controlViewModel.runImageHttp(app)
            } else {
                //
                Log.v(TAG, "(useImageModeHttp) $useImageModeHttp")
                controlViewModel.runImageTcp(app)
            }

        }


    }

    private fun getIntR(
        app: Application,
        sharedPref: SharedPreferences,
        name: Int,
        default: Int
    ): Int {
        val defaultValue = app.resources.getInteger(default)
        val r = sharedPref.getInt(app.resources.getString(name), defaultValue)
        return if (r == 0) defaultValue else r
    }
}