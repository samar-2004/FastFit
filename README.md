# FASTFit

FASTFit is a production-ready Android fitness app and smart gym companion. It lets
members create an account, browse machine-based workouts, watch tutorial videos
inside the app, book training slots, plan their nutrition, and track their progress
over time. Everything is backed by Firebase, so user accounts, bookings, and
progress are stored in the cloud and stay in sync.

The app is written in native Android using Java and the Android View system with
Material 3 components. The brand theme is red (#dc000a) with black and white,
applied consistently across every screen, in both light and dark mode.

## What this project is about

A gym member opens FASTFit and is greeted by a splash screen, a one-time
onboarding walkthrough, and then a sign-in screen. After signing in they land on a
home dashboard with a bottom navigation bar. From there they can:

1. See a personal dashboard with a greeting, live stats (total workouts, total
   minutes, day streak), quick actions, featured workouts, and upcoming bookings.
2. Browse a searchable catalog of workouts loaded from the cloud.
3. Open any workout to read its details and watch a tutorial video that plays
   directly inside the app.
4. Book a training slot (morning, afternoon, or evening), add nutrition items to a
   cart, review a summary, and confirm. The confirmed booking is saved to their
   account.
5. Track progress with a weekly activity chart, log workouts and body weight, and
   review a feed of recent activity.
6. Manage their profile, upload an avatar, switch dark mode on or off, control
   push notifications, and sign out.

The goal of the project is to show a complete, real-world app flow: authentication,
cloud data, media, scheduling, and personalization, all in one coherent product
with a single consistent design.

## Features

Accounts and onboarding
- Splash screen, then a one-time onboarding walkthrough
- Email and password sign-up and sign-in
- Google Sign-In
- Password reset by email
- Persistent login session and sign out

Home (bottom navigation)
- Home dashboard: greeting, live stats, quick actions, featured workouts carousel,
  and upcoming bookings
- Workouts: cloud-backed catalog with live search and pull-to-refresh
- Progress: weekly activity bar chart, log workout, log weight, recent activity feed
- Profile: avatar, name, email, goal, and a settings menu

Workout and booking flow
- Exercise detail screen with an embedded in-app YouTube tutorial player, exercise
  stats (sets, reps, minutes, calories), and a description
- Slot booking for morning, afternoon, or evening
- Nutrition cart with quantity controls and a running total
- Booking summary that saves the booking to Firestore, logs a progress entry, and
  updates the user's lifetime stats
- Share the plan through the system share sheet or send it by SMS
- Booking history list

Profile and settings
- Edit profile, including avatar upload to cloud storage
- Dark mode toggle with real dark colors
- Push notification toggle
- Change password (sends a reset link)
- About dialog and app version
- Sign out

Notifications
- Firebase Cloud Messaging with a dedicated notification channel
- Tappable notifications that open the app

## Tech stack

- Java, Android View system, Material 3
- Firebase Authentication (email/password and Google)
- Cloud Firestore (profiles, bookings, progress, workout catalog)
- Firebase Storage (profile photos)
- Firebase Cloud Messaging (push notifications)
- Firebase Analytics
- Google Play Services Auth (Google Sign-In)
- android-youtube-player (in-app YouTube playback)
- Glide (image loading)
- MPAndroidChart (progress chart)
- CircleImageView (avatar)
- RecyclerView, SwipeRefreshLayout, Fragment, Lifecycle

## Architecture

All backend access goes through a single repository facade with a simple callback
interface, which keeps activities and fragments thin and easy to follow.

```
com.example.fastfit
  FastFitApp                 Application: applies saved theme, creates notif channel
  SplashScreen               routes to onboarding, login, or home based on state
  OnboardingActivity         one-time intro
  HomeActivity               bottom navigation shell hosting four fragments
  ExerciseDetailActivity     embedded YouTube tutorial and booking entry point
  SlotBookingActivity        choose a training slot
  NutritionActivity          nutrition cart
  WorkoutSummaryActivity     summary, persist booking, share, SMS
  auth/                      LoginActivity, SignupActivity, ForgotPasswordActivity
  ui/                        HomeFragment, WorkoutsFragment, ProgressFragment,
                             ProfileFragment, EditProfileActivity, SettingsActivity,
                             BookingHistoryActivity
  adapter/                   WorkoutAdapter, FeaturedWorkoutAdapter, BookingAdapter,
                             ProgressAdapter
  model/                     User, Workout, Booking, NutritionItem, ProgressEntry
  data/                      Repo (Auth, Firestore, Storage facade), Prefs, Callback
  fcm/                       FastFitMessagingService
  util/                      Validate, PasswordToggle
```

## Data model (Cloud Firestore)

Collections are created automatically as the app is used.

```
users/{uid}                  profile, lifetime stats, goal, fcmToken
users/{uid}/bookings/{id}    booked sessions (workout, slots, nutrition total, status)
users/{uid}/progress/{id}    logged workouts and weight entries
workouts/{id}                workout catalog, auto-seeded on first launch
```

## Project layout

```
FastFit/
  app/
    google-services.json     Firebase config (project fastfit-5eda9)
    src/main/java/...         app source (see architecture above)
    src/main/res/             layouts, drawables, colors, styles, menus
  firestore.rules            Firestore security rules
  storage.rules              Storage security rules
  FIREBASE_SETUP.md          step-by-step backend setup
  README.md                  this file
```

## Build and run

The project uses Gradle 9 and Android Gradle Plugin 9, which require JDK 17 or
newer. If your default Java is older, point the build at a newer JDK first.

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
./gradlew :app:assembleDebug
```

The debug APK is produced at:

```
app/build/outputs/apk/debug/app-debug.apk
```

You can also open the project in Android Studio and press Run.

## Backend setup

The bundled `app/google-services.json` already points at the Firebase project
`fastfit-5eda9`, so the app builds and connects out of the box. To enable every
feature end to end (or to point the app at your own Firebase project), follow
`FIREBASE_SETUP.md`. In short:

1. Enable Email/Password and Google sign-in methods in Firebase Authentication,
   and register your debug SHA-1 fingerprint for Google Sign-In.
2. Create a Cloud Firestore database and publish `firestore.rules`.
3. Enable Storage and publish `storage.rules`.
4. Cloud Messaging needs no extra setup; send a test message from the console.

## Security

Firestore and Storage rules in this repo restrict each user to reading and writing
only their own data. The workout catalog is readable by any signed-in user.

## License

Copyright 2026 FASTFit. For educational and demonstration use.
