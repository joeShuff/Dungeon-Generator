package com.joeshuff.dddungeongenerator.util

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