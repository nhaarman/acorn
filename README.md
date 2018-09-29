# Bravo

Bravo is a carefully designed library aimed to make navigation in applications
easier.

Navigation on Android is complex, since the default way of navigating through
and using Activities or Fragments severely violate separation of concerns.
This project aims to be able to reclaim this separation by clearly providing a
separation between UI, presentation and navigation.

<p align="center">
  <br>
  <img src=".github/art/diagram_readme.png">
</p>

The core of Bravo consists of two interfaces: `Scene` and `Navigator`.

Scenes represent destinations in the application where the user can navigate to.
Often these are the screens of the application, but they can represent any
navigational node in the application.

Navigators control the navigational state of the application.
They respond to events emitted by Scenes and update the navigational state
accordingly, for example by changing the active Scene.

Coupling to Android, the Activity subscribes to changes in Scenes by the
Navigator, and provides the user interface.

## Extensibility

Bravo is fully extensible.
The core artifacts (`bravo` and `bravo-android`) provide interfaces that are
simple but powerful enough to form a basis for writing the presentation layer
of mobile applications.

The library also provides two base extension artifacts that can be used as a
basis for the presentation layer of your application.  
`ext-bravo` provides default base implementations for working with Scenes and
Navigators.  
`ext-bravo-android` provides a mechanism to couple the Android framework to
your application.

Lastly, there are some helper extension artifacts that provide useful utility
implementations which use external dependencies, such as RxJava or Android
LiveData.

## Setup

Bravo currently only exists as a SNAPSHOT.
Gradle users can add the following line to their `repositories` section:

```groovy
maven { url "https://oss.sonatype.org/content/repositories/snapshots" }
```

You can declare the dependencies as follows, replacing `x.x.x` by the latest
snapshot version:

```groovy
// To get started quickly, add the following artifacts
implementation "com.nhaarman.bravo.ext:bravo:x.x.x-SNAPSHOT"
implementation "com.nhaarman.bravo.ext:bravo-android:x.x.x-SNAPSHOT"
```

As mentioned before, Bravo consists of a core and some extension artifacts.
The core artifacts live in the `com.nhaarman.bravo` group:

|Group| Artifact | Description | Dependencies|
|-----|----------|-------------|-------------|
|`com.nhaarman.bravo`| `bravo` | The core library, containing only the interfaces for Bravo without any functionality. | - |
|`com.nhaarman.bravo`| `bravo-android` | The core library for Android, containing only the interfaces for working with Bravo on Android. | `com.nhaarman.bravo:bravo` |

The extension libraries provide default implementations to the core, and live
in the `com.nhaarman.bravo.ext` group:

|Group| Artifact | Description | Dependencies|
|-----|----------|-------------|-------------|
|`com.nhaarman.bravo.ext`| `bravo` | The main extension on the Bravo core, providing default implementations. |`com.nhaarman.bravo:bravo`|
|`com.nhaarman.bravo.ext`| `bravo-rx` | An RxJava extension for Bravo. |`com.nhaarman.bravo:bravo`|
|`com.nhaarman.bravo.ext`| `bravo-testing` | Provides testing utilities for JVM tests. |`com.nhaarman.bravo:bravo`|
||||
|`com.nhaarman.bravo.ext`| `bravo-android` | The main extension on the Bravo-Android core, providing default implementations. |`com.nhaarman.bravo:bravo`<br>`com.nhaarman.bravo:bravo-android`|
|`com.nhaarman.bravo.ext`| `bravo-android-testing` | Provides testing facilities for instrumentation testing with Bravo-Android. |`com.nhaarman.bravo:bravo`<br>`com.nhaarman.bravo:bravo-android`<br>`com.nhaarman.bravo.ext:bravo-android`|
|`com.nhaarman.bravo.ext`| `bravo-android-timber` | Provides a `TimberLogger` |`com.nhaarman.bravo:bravo`<br>`com.nhaarman.bravo.ext:bravo`|
|`com.nhaarman.bravo.ext`| `bravo-android-lifecycle` | Provides `LifecycleScene` |`com.nhaarman.bravo:bravo`<br>`com.nhaarman.bravo.ext:bravo`|

