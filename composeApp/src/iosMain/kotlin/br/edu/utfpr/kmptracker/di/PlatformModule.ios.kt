package br.edu.utfpr.kmptracker.di

import androidx.room.Room
import androidx.room.RoomDatabase
import br.edu.utfpr.kmptracker.data.local.AppDatabase
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

actual val platformModule = module {
    single<HttpClientEngine> { Darwin.create() }
    single<RoomDatabase.Builder<AppDatabase>> { databaseBuilder() }
}

@OptIn(ExperimentalForeignApi::class)
private fun databaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val documents = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = true,
        error = null,
    )
    val path = requireNotNull(documents?.path) + "/kmp_tracker.db"
    return Room.databaseBuilder<AppDatabase>(name = path)
}
