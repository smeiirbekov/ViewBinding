package com.sm.viewbinding

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
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
            viewLifecycleOwnerLiveData.value?.lifecycle?.currentState?.isAtLeast(Lifecycle.State.INITIALIZED) == true
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