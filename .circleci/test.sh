#!/usr/bin/env bash

./gradlew clean \
    test \
    ktlint \
    \
    :ext-bravo-android:lint \
    :ext-bravo-android-testing:lint \
    :ext-bravo-android-timber:lint \
    :ext-bravo-android-lifecycle:lint \
    \
    :samples:hello-world:lintRelease \
    :samples:hello-navigation:lintRelease \
    :samples:hello-staterestoration:lintRelease \
    :samples:hello-transitionanimation:lintRelease \
    :samples:hello-startactivity:lintRelease \
    :samples:hello-sharedata:lintRelease \
    \
    :samples:notes-app:android:lintRelease \
    \
    packageDebugAndroidTest \
    \
    publishToMavenLocal \
    --max-workers=1 --rerun-tasks
