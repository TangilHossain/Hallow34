# Hallow34

A modern Android application built with Kotlin, Jetpack Compose, and Firebase for user management and community features.

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose, Material 3
- **Architecture:** MVVM, Clean Architecture
- **DI:** Hilt
- **Backend:** Firebase (Auth, Firestore, Cloud Messaging, Crashlytics)
- **Image Hosting:** Cloudinary
- **Navigation:** Jetpack Navigation Compose

## Features

- Firebase Authentication with BP Number login
- User profile management with image upload
- Push notifications via FCM
- Admin panel for user management and banners
- Home district filtering
- Pull-to-refresh UI
- Hamburger menu navigation

## Project Structure

```
app/src/main/java/com/shawonshagor0/hallow34/
├── data/           # Data layer (repositories, models, mappers)
├── di/             # Hilt dependency injection modules
├── domain/         # Domain layer (use cases, entities)
├── presentation/   # UI layer (screens, viewmodels)
└── ui/             # Theme and composables
```

## Development Setup

### Prerequisites

- Android Studio Ladybug or later
- JDK 11+
- Firebase project configured

### Configuration

1. **Clone the repository**
   ```bash
   git clone https://github.com/TangilHossain/Hallow34.git
   cd Hallow34
   ```

2. **Firebase Setup**
   - Create a Firebase project at [Firebase Console](https://console.firebase.google.com)
   - Download `google-services.json` and place it in `app/`
   - Enable Authentication, Firestore, and Cloud Messaging

3. **Cloudinary Setup**
   - Create account at [Cloudinary](https://cloudinary.com)
   - Copy `cloudinary.properties.example` to `cloudinary.properties`
   - Fill in your credentials:
     ```properties
     CLOUDINARY_CLOUD_NAME=your_cloud_name
     CLOUDINARY_API_KEY=your_api_key
     CLOUDINARY_API_SECRET=your_api_secret
     CLOUDINARY_UPLOAD_PRESET=your_upload_preset
     ```

4. **Build and Run**
   ```bash
   ./gradlew assembleDebug
   ```

### Firebase Functions (Optional)

```bash
cd functions
npm install
firebase deploy --only functions
```

## Security Notes

- Never commit `google-services.json` or `cloudinary.properties`
- Use `.example` files as templates
- API keys are loaded via BuildConfig at compile time

## Admin Access

BP Numbers `8414116694` and `8000000000` have admin privileges.

## License

Private repository - All rights reserved.
