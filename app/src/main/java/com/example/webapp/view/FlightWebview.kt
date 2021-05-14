package com.example.webapp.view

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Environment
import android.util.AttributeSet
import android.webkit.*
import android.widget.ProgressBar
import android.widget.Toast
import im.delight.android.webview.AdvancedWebView
import com.example.webapp.helper.ProgressHelper

class FlightWebView : AdvancedWebView {
    var progressBar: ProgressBar? = null
    private var openAppEnable = false
    var client: MyWebViewClient? = null
        private set

    fun setOpenAppEnable(openAppEnable: Boolean) {
        this.openAppEnable = openAppEnable
    }

    constructor(context: Context?) : super(context) {
        initializeOptions()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initializeOptions()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        initializeOptions()
    }

    @SuppressLint("NewApi", "SetJavaScriptEnabled")
    fun initializeOptions() {
        webChromeClient = CustomChromeClient(context, this)
        client = MyWebViewClient()
        webViewClient = client!!
        setDownloadListener { url: String, _: String?, contentDisposition: String, mimetype: String, _: Long ->
            downloadBySystem(
                url,
                contentDisposition,
                mimetype
            )
        }
    }

    class MyWebViewClient : WebViewClient() {
        private var openAppEnable = false
        fun setOpenAppEnable(openAppEnable: Boolean) {
            this.openAppEnable = openAppEnable
        }

        private fun lookup(context: Context, url: String): Boolean {
            if (!openAppEnable) return false
            try {
                val packageManager = context.packageManager
                val intent: Intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                val info = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
                // 跳到该应用
                if (info != null) {
                    context.startActivity(intent)
                    return true
                }
            } catch (ignore: Throwable) {
            }
            return false
        }

        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            shouldOverrideUrlLoading(view, request.url.toString())
            return true
        }

        override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
            handler.proceed()
        }

        override fun shouldOverrideUrlLoading(view: WebView, newurl: String): Boolean {
            return if (newurl.startsWith("http://") || newurl.startsWith("https://")) {
                false
            } else {
                lookup(view.context, newurl)
            }
        }

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
        }
    }

    private fun downloadBySystem(url: String, contentDisposition: String, mimeType: String) {
        Toast.makeText(context, "Start download..", Toast.LENGTH_SHORT).show()
        val request = DownloadManager.Request(Uri.parse(url))
        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setAllowedOverMetered(true)
        request.setVisibleInDownloadsUi(false)
        request.setAllowedOverRoaming(true)
        val fileName = URLUtil.guessFileName(url, contentDisposition, mimeType)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
    }

    open class CustomChromeClient(var activity: Context, private val webView: FlightWebView) :
        WebChromeClient() {
        private var mProgressHelper: ProgressHelper? = null
        override fun onProgressChanged(view: WebView, newProgress: Int) {
            initProgressHelper()
            if (mProgressHelper != null) {
                mProgressHelper?.progress(newProgress)
            }
            super.onProgressChanged(view, newProgress)
        }

        private fun initProgressHelper() {
            if (webView.progressBar != null && mProgressHelper == null) {
                mProgressHelper = ProgressHelper(webView.progressBar!!)
            }
        }
    }
}