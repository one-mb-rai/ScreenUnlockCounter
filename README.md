# ScreenUnlockCounter
 Simple counter widget for counting number of times screen is unlocked by user

# Screen Unlock Counter App

## Overview

The Screen Unlock Counter App is an Android application that tracks and displays the number of screen unlock events. It includes a widget for the home screen, a foreground service for monitoring screen unlocks, and a simple UI built using Jetpack Compose.

## Features

- Screen unlock counter widget.
- Foreground service to monitor screen unlock events.
- Jetpack Compose-based UI for the main activity.

## Prerequisites

- Android Studio (latest version recommended).
- Android device or emulator with Android API level 21 and above.

## Getting Started

1. Clone the repository to your local machine.

    ```bash
    git clone https://github.com/one-mb-rai/ScreenUnlockCounter.git
    ```

2. Open the project in Android Studio.

3. Build and run the application on your device or emulator.

## Permissions

The app requires the `POST_NOTIFICATIONS` permission to run the foreground service and display notifications.

## Usage

1. Launch the app on your device.
2. Grant the necessary notification permission if prompted.
3. The screen unlock counter will be displayed on the widget and in the app's notification.

## Code Structure

The repository is organized into three main components:

- **`app`**: Contains the main Android application code.
- **`appwidget`**: Includes code related to the GlanceAppWidget and its functionality.
- **`service`**: Contains the ScreenUnlockService for monitoring screen unlock events.

## Contributing

Contributions are welcome! Feel free to open issues, create pull requests, or suggest improvements.


# Look at the counter increasing on home screen on each unlock
https://github.com/one-mb-rai/ScreenUnlockCounter/assets/16004196/ad27eb7c-bf8d-43bc-a30e-fcb6a454e6de

