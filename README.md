# PortWatch VI — Android widget
Unofficial native Android dashboard for the public PortWatch VI tracker. No game files, keys or executables included.

## 2.0 Expressive
- System wallpaper-derived Android 12+ color resources; light/dark/system theme and older Android fallback.
- Responsive square, compact and large Canvas widget layouts, scalloped progress dial, tonal cards, ranking bars and concise news.
- In-app widget previews, GPU filter, news toggle and 15/30/60-minute or manual refresh. Android may defer background jobs.
- Cached public tracker data, Russian news translation (external Google translation request), manual refresh.
- Native Java and RemoteViews, no third-party libraries. Device layout and tracker GPU field compatibility still require on-device testing.

Build with JDK 17, Android SDK 35 and Gradle 8.9:
`gradle :app:assembleDebug`

Actions produces the debug APK as PortWatch-VI-APK. Debug signing keys may differ between runners: uninstall the previous debug build if Android rejects the update.
