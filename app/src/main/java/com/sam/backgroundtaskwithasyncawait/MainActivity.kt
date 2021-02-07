package com.sam.backgroundtaskwithasyncawait

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

class MainActivity : AppCompatActivity() {
    lateinit var scope: CoroutineScope
    lateinit var button: Button
    private val JOB_TIME1 = 3000
    private val JOB_TIME2 = 4000
    private val PROGRESS_START = 0
    private val PROGRESS_MAX = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val resultProgress1 = findViewById<ProgressBar>(R.id.progress_bar1)
        val resultProgress2 = findViewById<ProgressBar>(R.id.progress_bar2)
        resultProgress1.scaleY = 5f
        resultProgress2.scaleY = 5f
        button = findViewById(R.id.start_task)
        button.setOnClickListener {
            scope = CoroutineScope(Dispatchers.Main)
            startTask()
        }
    }

    private fun startTask() {
        var result = ""
        scope.launch {
            var time = measureTimeMillis {
                val result1: Deferred<String> = async {
                    println("DEBUG async 1 ${Thread.currentThread().name}")
                    getDetaFromNetwork1()
                }
                val result2: Deferred<String> = async {
                    println("DEBUG async 2 ${Thread.currentThread().name}")
                    getDetaFromNetwork2()
                }
                updateUI(result1.await())
                updateUI(result2.await())
            }
            println("DEBUG total Ealpsed Time ${time}")
        }
    }

    private suspend fun getDetaFromNetwork1(): String {
        withContext(Dispatchers.IO) {
            for (i in PROGRESS_START..PROGRESS_MAX){
                delay((JOB_TIME1/PROGRESS_MAX).toLong())
                println("DEBUG getDataFromNetwork 1: ${i}: ${Thread.currentThread().name}")
                showProgressBar1(i)
            }
        }
        return "Task 1 Completed"
    }

    private suspend fun showProgressBar1(i: Int) {
        withContext(Dispatchers.Main){
            val resultProgress1 = findViewById<ProgressBar>(R.id.progress_bar1)
            resultProgress1.progress = i
        }
    }


    private suspend fun getDetaFromNetwork2(): String {
        withContext(Dispatchers.IO) {
            for (i in PROGRESS_START..PROGRESS_MAX){
                delay((JOB_TIME2/PROGRESS_MAX).toLong())
                println("DEBUG getDataFromNetwork 2: ${i}: ${Thread.currentThread().name}")
                showProgressBar2(i)
            }
        }
        return "Task 2 Completed"
    }

    private suspend fun showProgressBar2(i: Int) {
        withContext(Dispatchers.Main){
            val resultProgress2 = findViewById<ProgressBar>(R.id.progress_bar2)
            resultProgress2.progress = i
        }
    }

    private suspend fun updateUI(message: String) {
        withContext(Dispatchers.IO) {
            println("DEBUG updateUI ${Thread.currentThread().name}")
            val resultTxt = findViewById<TextView>(R.id.test_Result)
            resultTxt.setText(message)
        }
    }
}