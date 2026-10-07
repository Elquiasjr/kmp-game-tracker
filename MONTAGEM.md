# Como montar o repositório (apague este arquivo antes de publicar)

Este pacote traz todo o código-fonte, mas não traz dois itens que precisam ser
gerados pelas ferramentas oficiais: o Gradle Wrapper (`gradlew`, `gradlew.bat`,
`gradle/wrapper/`) e o projeto Xcode (`iosApp/`).

1. Gere um projeto em https://kmp.jetbrains.com com:
   - Nome: `kmp-game-tracker`  · ID: `br.edu.utfpr.kmptracker`
   - Alvos: Android, iOS (UI compartilhada com Compose) e Desktop.
2. Do projeto gerado, copie para esta pasta:
   - `gradlew`, `gradlew.bat` e a pasta `gradle/wrapper/`
   - a pasta `iosApp/` inteira
3. Compare o `gradle/libs.versions.toml` gerado com o deste projeto e, se forem
   diferentes, use as versões do gerado para `kotlin`, `agp`,
   `compose-multiplatform`, `lifecycle` e `navigation`.
4. Se o wizard separar o Android num módulo próprio (`androidApp`), mantenha a
   estrutura deste projeto (tudo em `composeApp`) e ajuste apenas as versões.
5. Crie o `local.properties` com a sua chave e rode `./gradlew :composeApp:jvmTest`
   e `./gradlew :composeApp:run` para confirmar.
6. `git init`, primeiro commit e push para o GitHub.
