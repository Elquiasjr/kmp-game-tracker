package br.edu.utfpr.kmptracker

/**
 * Declarada no código comum e implementada em cada plataforma
 * (androidMain, iosMain e jvmMain) com `actual`.
 */
expect fun platformName(): String
