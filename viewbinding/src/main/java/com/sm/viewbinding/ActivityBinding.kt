package com.sm.viewbinding

import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Delegate to bind ViewBinding with associated layout.
 * ViewBinding is only accessible after activity's setContentView call
 * @return [ViewBinding]
 * */
fun <B : ViewBinding> ComponentActivity.viewBinding(
    bind: (view: View) -> B
) = object : ReadOnlyProperty<ComponentActivity, B> {

    private var binding: B? = null

    private fun checkBinding(){
        if (binding == null && window != null) {
            val content = findViewById<ViewGroup>(android.R.id.content)?.getChildAt(0)
            if (content != null) binding = bind(content)
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
            "ViewBinding is not accessible before setContentView()"
        )
    }
}