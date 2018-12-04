# Scenes

_This page assumes you have knowledge of the responsibilities of a Scene, as
described in [Concepts: Scenes](../concepts/scenes/scenes)._

## The `Scene` interface

The simplest, but most cumbersome, way to create your own Scene is to directly
implement the `Scene` interface, and override the methods that you need:

```kotlin
interface MyContainer : Container {

    var text: String
}

class MyScene : Scene<MyContainer> {

    override fun attach(v : MyContainer) {
        v.text = "Hello World!"
    }
}
```

A Scene provides the following methods you can override:

 - `onStart()`: Called when the Scene is started;
 - `attach(V)`: Attaches the Container to the Scene;
 - `detach(V)`: Detaches the Container from the Scene;
 - `onStop()`: Called when the Scene is stopped;
 - `onDestroy()`: Called when the Scene gets destroyed.


### `onStart` / `onStop`

These two methods mark the active stage of the Scene.
You could choose to register to location updates when the Scene becomes active,
and cancel the registration when the Scene becomes inactive:

```kotlin
interface MyContainer : Container

class MyScene(
    private val locationProvider: LocationProvider
): Scene<MyContainer> {

    private val listener = { location: Location? ->
        // Process location update
    }

    override fun onStart() {
        locationProvider.registerLocationUpdates(listener)
    }

    override fun onStop() {
        locationProvider.unregisterLocationUpdates(listener)
    }
}
```

### `attach`  / `detach`

These two methods give you access to the user interface.
You can grab a reference to the Container instance in `attach` to be able
to pass data to it.
However, you must make sure to remove the reference in `detach` to prevent
memory leaks.
For example, if we expand on the previous example:

```kotlin
interface MyContainer : Container {

    var location : Location?
}

class MyScene(
    private val locationProvider: LocationProvider
): Scene<MyContainer> {

    private var view: MyContainer? = null

    private val listener = { location: Location? ->
        view?.location = location
    }

    override fun onStart() {
        locationProvider.registerLocationUpdates(listener)
    }

    override fun attach(v: MyContainer) {
        this.view = v
    }

    override fun detach(v: MyContainer) {
        this.view = null
    }

    override fun onStop() {
        locationProvider.unregisterLocationUpdates(listener)
    }
}
```

The implementation above will start to listen to location updates as soon as the
Scene becomes active.
When a view is attached, the location updates will be passed on to it.
When the view is detached, the reference to the view is removed, but the Scene
will still listen to location updates.
Finally, When the Scene becomes inactive, it will stop listening to location
updates as well.


### `onDestroy()`

`onDestroy()` will be called once and only once at the end of the lifetime of
the Scene.
When this method is called, the Scene must be regarded as destroyed and no more
calls to its lifecycle methods will be made.
You can use this callback to release resources if you already haven't done so.


## State saving

By default, a `Scene` does not support state saving, nor does it save or restore
view hierarchy state such as user input or scroll positions.

### View hierarchy state saving

During the lifetime of a Scene it can happen that it receives multiple calls to
`attach` and `detach`.
Often, subsequent calls to `attach` will have fresh instances of the Container
passed to it, losing any view hierarchy state.

The Scene can save and restore the container state between these subsequent
calls to `attach` if the Container type implements the `RestorableContainer`
interface.
This interface provides a `saveInstanceState` method and a
`restoreInstanceState` method.
The Scene can use these methods to restore the view state:

```kotlin
interface MyContainer: RestorableContainer

class MyScene : Scene<MyContainer> {

    private var containerState: ContainerState? = null

    override fun attach(v: MyContainer) {
        containerState?.let { v.restoreInstanceState(it) }
    }

    override fun detach(v: MyContainer) {
        containerState = v.saveInstanceState()
    }
}
```

### Scene state saving

To have your Scene's state saved to prepare for process deaths, implement the
`SavableScene` interface.
This interface provides a `saveInstanceState()` method that will be called at
the appropriate time.
When saving a Scene, you generally want to save as little as possible, but just
enough to be able to reconstruct it after process death.
Next to this, you can also choose to save the view hierarchy state with it, if
the Container supports it:

