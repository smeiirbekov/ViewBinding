package com.sm.viewbinding

import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import java.lang.IllegalStateException
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Delegate to bind ViewBinding with associated layout.
 * ViewBinding is only accessible when fragment's view is ready and before onDestroyView
 * @return [ViewBinding]
 * */
fun <B : ViewBinding> Fragment.viewBinding(
    bind: (view: View) -> B
) = object : ReadOnlyProperty<Fragment, B> {

    private var binding: B? = null

    private fun checkBinding(){
        if (binding == null &&
            view != null &&
            viewLifecycleOwnerLiveData.value?.lifecycle?.currentState != Lifecycle.State.DESTROYED
        ) {
            binding = bind(requireView())
        }
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
        return binding ?: throw IllegalStateException(
            "ViewBinding is only accessible after onCreateView (when Fragment.view != null) and before onDestroyView"
        )
    }
}

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

/**
 * Delegate to inflate ViewBinding with associated layout.<br/>
 * ViewBinding is only accessible after super.onCreate().
 * DO NOT CALL setContentView or pass layoutId to the activity constructor as inflation will happen here.
 * @return [ViewBinding]
 * */
@JvmName("viewBindingInflate")
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