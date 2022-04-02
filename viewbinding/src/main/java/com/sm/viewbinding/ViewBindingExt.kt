package com.sm.viewbinding

import android.view.LayoutInflater
import android.view.View
import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun <B : ViewBinding> Fragment.viewBinding(
    bind: (view: View) -> B
) = object : ReadOnlyProperty<Fragment, B> {

    private var binding: B? = null

    @MainThread
    private fun checkBinding(){
        if (binding == null)
            binding = bind(requireView())
    }

    init {
        viewLifecycleOwnerLiveData.observe(this@viewBinding) { viewLifecycleOwner ->
            if (viewLifecycleOwner == null) return@observe
            checkBinding()
            viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    binding = null
                }
            })
        }
    }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): B {
        checkBinding()
        return binding ?: throw NullPointerException(
            "Can not access binding outside of onCreateView and onDestroyView"
        )
    }
}

fun <B : ViewBinding> ComponentActivity.viewBinding(
    inflate: (inflater: LayoutInflater) -> B
) = object : ReadOnlyProperty<ComponentActivity, B> {

    private var binding: B? = null

    init {
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                binding = inflate(layoutInflater)
                setContentView(binding?.root)
            }
        })
    }

    override fun getValue(thisRef: ComponentActivity, property: KProperty<*>): B {
        return binding ?: throw NullPointerException(
            "Can not access binding before of onCreate.super()"
        )
    }
}