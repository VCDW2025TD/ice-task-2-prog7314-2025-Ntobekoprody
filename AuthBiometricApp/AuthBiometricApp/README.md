# AuthBiometricApp (Kotlin + Compose)
**Step 3: Authentication — Firebase Google SSO + BiometricPrompt**

This sample gives you:
- Google SSO using **Firebase Authentication**
- **BiometricPrompt** (fingerprint/face) toggle to protect the app after login
- Jetpack Compose UI
- Ready for your POE submission

---

## 1) Configure Firebase
1. Create a Firebase project → Add Android app with package `com.example.authbio`
2. Download **google-services.json** and place it at:
   `app/google-services.json` (replace the placeholder file)
3. In Firebase Console → Authentication → Sign-in method → **Enable Google**

## 2) Get your Web Client ID
- From the `google-services.json`, copy the **client_id** that ends with
  `.apps.googleusercontent.com` (or from Google Cloud Console OAuth credentials).
- Put it into `app/src/main/res/values/strings.xml` as `default_web_client_id`.

## 3) Run
```bash
./gradlew assembleDebug
# or open in Android Studio and click Run
```

## 4) Flow
- App launches to **SignInActivity**
- Tap **Continue with Google** → Firebase signs you in
- You land on **MainActivity**, where you can toggle **Enable biometric unlock**
- On next launch, you’ll be prompted to authenticate biometrically (if available & enabled)

## 5) Notes for POE
- This demo stores a simple boolean flag for biometrics in SharedPreferences.
  For high‑security scenarios, consider encrypting with Android Keystore.
- You can customize the UI and extend guarded areas as needed.

## Troubleshooting
- If you see *DEVELOPER_ERROR* during sign‑in, make sure your SHA‑1/SHA‑256 fingerprints
  are registered in Firebase project settings and you rebuilt the app.
- Ensure `default_web_client_id` matches the one assigned to your Android app.