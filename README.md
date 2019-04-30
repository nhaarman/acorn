# Acorn

Acorn is a carefully designed library that brings true modularity to your 
presentation layer and allows you to have full control over your transition
animations.

Activities and Fragments restrict application development in such a way that
creating modular, testable components becomes a difficult thing to do.
Furthermore, implementing transition animations to visualize going from one 
screen to another with either of these components is non trivial.

Acorn provides modularity by grouping specific sets of screens together as 
building blocks, building up your application into several composable flows.  
The view layer is decoupled from navigation and reacts to screen changes, giving
you full control over transition animations.

![](/docs/src/orchid/resources/media/acorn_diagram_extended.svg)

You can read more about Acorn on the
[documentation website](https://nhaarman.github.io/acorn).

## Easy setup [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.nhaarman.acorn/acorn/badge.png)](https://maven-badges.herokuapp.com/maven-central/com.nhaarman.acorn/acorn)

Acorn is hosted on Maven Central.

To get started quickly, you can include the `ext-acorn-android` dependency, 
which includes the necessary base to create an app using Acorn.

```groovy
implementation "com.nhaarman.acorn.ext:acorn-android:x.x.x"
```

If you use `androidx.appcompat`, you can use `ext-acorn-android-appcompat` instead:

```groovy
implementation "com.nhaarman.acorn.ext:acorn-android-appcompat:x.x.x"
```

Using the dependencies above will transitively pull all other dependencies you
need as well.  

For more advanced configuration, see 
[Setup](https://nhaarman.github.io/acorn/wiki/setup.html).

## Getting started

Acorn has several [sample projects](samples) introducing the different concepts.
You can also visit the [Getting started](https://nhaarman.github.io/acorn/wiki/getting_started/)
documentation page for more information.

## Building Acorn

Acorn is built with Gradle.

 - Running `./gradlew test` will run all JVM tests;
 - Running `./gradlew pitest` will generate [PIT testing reports](http://pitest.org/) for JVM modules;
 - Running `./test` will run the entire test suite, you will need to have a connected Android device with API 23+.
 - Running `./gradlew publishToMavenLocal` will install a copy of all the libraries in your local maven repository.
 
### Versioning

Acorn follows [semantic versioning](https://semver.org/), and will determine the
version number based on [git tags](.ops/git.gradle).

**Warning!** Acorn's API *may* not have stabilized yet, and breaking API changes
may occur until 1.0 is reached.

### Linter

Acorn uses [ktlint](https://github.com/shyiko/ktlint) which is enforced in CI.
