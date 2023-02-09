package com.itg.ddlnet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.itg.net.DdNet
import com.itg.net.base.DdCallback
import com.itg.net.download.interfaces.IProgressCallback
import com.itg.net.download.interfaces.Task
import com.itg.net.reqeust.model.Get
import com.itg.net.reqeust.model.Post
import java.io.File

class MainActivity : AppCompatActivity() {
    var task:Task?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.download).setOnClickListener {
            DdNet.instance.download.cancel(task)
            task = DdNet.instance.download
                .downloadTask()
                .path("${filesDir}/a.png")
                .url("https://img.ddimg.mobi/eec4b4388b26f1600152066290.png")
                .autoCancel(this)
                .prepareEnd()
                .setProgressListener("https://img.ddimg.mobi/eec4b4388b26f1600152066290.png",object : IProgressCallback{
                    override fun onConnecting(task: Task?) {
                        Log.e("MainActivity","onConnecting")
                    }

                    override fun onProgress(task: Task?) {
                        Log.e("MainActivity","onProgress"+task?.getProgress())
                    }

                    override fun onFail(error: String?, url: String?) {
                        Log.e("MainActivity","onFail")
                    }

                })
                .start()
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
            DdNet.instance.post()
                .url("")
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
//        DdNet.instance.download.cancel(task)
    }
}