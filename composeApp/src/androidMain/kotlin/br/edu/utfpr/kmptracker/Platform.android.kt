package br.edu.utfpr.kmptracker

import android.os.Build

actual fun platformName(): String = "Android ${Build.VERSION.RELEASE}"
