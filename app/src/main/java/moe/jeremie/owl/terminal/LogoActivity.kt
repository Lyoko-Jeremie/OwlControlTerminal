package moe.jeremie.owl.terminal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import moe.jeremie.owl.terminal.databinding.ActivityLogoBinding

class LogoActivity : AppCompatActivity() {
    private val TAG = "LogoActivity"

    private lateinit var binding: ActivityLogoBinding

    private val controlViewModel: ControlViewModel by viewModels<ControlViewModel>()

    override fun onDestroy() {
        super.onDestroy()
        Log.v(TAG, "onDestroy")
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_logo)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_logo)

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


    }
}