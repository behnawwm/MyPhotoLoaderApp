package com.example.myphotoloaderapp.Util.Common

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.IdRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity

/**
 * Extension method to set Status Bar Color and Status Bar Icon Color Type(dark/light)
 */
enum class StatusIconColorType {
    Dark, Light
}
fun Activity.setStatusBarColor(color: Int, iconColorType: StatusIconColorType = StatusIconColorType.Light) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        this.window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            statusBarColor = color
            decorView.systemUiVisibility = when (iconColorType) {
                StatusIconColorType.Dark -> View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                StatusIconColorType.Light -> 0
            }
        }
    } else
        this.window.statusBarColor = color
}

/**
 * Setup actionbar
 */
fun AppCompatActivity.setupActionBar(@IdRes toolbarId: Int, action: ActionBar.() -> Unit) {
    setSupportActionBar(findViewById(toolbarId))
    supportActionBar?.run {
        action()
    }
}

/**
 * Extension method to get ContentView for ViewGroup.
 */
fun Activity.getContentView(): ViewGroup {
    return this.findViewById(android.R.id.content) as ViewGroup
}