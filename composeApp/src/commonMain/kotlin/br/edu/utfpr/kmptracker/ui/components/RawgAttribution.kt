package br.edu.utfpr.kmptracker.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler

/**
 * Os termos de uso da RAWG exigem atribuição com link ativo
 * em toda tela que exibe dados ou imagens da API.
 */
@Composable
fun RawgAttribution(modifier: Modifier = Modifier) {
    val uriHandler = LocalUriHandler.current // abre o navegador em qualquer plataforma
    TextButton(onClick = { uriHandler.openUri("https://rawg.io") }, modifier = modifier) {
        Text("Dados e imagens fornecidos por RAWG (rawg.io)", style = MaterialTheme.typography.labelSmall)
    }
}
