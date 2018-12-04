# Navigators

_This page assumes you have knowledge of the responsibilities of a Navigator, as
described in [Concepts: Navigators](../concepts/navigators/navigators)._

## The `Navigator` interface

Just as with Scenes, you can implement the `Navigator` interface to create your
own Navigator.
This time however, this is actually discouraged: it can be tricky to properly
deal with managing the different lifecycles.
There are a couple of excellent base classes available however that provide most
of the basic implementations you'll need.
If you still need to implement your own `Navigator`, have a look at
[Scene Management](../concepts/navigators/scene_management), and study the
existing implementations and their tests.


Like a Scene, the Navigator has a couple of lifecycle methods, and it provides
an interface `Events` to be able to notify interested parties of events.
As a user of one of the base classes mentioned above you usually won't have to
override these methods, although you can hook into them when you need it.

 - `onStart()`: Called when the Navigator is started;
 - `onStop()`: Called when the Navigator is stopped;
 - `onDestroy()`: Called when the Navigator gets destroyed;
 - `addNavigatorEventsListener(Navigator.Events)`: Allows interested parties to
register an events listener.

## State saving

By default, a `Navigator` does not support state saving, although each of the
base implementations below do.
To support Navigator state saving, it can implement the `SavableNavigator`
interface.
In this method, the Navigator implementation must include everything necessary
to be able to reconstruct itself from a serialized state.
This includes the Navigator's own internal state, but also that of its Scenes.
When using one of the base implementations this is done mostly for you, but you
can also choose to override the `saveInstanceState` method to include your own
data.

```kotlin
class MyNavigator(
    private val userId: String,
    savedState: NavigatorState?
) : SingleSceneNavigator(savedState) {

    override fun saveInstanceState() : NavigatorState {
        return super.saveInstanceState().also {
            it["user_id"] = userId
        }
    }

    companion object {

        fun from(savedState: NavigatorState) : MyNavigator {
            return MyNavigator(
                userId = savedState["user_id"],
                savedState = savedState
            )
        }
    }
}
```

## Base implementations

There are some base Navigator implementations to help you get started.
Each of these implementations implement `SavableNavigator` to allow their state
to be saved, and have their own strategies of doing this.

### SingleSceneNavigator<sup>1</sup>

The `SingleSceneNavigator` is a very basic Navigator that is only able to host
a single Scene during its lifetime, without ever navigating to a different Scene.
This class can sometimes come in handy when composing Navigators.

The SingleSceneNavigator provides a single abstract `createScene` method that
needs to be overridden, which provides the Scene to use in this Navigator.
If the Navigator is restored from a saved state and the Scene implements
`SavableScene`, the `createScene` method will be called with the instance that
was returned in `SavableScene#saveInstanceState`.

```kotlin
class MyHelloWorldNavigator(
    savedState: NavigatorState?
) : SingleSceneNavigator(savedState) {

    override fun createScene(state: SceneState?) : Scene<out Container> {
        return HelloWorldScene(state)
    }
}
```

The SingleSceneNavigator will automatically save its own and the Scene's
instance state when necessary, and reconstruct itself from any saved state
passed to its constructor.

### ReplacingNavigator<sup>1</sup>

The `ReplacingNavigator` is a Navigator that can switch between several
different Scenes, but has no back behavior.
When the user presses the back button, the Navigator will always directly finish
regardless of how many Scenes it has seen.

The ReplacingNavigator provides an abstract method `initialScene` that needs to
be overridden, which provides the initial Scene to use when not restored from a
saved state.
When the ReplacingNavigator is restored from a saved state, this method will not
be called.

Instead to support state restoration, this ReplacingNavigator provides an
abstract `instantiateScene` method which takes in a `KClass<Scene<*>>` and an
optional `SceneState` instance.
Since state saving can occur at any time, users of the ReplacingNavigator must
be able to handle all Scenes used in it, even if the Scene itself does not
implement `SavableScene`.

```kotlin
class MyNavigator(
    savedState: NavigatorState?
) : ReplacingNavigator(savedState) {

    override fun initialScene() : Scene<out Container> {
        return MyFirstScene()
    }

    fun onEvent() {
        replace(MySecondScene())
    }

    override fun instantiateScene(
        sceneClass: KClass<out Scene<*>>,
        state: SceneState?
    ) : Scene<out Container> {
        return when(sceneClass) {
            MyFirstScene::class -> MyFirstScene(state)
            MySecondScene::class -> MySecondScene(state)
            else -> error("Unknown scene class: $sceneClass")
        }
    }
}
```

