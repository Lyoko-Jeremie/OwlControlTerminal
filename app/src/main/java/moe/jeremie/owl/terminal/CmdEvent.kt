package moe.jeremie.owl.terminal

import java.util.Optional

enum class CmdRotate(val v: Int) {
    IGNORE(0),
    CW(1),
    CCW(2),
}

enum class CmdMove(val v: Int) {
    IGNORE(0),
    UP(1),
    DOWN(2),
    LEFT(3),
    RIGHT(4),
    FORWARD(5),
    BACK(6),
}

enum class Cmd(val v: Int) {
    PING(0),
    EmergencyStop(120),
    UNLOCK(92),
    CALIBRATE(90),
    BREAK(10),
    TAKEOFF(11),
    LAND(12),
    MOVE(13),
    ROTATE(14),
    JoyCon(60),
    JoyConSimple(61),
    JoyConGyro(62),
}

private var id_: Int = 0

data class CmdEvent(
    val cmd: Cmd,
    val move: CmdMove = CmdMove.IGNORE,
    val rotate: CmdRotate = CmdRotate.IGNORE,
    val moveDistance: Int = 100,
    val rotateRote: Int = 1,
    val id: Int = ++id_,
    val joyCon: JoyCon? = null,
    val joyConGyro: JoyConGyro? = null,
)

data class JoyConGyro(
    // area direct speed
    val x: Int = 0,
    val y: Int = 0,
    val z: Int = 0,
    // rotate 4-matrix
    val a: Int = 0,
    val b: Int = 0,
    val c: Int = 0,
    val d: Int = 0,
)

data class JoyCon(
    // -127~+127
    val leftRockerX: Int = 0,
    // -127~+127
    val leftRockerY: Int = 0,
    // -127~+127
    val rightRockerX: Int = 0,
    // -127~+127
    val rightRockerY: Int = 0,

    // 0~+127(+127)
    val leftBackTop: Int = 0,
    // 0~+127(+127)
    val leftBackBottom: Int = 0,
    // 0~+127(+127)
    val rightBackTop: Int = 0,
    // 0~+127(+127)
    val rightBackBottom: Int = 0,

    // 0/+127
    val CrossUp: Int = 0,
    // 0/+127
    val CrossDown: Int = 0,
    // 0/+127
    val CrossLeft: Int = 0,
    // 0/+127
    val CrossRight: Int = 0,

    // 0/+127
    val A: Int = 0,
    // 0/+127
    val B: Int = 0,
    // 0/+127
    val X: Int = 0,
    // 0/+127
    val Y: Int = 0,

    // 0/+127
    val buttonAdd: Int = 0,
    // 0/+127
    val buttonReduce: Int = 0,
)
