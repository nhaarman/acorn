#!/usr/bin/env bash

./gradlew clean \
    test \
    ktlint \
    --rerun-tasks

./gradlew :samples:hello-world:lintRelease \
    :samples:hello-navigation:lintRelease \
    :samples:hello-staterestoration:lintRelease \
    :samples:hello-transitionanimation:lintRelease \
    :samples:hello-startactivity:lintRelease \
    :samples:hello-sharedata:lintRelease \
    :samples:notes-app:aac-navigation:lintRelease \
    :samples:notes-app:cicerone:lintRelease \
    :samples:notes-app:conductor:lintRelease \
    :samples:notes-app:mosby:lintRelease \
    :notes-app-bravo-android:lintRelease \
    --rerun-tasks

./gradlew publishToMavenLocal --rerun-tasks
