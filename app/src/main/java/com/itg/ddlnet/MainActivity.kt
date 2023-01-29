package com.itg.ddlnet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.itg.net.DdNet
import com.itg.net.base.DdCallback
import com.itg.net.reqeust.model.Get
import com.itg.net.reqeust.model.Post
import java.io.File

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.send).setOnClickListener {
            DdNet.instance.builder(DdNet.GET).send(object :DdCallback{
                override fun onFailure(er: String?) {

                }

                override fun onResponse(result: String?, code: Int) {

                }

            })
        }

    }
}