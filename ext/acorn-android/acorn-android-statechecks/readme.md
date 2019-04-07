## Experimental StateChecker API

The classes in this module provide a way to catch state restoration 
errors early when running debug builds.

State restoration doesn't happen very often, but is nevertheless a very
important topic to support.
And since this doesn't happen often, especially during short-lived debug
builds, programmer errors made here are often overlooked.

By simply including `com.nhaarman.acorn.ext:acorn-android-statechecks` 
in your debug builds, a hook is registered and a StateChecker will try 
to save and restore the entire Navigator state upon each Scene 
transition.
Restoration checking is done by creating a new Navigator from the saved
state and immediately destroying it.
The Navigator will never reach its 'started' state.

**Note**: Instantiating the Navigator _may_ cause side effects if your
Navigator or one of its Scenes does work outside of the 'started' state.

To include this artifact, add the following to your `dependencies` 
section:

```groovy
debugImplementation "com.nhaarman.acorn.ext:acorn-android-statechecker:x.x.x"
```

Be careful to not include this artifact in production builds!
