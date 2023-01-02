package moe.jeremie.owl.terminal.database

import androidx.room.Entity

@Entity(tableName = "airplane_connect_config")
data class AirplaneConfig(
    var ip: kotlin.String = "127.0.0.1",
    var portCmd: kotlin.Int = 23333,
    var portImageTcp: kotlin.Int = 23332,
    var portImageHttp: kotlin.Int = 23331,
    var imageModeHttp: kotlin.Boolean = true,
)
