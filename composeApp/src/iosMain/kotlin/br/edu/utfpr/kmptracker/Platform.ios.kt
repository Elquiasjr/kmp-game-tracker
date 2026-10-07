package br.edu.utfpr.kmptracker

import platform.UIKit.UIDevice

// API do UIKit chamada diretamente do Kotlin (interop com Objective-C).
actual fun platformName(): String =
    "${UIDevice.currentDevice.systemName()} ${UIDevice.currentDevice.systemVersion}"
