#!/usr/bin/env bash

./ktlint

./gradlew clean \
    test \
    \
    :ext-acorn-android-testing:lint \
    :ext-acorn-android-timber:lint \
    :ext-acorn-android-lifecycle:lint \
    \
    :samples:hello-bottombar:lintRelease \
    :samples:hello-concurrentpairnavigator:lintRelease \
    :samples:hello-navigation:lintRelease \
    :samples:hello-overridingback:lintRelease \
    :samples:hello-sharedata:lintRelease \
    :samples:hello-startactivity:lintRelease \
    :samples:hello-staterestoration:lintRelease \
    :samples:hello-transitionanimation:lintRelease \
    :samples:hello-viewfactory:lintRelease \
    :samples:hello-world:lintRelease \
    \
    :samples:notes-app:android:lintRelease \
    \
    packageDebugAndroidTest \
    \
    publishToMavenLocal \
    --max-workers=1 --rerun-tasks -Dorg.gradle.jvmargs=-Xmx1536m
