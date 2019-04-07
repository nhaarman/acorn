# ViewControllerFactories

The `ViewControllerFactory` interface provides a modular way of creating 
`ViewController` instances. 
The interface provides a `supports(Scene)` and a 
`viewControllerFor(Scene, ViewGroup)` function that can be implemented as needed.

ViewControllerFactories are used when the system needs to show the layout for a 
Scene from scratch. 
This can occur to show the initial screen to start the application with, but it 
is also used to create views after configuration changes or process deaths.
This means that for every Scene in your application, some ViewControllerFactory
instance must be able to create a ViewController for that Scene.

## Implementing `ViewControllerFactory`

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

The system uses the `ViewController.view` property to attach to the root 
ViewGroup, in this case `layout`.

## Composing ViewControllerFactories

To allow for modularity, ViewControllerFactories can naturally be composed.
Acorn provides a `ComposingViewControllerFactory` class to support this:

```kotlin
class FirstSceneViewControllerFactory : ViewControllerFactory { /* ... */ }
class SecondSceneViewControllerFactory : ViewControllerFactory { /* ... */ }

val myViewControllerFactory = ComposingViewControllerFactory.from(
    FirstSceneViewControllerFactory(),
    SecondSceneViewControllerFactory()
)
```

Of course, ViewControllerFactories can be composed indefinitely.


## ViewControllerFactory DSL

Usually when creating a ViewController it suffices to provide a mapping from a 
Scene key to a layout resource id and a wrapper function.
The ViewControllerFactory DSL provides a DSL to easily create these mappings:

```kotlin
val myViewControllerFactory = bindViews {

    bind(
        sceneKey = defaultKey<FirstScene>(),
        layoutResId = R.layout.first_scene,
        wrapper = { view: View ->
            FirstSceneViewController(view)
        }
    )

    bind(
        defaultKey<SecondScene>(),
        R.layout.second_scene,
        ::SecondSceneViewController
    )
}
```

The resulting ViewControllerFactory will inflate the `layoutResId` and invoke
the `wrapper` function with the inflated view.