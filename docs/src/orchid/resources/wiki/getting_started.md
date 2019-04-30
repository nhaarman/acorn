---
---

This page provides a quick overview on getting started with Acorn.
Be sure to have the necessary dependencies included as shown in 
{{ anchor('Setup') }}.

Acorn provides several helper classes that can get you started quicky.
The easiest way is to have your main Activity extend from 
{{ anchor('AcornActivity') }} or {{ anchor('AcornAppCompatActivity') }}
and implement the `provideNavigatorProvider` method:

{% highlight 'kotlin' %}
object MyNavigatorProvider : NavigatorProvider { /* ... */ }

class MainActivity : AcornActivity() {

    override fun provideNavigatorProvider() : NavigatorProvider {
        return MyNavigatorProvider
    }
}
{% endhighlight %}

If you can't or don't want to extend from AcornActivity, you can use the 
{{ anchor('AcornActivityDelegate') }} class.

### Scene/Container/ViewController

You can create a very simple {{ anchor('Scene') }} to get started by 
implementing the {{ anchor('BaseSavableScene') }} class and implementing the 
{{ anchor('ProvidesView') }} interface:

{% highlight 'kotlin' %}
interface MyContainer : Container

class MyViewController(override val view: View) : RestorableViewController, MyContainer

class MyScene : BaseSavableScene<MyContainer>(null), ProvidesView {

    override fun createViewController(parent: ViewGroup) : ViewController {
        val layout = LayoutInflater.from(parent.context)
            .inflate(R.layout.my_scene, parent, false) 
            
        return MyViewController(layout)
    }
}
{% endhighlight %}

Passing `null` to the {{ anchor('BaseSavableScene') }} constructor basically 
implies the Scene does not support state restoration.  
See {{ anchor('Scenes') }} for more information on how to create Scenes.

### Navigator

The simplest of Navigators is one that extends the 
{{ anchor('SingleSceneNavigator') }} class:

{% highlight 'kotlin' %}
class MyNavigator : SingleSceneNavigator(null) {

    override fun createScene(state: SceneState?) : Scene<out Container> {
        return MyScene()
    }
}
{% endhighlight %}

Passing `null` to the {{ anchor('SingleSceneNavigator') }} constructor basically 
implies the Navigator does not support state restoration.  
See {{ anchor('Navigators') }} for more information on how to create Navigators.

### NavigatorProvider

You will need to implement the {{ anchor('NavigatorProvider') }} interface to 
let Acorn know which {{ anchor('Navigator') }} to use.  
The {{ anchor('AbstractNavigatorProvider') }} class provides a base 
implementation you can use:

{% highlight 'kotlin' %}
object MyNavigatorProvider : AbstractNavigatorProvider<MyNavigator>() {

    override fun createNavigator(savedState: NavigatorState?) : MyNavigator {
        return MyNavigator()
    }
}
{% endhighlight %}

The {{ anchor('NavigatorProvider') }} needs to be cached between Activity instances.  

See [Navigators](navigators) for more information.

## Configuration

The setup in the previous section falls back to the default configuration Acorn
provides.
There are a couple of things you can customize.

### ViewControllerFactory

By default, Acorn relies on Scenes implementing the 
{{ anchor('ViewControllerFactory') }} interface for creating 
{{ anchor('ViewController') }} instances.
You can also supply your own ViewControllerFactory instance to Acorn by 
overriding the `provideViewControllerFactory` function in 
{{ anchor('AcornActivity') }}, or passing it to the 
{{ anchor('AcornActivityDelegate') }}.

See [ViewControllerFactories](viewcontrollerfactories) for more information on
creating ViewControllerFactories.

### TransitionFactory

Acorn will use default transition animations to animate transition between 
Scenes. 
If you want to provide custom transition animations, you need to implement the
{{ anchor('TransitionFactory') }} interface and supply it to Acorn by overriding 
the `provideTransitionFactory` function in {{ anchor('AcornActivity') }}, or 
passing it to the {{ anchor('AcornActivityDelegate') }}.

See [Transition Animations](transition_animations) for more information on
creating TransitionFactories.

### ActivityControllerFactory

If your application needs to start external Activities you need to implement the
{{ anchor('ActivityControllerFactory') }} interface and supply it to Acorn. 
You can do this by overriding the `provideActivityControllerFactory` function in
{{ anchor('AcornActivity') }}, or by passing it to the 
{{ anchor('AcornActivityDelegate') }}.
