---
title: 'Transition Animations'

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

Providing the right transition animations between your Scenes can greatly
enhance the user experience.
Pressing an image in a list of image to view its details
For example, when navigating from a grid of images to a detail page of one of 
those images, a 'shared element transition' could be created to animate that
image from its location and size in the first screen to its new location and
size in the second screen.
These kinds of animations provide great contextual value to your users.

Acorn allows you to provide custom {{ anchor('SceneTransition') }} 
implementations between specific Scenes.
By giving you access the the absolute root `ViewGroup` in the Activity's view
hierarchy (usually `android.R.id.content`), Acorn provides you with full control
over your transition animations.

## The {{ anchor('SceneTransition') }} interface

To create your own transition animation, you need to implement the 
SceneTransition interface.
This interface includes an `execute(ViewGroup, Callback)` method which provides
you with the root ViewGroup of your Activity, and a `Callback` instance to let 
Acorn know the transition is finished.

The `MySceneTransition` class below shows you how you can create a simple 
fade-in/fade-out transition between to layouts:

{% highlight 'kotlin' %}
class MySceneTransition : SceneTransition {

    override fun execute(parent: ViewGroup, callback: Callback) {
        // By default, parent is the android.R.id.content Framelayout that hosts
        // your layouts. It's only element is the root of the 'old' Scene.
        val oldLayout = parent.getChildAt(0)
    
        // We can inflate our new layout exactly as we want it
        val newLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.new_view, parent, false)
        
        // Starting out with a completely transparent view..
        newLayout.alpha = 0f
        parent.addView(newLayout)
        
        // .. animating the old view out..
        oldLayout.animate()
            .alpha(0f)
            
        // .. and the new view in.
        newLayout.animate()
            .alpha(1f)
            .withEndAction {
                // Take care to clean up our layout
                parent.removeView(oldLayout)
                
                // Notify Acorn the transition has completed.
                callback.onComplete(NewViewController(parent))
            }
    }
}
{% endhighlight %}

## The Callback interface

The `SceneTransition.Callback` interface provides two methods: `attach` and 
`onComplete`.  
If you implement your own SceneTransition, you're required to invoke 
`Callback#onComplete` when the transition has finished. 
If you fail to do this, the system may fail to execute future Scene transitions.

The `Callback#attach` function can be invoked _during_ the transition, and 
attaches the ViewController to the Scene before the transition has ended.
This can be useful if your Scene needs to populate the View with certain data 
and doing this only after the transition has finished leads to unwanted 
'popping' results.

## Meaningful transitions

Creating meaningful transitions can greatly enhance the user experience of your
application.
Chris Horner gave an [excellent talk](https://www.youtube.com/watch?v=9Y5cbC5YrOY)
at Droidcon SF 2017 about Android's 
[Transition framework](https://developer.android.com/training/transitions/) 
that shows you how to create these transition animations.
