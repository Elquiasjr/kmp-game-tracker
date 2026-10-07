package br.edu.utfpr.kmptracker.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import br.edu.utfpr.kmptracker.data.local.AppDatabase
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val platformModule = module {
    single<HttpClientEngine> { OkHttp.create() }
    single<RoomDatabase.Builder<AppDatabase>> { databaseBuilder(androidContext()) }
}

private fun databaseBuilder(context: Context): RoomDatabase.Builder<AppDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath("kmp_tracker.db")
    return Room.databaseBuilder<AppDatabase>(context = appContext, name = dbFile.absolutePath)
}
