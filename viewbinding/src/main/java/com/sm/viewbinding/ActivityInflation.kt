package com.sm.viewbinding

import android.os.Looper
import android.view.LayoutInflater
import androidx.activity.ComponentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import java.lang.IllegalStateException
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Delegate to inflate ViewBinding with associated layout.<br/>
 * ViewBinding is only accessible after super.onCreate().
 * DO NOT CALL setContentView or pass layoutId to the activity constructor as inflation will happen here.
 * @return [ViewBinding]
 * */
fun <B : ViewBinding> ComponentActivity.viewBinding(
    inflate: (inflater: LayoutInflater) -> B
) = object : ReadOnlyProperty<ComponentActivity, B> {

    private var binding: B? = null

    private fun checkBinding(){
        if (binding == null &&
            window != null &&
            Looper.myLooper() == Looper.getMainLooper()
        ) {
            binding = inflate(layoutInflater)
            setContentView(binding?.root)
        }
    }

    init {
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                checkBinding()
            }
        })
    }

    override fun getValue(thisRef: ComponentActivity, property: KProperty<*>): B {
        checkBinding()
        return binding ?: throw IllegalStateException(
            "ViewBinding is not accessible before super.onCreate()"
        )
    }
}