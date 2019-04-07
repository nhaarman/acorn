# Transition animations

Transition animations provide context and visual connections between different
states through motion and transformations between common elements[^1].
When navigating from one Activity to another, Android performs a default
window animation to indicate a change of scenery.
Overriding the default animations can be a powerful tool to provide this visual
connection between scenes, and should be a carefully designed system.

There are two main types of transitions:

 - Screen transitions
 - Shared element transitions
 
For each of these transition types, it is possible to go forward to a new screen,
or backward to a previous screen.

### Screen transitions

Screen transitions are transitions where the entire layout is replaced by
another.
Often, default animations can be used for the transition, such as fading in the
new layout from the bottom, or sliding in from the right.
Acorn provides the `FadeInFromBottomTransition` and `FadeOutToBottomTransition`
classes that handle these simple default cases.

### Shared element transitions

You can also choose to leave the main layout intact, and only add new views to
it, while removing others.
This leaves you with full control on whether to keep a toolbar or navigation
drawer in place, while replacing only a content view.

It is also possible to actually start a very specific animation from a specific
point, such as a button.
When executing the transition you need to know details on what was clicked to
determine how to start the animation.
Likewise when navigating back to the previous screen, you need to know how to
reverse the animation.

---

[^1]: https://developer.android.com/training/transitions/start-activity
