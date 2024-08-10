package com.momid.momidcontroller

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import java.io.OutputStream
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.util.concurrent.Executors
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.math.sqrt

val LEAST = (- 128).toByte()
val Most = (+ 128 - 1).toByte()

lateinit var left: ImageButton
lateinit var right: ImageButton
lateinit var up: ImageButton
lateinit var down: ImageButton

lateinit var cross: ImageButton
lateinit var square: ImageButton
lateinit var triangle: ImageButton
lateinit var circle: ImageButton

lateinit var joyStick: ImageButton
lateinit var joyStickArea: View

lateinit var screen: View
lateinit var joystickScreen: View

var crossX = 0f
var crossY = 0f
var squareX = 0f
var squareY = 0f
var circleX = 0f
var circleY = 0f
var triangleX = 0f
var triangleY = 0f

val report = ByteArray(11) // left-right, up-down, cross, square, triangle, circle

var outputStream: OutputStream? = null

val reportExecutor = Executors.newSingleThreadExecutor()

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val windowInsetsController =
            WindowCompat.getInsetsController(window, window.decorView)
        // Configure the behavior of the hidden system bars.
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            ViewCompat.onApplyWindowInsets(v, insets)
            insets
        }

//        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        left = findViewById(R.id.left_arrow)
        right = findViewById(R.id.right_arrow)
        up = findViewById(R.id.up_arrow)
        down = findViewById(R.id.down_arrow)

        cross = findViewById(R.id.cross)
        square = findViewById(R.id.square)
        triangle = findViewById(R.id.triangle)
        circle = findViewById(R.id.circle)

        joyStick = findViewById(R.id.button)
        joyStickArea = findViewById(R.id.button3)

        screen = findViewById(R.id.screen)
        joystickScreen = findViewById(R.id.joystick_screen)

