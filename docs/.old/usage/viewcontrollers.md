# ViewControllers

The `ViewController` interface extends the `Container` interface to provide a
bridge between the view layer and the `Scene`, 'controlling' the view.
For every Scene and its Container a ViewController implementation needs to be 
available to attach to the Scene.


## Implementing the ViewController

The ViewController interface contains a `view` property that provides access to
the root of the Scene layout.
When creating a ViewController for a specific Scene/Container combination, it 
should implement its Container interface:

```kotlin
interface MyContainer {

    var text: Styyring
    
    fun registerClickListener(f: () -> Unit)
}

class MyViewController(
    override val view: ViewGroup
) : ViewController, MyContainer {

    override var text: String = ""
        set(value) {
            view.findViewById<TextView>(R.id.textView).text = value
        }
        
    override fun registerClickListener(f: () -> Unit) {
        view.findViewById<Button>(R.id.button).setOnClickListener { f() }
    }
}
```

## Restoring view state

To support view state restoration your `Container` interface needs to implement 
the `RestorableContainer` interface, which provides the necessary 
`saveInstanceState` and `restoreInstanceState` methods.
ViewController implementations are responsible for implementing these methods
properly, by saving and restoring the view's hierarchy state:

```kotlin
interface MyContainer : RestorableContainer {

    var text: String
}

class MyViewController(
    override val view: View
) : ViewController, MyContainer {

    override var text: String = ""
        set(value) {
            view.findViewById<TextView>(R.id.textView).text = value
        }

    override fun saveInstanceState() = containerState {
        it.hierarchyState = view.saveHierarchyState()
    }

    override fun restoreInstanceState(bundle: ContainerState) {
        bundle.hierarchyState?.let { view.restoreHierarchyState(it) }
    }
    
    companion object {
    
       private var ContainerState.hierarchyState: SparseArray<Parcelable>?
           get() = get("hierarchy_state")
           set(value) {
               setUnchecked("hierarchy_state", value)
           }
    } 
}
```

Doing this for every view can be tedious, and thus Acorn provides a special 
`RestorableViewController` interface that implements this for you.

```kotlin
class MyViewController(
    override val view: View
) : RestorableViewController, MyContainer {

    override var text: String = ""
        set(value) {
            view.findViewById<TextView>(R.id.textView).text = value
        }
}
```

If you need to save additional custom state, you can invoke the super method.

#### Kotlin Android Extensions

The [Kotlin Android Extensions](https://kotlinlang.org/docs/tutorials/android-plugin.html)
plugin provides a view binding functionality to save you from tedious 
`findViewById` calls.
The `RestorableViewController` interface also extends the `LayoutContainer`
interface this plugin provides, saving you some hassle.

```kotlin
class MyViewController(
    override val view: View
) : RestorableViewController, MyContainer {

    override var text: String = ""
        set(value) {
            view.textView.text = value
        }
}
```

## Creating ViewControllers

When a new Scene becomes active, the view layer needs to obtain a reference to 
the proper ViewController implementation.  
There are two main ways of providing the system with a ViewController instance:
using the `ViewControllerFactory` interface, or by providing a custom 
`Transition` implementation.

### ViewControllerFactory

The `ViewControllerFactory` interface is used when the system needs to show the 
layout for a Scene from scratch. 
This can occur to show the initial screen to start the application with, but it 
is also used to create views after configuration changes or process deaths.
This means that for every Scene in your application, some ViewControllerFactory
instance must be able to create a ViewController for that Scene.

The ViewControllerFactory interface provides the `supports(Scene)` and
`viewControllerFor(Scene, ViewGroup)` functions.
The `supports` function is used to test whether the implementation is capable of
creating a ViewController instance, the `viewControllerFor` function is invoked
to actually create the instance.
In the `viewControllerFor` function the implementation needs to inflate the 
proper layout and return an appropriate ViewController instance.

The ViewControllerFactory can be implemented as follows:

```kotlin
class MyViewControllerFactory : ViewControllerFactory {

    override fun supports(scene: Scene<*>): Boolean {
        return scene.key == SceneKey.defaultKey<MyScene>()
    }
    
    override fun viewControllerFor(scene: Scene<*>, parent: ViewGroup): ViewController {
        val layout = LayoutInflater.from(parent.context)
            .inflate(R.layout.my_layout, parent, false)
    
        return MyViewController(layout)
    }
}
```

ViewControllerFactories can be composed, and thus modularity remains guaranteed.
See [ViewControllerFactories](viewcontrollerfactories) for details on creating ViewControllerFactories.

#### ProvidesView

Having to maintain a decoupled mapping from Scenes to ViewControllers can be 
error-prone, despite the fact that it provides true decoupling from the Android
framework.
For convenience, Scenes can also implement the `ViewControllerFactory` interface
themselves, keeping related code together.

The `ProvidesView` interface extends the `ViewControllerFactory` to make this
even easier and take away some of the boilerplate code.

```kotlin
class MyScene : Scene<MyContainer>, ProvidesView {

    override fun createViewController(parent: ViewGroup) : ViewController {
        val layout = LayoutInflater.from(parent.context)
            .inflate(R.layout.my_layout, parent, false)
    
        return MyViewController(layout)
    }
}
```

### Transition Animations

The ViewControllerFactory is only used if the Activity needs to display the 
Scene from scratch, or if there is no custom transition animation defined to 
animate the transition between the previous and the new Scene.
When a transition animation _is_ defined for the Scene transition, the 
`Transition` implementation is responsible for creating the ViewController.

```kotlin
class MyTransition : Transition {

    override fun execute(parent: ViewGroup, callback: Callback) {
        /* ... */
    
        val newLayout: View = /* ... */
        
        /* ... */
    }
    
    private fun onAnimationEnd(newLayout: View, callback : Callback) {
        callback.onComplete(MyViewController(newLayout))
    }
}
```
