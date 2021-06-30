package com.example.myphotoloaderapp.Util.Common

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

///**
// * Extension method to simplify view binding.
// */
//fun <T : ViewDataBinding> View.bind() = DataBindingUtil.bind<T>(this) as T
//
///**
// * Extension method to provide quicker access to the [LayoutInflater] from a [View].
// */
//fun View.getLayoutInflater() = context.getLayoutInflater()


/**
 * Show a snackbar with [message]
 */
inline fun View.snack(
    message: String,
    @BaseTransientBottomBar.Duration length: Int = Snackbar.LENGTH_LONG,
    f: Snackbar.() -> Unit
) {
    val snack = Snackbar.make(this, message, length)
    snack.f()
    snack.show()
}


/**
 * Extension method to get a view as bitmap.
 */
fun View.getBitmap(): Bitmap {
    val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bmp)
    draw(canvas)
    canvas.save()
    return bmp
}

/**
 * Toggle a view's visibility
 */
fun View.toggleVisibility(): View {
    if (visibility == View.VISIBLE) {
        visibility = View.INVISIBLE
    } else {
        visibility = View.INVISIBLE
    }
    return this
}

fun View.invisible(): View {
    visibility = View.INVISIBLE
    return this
}
/**
 * Set an onclick listener
 */
fun <T : View> T.click(block: (T) -> Unit) = setOnClickListener { block(it as T) }

/**
 * Extension method to set OnClickListener on a view.
 */
fun <T : View> T.longClick(block: (T) -> Boolean) = setOnLongClickListener { block(it as T) }