package com.petal.browser.ui.components

import android.app.Activity
import android.content.SharedPreferences
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import androidx.preference.PreferenceManager
import com.google.android.material.color.MaterialColors

object PetalWritingToolsBar {
    private const val PREF = "sp_writing_tools_bar"
    @JvmStatic fun attach(activity: Activity, host: ViewGroup, preferences: SharedPreferences) {
        val bar = LinearLayout(activity).apply { tag = "petal_writing_tools"; orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL; setPadding(8, 4, 8, 4); setBackgroundColor(MaterialColors.getColor(activity, com.google.android.material.R.attr.colorSurfaceContainerHigh, Color.WHITE)); elevation = 8f; visibility = View.GONE }
        listOf("Undo" to { (activity.currentFocus as? EditText)?.onTextContextMenuItem(android.R.id.undo) }, "Redo" to { (activity.currentFocus as? EditText)?.onTextContextMenuItem(android.R.id.redo) }, "Select all" to { (activity.currentFocus as? EditText)?.selectAll() }, "Cut" to { (activity.currentFocus as? EditText)?.onTextContextMenuItem(android.R.id.cut) }, "Copy" to { (activity.currentFocus as? EditText)?.onTextContextMenuItem(android.R.id.copy) }, "Paste" to { (activity.currentFocus as? EditText)?.onTextContextMenuItem(android.R.id.paste) }, "Clear" to { (activity.currentFocus as? EditText)?.setText("") }, "Hide" to { activity.getSystemService(android.view.inputmethod.InputMethodManager::class.java)?.hideSoftInputFromWindow(bar.windowToken, 0) }).forEach { (label, action) -> bar.addView(MaterialButton(activity).apply { text = label; isAllCaps = false; minWidth = 0; insetTop = 0; insetBottom = 0; setPadding(12, 0, 12, 0); setOnClickListener { action() }; layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 44) }) }
        host.addView(bar, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply { gravity = Gravity.BOTTOM })
        fun update(i: WindowInsetsCompat) { bar.visibility = if (i.isVisible(WindowInsetsCompat.Type.ime()) && preferences.getBoolean(PREF, true)) View.VISIBLE else View.GONE; (bar.layoutParams as? ViewGroup.MarginLayoutParams)?.let { it.bottomMargin = 0; bar.layoutParams = it } }
        ViewCompat.getRootWindowInsets(host)?.let(::update)
        preferences.registerOnSharedPreferenceChangeListener { _, key -> if (key == PREF) ViewCompat.getRootWindowInsets(host)?.let(::update) }
    }

    @JvmStatic fun update(host: ViewGroup, i: WindowInsetsCompat) {
        for (n in 0 until host.childCount) {
            val child = host.getChildAt(n)
            if (child is LinearLayout && child.tag == "petal_writing_tools") {
                val enabled = PreferenceManager.getDefaultSharedPreferences(host.context).getBoolean(PREF, true)
                child.visibility = if (enabled && i.isVisible(WindowInsetsCompat.Type.ime())) View.VISIBLE else View.GONE
                (child.layoutParams as? ViewGroup.MarginLayoutParams)?.let { it.bottomMargin = 0; child.layoutParams = it }
            }
        }
    }
}
