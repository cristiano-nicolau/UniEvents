# UniEvents

UniEvents is a mobile application designed to streamline the event attendance experience for university students and event coordinators. Users can browse upcoming lectures and seminars, access event details, and register for events. The app features integrated mapping for easy navigation and digital ticketing for efficient check-ins.

## Features

- **Centralized Event Management**: Access a comprehensive list of academic events.
- **Detailed Event Information**: View event details including attendees, capacity, and venue location.
- **Seamless Registration**: Register for events with a few clicks.
- **Integrated Navigation**: Get directions to event venues.
- **Digital Ticketing**: Present QR codes for quick check-ins.

## Target Users

1. **University Students**
   - Stay informed about relevant academic events.
   - Easy registration and navigation to events.
   - Manage event registrations and check-ins.

2. **Event Coordinators**
   - Create and manage event listings.
   - Monitor registrations and manage event capacity.
   - Facilitate smooth and efficient check-ins.

## Technical Overview

- **Tech Stack**: Kotlin, Firebase Firestore, Firebase Auth, Google Maps API, ZXing for QR code scanning.
- **Architecture**: MVVM architecture using Jetpack Compose for UI, ViewModel, and Repository pattern.
- **Firebase Integration**: Real-time database synchronization and user authentication.
- **Device Sensors**: Access location and compass sensors for real-time navigation.

## Test Accounts

- **Student Account**
  - Email: test@ua.pt
  - Password: password

- **Event Coordinator Account**
  - Email: antonio@example.com
  - Password: password

## Installation

1. Clone the repository:
   ```sh
   git clone https://github.com/yourusername/UniEvents.git ```

2. Open the project in Android Studio.
3. Build and run the project on an emulator or physical device.

## Contributors

- Miguel Aido Miragaia - 108317
- Cristiano Antunes Nicolau - 108536
