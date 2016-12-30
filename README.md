# fake-gps
fake-gps is a mock of location service for android

# Building

```
% ./bootstrap
% ./configure --with-android-home=$ANDROID_HOME --with-map-api-key=$YOUR_API_KEY --with-geocoding-api-key=$YOUR_API_KEY --with-debug-keystore-path=$YOUR_KEYSTORE_PATH --with-debug-keystore-pass=$YOUR_KEYSTORE_PASS --with-debug-key-alias=$YOUR_KEY_ALIAS --with-debug-key-pass=$YOUR_KEY_PASS
% make # assemble debug apk
% make install # install debug apk via 'adb install -r'
```
