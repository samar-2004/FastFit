# Firebase Setup — FASTFit

Your `app/google-services.json` is already wired to the Firebase project
**`fastfit-5eda9`**, so the app builds and connects out of the box. To make every
feature work end-to-end, finish enabling the backend services below in the
[Firebase console](https://console.firebase.google.com/).

---

## 1. Authentication
**Build → Authentication → Get started → Sign-in method**

- Enable **Email/Password**.
- Enable **Google**. Set a support email when prompted.
  - Google Sign-In uses the Web client ID that ships in `google-services.json`
    (`default_web_client_id`), which the app reads automatically — no code change needed.
  - For Google Sign-In on a device you must register your app's **SHA-1**
    (and SHA-256) fingerprint:
    **Project settings → Your apps → Android app → Add fingerprint**.
    Get the debug SHA-1 with:
    ```bash
    ./gradlew signingReport
    ```
    After adding the fingerprint, **re-download `google-services.json`** and replace
    the one in `app/`.

## 2. Cloud Firestore
**Build → Firestore Database → Create database** (Production mode, nearest region).

Then publish the security rules from this repo (`firestore.rules`):
```bash
# Option A — Firebase CLI
firebase deploy --only firestore:rules

# Option B — paste firestore.rules into Console → Firestore → Rules → Publish
```

Collections are created automatically by the app:
```
users/{uid}                      profile, stats, fcmToken
users/{uid}/bookings/{id}        booked sessions
users/{uid}/progress/{id}        logged workouts & weight
workouts/{id}                    catalog (auto-seeded on first launch)
```

## 3. Storage  (profile photos)
**Build → Storage → Get started**, then publish `storage.rules`:
```bash
firebase deploy --only storage
```

## 4. Cloud Messaging  (push notifications)
No extra setup needed — the app registers `FastFitMessagingService` and a
notification channel automatically. Send a test push from
**Engage → Messaging → New campaign → Notifications**.
Users can toggle the `promotions` topic from in-app **Settings**.

---

## Deploy all rules at once
```bash
npm install -g firebase-tools
firebase login
firebase use fastfit-5eda9
firebase deploy --only firestore:rules,storage
```

> Replacing `google-services.json` with a different Firebase project is the only
> change required to point the app at your own backend.
