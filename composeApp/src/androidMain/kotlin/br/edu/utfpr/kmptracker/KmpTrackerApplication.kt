package br.edu.utfpr.kmptracker

import android.app.Application
import br.edu.utfpr.kmptracker.di.initKoin
import org.koin.android.ext.koin.androidContext

class KmpTrackerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // O Room precisa do Context do Android para localizar o arquivo do banco.
        initKoin { androidContext(this@KmpTrackerApplication) }
    }
}
