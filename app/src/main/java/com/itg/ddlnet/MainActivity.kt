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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.send).setOnClickListener {
            DdNet.instance.download
                .downloadTask()
                .path("${filesDir}/a.png")
                .url("https://img.ddimg.mobi/eec4b4388b26f1600152066290.png")
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

    }


    override fun onDestroy() {
        super.onDestroy()
    }
}