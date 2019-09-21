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
        return MySceneViewController(parent.inflate(R.layout.my_scene))
    }
}
{% endhighlight %}

### {{ anchor('ProvidesView') }}

If we allow ourselves to be flexible with the 'platform agnostic' requirement,
we can allow the Scene itself to define the layout it wants to inflate.
You lose a little of purity this way, but gain a lot by not having to write
boilerplate.

The `ext-acorn-android` artifact provides a special {{ anchor('ProvidesView') }}
interface that extends ViewControllerFactory and allows this:

{% highlight 'kotlin' %}
class MyScene<MyContainer> : Scene, ProvidesView {

    override fun createViewController(parent: ViewGroup): ViewController {
        return MyViewController(parent.inflate(R.layout.myscene))
    }
}
{% endhighlight %}

Since `MyScene` now becomes its own ViewControllerFactory we are now able to
skip the creation of a ViewControllerFactory, and use the Scene instance directly.
The {{ anchor('SceneViewControllerFactory') }} class provided by the library can
be used to deal with these kind of Scenes, and is used by default if you use one
of the {{ anchor('AcornActivity') }} classes.

### The ViewControllerFactory DSL

In some cases this `ProvidesView` solution just doesn't fit, and you still need
to write a custom ViewControllerFactory implementation.
The {{ anchor('bindViews') }} method provides access to a convenient DSL to
quickly create the Scene-layout mapping:

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
