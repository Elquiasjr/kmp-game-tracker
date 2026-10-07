package br.edu.utfpr.kmptracker.di

import br.edu.utfpr.kmptracker.config.Secrets
import br.edu.utfpr.kmptracker.data.GameRepository
import br.edu.utfpr.kmptracker.data.local.AppDatabase
import br.edu.utfpr.kmptracker.data.local.buildDatabase
import br.edu.utfpr.kmptracker.data.remote.RawgApi
import br.edu.utfpr.kmptracker.data.remote.createHttpClient
import br.edu.utfpr.kmptracker.ui.detail.DetailViewModel
import br.edu.utfpr.kmptracker.ui.discover.DiscoverViewModel
import br.edu.utfpr.kmptracker.ui.library.LibraryViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

/**
 * Cada plataforma fornece:
 *  - HttpClientEngine (OkHttp ou Darwin)
 *  - RoomDatabase.Builder<AppDatabase> (onde o arquivo SQLite fica)
 */
expect val platformModule: Module

val sharedModule = module {
    single { createHttpClient(engine = get(), apiKey = Secrets.RAWG_API_KEY) }
    single { RawgApi(get()) }
    single<AppDatabase> { buildDatabase(get()) }
    single { get<AppDatabase>().gameDao() }
    single { GameRepository(api = get(), dao = get()) }

    viewModelOf(::DiscoverViewModel)
    viewModelOf(::LibraryViewModel)
    viewModel { params -> DetailViewModel(gameId = params.get(), repository = get()) }
}

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(sharedModule, platformModule)
    }
}
