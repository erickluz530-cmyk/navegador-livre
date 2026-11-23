# Navegador Livre 2.1 (Android / Kotlin)

Navegador independente com interface inspirada em navegadores modernos. Baseado em Android WebView; não é o Google Chrome ou Samsung Internet.

## Incluído
- Barra de endereços e busca Google, página inicial e seis atalhos
- Abas, favoritos, navegação de voltar/avançar/recarregar
- Tema claro/escuro
- Downloads via Android DownloadManager para pasta Downloads (solicita permissão em Android 9 ou anterior)
- Nova janela de navegação privada em processo Android separado, usando diretório WebView diferente (Android 9+). Dados de cookies/web storage privados são apagados ao fechar a Activity; downloads permanecem no dispositivo. Não equivale a anonimato na internet.
- Vídeos HTML5 com tela cheia, conforme o suporte do site e do Android WebView. Não reproduz o player proprietário da Samsung nem garante compatibilidade com todos os serviços DRM.
- Sem bibliotecas de publicidade dentro do aplicativo; sites acessados podem exibir publicidade.

## Atualização pelo GitHub
1. Extraia o ZIP, abra a pasta `NavegadorLivre` e envie **o conteúdo dela** para a raiz do mesmo repositório GitHub (`app`, `.github`, `build.gradle.kts`, `settings.gradle.kts` etc.). Substitua os arquivos antigos.
2. Em `.github/workflows/main.yml`, mantenha o workflow `main.yml` atualizado, sem criar outro workflow duplicado.
3. Acesse Actions → Gerar APK Android → selecione a execução com ✔ → Artifacts → `navegador-livre-debug-apk`.
4. Descompacte e instale `app-debug.apk`. O código usa o mesmo applicationId que a v1, mas builds debug geradas em runners diferentes **podem usar chaves de assinatura diferentes**, exigindo desinstalar a versão antiga antes de instalar; isso apaga dados locais do app.

## Estrutura
- `app/src/main/java/com/example/navegadorlivre/MainActivity.kt`: UI, abas, WebView, downloads, vídeo, temas
- `app/src/main/java/com/example/navegadorlivre/PrivateActivity.kt`: Activity de navegação privada (processo isolado)
- `app/src/main/AndroidManifest.xml`: permissões e activities
- `.github/workflows/main.yml`: montagem do APK com GitHub Actions e SDK v4

## Limitações
Build não foi executado neste ambiente; valide no GitHub Actions. Apps de navegador completos exigem tratamento adicional de autenticação, uploads, permissões dos sites, pop-ups, recuperação de sessão e segurança.

## Correções 2.1
- Barra de endereços respeita a barra de status/navegação do Android 15.
- Toque no campo seleciona o URL inteiro para digitar outro endereço.
- Botão + abre aba e mostra confirmação e contador de abas.
- Links com abertura de nova janela pelo gesto do usuário podem abrir nova aba.
- URL carregada em segundo plano não sobrescreve edição em andamento.
