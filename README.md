# BrowserStack Product Catalog (Jetpack Compose)

A complete, feature-rich Android application built with **Jetpack Compose** and **Material 3**, following the **MVVM** architecture and **Clean Architecture** principles. This project replicates a professional e-commerce experience with persistent data storage, responsive UI, and optimized user flows.

## 🚀 Features

- **Modern UI/UX**:
    - **Hamburger Menu (Navigation Drawer)**: A clean side drawer for easy access to Offers, Orders, and Authentication.
    - **Responsive Grid**: Adaptive layout (2 columns portrait, 3 columns landscape) with dynamic vendor filtering (Apple, Samsung, Google, OnePlus).
    - **Edge-to-Edge Design**: Optimized status bar and system bar handling for a truly immersive experience.
- **Product Management**:
    - **Interactive Catalog**: Full-screen grid view with high-quality product imagery.
    - **Product Details**: Immersive popup with detailed descriptions and direct "Add to Cart/Favorite" actions.
    - **Vendor Filtering**: Instant catalog updates based on selected brand.
- **Persistence & Personalization**:
    - **Favorites System**: Save items to a persistent favorites list using **Jetpack DataStore**.
    - **Order History**: Persistent tracking of all successful purchases.
    - **Theme Support**: Integrated Dark and Light modes that persist across app sessions.
- **Shopping Cart & Checkout**:
    - **Real-time Cart**: Persistent cart with live badge count and total amount calculation.
    - **Cart Bottom Sheet**: Quick-access summary for managing your bag from any screen.
    - **Optimized Checkout**: Multi-field form with smart keyboard actions (ImeAction.Next/Done) for seamless input.
- **Authentication**: 
    - Dedicated Sign-In screen with credential-specific instructions.
    - **Secure Sign-Out**: Confirmation dialog that clears all user-specific data (cart, favorites, orders) for security.

## 🛠 Tech Stack

- **Language**: Kotlin 2.0.21
- **UI Framework**: Jetpack Compose (Material 3)
- **Architecture**: MVVM (Model-View-ViewModel)
- **Persistence**: Jetpack DataStore (Preferences)
- **Navigation**: Jetpack Compose Navigation
- **Asynchronous Flow**: Kotlin Coroutines & Flow
- **Image Loading**: Painter Resource-based dynamic loading

## 📂 Project Structure

- **`ui/`**: Contains all Composable screens and theme definitions.
    - **`ProductsScreen.kt`**: The heart of the app, featuring the navigation drawer and product grid.
    - **`SignInScreen.kt`**: Handles user authentication with optimized keyboard interactions.
    - **`CheckoutScreen.kt`**: A smooth checkout flow with form validation.
- **`model/`**: Defines the data structures for `Product`, `Vendor`, and `UiState`.
- **`data/`**: Handles data sourcing and persistence.
    - **`ProductRepository.kt`**: Static data source for the product catalog.
    - **`UserPreferences.kt`**: Manages DataStore read/write operations for persistent settings.

## 🛠 Installation & Setup

1. Clone the repository.
2. Open the project in **Android Studio (Koala or newer)**.
3. Allow Gradle to sync and download dependencies.
4. Run the app on an emulator or a physical device (API 24+).

## 📸 Usage Guidelines

- **Authentication**: Use the provided credentials on the Sign-In screen:
    - `browserstack@mailinator.com` / `123456`
- **Filtering**: Use the "Vendors" horizontal list at the top of the products grid to filter by brand.
- **Navigation**: Use the Hamburger menu (≡) at the top-left for Orders, Offers, and Theme switching.
- **Checkout**: Add items to your bag, open the Cart Bottom Sheet, and click "Checkout" to complete your purchase.

---
Developed as a demonstration of modern Android development using Jetpack Compose.
