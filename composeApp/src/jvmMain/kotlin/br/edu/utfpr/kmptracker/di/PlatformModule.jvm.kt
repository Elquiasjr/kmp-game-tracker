package br.edu.utfpr.kmptracker.di

import androidx.room.Room
import androidx.room.RoomDatabase
import br.edu.utfpr.kmptracker.data.local.AppDatabase
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import java.io.File
import org.koin.dsl.module

actual val platformModule = module {
    single<HttpClientEngine> { OkHttp.create() }
    single<RoomDatabase.Builder<AppDatabase>> { databaseBuilder() }
}

private fun databaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    // Banco em ~/.kmptracker/kmp_tracker.db
    val dbFile = File(System.getProperty("user.home"), ".kmptracker/kmp_tracker.db")
    dbFile.parentFile?.mkdirs()
    return Room.databaseBuilder<AppDatabase>(name = dbFile.absolutePath)
}
