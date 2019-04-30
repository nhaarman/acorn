Acorn is tactically divided in several modules to be able to separate different
concerns from each other. Core artifacts define the general contracts of Acorn,
while extension artifacts provide the implementations.


## Easy setup [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.nhaarman.acorn/acorn/badge.png)](https://maven-badges.herokuapp.com/maven-central/com.nhaarman.acorn/acorn)

Acorn is hosted on Maven Central.

To get started quickly, you can include the `ext-acorn-android` dependency, 
which includes the necessary base to create an app.

```groovy
implementation "com.nhaarman.acorn.ext:acorn-android:x.x.x"
```

If you use `androidx.appcompat`, you can use `ext-acorn-android-appcompat` instead:

```groovy
implementation "com.nhaarman.acorn.ext:acorn-android-appcompat:x.x.x"
```

Using the dependencies above will transitively pull all other dependencies you
need as well.


## Advanced setup

As mentioned before, Acorn is divided in several modules.

### Core

The core modules provide the general contracts of Acorn as interfaces and simple
data classes.

|Group| Artifact | Description | Dependencies|
|-----|----------|-------------|-------------|
|`com.nhaarman.acorn`| `acorn` | The core library, containing only the interfaces for Acorn without any functionality. | - |
|`com.nhaarman.acorn`| `acorn-android` | The core library for Android, containing only the interfaces for working with Acorn on Android. | `com.nhaarman.acorn:acorn` |

### JVM extensions

The following artifacts build upon the `acorn` artifact and provide some default
implementations for `Navigator` and `Scene`:


|Group| Artifact | Description | Dependencies|
|-----|----------|-------------|-------------|
|`com.nhaarman.acorn.ext`| `acorn` | The main extension on the Acorn core, providing default implementations. |`com.nhaarman.acorn:acorn`|
|`com.nhaarman.acorn.ext`| `acorn-rx` | An RxJava extension for Acorn. |`com.nhaarman.acorn:acorn`|
|`com.nhaarman.acorn.ext`| `acorn-testing` | Provides testing utilities for JVM tests. |`com.nhaarman.acorn:acorn`|


### Android extensions

Finally, the following artifacts provide the necessary implementations to make
Acorn work on Android:


|Group| Artifact | Description | Dependencies|
|-----|----------|-------------|-------------|
|`com.nhaarman.acorn.ext`| `acorn-android` | The main extension on the Acorn-Android core, providing default implementations. |`com.nhaarman.acorn:acorn`<br>`com.nhaarman.acorn:acorn-android`|
|`com.nhaarman.acorn.ext`| `acorn-android-testing` | Provides testing facilities for instrumentation testing with Acorn-Android. |`com.nhaarman.acorn:acorn`<br>`com.nhaarman.acorn:acorn-android`<br>`com.nhaarman.acorn.ext:acorn-android`|
|`com.nhaarman.acorn.ext`| `acorn-android-timber` | Provides a `TimberLogger` |`com.nhaarman.acorn:acorn`<br>`com.nhaarman.acorn.ext:acorn`|
|`com.nhaarman.acorn.ext`| `acorn-android-lifecycle` | Provides `LifecycleScene` |`com.nhaarman.acorn:acorn`<br>`com.nhaarman.acorn.ext:acorn`|
