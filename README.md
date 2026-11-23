# Navegador Livre para Android

Navegador Android simples inspirado na experiência de um navegador moderno. Usa o WebView do Android (não o motor completo do Google Chrome).

## Recursos
- Pesquisar na web ou abrir URLs
- Criar, alternar e fechar abas
- Voltar, avançar, atualizar, página inicial
- Salvar e abrir favoritos
- Compartilhar endereço

## Como gerar o APK no GitHub
1. Crie um repositório GitHub vazio, por exemplo `navegador-livre`.
2. Extraia este ZIP e envie **o conteúdo da pasta NavegadorLivre** ao repositório, inclusive a pasta `.github` (oculta em alguns gerenciadores).
3. Vá em **Actions** > **Gerar APK Android** > **Run workflow** (ou envie um commit na branch `main`).
4. Após a execução, abra a execução concluída e baixe o artefato `navegador-livre-debug-apk`.
5. Extraia o ZIP de artefato para obter `app-debug.apk` e instale no Android.

## Android Studio / Codespaces
Importe como projeto Gradle, aguarde sincronização e execute `gradle assembleDebug`. SDK 35 e Java 17 são necessários.

## Limites da versão inicial
Não é uma cópia do Chrome. Esta versão usa WebView, não oferece sincronização Google, extensões, modo anônimo, gerenciador de downloads ou suporte avançado a múltiplas janelas. Alguns sites não funcionam integralmente em WebView. O APK do workflow é de teste e não é assinado para publicação na Play Store.
