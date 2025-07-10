# Coffee Shop Android Application

A modern, fully-featured Android e-commerce app for ordering coffee — integrated with Firebase, built using clean MVVM architecture, and supporting multi-language experience.

##  Features

- **Authentication**: Firebase Auth for login/registration
- **Cart**: Add, update, and delete items with real-time LiveData updates
- **Order Placement**: Full checkout flow with address, confirmation, and Firebase order storage
- **Favorites**: Save and sync favorite coffees
- **Search**: Filter by category and name
- **Localization**: Supports English, Ukrainian, and Polish with dynamic switching
- **Profile**: Edit name, photo, and password with Cloudinary upload
- **Firebase Realtime Database** for live data sync
- **Material Design** with smooth animations and ViewBinding

## Tech Stack

- **Language**: Java
- **Architecture**: MVVM + Repository
- **Backend**: Firebase Auth + Realtime Database
- **Image Hosting**: Cloudinary
- **UI**: Material Components, ViewBinding, Glide
- **Local Storage**: Room
- **Reactive**: LiveData, ViewModel, DataBinding

## Project Structure

```
com.example.coffee_shop/
├── presentation/         # UI layer (fragments, activities)
│   ├── auth/             # Auth screens (login, register)
│   ├── profile/          # Profile view & edit
│   ├── cart/             # Cart & checkout logic
│   ├── search/           # Search & filters
│   ├── main/             # Main activity & navigation
│   └── common/           # Shared components (modals, buttons)
├── data/                 # Data layer
│   ├── models/           # Model classes (Coffee, CartItem, Order, etc.)
│   ├── repository/       # Data access abstraction
│   ├── remote/           # Firebase interactions
│   └── database/         # Room setup
├── core/                 # Utils & managers (localization, image upload)
└── MyApp.java            # Application-level ViewModelStore
```

## Setup Instructions

### Requirements

- Android Studio Arctic Fox or newer
- Java 8+
- Android SDK 24+

### Firebase Configuration

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a new project and enable:
   - Firebase Authentication
   - Firebase Realtime Database
3. Download `google-services.json` and place it inside the `app/` directory

### Cloudinary Configuration

1. Create an account at [cloudinary.com](https://cloudinary.com)
2. Add your API credentials in `CloudinaryManager.java`

### Build and Run

```bash
./gradlew assembleDebug
```

Or simply click "Run" in Android Studio.

## Highlights

- Clean MVVM architecture with proper separation of concerns
- Real-time sync between cart/favorites and Firebase
- Application-wide ViewModelStore for shared state
- Localization and multi-language switching without restart
- Smooth animations and responsive UI with ViewBinding

## Release Build

```bash
./gradlew assembleRelease
```

## Author

**Maryna**  
[GitHub](https://github.com/MarynaRina) • [LinkedIn](www.linkedin.com/in/maryna-maksymchuk-637082287)


