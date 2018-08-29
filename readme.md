# Bravo

Bravo is a carefully designed library aimed to make navigation in applications
easier.

Navigation on Android is complex, since the default way of navigating through
and using Activities or Fragments severely violate separation of concerns.
This project aims to be able to reclaim this separation by clearly providing a
separation between UI, presentation and navigation.

## Setup

Bravo currently only exists as a SNAPSHOT.
Gradle users can add the following line to their `repositories` section:

```groovy
maven { url "https://oss.sonatype.org/content/repositories/snapshots" }
```

You can declare the dependencies as follows, replacing `x.x.x` by the latest
snapshot version:

```groovy
// Core artifacts containing interfaces only
implementation "com.nhaarman.bravo:bravo:x.x.x-SNAPSHOT"
implementation "com.nhaarman.bravo:bravo-android:x.x.x-SNAPSHOT"

// Artifacts that extend upon the core
implementation "com.nhaarman.bravo.ext:bravo:x.x.x-SNAPSHOT"
implementation "com.nhaarman.bravo.ext:bravo-rx:x.x.x-SNAPSHOT"
implementation "com.nhaarman.bravo.ext:bravo-android:x.x.x-SNAPSHOT"
implementation "com.nhaarman.bravo.ext:bravo-android-testing:x.x.x-SNAPSHOT"
implementation "com.nhaarman.bravo.ext:bravo-android-timber:x.x.x-SNAPSHOT"
```

Bravo is built in two main parts: the core libraries and the extension libraries
that implement the core.
The core libraries provide the interfaces that carry the spirit of Bravo:

|Group| Artifact | Description | Dependencies|
|-----|----------|-------------|-------------|
|`com.nhaarman.bravo`| `bravo` | The core library, containing only the interfaces for Bravo without any functionality. | - |
|`com.nhaarman.bravo`| `bravo-android` | The core library for Android, containing only the interfaces for working with Bravo on Android. | `com.nhaarman.bravo:bravo` |

The extension libraries provide default implementations of the core:

|Group| Artifact | Description | Dependencies|
|-----|----------|-------------|-------------|
|`com.nhaarman.bravo.ext`| `bravo` | The main extension on the Bravo core, providing default implementations. |`com.nhaarman.bravo:bravo`|
|`com.nhaarman.bravo.ext`| `bravo-rx` | An RxJava extension for Bravo. |`com.nhaarman.bravo:bravo`|
|`com.nhaarman.bravo.ext`| `bravo-android` | The main extension on the Bravo-Android core, providing default implementations. |`com.nhaarman.bravo:bravo`<br>`com.nhaarman.bravo:bravo-android`|
|`com.nhaarman.bravo.ext`| `bravo-android-testing` | Provides testing facilities for instrumentation testing with Bravo-Android. |`com.nhaarman.bravo:bravo`<br>`com.nhaarman.bravo:bravo-android`<br>`com.nhaarman.bravo.ext:bravo-android`|
|`com.nhaarman.bravo.ext`| `bravo-android-timber` | Provides a `TimberLogger` |`com.nhaarman.bravo:bravo`<br>`com.nhaarman.bravo.ext:bravo`|

