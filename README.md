# KMP Game Tracker

App de demonstração de **Kotlin Multiplatform** e **Compose Multiplatform**: um tracker de jogos que consome a API pública da [RAWG](https://rawg.io/apidocs), roda no **Android**, no **Desktop** (Windows, macOS, Linux) e no **iOS** a partir de um único código Kotlin, e continua funcionando sem internet.

> Dados e imagens fornecidos por [RAWG](https://rawg.io). Projeto acadêmico, sem fins comerciais.

## Funcionalidades

- **Descobrir:** busca de jogos na RAWG com *debounce* e grade adaptativa (2 colunas no celular, várias no desktop).
- **Detalhes:** capa, data de lançamento, notas, gêneros e descrição.
- **Biblioteca:** marque jogos como *Quero jogar*, *Jogando*, *Zerado* ou *Abandonado*; a lista atualiza sozinha.
- **Offline:** tudo o que já foi visto fica salvo no banco local e continua disponível sem rede.

## O que cada parte demonstra

| Recurso da tecnologia | Onde ver no código |
|---|---|
| Código comum + source sets por plataforma | `composeApp/src/commonMain`, `androidMain`, `iosMain`, `jvmMain` |
| `expect` / `actual` | `Platform.kt` e `Platform.*.kt`; `AppDatabaseConstructor` |
| Interop com APIs nativas | `UIDevice` (iOS), `Build` (Android), `NSFileManager` (iOS) |
| UI compartilhada (Compose Multiplatform + Material 3) | `App.kt`, `ui/` |
| Navegação type-safe | `navigation/Routes.kt`, `App.kt` |
| ViewModel + `StateFlow` | `ui/*/…ViewModel.kt` |
| Rede (Ktor + kotlinx.serialization) | `data/remote/RawgApi.kt`, `Dtos.kt` |
| Banco local reativo (Room KMP + SQLite empacotado) | `data/local/` |
| Offline-first (fonte única de verdade) | `data/GameRepository.kt` |
| Injeção de dependências (Koin) | `di/Koin.kt` e `di/PlatformModule.*.kt` |
| Imagens (Coil 3) | `ui/components/GameCard.kt`, `App.kt` |
| Testes em `commonTest` com `MockEngine` | `composeApp/src/commonTest` |
| CI | `.github/workflows/ci.yml` |

## Pré-requisitos

- **JDK 17** ou mais recente.
- **Android Studio** ou **IntelliJ IDEA** com o plugin *Kotlin Multiplatform*.
- **Android SDK** (instalado pelo Android Studio) para rodar no Android.
- **macOS com Xcode** apenas se quiser rodar no iOS.
- Uma **chave gratuita da RAWG**: crie uma conta em <https://rawg.io/apidocs>.

## Instalação e configuração

```bash
git clone https://github.com/Elquiasjr/kmp-game-tracker.git
cd kmp-game-tracker
cp local.properties.example local.properties   # no Windows: copy local.properties.example local.properties
```

Abra `local.properties` e coloque a sua chave:

```properties
RAWG_API_KEY=sua_chave_aqui
```

O arquivo `local.properties` está no `.gitignore`: a chave nunca vai para o GitHub. Durante o build, o Gradle gera a classe `Secrets` em `composeApp/build/generated` a partir dela. No CI, a chave pode vir da variável de ambiente `RAWG_API_KEY`.

## Executando

**Desktop** (o jeito mais rápido; não precisa de emulador):

```bash
./gradlew :composeApp:run        # no Windows: gradlew.bat :composeApp:run
```

**Android:** abra o projeto no Android Studio, escolha a configuração `composeApp` e um emulador/aparelho, e clique em *Run*. Pelo terminal:

```bash
./gradlew :composeApp:installDebug
```

**iOS** (somente macOS): abra `iosApp/iosApp.xcodeproj` no Xcode e rode em um simulador, ou use a configuração `iosApp` no Android Studio/IntelliJ.

## Testes

```bash
./gradlew :composeApp:jvmTest      # rápido, roda os testes de commonTest na JVM
./gradlew :composeApp:allTests     # todos os alvos disponíveis na sua máquina
```

Os testes usam o `MockEngine` do Ktor com um JSON de exemplo da RAWG, então **não precisam de internet nem da chave da API**. Eles verificam que a chave e os parâmetros vão em cada requisição, que campos desconhecidos do JSON são ignorados, que erros HTTP viram exceção e que os mapeamentos entre API, banco e UI estão corretos.

### Teste manual do modo offline

1. Rode o app e faça algumas buscas e abra alguns jogos.
2. Desligue o Wi-Fi (ou ative o modo avião no emulador).
3. Busque de novo por um termo já usado: os jogos continuam aparecendo, com o aviso *"Sem conexão com a RAWG: exibindo jogos salvos"*.
4. A aba **Biblioteca** funciona normalmente, pois existe só no aparelho.

## Estrutura

```text
composeApp/src/
├── commonMain/kotlin/br/edu/utfpr/kmptracker/
│   ├── App.kt                 # tema, navegação e Coil
│   ├── Platform.kt            # expect fun platformName()
│   ├── data/
│   │   ├── remote/            # Ktor: RawgApi e DTOs
│   │   ├── local/             # Room: entidades, DAO e banco
│   │   ├── GameRepository.kt  # fonte única de verdade
│   │   └── Mappers.kt
│   ├── di/Koin.kt             # módulos de injeção
│   ├── domain/Game.kt         # modelos da UI
│   ├── navigation/Routes.kt
│   └── ui/                    # telas e ViewModels
├── commonTest/                # testes compartilhados
├── androidMain/               # Activity, Application, actual do Android
├── iosMain/                   # MainViewController, actual do iOS
└── jvmMain/                   # main() do Desktop, actual da JVM
```

## Arquitetura

```text
UI (Compose)  →  ViewModel (StateFlow)  →  GameRepository
                                               ├── RawgApi (Ktor)   → só escreve no banco
                                               └── GameDao (Room)   → a UI só lê daqui
```

A tela nunca lê direto da rede: ela observa consultas do Room, que são reativas. A rede apenas atualiza o banco. Sem internet, a atualização falha, mas os dados salvos continuam na tela.

## Problemas comuns

- **Lista sempre vazia e aviso de conexão:** confira a `RAWG_API_KEY` em `local.properties` e rode o build de novo.
- **Erro de versão ao sincronizar o Gradle:** as versões estão em `gradle/libs.versions.toml`. Compare com um projeto novo gerado em <https://kmp.jetbrains.com> e alinhe as versões de Kotlin, AGP e Compose Multiplatform.
- **iOS não compila no Linux/Windows:** esperado; o alvo iOS exige macOS com Xcode.

## Licença

Código sob licença MIT. Os dados e imagens de jogos pertencem à RAWG e aos seus respectivos detentores, e seguem os [termos de uso da API](https://rawg.io/apidocs).
