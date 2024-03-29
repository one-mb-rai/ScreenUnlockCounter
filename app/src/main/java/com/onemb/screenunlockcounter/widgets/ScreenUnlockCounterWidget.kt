/**
 * ScreenUnlockCounterWidget is a GlanceAppWidget responsible for displaying a screen unlock counter.
 * It uses a DataStore for persisting counter data and updates the counter on screen unlock events.
 */
package com.onemb.screenunlockcounter.widgets

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.onemb.screenunlockcounter.DB.AppDatabase
import com.onemb.screenunlockcounter.DB.ScreenUnlockCounter
import com.onemb.screenunlockcounter.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * ScreenUnlockCounterWidget is a GlanceAppWidget that displays the screen unlock counter.
 * It updates the counter on screen unlock events and provides a Glance theme for widget appearance.
 */
@Suppress("PrivatePropertyName", "SpellCheckingInspection")
class ScreenUnlockCounterWidget: GlanceAppWidget() {

    private val CounterIndex = stringPreferencesKey("counter")

    /**
     * Updates the screen unlock counter in the DataStore.
     */
    suspend fun incrementCounter(context: Context) {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val database = AppDatabase.getInstance(context)
        val counterDao = database.screenUnlockCounterDao()
        val store = AppWidgetDataStore.getInstance(context)
        suspend fun updatelocalStore(value: String) {
            store.updateData { preferences ->
                preferences.toMutablePreferences().apply {
                    set(CounterIndex, value)
                }
            }
        }
        withContext(Dispatchers.IO) {
            val existingCounter = counterDao.getCounterByDate(currentDate)

            if (existingCounter == null) {
                val newCounter = ScreenUnlockCounter(date = currentDate, counter = 1)
                counterDao.insert(newCounter)
                updatelocalStore(newCounter.counter.toString())
            } else {
                val updatedCounterValue = existingCounter.counter + 1
                counterDao.updateCounter(currentDate, updatedCounterValue)
                updatelocalStore(updatedCounterValue.toString())
            }

        }
    }

    /**
     * Provides the Glance content for the widget.
     * Displays the screen unlock counter using GlanceTheme and Compose components.
     */
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val store = AppWidgetDataStore.getInstance(context)
        val initial = store.data.first()
        val calendarIndex = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        val weekArray = arrayOf("SA", "SU", "M", "TU", "W", "TH", "F")
        val dayOfWeek = weekArray[calendarIndex]

        provideContent {
            GlanceTheme {
                val data by store.data.collectAsState(initial)
                Column(
                    modifier = GlanceModifier.fillMaxSize().background(Color.White).clickable {
                        val intent = Intent(context, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        context.startActivity(intent)},
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(modifier = GlanceModifier.padding(all = 16.dp).fillMaxSize()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = GlanceModifier.fillMaxWidth()
                        ) {
                            val modifier = GlanceModifier.defaultWeight()
                            for (item in weekArray) {
                                if(item == dayOfWeek) {
                                    val modifierActive = GlanceModifier.
                                    defaultWeight()
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = modifierActive,
                                    ) {
                                        Text(
                                            text = item,
                                            style = TextStyle(
                                                color = ColorProvider(Color.Red)
                                            )
                                        )
                                    }
                                } else {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = modifier
                                    ) {
                                        Text(text = item)
                                    }
                                }
                            }
                        }
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalAlignment = Alignment.Start,
                            modifier = GlanceModifier.fillMaxSize().padding(start = 10.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    text = "Screen Unlocks: ",
                                    style = TextStyle(
                                        color = ColorProvider(Color.Black),
                                        fontSize = TextUnit(24F, TextUnitType.Sp)
                                    )
                                )
                            }
                            Box(
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Text(
                                    text = "${data[CounterIndex] ?: 0}",
                                    style = TextStyle(
                                        color = ColorProvider(Color.Red),
                                        fontSize = TextUnit(24F, TextUnitType.Sp)
                                    )
                                )
                            }
                        }

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
