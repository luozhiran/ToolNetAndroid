package com.itg.ddlnet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.itg.net.DdNet
import com.itg.net.MEDIA_JSON
import com.itg.net.base.DdCallback
import com.itg.net.download.interfaces.IProgressCallback
import com.itg.net.download.interfaces.Task
import com.itg.net.reqeust.model.get.GetGenerator
import java.io.File

class MainActivity : AppCompatActivity() {
    var task:Task?=null
    var stop = true
    private val progress = object : IProgressCallback{
        override fun onConnecting(task: Task?) {
            Log.e("MainActivity","global onConnecting")

        }

        override fun onProgress(task: Task?) {
            Log.e("MainActivity","global onProgress"+task?.getProgress())

        }

        override fun onFail(error: String?, task: Task?) {
            Log.e("MainActivity",error?:"")

        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DdNet.instance.download.setGlobalProgressListener(progress)
        findViewById<Button>(R.id.download).setOnClickListener {
            for (i in 0.. 10) {
                val path = "${filesDir}/${i}.zip";
                task = DdNet.instance.download
                    .downloadTask()
                    .path(path)
                    .url("https://static-webkit.ddimg.mobi/libra/te/7f6a1b6a365a888b4ca5aabb41e21690.zip")
                    .autoCancel(this)
                    .tryAgainCount(3)
                    .prepareEnd()
                    .setProgressListener(object : IProgressCallback{
                        override fun onConnecting(task: Task?) {
                            Log.e("MainActivity","onConnecting")
                        }

                        override fun onProgress(task: Task?) {
                            if (task?.getProgress() == 100) {
                                Log.e("MainActivity", "download is success $i ${File(task.path()?:"").exists()}")
                            }
                            Log.e("MainActivity","onProgress:"+task?.getProgress())
                        }

                        override fun onFail(error: String?, task: Task?) {
                            Log.e("MainActivity","onFail $error")
                        }

                    })
                    .start()
            }


        }

        findViewById<Button>(R.id.get).setOnClickListener {
            DdNet.instance.get()
                .url("https://www.baidu.com")
                .autoCancel(this)
                .send(object :DdCallback{
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
                .send(object :DdCallback{
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