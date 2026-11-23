package com.example.navegadorlivre

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.app.AlertDialog
import android.text.InputType
import android.graphics.drawable.GradientDrawable
import java.net.URLEncoder

class MainActivity : Activity() {
    private val tabs = mutableListOf<WebView>()
    private var currentTab = 0
    private lateinit var addressBar: EditText
    private lateinit var tabButton: TextView
    private lateinit var webContainer: LinearLayout
    private val home = "https://www.google.com/"
    private val prefs by lazy { getSharedPreferences("browser", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(238, 242, 247)
        window.navigationBarColor = Color.WHITE
        buildUi()
        addTab(home)
    }

    private fun dp(n: Int) = (resources.displayMetrics.density * n + .5f).toInt()

    private fun button(text: String, action: () -> Unit): TextView = TextView(this).apply {
        this.text = text
        textSize = 19f
        setTextColor(Color.rgb(37, 57, 81))
        gravity = Gravity.CENTER
        setPadding(dp(8), dp(10), dp(8), dp(10))
        setOnClickListener { action() }
    }

    private fun buildUi() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.WHITE)
        }
        val toolbar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(5), dp(5), dp(5), dp(5))
            setBackgroundColor(Color.rgb(238, 242, 247))
        }
        toolbar.addView(button("⌂") { navigate(home) })
        addressBar = EditText(this).apply {
            setSingleLine(true)
            textSize = 15f
            hint = "Pesquisar ou digitar endereço"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_URI
            imeOptions = EditorInfo.IME_ACTION_GO
            setPadding(dp(12), dp(8), dp(12), dp(8))
            background = GradientDrawable().apply {
                setColor(Color.WHITE)
                cornerRadius = dp(24).toFloat()
            }
            setOnEditorActionListener { _, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_GO ||
                    (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                    navigate(text.toString())
                    (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                        .hideSoftInputFromWindow(windowToken, 0)
                    clearFocus()
                    true
                } else false
            }
        }
        toolbar.addView(addressBar, LinearLayout.LayoutParams(0, dp(44), 1f))
        tabButton = button("1") { showTabs() }
        tabButton.setTypeface(null, Typeface.BOLD)
        toolbar.addView(tabButton)
        toolbar.addView(button("⋮") { showMenu() })
        root.addView(toolbar)

        webContainer = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        root.addView(webContainer, LinearLayout.LayoutParams(-1, 0, 1f))

        val bottom = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.rgb(248, 250, 252))
        }
        listOf(
            "‹" to { back() },
            "›" to { if (active().canGoForward()) active().goForward() else Unit },
            "⟳" to { active().reload() },
            "☆" to { saveFavorite() },
            "+" to { addTab(home) }
        ).forEach { (label, action) ->
            bottom.addView(button(label, action), LinearLayout.LayoutParams(0, dp(52), 1f))
        }
        root.addView(bottom)
        setContentView(root)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun addTab(url: String) {
        val web = WebView(this).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.setSupportZoom(true)
            settings.builtInZoomControls = true
            settings.displayZoomControls = false
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                    return request.url.scheme !in listOf("http", "https")
                }
                override fun onPageFinished(view: WebView, url: String) {
                    if (view === tabs.getOrNull(currentTab)) addressBar.setText(url)
                }
            }
            webChromeClient = WebChromeClient()
        }
        tabs.add(web)
        switchTab(tabs.lastIndex)
        web.loadUrl(url)
    }

    private fun active() = tabs[currentTab]

    private fun switchTab(index: Int) {
        currentTab = index
        webContainer.removeAllViews()
        val web = active()
        (web.parent as? android.view.ViewGroup)?.removeView(web)
        webContainer.addView(web, LinearLayout.LayoutParams(-1, -1))
        addressBar.setText(web.url ?: "")
        tabButton.text = tabs.size.toString()
    }

    private fun navigate(raw: String) {
        val s = raw.trim()
        if (s.isEmpty()) return
        val url = when {
            s.startsWith("https://", true) || s.startsWith("http://", true) -> s
            !s.contains(' ') && s.contains('.') -> "https://$s"
            else -> "https://www.google.com/search?q=${URLEncoder.encode(s, "UTF-8")}" 
        }
        active().loadUrl(url)
    }

    private fun back() {
        if (active().canGoBack()) active().goBack()
        else if (tabs.size > 1) closeTab()
        else Toast.makeText(this, "Sem página anterior", Toast.LENGTH_SHORT).show()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() = back()

    private fun closeTab() {
        if (tabs.size == 1) {
            active().loadUrl(home)
            return
        }
        val web = tabs.removeAt(currentTab)
        webContainer.removeAllViews()
        web.destroy()
        switchTab((currentTab - 1).coerceAtLeast(0))
    }

    private fun showTabs() {
        val labels = tabs.mapIndexed { i, web -> "${i + 1}. ${web.title ?: web.url ?: "Nova aba"}" }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("Abas abertas")
            .setSingleChoiceItems(labels, currentTab) { dialog, which ->
                switchTab(which)
                dialog.dismiss()
            }
            .setPositiveButton("Nova aba") { _, _ -> addTab(home) }
            .setNegativeButton("Fechar aba atual") { _, _ -> closeTab() }
            .show()
    }

    private fun favorites(): List<String> = prefs.getString("favorites", "")
        .orEmpty().split('\n').filter { it.isNotBlank() }

    private fun saveFavorite() {
        val url = active().url ?: return
        val updated = (favorites() + url).distinct()
        prefs.edit().putString("favorites", updated.joinToString("\n")).apply()
        Toast.makeText(this, "Adicionado aos favoritos", Toast.LENGTH_SHORT).show()
    }

    private fun showFavorites() {
        val entries = favorites()
        if (entries.isEmpty()) {
            Toast.makeText(this, "Nenhum favorito salvo", Toast.LENGTH_SHORT).show()
            return
        }
        AlertDialog.Builder(this).setTitle("Favoritos")
            .setItems(entries.toTypedArray()) { _, which -> navigate(entries[which]) }
            .setNegativeButton("Voltar", null).show()
    }

    private fun showMenu() {
        AlertDialog.Builder(this).setTitle("Navegador Livre")
            .setItems(arrayOf("Nova aba", "Favoritos", "Compartilhar endereço", "Fechar aba")) { _, item ->
                when (item) {
                    0 -> addTab(home)
                    1 -> showFavorites()
                    2 -> {
                        val send = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(android.content.Intent.EXTRA_TEXT, active().url.orEmpty())
                        }
                        startActivity(android.content.Intent.createChooser(send, "Compartilhar"))
                    }
                    3 -> closeTab()
                }
            }.show()
    }

    override fun onDestroy() {
        tabs.forEach { web ->
            (web.parent as? android.view.ViewGroup)?.removeView(web)
            web.destroy()
        }
        tabs.clear()
        super.onDestroy()
    }
}
