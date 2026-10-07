package br.edu.utfpr.kmptracker

actual fun platformName(): String = "Desktop · ${System.getProperty("os.name")}"
