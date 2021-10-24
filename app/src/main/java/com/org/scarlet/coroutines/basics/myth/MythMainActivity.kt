package com.org.scarlet.coroutines.basics.myth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.org.scarlet.R
import kotlinx.coroutines.*
import kotlin.system.exitProcess

class MythMainActivity : AppCompatActivity() {

    private lateinit var textView: TextView
    private lateinit var findButton: Button
    private lateinit var cancelButton: Button
    private lateinit var status: TextView

    private var primeJob: Job? = null
    private var countingJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_myth)

        textView = findViewById(R.id.counter)
        findButton = findViewById(R.id.startButton)
        cancelButton = findViewById(R.id.stopButton)
        status = findViewById(R.id.findBigPrime)

        findButton.setOnClickListener {
            findButton.isEnabled = false
            status.text = "Calculating big prime number ..."

            showSnackbar("Launching finBigPrime ...")

            primeJob = lifecycleScope.launch {
//                findBigPrime_Wish_To_Be_NonBlocking()
                findBigPrime_ProperWay()
                status.text = "Done"
            }
        }

        cancelButton.setOnClickListener {
            cancelButton.isEnabled = false
            showSnackbar("Cancelling findBigPrime ...")
            status.text = "Cancelling findBigPrime ..."

            primeJob?.cancel()
            countingJob?.cancel()
        }
    }

    override fun onStart() {
        super.onStart()

        countingJob = lifecycleScope.launch {
            var value = 0
            while (true) {
                textView.text = value.toString().also {
                    value++
                }
                delay(1000)
            }
        }
    }

    private fun showSnackbar(msg: String) {
        Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()

        exitProcess(0)
    }
}