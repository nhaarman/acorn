# Transition animations 

Providing the right transition animations between your Scenes can greatly
enhance the user experience.
Acorn gives you access to the root `ViewGroup` to allow you to have full control
over your transition animations.

## The `Transition` interface

To create your own transition animation, you need to implement the `Transition`
interface.
This interface includes an `execute(ViewGroup, Callback)` method which provides
you with the root ViewGroup of your Activity, and a `Callback` to let the system
know the transition is finished.

The `MyTransition` class below shows you how you can create a simple 
fade-in/fade-out transition between to layouts:

```kotlin
class MyTransition : Transition {

    override fun execute(parent: ViewGroup, callback: Callback) {
        val oldLayout = parent.getChildAt(0)
    
        val newLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.new_view, parent, false)
        
        newLayout.alpha = 0f
        parent.addView(newLayout)
        
        oldLayout.animate()
            .alpha(0f)
            
        newLayout.animate()
            .alpha(1f)
            .withEndAction {
                parent.removeView(oldLayout)
                callback.onComplete(NewViewController(parent))
            }
    }
}
```

## The Callback interface

The `Transition.Callback` interface provides two methods: `attach` and 
`onComplete`.  
If you implement your own Transition, you're required to invoke 
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
