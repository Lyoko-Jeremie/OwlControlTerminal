package moe.jeremie.owl.terminal

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
    CALIBRATE(90),
    BREAK(10),
    TAKEOFF(11),
    LAND(12),
    MOVE(13),
    ROTATE(14),
}

private var id_: Int = 0

data class CmdEvent(
    val cmd: Cmd,
    val move: CmdMove = CmdMove.IGNORE,
    val rotate: CmdRotate = CmdRotate.IGNORE,
    val moveDistance: Int = 100,
    val rotateRote: Int = 1,
    val id: Int = ++id_
)
