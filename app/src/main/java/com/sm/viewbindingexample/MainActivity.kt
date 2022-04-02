package com.sm.viewbindingexample

import androidx.appcompat.app.AppCompatActivity
import com.sm.viewbinding.viewBinding
import com.sm.viewbindingexample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityMainBinding::inflate)

}