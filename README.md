# BrowserStack Product Catalog (Jetpack Compose)

A professional Android application built with **Jetpack Compose** and **Material 3**, adhering to **MVVM with Clean Architecture** patterns. This project demonstrates a robust e-commerce experience with a strong focus on modularity, testability, and a responsive user interface.

## 🚀 Key Features

- **MVVM + Clean Architecture**: Strict separation of concerns across Data, Domain, and UI layers.
- **Navigation Drawer**: Modern hamburger menu for easy navigation to user-specific features.
- **Dynamic Catalog**: Responsive grid layout with real-time vendor filtering and a 2-second "simulated refresh" loader.
- **Persistence Layer**: Favorites and order history are securely managed using **Jetpack DataStore**.
- **Interactive Details**: Immersive product detail popups with quick-action buttons.
- **Seamless Checkout**: Multi-step checkout flow with optimized keyboard interactions.
- **User Personas**: Specialized behaviors for different login credentials (e.g., `image_not_loader_user` for text-only mode).

## 🛠 Tech Stack

- **Language**: Kotlin 2.0
- **UI Framework**: Jetpack Compose (Material 3)
- **Asynchronous**: Kotlin Coroutines & StateFlow
- **Storage**: Jetpack DataStore (Preferences)
- **Image Handling**: Painter Resource management with brand-consistent containers.

## 📂 Architecture Breakdown

### 1. Domain Layer (`domain/`)
The core of the application, containing business logic and high-level abstractions.
- **Models**: Business entities (`Product`, `Vendor`).
- **Repositories**: Interfaces defining data contracts.
- **Use Cases**: Individual classes for specific actions (`GetProductsUseCase`, `ToggleFavoriteUseCase`, `SignInUseCase`, etc.).

### 2. Data Layer (`data/`)
Handles data retrieval and persistence.
- **Repositories Implementation**: Concrete implementations of domain interfaces.
- **Storage**: `UserPreferences` using DataStore for managing settings and user data.
- **DataSource**: Static repository for product catalog information.

### 3. UI Layer (`ui/`)
Manages the visual representation and user interaction.
- **ViewModels**: Observe Use Cases and expose state to the UI via `StateFlow`.
- **Composables**: Screens and components (e.g., `ProductsScreen`, `SignInScreen`, `CartBottomSheet`).
- **Theme**: Material 3 styling with adaptive Light/Dark mode support.

## 🛠 Installation & Setup

1. Open in **Android Studio (Koala or newer)**.
2. Sync Project with Gradle Files.
3. Run on a device or emulator (API 24+).

---
Developed as a showcase of modern Android development standards.
