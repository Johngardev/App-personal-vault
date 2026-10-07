# Personal Vault

Personal Vault is a Kotlin Multiplatform app for securely storing personal secrets, notes, and credentials on Android and iOS. The project is built with Compose Multiplatform and uses shared business logic, local persistence, and a simple vault-style workflow.

## Overview

This repository contains a multiplatform mobile application that allows users to:

- unlock the vault with a master password
- create and save secrets
- view stored items in a dashboard
- edit or delete saved entries
- store encrypted data locally
- run on both Android and iOS targets

The app is structured as a Kotlin Multiplatform project with a shared Compose UI layer and platform-specific app entry points.

## Features

- Android + iOS support via Kotlin Multiplatform
- Shared UI and app logic in the `composeApp` module
- Local database storage with Room
- Secret encryption flow using a vault abstraction
- Dashboard for listing saved secrets
- Create, update, and delete secret actions
- Biometric-ready authentication flow
- Material 3-based user interface

## Tech Stack

- Kotlin Multiplatform
- JetBrains Compose Multiplatform
- Android Jetpack
- Room database
- Kotlin Coroutines
- Multiplatform Settings
- iOS app wrapper via SwiftUI

## Project Structure

```text
App-personal-vault/
├── composeApp/
│   ├── src/
│   │   ├── androidMain/
│   │   ├── commonMain/
│   │   │   ├── kotlin/
│   │   │   │   └── com/johngardev/personalvault/
│   │   │   │       ├── auth/
│   │   │   │       ├── database/
│   │   │   │       ├── security/
│   │   │   │       ├── ui/
│   │   │   │       └── utilities/
│   │   ├── commonTest/
│   │   └── iosMain/
│   └── build.gradle.kts
├── iosApp/
│   └── iosApp/
├── build.gradle.kts
├── gradle.properties
├── gradlew
├── gradlew.bat
├── settings.gradle.kts
├── gradle/
└── README.md
