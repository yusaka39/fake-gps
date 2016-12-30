#                                               -*- Autoconf -*-
# Process this file with autoconf to produce a configure script.

AC_PREREQ([2.69])
AC_INIT([FULL-PACKAGE-NAME], [VERSION], [BUG-REPORT-ADDRESS])
AM_INIT_AUTOMAKE([-Wno-portability])

# Checks for programs.
AC_PROG_INSTALL

# Checks for libraries.

# Checks for header files.

# Checks for typedefs, structures, and compiler characteristics.

# Checks for library functions.

# Variables

AC_ARG_WITH([android-home],
            [AS_HELP_STRING([--with-android-home],
                            [ANDROID_HOME])],
            [ANDROID_HOME=$withval])
AC_MSG_CHECKING(ANDROID_HOME)
if test -n "$ANDROID_HOME"; then
  AC_SUBST(ANDROID_HOME)
  AC_MSG_RESULT($ANDROID_HOME)
else
  AC_MSG_ERROR([ANDROID_HOME is required])
fi

AC_ARG_WITH([map-api-key],
            [AS_HELP_STRING([--with-map-api-key],
                            [An api key for android google map api])],
            [MAP_API_KEY=$withval])
AC_MSG_CHECKING(MAP_API_KEY)
if test -n "$MAP_API_KEY"; then
  AC_SUBST(MAP_API_KEY)
  AC_MSG_RESULT($MAP_API_KEY)
else
  AC_MSG_ERROR([MAP_API_KEY is required])
fi

AC_ARG_WITH([geocoding-api-key],
            [AS_HELP_STRING([--with-geocoding-api-key],
                            [An api key for google geocoding api])],
            [GEOCODING_API_KEY=$withval])
AC_MSG_CHECKING(GEOCODING_API_KEY)
if test -n "$MAP_API_KEY"; then
  AC_SUBST(GEOCODING_API_KEY)
  AC_MSG_RESULT($GEOCODING_API_KEY)
else
  AC_MSG_ERROR([GEOCODING_API_KEY is required])
fi

AC_CONFIG_FILES(
    [Makefile]
    [app/src/main/AndroidManifest.xml]
    [app/src/main/kotlin/io/github/yusaka39/fakegps/ConfigureConfig.kt]
)
AC_OUTPUT