To switch Scenes in the ReplacingNavigator, the class provides a `replace`
method.
When calling this method, the ReplacingNavigator will handle the previous and
new Scenes' lifecycle methods appropriately and notify any listeners of the
change in scenery.

### StackNavigator<sup>1</sup>

The `StackNavigator` base class is the class you'll feel most familiar with, as
it uses a stack to model its internal state.
You can push Scenes on the stack, pop them off, or replace the top Scene with
another one.
Pressing the back button will pop the top Scene off the stack.

The StackNavigator provides an abstract method `initialStack` that needs to
be overridden, which provides the initial Scene stack to use when not restored
from a saved state.
When the StackNavigator is restored from a saved state, this method will not
be called.

Instead to support state restoration, this StackNavigator provides an
abstract `instantiateScene` method which takes in a `KClass<Scene<*>>` and an
optional `SceneState` instance.
Since state saving can occur at any time, users of the StackNavigator must
be able to handle all Scenes used in it, even if the Scene itself does not
implement `SavableScene`.
The StackNavigator will take care of preserving the order of the stack.

```kotlin
class MyNavigator(
    savedState: NavigatorState?
) : StackNavigator(savedState) {

    override fun initialStack() : List<Scene<out Container>> {
        return listOf(MyFirstScene())
    }

    fun onEvent() {
        push(MySecondScene())
    }

    override fun instantiateScene(
        sceneClass: KClass<out Scene<*>>,
        state: SceneState?
    ) : Scene<out Container> {
        return when(sceneClass) {
            MyFirstScene::class -> MyFirstScene(state)
            MySecondScene::class -> MySecondScene(state)
            else -> error("Unknown scene class: $sceneClass")
        }
    }
}
```

To manipulate the stack in the StackNavigator, the class provides three methods:
`push`, `pop`, and `replace`.
When calling one of these methods, the StackNavigator will handle the Scenes'
lifecycle methods appropriately and will notify any listeners of the change in
scenery.

## Composing Navigators

One of the strengths of Navigators is that they can be composed into a
larger structure.
This allows for modular and reusable blocks in your application.
When composing Navigators, you have the same freedom as you have with regular
Navigators which means that you can choose your own internal model to represent
the state.

When composing Navigators, you usually create a Navigator that exclusively deals
with other Navigator instances directly, instead of mixing Scenes and Navigators
together.

The base implementations below show an overview of the default composite
Navigator classes.

### CompositeReplacingNavigator<sup>1</sup>

The `CompositeReplacingNavigator` class is the composite version of the
ReplacingNavigator, and can switch between several child navigators.

```kotlin
class MyCompositeNavigator(
    savedState: NavigatorState?
) : CompositeReplacingNavigator(savedState) {

    override fun initialNavigator() : Navigator {
        return MyFirstNavigator()
    }

    fun onEvent() {
        replace(MySecondNavigator())
    }

    override fun instantiateNavigator(
        navigatorClass: KClass<out Navigator<*>>,
        state: NavigatorState?
    ) : Navigator<out Container> {
        return when(navigatorClass) {
            MyFirstNavigator::class -> MyFirstNavigator(state)
            MySecondNavigator::class -> MySecondNavigator(state)
            else -> error("Unknown navigator class: $navigatorClass")
        }
    }
}
```

### CompositeStackNavigator<sup>1</sup>

The `CompositeStackNavigator` class is the composite version of the
StackNavigator, and uses a stack to model its internal state.

```kotlin
class MyCompositeNavigator(
    savedState: NavigatorState?
) : CompositeStackNavigator(savedState) {

    override fun initialStack() : List<Navigator> {
        return listOf(MyFirstNavigator())
    }

    fun onEvent() {
        push(MySecondNavigator())
    }

    override fun instantiateNavigator(
        navigatorClass: KClass<out Navigator<*>>,
        state: NavigatorState?
    ) : Navigator<out Container> {
        return when(navigatorClass) {
            MyFirstNavigator::class -> MyFirstNavigator(state)
            MySecondNavigator::class -> MySecondNavigator(state)
            else -> error("Unknown navigator class: $navigatorClass")
        }
    }
}
```

----

1: This class is available in the `ext-acorn` artifact.
