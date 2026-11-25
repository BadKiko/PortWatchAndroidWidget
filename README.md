# PortWatch VI — Android widget
Unofficial native Android dashboard for the public PortWatch VI tracker. No game files, keys or executables included.

## 3.0.1
- Secondary dynamic tonal container for the widget background, tinted text and nested surfaces.
- Adaptive vector launcher icon with monochrome layer for Android 13+ themed icons.

## 3.0 Expressive
- Four selectable visual styles: Liquid Expressive, Organic Shapes (default), Pixel Racing and Editorial Minimal.
- Each style has dedicated square, compact and large layouts; the global style setting updates installed widgets immediately.
- Large news content appears in Liquid and in extra-tall variants of other designs.

## 2.0 Expressive
- System wallpaper-derived Android 12+ color resources; light/dark/system theme and older Android fallback.
- Responsive square, compact and large Canvas widget layouts, scalloped progress dial, tonal cards, ranking bars and concise news.
- In-app widget previews, GPU filter, news toggle and 15/30/60-minute or manual refresh. Android may defer background jobs.
- Cached public tracker data, Russian news translation (external Google translation request), manual refresh.
- Native Java and RemoteViews, no third-party libraries. Device layout and tracker GPU field compatibility still require on-device testing.

Build with JDK 17, Android SDK 35 and Gradle 8.9:
`gradle :app:assembleDebug`

Actions produces the debug APK as PortWatch-VI-APK. Debug signing keys may differ between runners: uninstall the previous debug build if Android rejects the update.
