package moe.jeremie.owl.terminal

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.Snackbar
import moe.jeremie.owl.terminal.databinding.ActivityMainConfigBinding


fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

class MainConfigActivity : AppCompatActivity() {

    private val TAG = "MainConfigActivity"

    private lateinit var binding: ActivityMainConfigBinding

    override fun onDestroy() {
        super.onDestroy()

        Log.v(TAG, "onDestroy")

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main_config)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main_config)

        binding.buttonReset.setOnClickListener {
            resetConfig()

            Snackbar.make(
                binding.rootConstraintLayout,
                resources.getString(R.string.ResetOk),
                Snackbar.LENGTH_SHORT
            ).show()

            loadConfig()
        }
        binding.buttonSave.setOnClickListener {
            saveConfig()

            Snackbar.make(
                binding.rootConstraintLayout,
                resources.getString(R.string.SaveOk),
                Snackbar.LENGTH_SHORT
            ).show()

            loadConfig()
        }

        binding.buttonStart.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        loadConfig()

    }

    fun saveConfig() {
        // https://developer.android.com/training/data-storage/shared-preferences
        val sharedPref = this.getSharedPreferences(
            getString(R.string.config_preference_file_key), Context.MODE_PRIVATE
        )

        with(sharedPref.edit()) {

            putString(
                getString(R.string.config_name_AirplaneIp),
                binding.editAirplaneIp.text.toString()
            )
            putInt(
                getString(R.string.config_name_PortCmd),
                binding.editPortCmd.text.toString().toIntOrNull() ?: 0
            )
            putInt(
                getString(R.string.config_name_PortImageTcp),
                binding.editPortImageTcp.text.toString().toIntOrNull() ?: 0
            )
            putInt(
                getString(R.string.config_name_PortImageHttp),
                binding.editPortImageHttp.text.toString().toIntOrNull() ?: 0
            )
            putBoolean(
                getString(R.string.config_name_UseImageModeHttp),
                binding.checkUseImageModeHttp.isChecked
            )

            apply()
        }

    }

    fun resetConfig() {
        // https://developer.android.com/training/data-storage/shared-preferences
        val sharedPref = this.getSharedPreferences(
            getString(R.string.config_preference_file_key), Context.MODE_PRIVATE
        )

        with(sharedPref.edit()) {

            remove(getString(R.string.config_name_AirplaneIp))
            remove(getString(R.string.config_name_PortCmd))
            remove(getString(R.string.config_name_PortImageTcp))
            remove(getString(R.string.config_name_PortImageHttp))
            remove(getString(R.string.config_name_UseImageModeHttp))

            apply()
        }

    }

    fun loadConfig() {
        // https://developer.android.com/training/data-storage/shared-preferences
        val sharedPref = this.getSharedPreferences(
            getString(R.string.config_preference_file_key), Context.MODE_PRIVATE
        ) ?: return

        val getString: (name: Int, default: Int) -> String = { name: Int, default: Int ->
            val defaultValue = resources.getString(default)
            val r = sharedPref.getString(getString(name), defaultValue)
            if (r != null && r.isNotEmpty()) r else defaultValue
        }
        val getInt = { name: Int, default: Int ->
            val defaultValue = resources.getInteger(default)
            val r = sharedPref.getInt(getString(name), defaultValue)
            if (r == 0) defaultValue else r
        }
        val getBoolean = { name: Int, default: Int ->
            val defaultValue = resources.getBoolean(default)
            sharedPref.getBoolean(getString(name), defaultValue)
        }

        val airplaneIp = getString(
            R.string.config_name_AirplaneIp,
            R.string.config_default_AirplaneIp
        )
        binding.editAirplaneIp.text = airplaneIp.toEditable()
        val portCmd = getInt(
            R.string.config_name_PortCmd,
            R.integer.config_default_PortCmd
        )
        binding.editPortCmd.setText("$portCmd")
        val portImageTcp = getInt(
            R.string.config_name_PortImageTcp,
            R.integer.config_default_PortImageTcp
        )
        binding.editPortImageTcp.setText("$portImageTcp")
        val portImageHttp = getInt(
            R.string.config_name_PortImageHttp,
            R.integer.config_default_PortImageHttp
        )
        binding.editPortImageHttp.setText("$portImageHttp")
        val useImageModeHttp = getBoolean(
            R.string.config_name_UseImageModeHttp,
            R.bool.config_default_UseImageModeHttp
        )
        binding.checkUseImageModeHttp.isChecked = useImageModeHttp

    }
}