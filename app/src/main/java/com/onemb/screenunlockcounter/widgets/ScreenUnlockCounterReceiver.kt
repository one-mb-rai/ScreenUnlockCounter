/**
 * ScreenUnlockCounterReceiver is a receiver class extending GlanceAppWidgetReceiver,
 * designed for handling broadcast events related to screen unlocks.
 */
package com.onemb.screenunlockcounter.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * The ScreenUnlockCounterReceiver class extends GlanceAppWidgetReceiver and is responsible
 * for receiving and handling broadcast events related to screen unlocks.
 */
class ScreenUnlockCounterReceiver: GlanceAppWidgetReceiver() {

    /**
     * Returns an instance of the ScreenUnlockCounterWidget, which extends GlanceAppWidget.
     * This is used for widget-related operations.
     */
    override val glanceAppWidget: GlanceAppWidget
        get() = ScreenUnlockCounterWidget()

    /**
     * Overrides the onReceive method to handle the broadcast event.
     * Updates the screen unlock counter using the ScreenUnlockCounterWidget.
     */
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val screenUnlockVal = ScreenUnlockCounterWidget()
        context.let {
            CoroutineScope(Dispatchers.Default).launch {
                if (intent.action != null && intent.action.equals(Intent.ACTION_USER_PRESENT)) {
                    // Update the screen unlock counter
                    screenUnlockVal.incrementCounter(it)
                    // Update all widgets in the context
                    screenUnlockVal.updateAll(context)
                }
            }
        }
    }
}