//        left.setOnTouchListener { view, event ->
//            if (event.action == MotionEvent.ACTION_DOWN) {
//                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING)
//                report[0] = LEAST
//                sendReport()
//            }
//
//            if (event.action == MotionEvent.ACTION_UP) {
//                report[0] = 0
//                sendReport()
//            }
//
//            return@setOnTouchListener true
//        }
//
//        right.setOnTouchListener { view, event ->
//            if (event.action == MotionEvent.ACTION_DOWN) {
//                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING)
//                report[0] = Most
//                sendReport()
//            }
//
//            if (event.action == MotionEvent.ACTION_UP) {
//                report[0] = 0
//                sendReport()
//            }
//
//            return@setOnTouchListener true
//        }
//
//        up.setOnTouchListener { view, event ->
//            if (event.action == MotionEvent.ACTION_DOWN) {
//                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING)
//                report[1] = Most
//                sendReport()
//            }
//
//            if (event.action == MotionEvent.ACTION_UP) {
//                report[1] = 0
//                sendReport()
//            }
//
//            return@setOnTouchListener true
//        }
//
//        down.setOnTouchListener { view, event ->
//            if (event.action == MotionEvent.ACTION_DOWN) {
//                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING)
//                report[1] = LEAST
//                sendReport()
//            }
//
//            if (event.action == MotionEvent.ACTION_UP) {
//                report[1] = 0
//                sendReport()
//            }
//
//            return@setOnTouchListener true
//        }

        cross.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING)
                report[2] = 1
                sendReport()
                return@setOnTouchListener true
            }

            if (event.action == MotionEvent.ACTION_UP) {
                report[2] = 0
                sendReport()
                return@setOnTouchListener true
            }

            return@setOnTouchListener false
        }

        square.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING)
                report[3] = 1
                sendReport()
                return@setOnTouchListener true
            }

            if (event.action == MotionEvent.ACTION_UP) {
                report[3] = 0
                sendReport()
                return@setOnTouchListener true
            }

            return@setOnTouchListener false
        }

        triangle.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING)
                report[4] = 1
                sendReport()
                return@setOnTouchListener true
            }

            if (event.action == MotionEvent.ACTION_UP) {
                report[4] = 0
                sendReport()
                return@setOnTouchListener true
            }

            return@setOnTouchListener false
        }

        circle.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING)
                report[5] = 1
                sendReport()
                return@setOnTouchListener true
            }

            if (event.action == MotionEvent.ACTION_UP) {
                report[5] = 0
                sendReport()
                return@setOnTouchListener true
            }

            return@setOnTouchListener false
        }

        var joyStickX = joyStick.x
        var joyStickY = joyStick.y

        var initialX = joyStickX
        var initialY = joyStickY

        var joystickRawX = 0f
        var joystickRawY = 0f

        var isJoystickPressed = false

        var isJoyStickInArea = true

        var joyStickR = 0f
        var joyStickAreaR = 0f
        var allowedDistance = 0f

        var isJoystickScreenPressed = false

        joyStick.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                joyStickX = joyStick.x
                joyStickY = joyStick.y

                val joystickLocation = joyStick.rawXY()
                joystickRawX = joystickLocation[0]
                joystickRawY = joystickLocation[1]

                joyStickR = joyStick.width.toFloat() / 2
                joyStickAreaR = joyStickArea.width.toFloat() / 2
                allowedDistance = joyStickAreaR - joyStickR

                crossX = cross.x + cross.width / 2
                crossY = cross.y + cross.width / 2
                squareX = square.x + square.width / 2
                squareY = square.y + square.width / 2
                circleX = circle.x + circle.width / 2
                circleY = circle.y + circle.width / 2
                triangleX = triangle.x + triangle.width / 2
                triangleY = triangle.y + triangle.width / 2

                joyStick.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })


        joyStick.setOnTouchListener { view, event ->
            println("" + event.rawX + "   " + event.rawY)
            println("" + event.x + "   " + event.y)
            val rawX = event.rawX
            val rawY = event.rawY
            println(event.actionIndex)
            val action = event.action
            if (action == MotionEvent.ACTION_DOWN) {
                if (isJoystickScreenPressed) {
                    return@setOnTouchListener true
                }

                if (!isJoystickPressed) {
                    initialX = event.rawX
                    initialY = event.rawY
                    isJoystickPressed = true
                }
                return@setOnTouchListener true
            }

            else if (action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                if (isJoystickScreenPressed) {
                    return@setOnTouchListener true
                }

                joyStick.animate().x(joyStickX).y(joyStickY).setDuration(300).start()
                report[0] = 0
                report[1] = 0
                isJoystickPressed = false
                sendReport()
                return@setOnTouchListener true
            } else if (action == MotionEvent.ACTION_MOVE) {
                if (isJoystickScreenPressed) {
                    return@setOnTouchListener true
                }

                var leftRight = (rawX - initialX) / allowedDistance
                var upDown = - (rawY - initialY) / allowedDistance

                if (leftRight > 1) {
                    leftRight = 1f
                }

                if (leftRight < - 1) {
                    leftRight = -1f
                }

                if (upDown > 1) {
                    upDown = 1f
                }

                if (upDown < - 1) {
                    upDown = -1f
                }

                val xDistance = rawX - initialX
                val yDistance = rawY - initialY
                val r = sqrt(xDistance * xDistance + yDistance * yDistance)

                if (r > allowedDistance) {
                    if (isJoyStickInArea) {
                        joyStick.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        isJoyStickInArea = false
                    }
                    val angle = atan2(yDistance, xDistance)
                    val refinedXDistance = cos(angle) * allowedDistance
                    val refinedYDistance = sin(angle) * allowedDistance

                    joyStick.x = joyStickX + refinedXDistance
                    if (rawY > 0) {
                        joyStick.y = joyStickY + refinedYDistance
                    }
                    report[0] = (leftRight * Most.toInt().toFloat()).toInt().toByte()
                    if (rawY > 0) {
                        report[1] = (upDown * Most.toInt().toFloat()).toInt().toByte()
                    }
                    sendReport()
                    return@setOnTouchListener true
                } else {
                    isJoyStickInArea = true
                }

                joyStick.x = joyStickX + (rawX - initialX)
                if (rawY > 0) {
                    joyStick.y = joyStickY + (rawY - initialY)
                }
                report[0] = (leftRight * Most.toInt().toFloat()).toInt().toByte()
                if (rawY > 0) {
                    report[1] = (upDown * Most.toInt().toFloat()).toInt().toByte()
                }
                sendReport()
                return@setOnTouchListener true
            }

            return@setOnTouchListener false
        }

        joystickScreen.setOnTouchListener { view, event ->

            val rawX = event.rawX
            val rawY = event.rawY

            val action = event.action

            if (action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                isJoystickScreenPressed = false

                joyStick.animate().x(joyStickX).y(joyStickY).setDuration(300).start()
                report[0] = 0
                report[1] = 0
                isJoystickPressed = false
                sendReport()
                return@setOnTouchListener true
            } else if (action == MotionEvent.ACTION_MOVE || action == MotionEvent.ACTION_DOWN) {
                if (!isJoystickScreenPressed) {
                    joyStick.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                }

                isJoystickScreenPressed = true

                var leftRight = (rawX - joystickRawX) / allowedDistance
                var upDown = - (rawY - joystickRawY) / allowedDistance

                if (leftRight > 1) {
                    leftRight = 1f
                }

                if (leftRight < - 1) {
                    leftRight = -1f
                }

                if (upDown > 1) {
                    upDown = 1f
                }

                if (upDown < - 1) {
                    upDown = -1f
                }

                val xDistance = rawX - joystickRawX
                val yDistance = rawY - joystickRawY
                val r = sqrt(xDistance * xDistance + yDistance * yDistance)

                if (r > allowedDistance) {
                    if (isJoyStickInArea) {
                        joyStick.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        isJoyStickInArea = false
                    }
                    val angle = atan2(yDistance, xDistance)
                    val refinedXDistance = cos(angle) * allowedDistance
                    val refinedYDistance = sin(angle) * allowedDistance

                    joyStick.x = joyStickX + refinedXDistance
                    if (rawY > 0) {
                        joyStick.y = joyStickY + refinedYDistance
                    }
                    report[0] = (leftRight * Most.toInt().toFloat()).toInt().toByte()
                    if (rawY > 0) {
                        report[1] = (upDown * Most.toInt().toFloat()).toInt().toByte()
                    }
                    sendReport()
                    return@setOnTouchListener true
                } else {
                    isJoyStickInArea = true
                }

                joyStick.x = joyStickX + (rawX - joystickRawX)
                if (rawY > 0) {
                    joyStick.y = joyStickY + (rawY - joystickRawY)
                }
                report[0] = (leftRight * Most.toInt().toFloat()).toInt().toByte()
                if (rawY > 0) {
                    report[1] = (upDown * Most.toInt().toFloat()).toInt().toByte()
                }
                sendReport()
                return@setOnTouchListener true
            }

            return@setOnTouchListener false
        }

        val broadcastAddress = InetAddress.getByName("255.255.255.255")
        var connectAddress: InetAddress? = null

        Thread {
            val socket = DatagramSocket();
            socket.broadcast = true
            val packet = DatagramPacket(ByteArray(3), 0, 3, broadcastAddress, 3333)
            socket.send(packet)

            val responseBuffer = ByteArray(3000)
            val responsePacket = DatagramPacket(responseBuffer, responseBuffer.size)

            socket.soTimeout = 5000

            try {
                socket.receive(responsePacket)
                val responseMessage = String(responsePacket.data, 0, responsePacket.length)
                val deviceAddress = responsePacket.address
                connectAddress = deviceAddress
                println("Device discovered at: $deviceAddress")
                println("Response message: $responseMessage")
            } catch (e: Exception) {
                println("No response received: ${e.message}")
            }

            val tcpSocket = Socket()
            tcpSocket.connect(InetSocketAddress(connectAddress, 3338))
            outputStream = tcpSocket.getOutputStream()
//            while (true) {
//                outputStream.write(report, 0, 11)
//                Thread.sleep(3000)
//            }
        }.start()
    }
}

