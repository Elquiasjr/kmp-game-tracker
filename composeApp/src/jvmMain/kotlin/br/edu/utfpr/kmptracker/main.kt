package br.edu.utfpr.kmptracker

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import br.edu.utfpr.kmptracker.di.initKoin

fun main() {
    initKoin()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "KMP Game Tracker",
            state = rememberWindowState(width = 1100.dp, height = 760.dp),
        ) {
            App()
        }
    }
}
