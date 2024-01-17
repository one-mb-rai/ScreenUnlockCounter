/**
 * ScreenUnlockCounterWidget is a GlanceAppWidget responsible for displaying a screen unlock counter.
 * It uses a DataStore for persisting counter data and updates the counter on screen unlock events.
 */
package com.onemb.screenunlockcounter

import android.content.Context
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.glance.ExperimentalGlanceApi
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.text.Text
import kotlinx.coroutines.flow.first

/**
 * ScreenUnlockCounterWidget is a GlanceAppWidget that displays the screen unlock counter.
 * It updates the counter on screen unlock events and provides a Glance theme for widget appearance.
 */
class ScreenUnlockCounterWidget: GlanceAppWidget() {

    private val CounterIndex = stringPreferencesKey("counter")

    /**
     * Updates the screen unlock counter in the DataStore.
     */
    suspend fun updateCounter(context: Context) {
        val store = AppWidgetDataStore.getInstance(context)
        store.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                val currentCounter = preferences[CounterIndex]?.toInt() ?: 0
                set(CounterIndex, (currentCounter + 1).toString())
            }
        }
    }

    /**
     * Provides the Glance content for the widget.
     * Displays the screen unlock counter using GlanceTheme and Compose components.
     */
    @OptIn(ExperimentalGlanceApi::class)
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val store = AppWidgetDataStore.getInstance(context)
        val initial = store.data.first()
        provideContent {
            val data by store.data.collectAsState(initial)
            GlanceTheme(GlanceTheme.colors) {
                Column(
                    modifier = GlanceModifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column {
                        Text(
                            text = "Screen Unlocks ${data[CounterIndex] ?: 0}"
                        )
                    }
                }
            }
        }
    }
}

/**
 * AppWidgetDataStore is a singleton object managing the DataStore for the widget preferences.
 */
object AppWidgetDataStore {
    private const val PREFERENCES_NAME = "ScreenUnlockCounterWidget"

    /**
     * Extension property for obtaining the DataStore instance associated with the widget.
     */
    private val Context.myWidgetStore by preferencesDataStore(PREFERENCES_NAME)

    /**
     * Provides a singleton instance of the DataStore for the widget preferences.
     */
    fun getInstance(context: Context): DataStore<Preferences> {
        return context.myWidgetStore
    }
}
