# Getting Started

This page provides a quick overview on getting started with Acorn.
Be sure to have the necessary dependencies included as shown in [Setup](setup).

Acorn provides several helper classes that can get you started quicky.
The easiest way is to have your MainActivity extend from `AcornActivity` or 
`AcornAppCompatActivity` and implement the `provideNavigatorProvider` 
function:

```kotlin
object MyNavigatorProvider : NavigatorProvider { /* ... */ }

class MainActivity : AcornActivity() {

    override fun provideNavigatorProvider() : NavigatorProvider {
        return MyNavigatorProvider
    }
}
```

If you can't or don't want to extend from `AcornActivity`, you can use the 
`AcornActivityDelegate` class.

### Scene/Container/ViewController

You can create a very simple Scene to get started by implementing the 
`BaseSavableScene` class and implementing the `ProvidesView` interface:

```kotlin
interface MyContainer : Container

class MyViewController(override val view: View) : RestorableViewController, MyContainer

class MyScene : BaseSavableScene<MyContainer>(null), ProvidesView {

    override fun createViewController(parent: ViewGroup) : ViewController {
        val layout = LayoutInflater.from(parent.context)
            .inflate(R.layout.my_scene, parent, false) 
            
        return MyViewController(layout)
    }
}
```

Passing `null` to the `BaseSavableScene` constructor basically implies the Scene 
does not support state restoration.  
See [Scenes](scenes) for more information on how to create Scenes.

### Navigator

The simplest of Navigators is one that extends the `SingleSceneNavigator` class:

```kotlin
class MyNavigator : SingleSceneNavigator(null) {

    override fun createScene(state: SceneState?) : Scene<out Container> {
        return MyScene()
    }
}
```

Passing `null` to the `SingleSceneNavigator` constructor basically implies the
Navigator does not support state restoration.  
See [Navigators](navigators) for more information on how to create Navigators.

### NavigatorProvider

You will need to implement the `NavigatorProvider` interface to let Acorn know
which `Navigator` to use.  
The `AbstractNavigatorProvider` class provides a base implementation you can 
use:

```kotlin
object MyNavigatorProvider : AbstractNavigatorProvider<MyNavigator>() {

    override fun createNavigator(savedState: NavigatorState?) : MyNavigator {
        return MyNavigator()
    }
}
```

The NavigatorProvider needs to be cached between Activity instances.  
See [Navigators](navigators) for more information.

## Configuration

The setup in the previous section falls back to the default configuration Acorn
provides.
There are a couple of things you can customize.

### ViewControllerFactory

By default, Acorn relies on Scenes implementing the `ViewControllerFactory` 
interface for creating `ViewController` instances.
You can also supply your own ViewControllerFactory instance to Acorn by 
overriding the `provideViewControllerFactory` function in `AcornActivity`, or 
passing it to the `AcornActivityDelegate`.

See [ViewControllerFactories](viewcontrollerfactories) for more information on
creating ViewControllerFactories.

### TransitionFactory

Acorn will use default transition animations to animate transition between 
Scenes. 
If you want to provide custom transition animations, you need to implement the
`TransitionFactory` interface and supply it to Acorn by overriding the 
`provideTransitionFactory` function in `AcornActivity`, or passing it to the
`AcornActivityDelegate`.

See [Transition Animations](transition_animations) for more information on
creating TransitionFactories.

### ActivityControllerFactory

If your application needs to start external Activities you need to implement the
`ActivityControllerFactory` interface and supply it to Acorn. 
You can do this by overriding the `provideActivityControllerFactory` function in
`AcornActivity`, or by passing it to the `AcornActivityDelegate`.