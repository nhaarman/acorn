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
implementation "com.nhaarman.bravo:bravo:x.x.x-SNAPSHOT"
implementation "com.nhaarman.bravo:bravo-android:x.x.x-SNAPSHOT"
implementation "com.nhaarman.bravo:bravo-android-tests:x.x.x-SNAPSHOT"
```

Bravo is divided in three main parts:

| Artifact | Description | Dependencies|
|----------|-------------|-------------|
| `bravo-core` | The core library, containing only the interfaces for Bravo without any functionality. | - |
| `bravo` | The main library which provides JVM implementations for Navigators and Scenes. | `bravo-core` |
| `bravo-android` | The main library for Android, which provides default Android implementations. | `bravo`, `bravo-core` |
|----|

Next to these, there is a `bravo-android-tests` artifact that provides helper classes for running instrumentation tests.

# Description

In Bravo, dedicated `Navigator` classes are responsible for handling navigation
from screen to screen in your application.
Screens are represented by `Scene` classes, and are responsible for bridging
business logic and the UI.

