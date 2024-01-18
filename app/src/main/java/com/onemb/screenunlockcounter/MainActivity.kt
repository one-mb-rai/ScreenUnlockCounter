/**
 * MainActivity is the entry point of the Android application. It sets up the UI using Compose
 * and initiates the ScreenUnlockService to monitor screen unlock events.
 */
package com.onemb.screenunlockcounter

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.onemb.screenunlockcounter.DB.AppDatabase
import com.onemb.screenunlockcounter.ui.theme.ScreenUnlockCounterTheme
import com.onemb.screenunlockcounter.widgets.ScreenUnlockService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

/**
 * MainActivity is the entry point of the Android application. It sets up the UI using Compose
 * and initiates the ScreenUnlockService to monitor screen unlock events.
 */
@Suppress("DEPRECATION")
class MainActivity : ComponentActivity() {

    /**
     * Called when the activity is created. Sets up the UI using Compose and initiates
     * the ScreenUnlockService to monitor screen unlock events.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScreenUnlockCounterTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.primary) {
                    CalendarView(context = this)
                }
            }
//            CalendarView()
        }

        // Check and request notification permission
        if (checkPermission()) {
            startService(Intent(this, ScreenUnlockService::class.java))
        } else {
            requestPermission()
        }
    }

    /**
     * Checks if the app has the necessary notification permission.
     */
    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Requests notification permission from the user.
     */
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            7562
        )
    }

    /**
     * Handles the result of the permission request.
     */
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            7562 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startScreenUnlockService()
                }
                return
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    /**
     * Starts the ScreenUnlockService.
     */
    private fun startScreenUnlockService() {
        val serviceIntent = Intent(this, ScreenUnlockService::class.java)
        startService(serviceIntent)
    }
}

suspend fun updateCounterState(selectedDate: LocalDate, counterState: MutableState<Int>, context: Context) {
        withContext(Dispatchers.IO) {
            val database = AppDatabase.getInstance(context)
            val counterDao = database.screenUnlockCounterDao()
            val existingCounter = counterDao.getCounterByDate(selectedDate.toString())

            // Update the counterState with the fetched value
            // Use a state variable for counterState
            counterState.value = existingCounter?.counter ?: 0
    }
}

@SuppressLint("AutoboxingStateCreation")
@Composable
fun CalendarView(context: Context) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    val currentMonth = remember { mutableStateOf(YearMonth.now()) }

    val counterState = remember { mutableStateOf(0) }


    LaunchedEffect(selectedDate) {
        updateCounterState(selectedDate, counterState, context)
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        MonthSelector(currentMonth = currentMonth.value) {
            currentMonth.value = it
        }

        Spacer(modifier = Modifier.height(16.dp))

        CalendarGrid(
            currentMonth = currentMonth.value,
            selectedDate = selectedDate
        ) { date ->
            selectedDate = date
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            onValueChange = {
//                updateCounterState()
            },
            label = { Text("Selected Date") },
            enabled = false,
            modifier = Modifier.background(MaterialTheme.colorScheme.error)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Screen Unlocked: ${counterState.value}")    }
}

@Composable
fun MonthSelector(currentMonth: YearMonth, onMonthChange: (YearMonth) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = { onMonthChange(currentMonth.minusMonths(1)) }) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
        }

        Text(
            text = currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault()) +
                    " ${currentMonth.year}",
            style = MaterialTheme.typography.bodyMedium
        )

        IconButton(onClick = { onMonthChange(currentMonth.plusMonths(1)) }) {
            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null)
        }
    }
}

@OptIn(ExperimentalStdlibApi::class)
@Composable
fun CalendarGrid(currentMonth: YearMonth, selectedDate: LocalDate, onDateSelected: (LocalDate) -> Unit) {
    val daysOfWeek = DayOfWeek.entries.toTypedArray()
    val firstDayOfMonth = currentMonth.atDay(1)
    val startDay = currentMonth.atDay(1).minusDays(firstDayOfMonth.dayOfWeek.value.toLong())

    LazyColumn {
        items(6) { week ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                for (day in daysOfWeek) {
                    val date = startDay.plusDays((week * 7 + day.value).toLong())
                    val isSelected = date == selectedDate
                    CalendarDay(date = date, isSelected = isSelected) {
                        onDateSelected(it)
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarDay(date: LocalDate, isSelected: Boolean, onDateSelected: (LocalDate) -> Unit) {
    Box(
        modifier = Modifier.width(50.dp)
//            .fillMaxSize()
            .padding(4.dp)
            .background(if (isSelected) Color.Blue else Color.Transparent)
            .clickable { onDateSelected(date) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = if (isSelected) Color.White else Color.Black
            )
        )
    }
}
