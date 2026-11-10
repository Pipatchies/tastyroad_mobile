[![TastyRoad Mobile CI/CD](https://github.com/Pipatchies/tastyroad_mobile/actions/workflows/ci-cd.yml/badge.svg)](https://github.com/Pipatchies/tastyroad_mobile/actions/workflows/ci-cd.yml)
[![Kotlin Multiplatform](https://img.shields.io/badge/Kotlin-Multiplatform-7F52FF?logo=kotlin)](https://kotlinlang.org/docs/multiplatform.html)
[![Compose Multiplatform](https://img.shields.io/badge/Compose-Multiplatform-4285F4?logo=jetpackcompose)](https://www.jetbrains.com/lp/compose-multiplatform/)

# 🍕 TastyRoad — Application de Randos Gourmandes 🌳

**TastyRoad** est l'application cliente officielle connectée à **TastyRoad API**. Développée en **Kotlin Multiplatform (KMP)** et **Compose Multiplatform**, elle permet aux utilisateurs de créer, explorer et partager des itinéraires de randonnées et roadtrips gastronomiques sur toutes les plateformes depuis une seule base de code.

## 📱 Plateformes Supportées

Grâce à Compose Multiplatform, l'interface et la logique métier sont partagées à travers :
- 🟢 **Android** (Natifs via `:app:androidApp`)
- 🍎 **iOS** (Partage de framework natif)
- 💻 **Desktop / JVM** (Windows, macOS, Linux via `:app:desktopApp`)
- 🌐 **Web** (JS & WebAssembly - WasmJS via `:app:webApp`)

---

## 🛠️ Stack Technique

| Brique | Technologie |
| :--- | :--- |
| **Langage & Framework** | Kotlin / Kotlin Multiplatform (KMP) |
| **Interface Utilisateur (UI)** | Jetpack Compose / Compose Multiplatform |
| **Architecture** | **MVVM** (Model-View-ViewModel) partagé |
| **Réseau & API** | Ktor Client (`ktor-client-core`, `ktor-client-cio`) |
| **Sérialisation (JSON / GeoJSON)**| `kotlinx.serialization` |
| **Navigation** | Jetpack Navigation Compose (`androidx.navigation.compose`) |
| **Chargement d'Images** | Coil Compose (`coil-network-ktor`) |
| **Gestion d'État & Asynchrone** | Kotlin Coroutines & StateFlow |
| **Sécurité (Tokens JWT)** | EncryptedSharedPreferences (`security-crypto`) sur Android |

---

## 📂 Architecture du Projet

Le projet suit une architecture modulaire KMP stricte, séparant les points d'entrée spécifiques des plateformes et la logique partagée :

```text
tastyroad_mobile/
├── app/
│   ├── androidApp/      # Point d'entrée Android (MainActivity, Manifest)
│   ├── desktopApp/      # Point d'entrée JVM/Desktop (Main.kt)
│   ├── webApp/          # Point d'entrée Web (JS/WasmJS)
│   └── shared/          # 🧠 Cœur de l'application (Logique & UI partagée)
│       └── src/commonMain/kotlin/fr/tastyroad/
│           ├── domain/      # Modèles de données (Itinerary, FoodPlace, GeoJson...)
│           ├── network/     # Configuration Ktor Client (ApiClient, Config)
│           ├── repository/  # Abstraction de l'accès aux données (API ↔ App)
│           ├── session/     # Gestion de la session utilisateur (AuthSession)
│           └── ui/
│               ├── components/ # Composants UI réutilisables (Cartes, Boutons...)
│               ├── navigation/ # Graphe de navigation (AppNavigation)
│               ├── screens/    # Écrans principaux (Map, Home, Auth...)
│               ├── theme/      # Design System (Couleurs, Typo, Material 3)
│               └── viewmodels/ # Logique de présentation (StateFlows)
├── core/                # Utilitaires et briques transverses (modules métiers)
└── server/              # Brique serveur optionnelle (Mocks Ktor)
```

---

## 🚀 Installation & Démarrage rapide

### 1. Prérequis
- [Android Studio](https://developer.android.com/studio) (version récente supportant KMP) ou IntelliJ IDEA.
- Plugin **Kotlin Multiplatform Mobile (KMM)** installé.
- Le backend **TastyRoad API** démarré en local (ou utiliser l'URL de production).

### 2. Cloner le dépôt
```bash
git clone https://github.com/Pipatchies/tastyroad_mobile.git
cd tastyroad_mobile
```

### 3. Configurer l'environnement
Dans le module `shared/src/commonMain/kotlin/fr/tastyroad/network/Config.kt`, assure-toi que l'URL pointe vers l'API de développement ou de production :

```kotlin
object Config {
    // Pour le développement local (Emulateur Android)
    const val BASE_URL = "http://10.0.2.2:8000/api/" 
    // Pour la production
    // const val BASE_URL = "https://tastyroad-api.onrender.com/api/"
}
```

### 4. Lancer l'application
- **Android** : Sélectionne la configuration d'exécution `androidApp` dans Android Studio et clique sur "Run" (▶️).
- **Desktop (JVM)** : Exécute la tâche Gradle `./gradlew :app:desktopApp:run`.
- **Web (Wasm)** : Exécute `./gradlew :app:webApp:wasmJsBrowserRun` pour lancer dans le navigateur.
- **iOS** : Ouvre le projet avec Xcode ou lance la configuration iOS via Android Studio / Fleet (nécessite un Mac).

---

## 🗺️ Fonctionnalités Principales (Écrans)

| Fonctionnalité | Composant/ViewModel | Description |
| :--- | :--- | :--- |
| **Authentification** | `AuthScreen` / `AuthViewModel` | Connexion, Inscription et gestion des tokens JWT (`AuthSession`). |
| **Exploration (Home)** | `HomeScreen` / `HomeViewModel` | Découverte des itinéraires tendances et lieux gastronomiques récents. |
| **Carte Interactive** | `MapScreen` / `MapViewModel` | Visualisation GeoJSON des tracés GPS (`LineString`) et des étapes (`Point`). |
| **Création d'Itinéraire** | `CreateItineraryScreen` | Formulaire de création de roadtrips, ajout d'étapes gustatives et tracé. |
| **Espace Personnel** | `ProfileScreen` | Gestion du profil, accès aux favoris, historiques et paramètres. |

---

## 📡 Intégration avec l'API (Ktor)

L'application communique avec le backend via **Ktor Client**. L'ensemble des appels est centralisé dans les `Repository` (ex: `ItineraryRepository`, `FoodPlaceRepository`), qui exposent des données typées depuis le JSON de l'API (incluant le standard **GeoJSON** pour les coordonnées géospatiales).

Les requêtes protégées intègrent automatiquement le token JWT d'accès (`Bearer`) et gèrent son rafraîchissement transparent grâce aux intercepteurs Ktor (`AuthPlugin`).

---

## 🧪 Tests & Qualité de Code

- **Tests Multiplateformes** : Les tests de la logique partagée (ViewModels, Repositories, Parsing) sont situés dans `commonTest` et s'appuient sur `kotlin.test` et `kotlinx-coroutines-test`.
- **Mock Réseau** : Utilisation de `ktor-client-mock` pour tester les flux de données sans dépendre de l'API backend.

Pour lancer les tests de la logique partagée :
```bash
./gradlew :app:shared:allTests
```

---

## 🔒 Sécurité & RGPD (Client-side)

- **Stockage Sécurisé** : Sur Android, les paires de clés JWT (Access / Refresh) sont sauvegardées dans un `EncryptedSharedPreferences` via la librairie `androidx.security:security-crypto`, empêchant le vol de token sur les appareils non rootés (`TokenStorage`).
- **Déconnexion & Oubli** : Conformément à l'API, l'action de suppression de compte depuis `ProfileScreen` nettoie toutes les données locales de l'application et révoque l'accès.