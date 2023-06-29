# Location Reminder

## Description

This is a mobile application that helps users manage reminders based on their current location. It provides features for user authentication, registration, reminder creation, and display of reminders on a map.

## Installation

To install and run the app on your device, follow these steps:

1. Clone the repository: `git clone [repository URL]`.
2. Open the project in Android Studio.
3. Build and run the app on your preferred emulator or physical device.

## Features

### Login Screen

- Users can log in using either an email address or a Google account.
- Upon successful login, the user is navigated to the Reminders screen.
- If the user does not have an account, they are directed to the Register screen.
- ![image](https://github.com/hugo-andreassa/location-reminder/assets/50621697/48038a98-0298-4d90-a4cc-6ddb1987de4c)

### Register Screen

- Allows users to register using an email address or a Google account.

### Reminders Screen

- Displays reminders retrieved from local storage.
- If there are no reminders, a "No Data" indicator is displayed.
- In case of any errors, an error message is shown.
- ![image](https://github.com/hugo-andreassa/location-reminder/assets/50621697/73e430c1-5458-477e-b89b-aa3934c167d0)

### Map Screen

- Shows a map with the user's current location.
- Asks the user to select a point of interest to create a reminder.
![image](https://github.com/hugo-andreassa/location-reminder/assets/50621697/3bbb7bce-830a-4499-9669-da0cdebfca87)

### Add Reminder Screen

- Allows the user to add a reminder for a selected location.
- Each reminder includes a title, description, and selected location.
- ![image](https://github.com/hugo-andreassa/location-reminder/assets/50621697/3aceb1da-c267-479a-8c15-73f11e3f9780)

### Local Storage

- Reminder data is saved to local storage.

### Geofencing and Notifications

- For each reminder, a geofencing request is created in the background.
- When the user enters the geofencing area, a notification is fired.

### Testing

- Provides testing for ViewModels, Coroutines, and LiveData objects.
- Uses Espresso and Mockito to test each screen of the app.

## Dependencies

The app has the following dependencies:

- Espresso and Mockito
- Google Maps
