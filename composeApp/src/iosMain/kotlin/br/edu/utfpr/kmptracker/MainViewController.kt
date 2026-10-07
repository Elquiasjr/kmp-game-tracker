package br.edu.utfpr.kmptracker

import androidx.compose.ui.window.ComposeUIViewController
import br.edu.utfpr.kmptracker.di.initKoin
import platform.UIKit.UIViewController

private var koinStarted = false

/** Chamado pelo ContentView.swift do projeto iosApp. */
fun MainViewController(): UIViewController {
    if (!koinStarted) {
        initKoin()
        koinStarted = true
    }
    return ComposeUIViewController { App() }
}
