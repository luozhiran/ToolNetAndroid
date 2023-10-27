package com.itg.ddlnet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.itg.net.DdNet
import com.itg.net.MEDIA_JSON
import com.itg.net.base.DdCallback
import com.itg.net.download.Task
import com.itg.net.download.interfaces.IProgressCallback
import com.itg.net.download.interfaces.ITask
import java.io.File

class MainActivity : AppCompatActivity() {
    var task: ITask? = null
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
        DdNet.instance.download.setGlobalProgressListener(progress)
        findViewById<Button>(R.id.download).setOnClickListener {
            for (i in 0..10) {
//                Thread.sleep(1000)
                Log.e("MainActivity", "延时$i")
                val path = "${filesDir}/$i.zip";
                DdNet.instance.download
                    .taskBuilder()
                    .path(path)
                    .url("https://static-webkit.ddimg.mobi/libra/te/7f6a1b6a365a888b4ca5aabb41e21690${i}.zip")
                    .tryAgainCount(3)
                    .autoRemote(this)
                    .stealth()
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
            DdNet.instance.get()
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
            DdNet.instance.postContent()
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
        DdNet.instance.download.remoteGlobalProgressListener(progress)
    }
}