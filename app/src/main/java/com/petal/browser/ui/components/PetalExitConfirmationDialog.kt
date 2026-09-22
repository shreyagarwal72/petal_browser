package com.petal.browser.ui.components

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.view.WindowManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.petal.browser.ui.theme.PetalExpressiveTheme

object PetalExitConfirmationDialog {
    @JvmStatic fun show(activity: Activity, onExit: Runnable) {
        if (activity.isFinishing || activity.isDestroyed) return
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view = ComposeView(activity).apply {
            setViewTreeLifecycleOwner(activity); setViewTreeViewModelStoreOwner(activity); setViewTreeSavedStateRegistryOwner(activity)
            setContent { PetalExpressiveTheme { BasicAlertDialog(onDismissRequest = { dialog.dismiss() }, properties = DialogProperties(usePlatformDefaultWidth = false)) { Surface(Modifier.fillMaxWidth().padding(16.dp), RoundedCornerShape(28.dp), color = MaterialTheme.colorScheme.surfaceContainerHigh, tonalElevation = 6.dp) { Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) { Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { Icon(Icons.Rounded.Close, null, Modifier.size(32.dp), tint = MaterialTheme.colorScheme.error) }; Text("Leave Petal Browser?", style = MaterialTheme.typography.headlineSmall); Text("Do you want to exit the browser?", color = MaterialTheme.colorScheme.onSurfaceVariant); Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) { TextButton(onClick = { dialog.dismiss() }) { Text("Stay") }; Button(onClick = { dialog.dismiss(); onExit.run() }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) { Text("Exit") } } } } } } }
        }
        dialog.setContentView(view); dialog.setOnShowListener { dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)); dialog.window?.setDimAmount(.68f); dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND); dialog.window?.setLayout((activity.resources.displayMetrics.widthPixels * .88f).toInt(), WindowManager.LayoutParams.WRAP_CONTENT) }; dialog.show()
    }
}
