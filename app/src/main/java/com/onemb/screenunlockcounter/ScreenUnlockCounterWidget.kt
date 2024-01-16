package com.onemb.screenunlockcounter

import android.content.Context
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.glance.ExperimentalGlanceApi
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.text.Text
import kotlinx.coroutines.flow.first


class ScreenUnlockCounterWidget: GlanceAppWidget() {
    private val CounterIndex = stringPreferencesKey("counter")

    suspend fun updateCounter(context: Context) {
        val store = AppWidgetDataStore.getInstance(context)
        store.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                val currentCounter = preferences[CounterIndex] ?.toInt()?: 0
                set(CounterIndex, (currentCounter.toInt() + 1).toString());
            }
        }
    }

    @OptIn(ExperimentalGlanceApi::class)
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val store = AppWidgetDataStore.getInstance(context)
        val initial = store.data.first()
        provideContent {
            val data by store.data.collectAsState(initial)
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

object AppWidgetDataStore {
    private const val PREFERENCES_NAME = "ScreenUnlockCounterWidget"
    private val Context.myWidgetStore by preferencesDataStore(PREFERENCES_NAME)

    fun getInstance(context: Context): DataStore<Preferences> {
        return context.myWidgetStore
    }
}