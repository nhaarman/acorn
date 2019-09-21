---
title: 'Binding transitions to SceneTransitions'

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

Just as with `ViewControllers`, Acorn needs to know when to use _your_
`SceneTransition` implementation, or to fallback to one of the default types.
A mapping needs to be created for this as well, and is done in a similar way as
with ViewControllers.

## The SceneTransitionFactory

The {{ anchor('SceneTransitionFactory') }} interface provides a
`transitionFor(previousScene: Scene<*>, newScene: Scene<*>, data: TransitionData?): SceneTransition`
function where you can define which implementation to use for a transition.
The `supports(Scene, Scene, TransitionData?): Boolean` method in that same
interface allows us to compose several SceneTransitionFactories together.

{% highlight 'kotlin' %}
interface SceneTransitionFactory {

    fun supports(previousScene: Scene<*>, newScene: Scene<*>, transitionData: TransitionData?): Boolean
    fun transitionFor(previousScene: Scene<*>, newScene: Scene<*>, transitionData: TransitionData?): SceneTransition
}
{% endhighlight %}

A simple implementation for a transition from `MyScene1` to `MyScene2` could
look like this:

{% highlight 'kotlin' %}
 class MySceneTransitionFactory : SceneTransitionFactory {

     override fun supports(previousScene: Scene<*>, newScene: Scene<*>, data: TransitionData?): Boolean {
         return previousScene is MyScene1 && newScene is MyScene2
     }

     override fun transitionFor(previousScene: Scene<*>, newScene: Scene<*>, data: TransitionData?): SceneTransition {
         return MyScene1MyScene2Transition()
     }
 }


### TransitionData

The {{ anchor('TransitionData') }} parameter can contain useful information
about the transition.
Currently the class only contains an `isBackwards` property to denote whether
the transition should be regarded as a 'step back in navigation'.
The `TransitionData` parameter can be null if the Navigator that caused the
transition does not support the notion of 'backwards', such as the
`ReplacingNavigator`.

{% highlight 'kotlin' %}
override fun transitionFor(previousScene: Scene<*>, newScene: Scene<*>, data: TransitionData?): SceneTransition {
    return when (data?.isBackwards) {
        true -> BackwardsTransition()
        else -> ForwardsTransition()
    }
}
{% endhighlight %}

## Transition extensions

Often screen transitions need some sort of 'default' behavior, such as hiding the keyboard
when leaving the screen.
Instead of having to write the code for hiding the keyboard in your transition implementation,
Acorn provides a special {{ anchor('DoBeforeTransition') }} class that can be used to
execute a common block of code before the actual transition starts.

The `SceneTransition.doOnStart` allows you to hook into this class.
For example, the `SceneTransition.hideKeyboardOnStart()` function uses the `doOnStart`
function to hide the keyboard:

{% highlight 'kotlin' %}
override fun transitionFor(previousScene: Scene<*>, newScene: Scene<*>, data: TransitionData?): SceneTransition {
    return MyTransition()
        .hideKeyboardOnStart()
}
{% endhighlight %}

The {{ anchor('DefaultSceneTransitionFactory') }} includes this keyboard hiding behavior by
default.

