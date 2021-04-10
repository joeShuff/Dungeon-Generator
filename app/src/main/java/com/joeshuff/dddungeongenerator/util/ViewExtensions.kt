package com.joeshuff.dddungeongenerator.util

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.widget.TextView

fun View.makeVisible() {this.visibility = View.VISIBLE}
fun View.isVisible(): Boolean  = this.visibility == View.VISIBLE

fun View.makeInvisible() {this.visibility = View.INVISIBLE}
fun View.isInvisible(): Boolean = this.visibility == View.INVISIBLE

fun View.makeGone() {this.visibility = View.GONE}
fun View.isGone(): Boolean = this.visibility == View.GONE

fun TextView.setTextFade(message: String, totalDuration: Long = 300) {
    animate().alpha(0f).setDuration(totalDuration / 2).withEndAction {
        text = message
        animate().alpha(1f).setDuration(totalDuration / 2).start()
    }.start()
}

fun View.setScrollListener(listener: (scrollX: Int, scrollY: Int) -> Unit) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        viewTreeObserver.addOnScrollChangedListener {
            val scrollY: Int = scrollY
            val scrollX: Int = scrollX

            listener.invoke(scrollX, scrollY)
        }
    } else {
        setOnScrollChangeListener { view, x, y, oldX, oldY ->
            listener.invoke(x, y)
        }
    }
}

fun screenWidth(): Int = Resources.getSystem().displayMetrics.widthPixels

fun screenHeight(): Int = Resources.getSystem().displayMetrics.heightPixels

fun Context.dpToExact(dp: Float) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).toInt()

fun Context.dpToExact(dp: Int) = dpToExact(dp.toFloat())

fun Context.spToExact(sp: Float) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, resources.displayMetrics).toInt()