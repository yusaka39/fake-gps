APK_PATH = app/build/outputs/apk/app-debug.apk

all:
	ANDROID_HOME=$(ANDROID_HOME) ./gradlew assembleDebug

install: all
	$(ANDROID_HOME)/platform-tools/adb install -r $(APK_PATH)
