package com.itg.ddlnet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.itg.net.Net
import com.itg.net.Download
import com.itg.net.MEDIA_JSON
import com.itg.net.reqeust.base.DdCallback
import com.itg.net.download.data.Task
import com.itg.net.download.interfaces.IProgressCallback
import java.io.File

class MainActivity : AppCompatActivity() {
    var stop = true
    private val progress = object : IProgressCallback {
        override fun onConnecting(task: Task) {

        }

        override fun onProgress(task: Task, complete:Boolean) {
        }

        override fun onFail(error: String?, task: Task) {
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
       Download.instance.setGlobalProgressListener(progress)
        findViewById<Button>(R.id.download).setOnClickListener {
            for (i in 0..10) {
//                Thread.sleep(1000)
                Log.e("MainActivity", "延时$i")
                val path = "${filesDir}/$i.zip"
                  Download.instance
                    .taskBuilder()
                    .path(path)
                    .url("https://static-webkit.ddimg.mobi/libra/te/7f6a1b6a365a888b4ca5aabb41e21690${i}.zip")
                    .tryAgainCount(3)
                    .autoRemoveActivity(this)
                    .setDownloadListener(object : IProgressCallback {
                        override fun onConnecting(task: Task) {
                            Log.e("MainActivity", "onConnecting $i")
                        }

                        override fun onProgress(task: Task, complete:Boolean) {
                            if (complete) {
                                Log.e(
                                    "MainActivity",
                                    "download is success $path $i ${File(task.path ?: "").exists()}"
                                )
                            }
                        }

                        override fun onFail(error: String?, task: Task) {
                            Log.e("MainActivity", "onFail $error")
                        }

                    })
                    .start()
            }


        }

        findViewById<Button>(R.id.get).setOnClickListener {
            Net.instance.get()
                .url("http://www.baidu.com")
                .addParam("key1", "a")
                .addParam("key2", "b")
                .autoCancel(this)
                .send(object : DdCallback {
                    override fun onFailure(er: String?) {

                    }

                    override fun onResponse(result: String?, code: Int) {

                    }

                })

        }

        findViewById<Button>(R.id.post).setOnClickListener {
            Net.instance.postContent()
                .url("https://www.baidu.com")
                .addContent("fadsf", MEDIA_JSON)
                .autoCancel(this)
                .send(object : DdCallback {
                    override fun onFailure(er: String?) {

                    }

                    override fun onResponse(result: String?, code: Int) {

                    }

                })

        }

    }


    override fun onDestroy() {
        super.onDestroy()
        Download.instance.remoteGlobalProgressListener(progress)
    }
}