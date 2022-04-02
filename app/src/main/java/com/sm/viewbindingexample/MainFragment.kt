package com.sm.viewbindingexample

import androidx.fragment.app.Fragment
import com.sm.viewbinding.viewBinding
import com.sm.viewbindingexample.databinding.FragmentMainBinding

class MainFragment: Fragment(R.layout.fragment_main) {

    private val binding by viewBinding(FragmentMainBinding::bind)

}