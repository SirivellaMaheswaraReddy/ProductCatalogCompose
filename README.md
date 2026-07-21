# BrowserStack Product Catalog (Jetpack Compose)

A complete, feature-rich Android application built with **Jetpack Compose** and **Material 3**, following the **MVVM** architecture and **Clean Architecture** principles. This project replicates a professional e-commerce experience with persistent data storage and a responsive UI.

## 🚀 Features

- **Product Catalog**: Responsive grid layout (2 columns portrait, 3 columns landscape) with dynamic vendor filtering (Apple, Samsung, Google, OnePlus).
- **Product Details**: Interactive popup for each product showing full details, description, and quick actions.
- **Persistent Favorites**: Save your favorite items! Favorites are stored persistently using **Jetpack DataStore**, ensuring they remain even after the app is closed.
- **Orders History**: A dedicated screen to view all previously placed orders, also saved persistently.
- **Shopping Cart System**:
    - Add/Remove items with real-time total amount calculation.
    - Persistent Cart Badge count in the header.
    - **Cart Bottom Sheet**: View and manage your bag easily from any screen.
- **Secure Checkout**: Simple checkout flow with shipping information validation.
- **Order Success**: A personalized congratulatory screen after every successful purchase.
- **Authentication**: Fully functional Sign-In screen with input validation and persistent login status.
- **Dark/Light Mode**: User-controlled theme toggle that persists across sessions.

## 🛠 Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Architecture**: MVVM (Model-View-ViewModel)
- **Navigation**: Compose Navigation with backstack management.
- **Storage**: Jetpack DataStore (Preferences) for lightweight persistence.
- **State Management**: StateFlow & Compose State (collectAsStateWithLifecycle).

## 📂 Project Highlights

### Architecture
- **UI Layer**: Composable functions organized by screen (`ProductsScreen`, `FavoritesScreen`, `OrdersScreen`, `CheckoutScreen`, `SignInScreen`).
- **ViewModel Layer**: `ProductsViewModel` handles business logic for catalog, cart, and favorites. `LoginViewModel` manages authentication.
- **Data Layer**: `ProductRepository` provides product data. `UserPreferences` manages persistence via DataStore.

### Key Implementation Details
- **Persistence**: Used `stringSetPreferencesKey` in DataStore to store collections of Product IDs for Favorites and Orders.
- **Navigation**: Implemented robust backstack handling to prevent blank screens and ensure a smooth "Return to Home" experience when clicking the logo.
- **Theming**: Integrated `isSystemInDarkTheme()` with a user override, allowing the app to respect system settings while giving users manual control.

## 🛠 Installation & Setup

1. Open the project in **Android Studio (Hedgehog or newer)**.
2. Build the project to sync Gradle dependencies.
3. Run the app on an emulator or a physical device (API 24+ recommended).

## 📸 Functionality
- **Sign In**: Use any non-empty email and password to log in.
- **Favorites**: Click the heart icon on any card or in the detail popup. Access your favorites via the heart icon in the TopBar.
- **Orders**: Successfully complete a checkout to see your items appearing in the "Orders" section.

---
Developed as a demonstration of modern Android development practices.