```kotlin
interface MyContainer: RestorableContainer

class MyScene(
    private val userId: String
) : Scene<MyContainer>, SavableScene {

    private var containerState: ContainerState? = null

    override fun attach(v: MyContainer) {
        containerState?.let { v.restoreInstanceState(it) }
    }

    override fun detach(v: MyContainer) {
        containerState = v.saveInstanceState()
    }

    override fun saveInstanceState(): SceneState {
        return sceneState {
            it["user_id"] = userId
            it["container_state"] = containerState
        }
    }
}
```

### Scene state restoration

After a process death, a Navigator class can ask you to restore your Scene from
a saved state.
If your Scene implements `SavableScene`, you will be passed the `SceneState`
instance that you returned from `saveInstanceState()`.
You can then pull out everything you need to be able to restore the Scene.
If we again expand on the previous example, we can add restoration support by
implementing a `create` method in the Scene's `companion object` as follows:

```kotlin
interface MyContainer: RestorableContainer

class MyScene(
    private val userId: String,
    savedState: SceneState? = null
) : Scene<MyContainer>, SavableScene {

    private var containerState: ContainerState? = savedState?.get("container_state")

    override fun attach(v: MyContainer) {
        containerState?.let { v.restoreInstanceState(it) }
    }

    override fun detach(v: MyContainer) {
        containerState = v.saveInstanceState()
    }

    override fun saveInstanceState(): SceneState {
        return sceneState {
            it["user_id"] = userId
            it["container_state"] = containerState
        }
    }

    companion object {

        fun create(savedState: SceneState) : MyScene {
            return MyScene(
                savedState["user_id"],
                savedState
            )
        }
    }
}
```

Now, we have restored our `userId` from the saved state, as well as any view
state that was saved with it.

## Base implementations

The samples above show how you can create Scenes with just the basic interfaces,
but there is a lot of boilerplate setup taking place.
Fortunately, there are some base implementations that take some of this
boilerplate out of your hands.

### BasicScene<sup>1</sup>

The `BasicScene` is a very simple abstract Scene class the provides a handle to
the currently attached view, as well has automatically saving and restoring the
view hierarchy state between subsequent `attach` calls.
We can take our [[`attach`  / `detach`]] example above and re-implement it using TODO
the BasicScene class:

```kotlin
interface MyContainer: Container

class MyScene(
    private val locationProvider: LocationProvider
): BasicScene<MyContainer> {

    private val listener = { location: Location? ->
        currentView?.location = location
    }

    override fun onStart() {
        locationProvider.registerLocationUpdates(listener)
    }

    override fun onStop() {
        locationProvider.unregisterLocationUpdates(listener)
    }
}
```

We don't have to manually keep a reference to the view anymore, and we don't
have to worry about releasing the reference since it's done for us.

### `BaseSavableScene`<sup>1</sup>

The `BaseSavableScene` class is an abstract class that handles the view hierarchy
state saving for you, and implements `SavableScene`.
If we take the sample from [[Scene state restoration]] above and re-implement it TODO
using the BaseSavableScene class, we get the following:

```kotlin
interface MyContainer: RestorableContainer

class MyScene(
    private val userId: String,
    savedState: SceneState? = null
) : BaseSavableScene<MyContainer>(savedState) {

    override fun saveInstanceState(): SceneState {
        return super.saveInstanceState().also {
            it["user_id"] = userId
        }
    }

    companion object {

        fun create(savedState: SceneState) : MyScene {
            return MyScene(
                savedState["user_id"],
                savedState
            )
        }
    }
}
```

We now only have to deal with saving and restoring our `userId`, and let the
BaseSavableScene handle the rest.

### `RxScene`<sup>2</sup>

The `RxScene` abstract class extends the `BaseSavableScene` class and provides
helper functions for working with Rx streams.

### `LifecycleScene`<sup>3</sup>

The `LifecycleScene` abstract class extends the `BaseSavableScene` class, and
implements the `androidx.lifecycle.LifecycleOwner` interface.

----

1: This class is available in the `ext-acorn` artifact.
2: This class is available in the `ext-acorn-rx` artifact.
3: This class is available in the `ext-acorn-android-lifecycle` artifact.
