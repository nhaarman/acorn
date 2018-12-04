# The View Layer

The Navigator class is in control of the Scenes that are active.
Activities can subscribe to Scene events from the Navigator class, and act 
accordingly by showing the user interface corresponding to the Scene.
This user interface can then be attached and detached from the Scene at
appropriate times.

The Activity instance provides a FrameLayout with id `android.R.id.content` 
where an application can put its views in.
Reacting to Scene changes can be as easy as replacing all views in this 
FrameLayout by the new view that represents the new Scene.
When a Scene change occurs, the Activity looks up the layout for the new Scene
to display and performs a layout transition to show the new interface.

## ViewControllers

The `Scene` class provides a `Container` parameter type that describes the 
contract its view representation should adhere to.
Scenes can define their own extensions of the Container interface to accept 
certain behavior, for example to show data or accept user input events.

```kotlin
interface MyContainer : Container {

    var text: String
    
    fun registerClickListener(f: () -> Unit)
}
```

The view layer must attach and detach instances of these Containers to the 
Scenes.
The `ViewController` interface is a special Container extension that acts as a 
wrapper around a `View` object, and can be used to create a bridge between the
Scene and the user interface.

```kotlin
class MyViewController(
    override val view: View
) : ViewController, MyContainer {

    override var text: String = ""
        set(value) {
            view.textView.text = value
        }
        
    override fun registerClickListener(f: () -> Unit) {
        view.button.setOnClickListener(f)
    }
}
```

## Transition animations

One of the great strengths of controlling the view layer this way is that you
gain full control over transition animations.
When a Scene change occurs, you can hook into the layout changing process and 
execute custom animations yourself.

You can do this by implementing the `Transition` interface and tell Acorn for 
which Scene changes it should be used.

```kotlin
interface Transition {

    fun execute(parent: ViewGroup, callback: Callback)

    interface Callback {
    
        fun attach(viewController: ViewController)
        fun onComplete(viewController: ViewController)
    }
}
```

The `execute` function provides the parent `ViewGroup` that hosts the 
application UI, and a callback instance to notify the system that the transition 
has finished.
Whatever you do in the `execute` function is up to you, as long as you provide
the callback with a `ViewController` instance to the callback that can be 
attached to the destination Scene.

The `Callback` interface also provides an additional `attach` method which can
be used to attach a ViewController to the new Scene before the transition has
ended.

## Advanced topics

To ensure the system's behavior is guaranteed, there are some advanced topics
below.
If you use the default extension artifacts, these are handled for you.

 - [The Activity lifecycle and Scene transitions](the_activity_lifecycle_and_scene_transitions)