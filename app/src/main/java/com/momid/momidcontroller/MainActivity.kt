package com.momid.momidcontroller

import android.os.Bundle
import android.view.MotionEvent
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.OutputStream
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.util.concurrent.Executors

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

val report = ByteArray(11) // left-right, up-down, cross, square, triangle, circle

var outputStream: OutputStream? = null

val reportExecutor = Executors.newSingleThreadExecutor()

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        left = findViewById(R.id.left_arrow)
        right = findViewById(R.id.right_arrow)
        up = findViewById(R.id.up_arrow)
        down = findViewById(R.id.down_arrow)

        cross = findViewById(R.id.cross)
        square = findViewById(R.id.square)
        triangle = findViewById(R.id.triangle)
        circle = findViewById(R.id.circle)

        left.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                report[0] = LEAST
                sendReport()
            }

            if (event.action == MotionEvent.ACTION_UP) {
                report[0] = 0
                sendReport()
            }

            return@setOnTouchListener true
        }

        right.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                report[0] = Most
                sendReport()
            }

            if (event.action == MotionEvent.ACTION_UP) {
                report[0] = 0
                sendReport()
            }

            return@setOnTouchListener true
        }

        up.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                report[1] = Most
                sendReport()
            }

            if (event.action == MotionEvent.ACTION_UP) {
                report[1] = 0
                sendReport()
            }

            return@setOnTouchListener true
        }

        down.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                report[1] = LEAST
                sendReport()
            }

            if (event.action == MotionEvent.ACTION_UP) {
                report[1] = 0
                sendReport()
            }

            return@setOnTouchListener true
        }

        cross.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                report[2] = 1
                sendReport()
            }

            if (event.action == MotionEvent.ACTION_UP) {
                report[2] = 0
                sendReport()
            }

            return@setOnTouchListener true
        }

        square.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                report[3] = 1
                sendReport()
            }

            if (event.action == MotionEvent.ACTION_UP) {
                report[3] = 0
                sendReport()
            }

            return@setOnTouchListener true
        }

        triangle.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                report[4] = 1
                sendReport()
            }

            if (event.action == MotionEvent.ACTION_UP) {
                report[4] = 0
                sendReport()
            }

            return@setOnTouchListener true
        }

        circle.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                report[5] = 1
                sendReport()
            }

            if (event.action == MotionEvent.ACTION_UP) {
                report[5] = 0
                sendReport()
            }

            return@setOnTouchListener true
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

fun sendReport() {
    reportExecutor.execute {
        outputStream?.write(report)
    }
}
