# Product Catalog - Jetpack Compose

A complete Jetpack Compose product catalog screen based on the provided BrowserStack demo UI and the supplied products JSON response.

## Included

- Kotlin + Jetpack Compose
- Material 3
- MVVM structure
- StateFlow + ViewModel
- Vendor filtering
- Product grid
- Add to cart count
- Favourite toggle
- Product response mapped into Kotlin models
- Responsive 2-column Compose grid

## Open the project

Open the `ProductCatalogCompose` folder in Android Studio.

## Product images

The supplied JSON contains SKU file names such as `iPhone12-device-info.png`. Since the actual image files were not included in the request, the project uses `ic_product_placeholder.xml` as a safe local placeholder.

To use the real product images:

1. Copy the product image files into `app/src/main/res/drawable`.
2. Add a SKU-to-drawable resource mapping in `ProductItem` or a dedicated image mapper.
3. Replace `R.drawable.ic_product_placeholder` with the mapped drawable resource.

The product data in `ProductRepository.kt` is populated from the exact JSON response provided.


# BrowserStack Product Catalog (Jetpack Compose)

A modern, responsive Product Catalog application built with **Jetpack Compose** following the **MVVM** architecture. This project replicates the BrowserStack demo UI, featuring product listing, vendor filtering, and a functional shopping cart system.

## 🚀 Features

- **Product Grid**: A responsive grid (2 columns portrait, 3 columns landscape) displaying product details.
- **Vendor Filtering**: Filter products dynamically by brand (Apple, Samsung, Google, OnePlus).
- **Shopping Cart System**:
    - Add products to cart from the main grid.
    - Real-time badge count update on the cart icon.
    - **Cart Bottom Sheet**: View added items, see total amount, and remove items.
- **Favorites**: Toggle favorite status on individual products.
- **Responsive UI**: Built entirely with Material 3 components and adaptive layouts.

## 🛠 Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Architecture**: MVVM (Model-View-ViewModel)
- **State Management**: StateFlow & Compose State (collectAsStateWithLifecycle)
- **Navigation**: Integration-ready for Sign-In and Checkout flows.

## 📂 Project Structure

The project follows a clean MVVM pattern, organizing code by responsibility:


### 💡 Key Logic Implementation
*   **Total Calculation**: Done reactively in the UI layer whenever `uiState.cartItems` changes:
    ```kotlin
    val totalAmount = uiState.cartItems.sumOf { it.price.toDoubleOrNull() ?: 0.0 }
    ```
*   **Grid Responsiveness**: Automatically switches between 2 columns (Portrait) and 3 columns (Landscape) using `LocalConfiguration`.
*   **One-Way Data Flow**: Events (Clicking Add/Remove) are sent to the ViewModel, which updates the StateFlow, triggering a UI recomposition.

## 📸 Functionality Overview

### Cart Management
The app calculates the `totalAmount` dynamically using the logic:


Users can open the **Your Bag** popup by clicking the cart icon, view their selected items, and remove them using the delete icon, which triggers a UI refresh via the ViewModel.

### Sign-In State
The "Sign In" button visibility is controlled by the `isLoggedIn` state. To ensure a fresh state during development (and avoid Android Auto Backup issues), the project is configured to clear data on new installs if specified in the Manifest.

## 🛠 Installation & Setup

1. Clone the repository.
2. Open the project in **Android Studio Hedgehog** or newer.
3. Ensure you have the `ic_iphone_img`, `ic_samsung_image`, etc., in your `res/drawable` folder (or use the provided placeholders).
4. Build and run on an emulator or physical device.

## 📝 License
This project is developed for demonstration purposes.