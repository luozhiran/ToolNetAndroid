package com.itg.net.reqeust

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

class MyLifecycleEventObserver : LifecycleEventObserver {
        var listener:(()->Unit)?=null

        fun setCallback( callback:()->Unit){
            listener = callback
        }
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            if (event == Lifecycle.Event.ON_DESTROY) {
                listener?.invoke()
            }
        }
    }