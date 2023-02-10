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
    private val progress = object : IProgressCallback{
        override fun onConnecting(task: Task?) {
            Log.e("MainActivity","global onConnecting")

        }

        override fun onProgress(task: Task?) {
            Log.e("MainActivity","global onProgress"+task?.getProgress())

        }

        override fun onFail(error: String?, task: Task?) {
            Log.e("MainActivity","global onConnecting")

        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        DdNet.instance.download.setGlobalProgressListener(progress)
        findViewById<Button>(R.id.download).setOnClickListener {
            task = DdNet.instance.download
                .downloadTask()
                .path("${filesDir}/a.png")
                .url("https://img.ddimg.mobi/eec4b4388b26f1600152066290.png")
                .autoCancel(this)
                .md5("b86af7bcbb96804377359266a3eaceb7")
                .prepareEnd()
                .setProgressListener(object : IProgressCallback{
                    override fun onConnecting(task: Task?) {
                        Log.e("MainActivity","onConnecting")
                    }

                    override fun onProgress(task: Task?) {
                        Log.e("MainActivity","onProgress"+task?.getProgress())
                    }

                    override fun onFail(error: String?, task: Task?) {
                        Log.e("MainActivity","onFail $error")
                    }

                })
                .start()
            task = DdNet.instance.download
                .downloadTask()
                .path("${filesDir}/b.png")
                .url("https://img.ddimg.mobi/product/590065d8f61271624240162850.png?width=800&height=800")
                .autoCancel(this)
                .prepareEnd()
                .setProgressListener(object : IProgressCallback{
                    override fun onConnecting(task: Task?) {
                        Log.e("MainActivity","onConnecting-======")
                    }

                    override fun onProgress(task: Task?) {
                        Log.e("MainActivity","onProgress==="+task?.getProgress())
                    }

                    override fun onFail(error: String?, task: Task?) {
                        Log.e("MainActivity","onFail=== $error")
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
        DdNet.instance.download.setGlobalProgressListener(progress)
    }
}