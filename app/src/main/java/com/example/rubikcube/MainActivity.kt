package com.example.rubikcube

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.net.URL
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initCube()
        navigator()
    }

    private fun renderCube(urlApi: String) {
        val textView: TextView = findViewById(R.id.textView)
        val urlBase = "https://www.ekramik.pl/edu/modules/rubik/api/"
        //val urlBase = "http://ananas.unixstorm.eu/rubikcube/"
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            val url = URL(urlBase.plus(urlApi))
            try {
                textView.text = (url.readText())
                showCube(getCubeWall(0), getCubeWall(1), getCubeWall(2))
            }
            catch (e: Exception) {
                textView.text = e.stackTraceToString()
            }
        }
    }

    private fun showCube(fs: String, ns: String, es: String) {
        val imageView = findViewById<ImageView>(R.id.imageView) // Declaring and initializing the ImageView
        val executor = Executors.newSingleThreadExecutor() // Declaring executor to parse the URL
        val handler = Handler(Looper.getMainLooper()) // Once the executor parses the URL and receives the image, handler will load it in the ImageView
        val baseURL = "https://www.ekramik.pl/edu/modules/rubik/img/rubikCube.php"
        var image: Bitmap? // Initializing the image
        executor.execute {
            val imageURL = baseURL.plus("?fs=").plus(fs).plus("&ns=").plus(ns).plus("&es=").plus(es)
            try {
                val `in` = URL(imageURL).openStream()
                image = BitmapFactory.decodeStream(`in`)
                handler.post {
                    imageView.setImageBitmap(image)
                }
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getCubeID(): Int {
        val textView: TextView = findViewById(R.id.textView)
        val cubeString: String = textView.text as String
        val endIndex =cubeString.indexOf(",",0)
        return (cubeString.substring(1,endIndex)).toInt()
    }

    private fun getCubeWall(wallID: Int): String {
        val textView: TextView = findViewById(R.id.textView)
        val cubeString: String = textView.text as String
        val startIndex =cubeString.indexOf(",",0)
        val beginIndex =cubeString.indexOf('"',startIndex+12*wallID)+1
        val endIndex =cubeString.indexOf('"',startIndex+12*wallID+2)
        return cubeString.substring(beginIndex,endIndex)
    }

    private fun initCube() {
        renderCube("apiNew.php".plus("?id=0"))
    }

    private fun navigator() {
        val buttons = intArrayOf(R.id.button_00, R.id.button_01, R.id.button_02, R.id.button_03,
                                 R.id.button_04, R.id.button_05, R.id.button_06, R.id.button_07,
                                 R.id.button_08, R.id.button_09, R.id.button_10, R.id.button_11,
                                 R.id.button_12, R.id.button_13, R.id.button_14, R.id.button_15)
        val apiStrings: Array<String> = arrayOf("New", "LeftColumnUp", "FrontWallUp", "RightColumnUp",
                                                "TopRowLeft", "TopRowRight", "FrontWallLeft", "FrontWallRight",
                                                "BottomRowLeft", "BottomRowRight", "LeftColumnDown", "FrontWallDown",
                                                "RightColumnDown", "FrontCounterClockWise", "FrontClockWise", "Mix")
        var currentButton: ImageButton
        for (n in 0..15) {
            currentButton = findViewById(buttons[n])
            currentButton.setOnClickListener {
                renderCube("api".plus(apiStrings[n]).plus(".php?id=").plus(getCubeID()))
            }
        }
    }
}
