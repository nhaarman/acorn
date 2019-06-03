---
title: 'Mapping Scenes to Layouts'

extraCss:
    - |
        inline:.scss:
        .image-preview {
            text-align: center; 
            img {
                max-width:80%;
            }    
        }
---

In a pure world, {{ anchor('Scenes','Scene') }} are completely platform agnostic. 
This means that there is absolutely no reference to any of the classes in the
Android SDK.
Unfortunately, this decoupling also means some mapping needs to exist between
different Scene implementations and the layouts that should represent these
Scenes.  
Otherwise, how would the Activity know what layout to inflate for the user?

### The {{ anchor('ViewControllerFactory') }}

The {{ anchor('ViewControllerFactory') }} is the interface that represents this
mapping.
It provides a `viewControllerFor(Scene, ViewGroup): ViewController` method that
inflates the right layout and results a proper {{ anchor('ViewController') }}
instance for the Scene.
The `supports(Scene): Boolean` method in that same interface allows us to 
compose several ViewControllerFactories together.

{% highlight 'kotlin' %}
interface ViewControllerFactory {

    fun supports(scene: Scene<*>): Boolean
    fun viewControllerFor(scene: Scene<*>, parent: ViewGroup): ViewController
}
{% endhighlight %}

A ViewControllerFactory for a single Scene `MyScene` could for example look like
this:

{% highlight 'kotlin' %}
class MySceneViewControllerFactory : ViewControllerFactory {
    
    override fun supports(scene: Scene<*>) = scene is MyScene
    
    override fun viewControllerFor(scene: Scene<*>, parent: ViewGroup) : ViewController {
        return MySceneViewController(parent.inflate(R.layout.my_scene)
    }
}
{% endhighlight %}

### {{ anchor('ProvidesView') }}

If we allow ourselves to be flexible with the 'platform agnostic' requirement,
we can store this layout 
The `ext-acorn-android` artifact provides a special {{ anchor('ProvidesView') }} 
interface that extends ViewControllerFactory and allows this:

{% highlight 'kotlin' %}
class MyScene<MyContainer> : Scene, ProvidesView {

    override fun createViewController(parent: ViewGroup): ViewController {
        return MyViewController(parent.inflate(R.layout.myscene))
    }
}
{% endhighlight %}

The {{ anchor('SceneViewControllerFactory') }} implementation can be used to 
deal with Scenes implementing this interface, saving us from having to implement
a ViewControllerFactory.

### The ViewControllerFactory DSL

To save you from having to implement the ViewControllerFactory each time, the
{{ anchor('bindViews') }} method allows you to quickly create the Scene-layout 
mapping in a DSL-like manner:

{% highlight 'kotlin' %}
val myViewControllerFactory = bindViews {

    bind(
        sceneKey = defaultKey<FirstScene>(),
        layoutResId = R.layout.first_scene,
        wrapper = { view: View ->
            FirstSceneViewController(view)
        }
    )

    // Or, shorter
    bind(
        defaultKey<SecondScene>(),
        R.layout.second_scene,
        ::SecondSceneViewController
    )
}
{% endhighlight %}

The resulting ViewControllerFactory instance will use the Scene's `key` to map
it to the proper layout, and invoke the `wrapper` function to create a 
ViewController for it.
