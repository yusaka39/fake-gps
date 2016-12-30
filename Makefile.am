APK_PATH = app/build/outputs/apk/app-debug.apk

all:
	$(SHELL) ./gradlew assembleDebug

install: all
	$(SHELL) $(ANDROID_HOME)/platform-tools/adb install -r $(APK_PATH)
