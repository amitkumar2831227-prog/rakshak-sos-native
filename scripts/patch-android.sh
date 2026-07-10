#!/bin/bash
set -e

MANIFEST="android/app/src/main/AndroidManifest.xml"
PKG_DIR="android/app/src/main/java/com/rakshak/sos"

mkdir -p "$PKG_DIR"

echo "Copying native plugin files..."
cp native/SosPlugin.java "$PKG_DIR/SosPlugin.java"
cp native/MainActivity.java "$PKG_DIR/MainActivity.java"

echo "Adding required permissions to AndroidManifest.xml..."
if ! grep -q "SEND_SMS" "$MANIFEST"; then
  sed -i '/<\/manifest>/i\
    <uses-permission android:name="android.permission.SEND_SMS" />\
    <uses-permission android:name="android.permission.CALL_PHONE" />\
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />\
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />\
    <uses-permission android:name="android.permission.VIBRATE" />' "$MANIFEST"
fi

echo "Patch complete."
cat "$MANIFEST"
