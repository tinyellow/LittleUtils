package com.littleyellow.utils.common

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class LocalBroadcast(val context: Context):BroadcastReceiver(), LifecycleObserver{

    private var receive: ((Intent?) -> Unit)? = null
    private val broadcastManager = LocalBroadcastManager.getInstance(context)

    private var lifecycle: Lifecycle? = null

    fun register(vararg actions:String,receive:(intent: Intent?) -> Unit){
        this.receive = receive
        broadcastManager.registerReceiver(this,
            IntentFilter().apply {
                actions.forEach { addAction(it) }
            }
        )
        lifecycle = when(context){
            is FragmentActivity-> {
                context.lifecycle
            }
            is Fragment -> {
                context.lifecycle
            }
            else -> null
        }
        lifecycle?.addObserver(this)
    }

    fun send(vararg actions:String,intent: Intent = Intent()){
        actions.forEach { intent.action = it }
        broadcastManager.sendBroadcast(intent)
    }

    fun unregister(){
        broadcastManager.unregisterReceiver(this)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        receive?.let { it(intent) }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy(){
        receive = null
        unregister()
        lifecycle?.removeObserver(this)
    }
}

class Broadcast(val context: Context):BroadcastReceiver(), LifecycleObserver{

    private var receive: ((Intent?) -> Unit)? = null

    private var lifecycle: Lifecycle? = null

    fun register(vararg actions:String,receive:(intent: Intent?) -> Unit){
        this.receive = receive
        context.registerReceiver(this,
            IntentFilter().apply {
                actions.forEach { addAction(it) }
            }
        )
        lifecycle = when(context){
            is FragmentActivity-> {
                context.lifecycle
            }
            is Fragment -> {
                context.lifecycle
            }
            else -> null
        }
        lifecycle?.addObserver(this)
    }

    fun send(vararg actions:String,intent: Intent = Intent()){
        actions.forEach { intent.action = it }
        context.sendBroadcast(intent)
    }

    fun unregister(){
        context.unregisterReceiver(this)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        receive?.let { it(intent) }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy(){
        receive = null
        unregister()
        lifecycle?.removeObserver(this)
    }
}