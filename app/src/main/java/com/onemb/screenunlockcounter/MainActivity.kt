/**
 * MainActivity is the entry point of the Android application. It sets up the UI using Compose
 * and initiates the ScreenUnlockService to monitor screen unlock events.
 */
package com.onemb.screenunlockcounter

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.onemb.screenunlockcounter.ui.theme.ScreenUnlockCounterTheme
import android.Manifest

/**
 * MainActivity is the entry point of the Android application. It sets up the UI using Compose
 * and initiates the ScreenUnlockService to monitor screen unlock events.
 */
class MainActivity : ComponentActivity() {

    /**
     * Called when the activity is created. Sets up the UI using Compose and initiates
     * the ScreenUnlockService to monitor screen unlock events.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScreenUnlockCounterTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Greeting("Android")
                }
            }
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

/**
 * Greeting is a Compose function displaying a greeting text.
 */
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

/**
 * Preview function for Greeting Compose component.
 */
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ScreenUnlockCounterTheme {
        Greeting("Android")
    }
}
