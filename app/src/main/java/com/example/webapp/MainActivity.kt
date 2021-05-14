package com.example.webapp

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.Message
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebView.WebViewTransport
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.webapp.config.AppConfig
import com.example.webapp.model.ConfigModel
import com.example.webapp.utils.AppUtils.getFromAssets
import com.example.webapp.view.FlightWebView
import com.example.webapp.view.FlightWebView.CustomChromeClient
import com.google.gson.Gson
import im.delight.android.webview.AdvancedWebView


class MainActivity : AppCompatActivity(), AdvancedWebView.Listener {
    private var mWebView: FlightWebView? = null
    private var mToolbar: Toolbar? = null
    private fun initToolbar() {
        setSupportActionBar(mToolbar)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        mToolbar!!.setNavigationOnClickListener {
            finish() //返回
        }
    }

    private var mMenu: Menu? = null
    private var mConfigModel: ConfigModel? = null
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (mConfigModel!!.appbar.menuEnable) {
            menuInflater.inflate(R.menu.menu_main, menu)
            mMenu = menu
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_exit) {
            finish()
        } else if (item.itemId == R.id.action_copy) {
            val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("Label", mWebView!!.url)
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(this, R.string.prompt_copy_success, Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mWebView = findViewById(R.id.web_view)
        mWebView?.setListener(this, this)
        mWebView?.setMixedContentAllowed(false)
        mWebView?.setGeolocationEnabled(true)
        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        mToolbar = findViewById(R.id.toolbar)
        val toolbarGroup = findViewById<FrameLayout>(R.id.toolbar_group)

        // 获取配置模型
        mConfigModel = model
        if (mConfigModel!!.landscape) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        // 设置toolbar
        if (mConfigModel!!.appbar.enable) {
            initToolbar()
            val themeColor = Color.parseColor(mConfigModel!!.theme.colorPrimary)
            mToolbar?.setBackgroundColor(themeColor)
            window.statusBarColor = themeColor
            supportActionBar!!.title = mConfigModel!!.appbar.title
        } else {
            toolbarGroup.removeView(mToolbar)
        }


        // 硬件加速
        if (mConfigModel!!.hardwareAccelerated) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
            )
        }

        // 电脑模式
        mWebView?.setDesktopMode(mConfigModel!!.pcMode)

        // url加载
        val launchUrl = mConfigModel!!.launchUrl
        if (!TextUtils.isEmpty(launchUrl)) {
            mWebView?.loadUrl(launchUrl)
        }

        // 设置支持打开第三方应用
        mWebView?.client!!.setOpenAppEnable(mConfigModel!!.openAppEnable)

        // 进度条
        if (mConfigModel!!.enableProgressBar) {
            mWebView?.progressBar = progressBar
        }
        progressBar.visibility = if (mConfigModel!!.enableProgressBar) View.VISIBLE else View.GONE


        // 多窗口支持
        if (mConfigModel!!.supportMultipleWindows) {
            mWebView?.settings?.setSupportMultipleWindows(true)
        }
        mWebView?.webChromeClient = object : CustomChromeClient(this, mWebView!!) {
            override fun onReceivedTitle(view: WebView, title: String) {
                if (mConfigModel!!.appbar.showWebTitle) {
                    mToolbar?.subtitle = title
                }
            }

            override fun onCreateWindow(
                view: WebView,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: Message
            ): Boolean {
                return if (!mConfigModel!!.supportMultipleWindows) {
                    super.onCreateWindow(view, isDialog, isUserGesture, resultMsg)
                } else {
                    val newWebView = AdvancedWebView(this@MainActivity)
                    // myParentLayout.addView(newWebView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                    val transport = resultMsg.obj as WebViewTransport
                    transport.webView = newWebView
                    resultMsg.sendToTarget()
                    true
                }
            }
        }
    }

    private val model: ConfigModel
        get() {
            val data: String? = if (packageName == "com.One.WoodenLetter") {
                intent.getStringExtra(ARG_DATA)
            } else {
                getFromAssets(this, AppConfig.CONFIG_FILE)
            }
            return parseModelByData(data)
        }

    private fun parseModelByData(data: String?): ConfigModel {
        return Gson().fromJson(data, ConfigModel::class.java)
    }

    @SuppressLint("NewApi")
    override fun onResume() {
        super.onResume()
        mWebView!!.onResume()
        // ...
    }

    @SuppressLint("NewApi")
    override fun onPause() {
        mWebView!!.onPause()
        // ...
        super.onPause()
    }

    override fun onDestroy() {
        mWebView!!.onDestroy()
        // ...
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        mWebView!!.onActivityResult(requestCode, resultCode, intent)
        // ...
    }

    override fun onBackPressed() {
        if (!mWebView!!.onBackPressed()) {
            return
        }
        // ...
        super.onBackPressed()
    }

    override fun onPageStarted(url: String, favicon: Bitmap?) {}
    override fun onPageFinished(url: String) {}
    override fun onPageError(errorCode: Int, description: String, failingUrl: String) {}
    override fun onDownloadRequested(
        url: String,
        suggestedFilename: String,
        mimeType: String,
        contentLength: Long,
        contentDisposition: String,
        userAgent: String
    ) {
    }

    override fun onExternalPageRequest(url: String) {}

    companion object {
        private const val ARG_DATA = "arg_data"
        fun getIntent(context: Context?, data: String?): Intent {
            val intent = Intent()
            intent.setClass(context!!, MainActivity::class.java)
            intent.putExtra(ARG_DATA, data)
            return intent
        }
    }
}