class Screen @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        var dispatch = true
        ev?.let { event ->
            for (index in 0 until event.pointerCount) {
                println("ooo")
                println("pointer count " + event.pointerCount)
                val x = event.getX(index) + this.x
                val y = event.getY(index) + this.y

                val crossDistance = hypot(x - crossX, y - crossY)
                val squareDistance = hypot(x - squareX, y - squareY)
                val circleDistance = hypot(x - circleX, y - circleY)
                val triangleDistance = hypot(x - triangleX, y - triangleY)

                val smallestDistance = listOf(
                    0 to crossDistance,
                    1 to squareDistance,
                    2 to circleDistance,
                    3 to triangleDistance
                ).minBy {
                    it.second
                }

                val createdEvent = event

                println(createdEvent.pointerCount)
                println("ooooo")

                if (smallestDistance.second > 3 * cross.width) {
                    return super.dispatchTouchEvent(createdEvent).also {
                        dispatch = it
                        println("dispatch is " + it)
                    }
//                    createdEvent.recycle()
                } else {
                    println("oooooooo")

                    when (smallestDistance.first) {
                        0 -> return cross.dispatchTouchEvent(createdEvent)
                        1 -> return square.dispatchTouchEvent(createdEvent)
                        2 -> return circle.dispatchTouchEvent(createdEvent)
                        3 -> return triangle.dispatchTouchEvent(createdEvent)
                    }
//                createdEvent.recycle()
                }
            }
        }

        return true
    }
}

fun View.rawXY(): FloatArray {
    val xy = IntArray(2)
    this.getLocationOnScreen(xy)
    return floatArrayOf(xy[0].toFloat(), xy[1].toFloat())
}

fun sendReport() {
    reportExecutor.execute {
        outputStream?.write(report)
    }
}
