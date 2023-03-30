# Urban Company Clone

This is a clone of the Urban Company app, which includes similar features like a booking system with date and time selection UI's using Recycler Views, location selection using both Google Maps SDK and Places API, payments using RazorPay API (in test mode), and cloud messaging using Firebase for implementing notifications in Android.

## Features

- **Booking System**: Allows customers to book services through the app with date and time selection UI's using Recycler Views.
- **Location Selection**: Customers can select their location using both Google Maps SDK and Places API, which provides a search filter where they can search and select places from the dropdown.
- **Payments**: The app supports payment using RazorPay API in test mode.
- **Notifications**: The app uses Firebase Cloud Messaging for implementing notifications in Android.

## Technical Details

The app is built using Android Studio and written in Java. Google Maps SDK and Google Places API are integrated into the application to provide location selection for the customers. RazorPay API is used for payment processing. Firebase Realtime Database is used for the backend, which stores information about services, customers, and payments. RESTful APIs are used to interact with the database and retrieve data.

The app is designed using the Model-View-ViewModel (MVVM) architecture, which separates the user interface logic from the business logic. The UI components are designed using XML and the layout is managed by the ConstraintLayout.

The app uses Material Design components to provide a modern look and feel. The app also supports multiple languages with the use of localization.

## Getting Started

To run the app, you'll need to clone this repository and open the project in Android Studio. You'll also need to set up Google Maps and Places API keys, RazorPay API keys, and a Firebase account and add the respective configuration files to the app. Once you've set up everything, you can build and run the app on your Android device or emulator.

## Contributing

Contributions to this project are welcome. If you find a bug or have a feature request, please open an issue on the Github repository. If you would like to contribute code, please fork the repository and create a pull request.

## Developer 

- [Jay Birthariya](https://github.com/JayBirthariya581)
