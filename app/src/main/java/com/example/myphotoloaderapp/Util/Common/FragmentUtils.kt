package com.example.myphotoloaderapp.Util.Common

import android.widget.Toast
import androidx.fragment.app.Fragment

/**
 * Extension method to display toast text for SupportFragment.
 */
fun Fragment.toast(text: CharSequence, duration: Int = Toast.LENGTH_LONG) = this?.let { activity.toast(text, duration) }