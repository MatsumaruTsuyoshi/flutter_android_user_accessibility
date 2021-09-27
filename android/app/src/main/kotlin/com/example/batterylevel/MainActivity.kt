package com.example.batterylevel

import android.accessibilityservice.AccessibilityService
import android.app.Activity
import android.content.Intent
import android.provider.Settings
import android.provider.Settings.Secure
import android.text.TextUtils.SimpleStringSplitter
import android.text.TextUtils.StringSplitter
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import androidx.annotation.NonNull
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel


class MainActivity: FlutterActivity() {
    private val CHANNEL = "samples.flutter.dev/accessibility"

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler {
            // Note: this method is invoked on the main thread.
            call, result ->
            //ユーザー補助が有効になっているか確認
            if ( call.method == "checkAccessibility"){
                val isAccessibility = checkAccessibility()
                result.success(isAccessibility)
            }
            //ユーザー補助を有効にしてもらうために設定ページへ移動する
            if ( call.method == "gotoAccessibility"){
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                if (context !is Activity) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            }

        }
    }
    private fun checkAccessibility(): Boolean {
        val cr = contentResolver
        //探したいパッケージ名（com.example.batterylevelはアプリ固有の名前）
        val SEARCH_PACKAGE_NAME = "com.example.batterylevel.MyAccessibilityService"

        val accessibilityEnalbledFlag: Int = Secure.getInt(
                cr, Secure.ACCESSIBILITY_ENABLED, 0)
        if (accessibilityEnalbledFlag == 1) {
            val settingValues = Secure.getString(cr,
                    Secure.ENABLED_ACCESSIBILITY_SERVICES)
            val splitter: StringSplitter = SimpleStringSplitter(':')
            splitter.setString(settingValues ?: "")

            //ここでSEARCH_PACKAGE_NAMEがあればTrueを返している
            for (accessibility in splitter) {
                return accessibility.contains(SEARCH_PACKAGE_NAME)
            }
        }else{
            return false
        }
        return false
    }
}

class MyAccessibilityService : AccessibilityService() {
    override fun onStart(intent: Intent, startId: Int) {
        super.onStart(intent, startId)
    }

    /**システムで起こったイベントを受け取る */
    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        Log.i(LOG_TAG, "イベント = " + event.eventType)
        Log.i(LOG_TAG, "パッケージ名 = " + event.packageName)
        Log.i(LOG_TAG, "クラス名 = " + event.className)
    }

    override fun onInterrupt() {
        //サービスが中断されたときの処理
    }

    companion object {
        private const val LOG_TAG = "MyAccessibilityService"
    }
}