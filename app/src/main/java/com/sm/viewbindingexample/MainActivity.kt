package com.sm.viewbindingexample

import androidx.appcompat.app.AppCompatActivity
import com.sm.viewbinding.inflateBinding
import com.sm.viewbindingexample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by inflateBinding(ActivityMainBinding::inflate)

}