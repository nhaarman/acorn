---
pageId: 'scene_management'
---

As {{ anchor('Scenes', 'Scene') }}  have absolutely no knowledge of any 
navigation flow, they will need to notify a component that does of events that 
should trigger a change in scenery.
Naturally, a {{ anchor('Navigator') }} is perfect for that.
The Navigator can listen to a Scene's events and react accordingly, for example
by switching to another Scene.

Depending on the way the flow is modelled in the Navigator, the lifecycle of its
Scenes can behave differently.  
In case of a stack for example, the Scene that is being replaced can either be
made inactive or be destroyed depending on whether a new Scene is pushed onto
the stack, or whether the Scene is popped off of the stack.
A Navigator that does not use a back stack may have completely different behavior
with respect to its Scene's lifecycles.
This means that the Navigator is completely responsible for the lifecycle of the
Scenes, and _must_ make the proper calls at the proper times.

When implementing your own Navigator instance, a few general guidelines can be
followed:

 - Scenes need to be started and stopped.
   When a Scene becomes the active Scene, a call to its `onStart` method should
   be made.
   If there already was an active Scene, its `onStop` method may be called.
 - Scenes must be destroyed.
   If a Scene has served its purpose in the Navigator, a call to its `onDestroy`
   method must be made.
   If the Scene was active at this point, a call to its `onStop` method _must be
   made before_ the call to the `onDestroy` method.
 - Scenes follow the Navigator lifecycle.
   If a Navigator enters its 'stopped' state, all of its Scene should enter their
   'stopped' state as well.
   If a Navigator is destroyed, Scenes must be destroyed as well following their
   lifecycle.

These guidelines are just that: guidelines.
If it makes sense for your Navigator to deviate from them, please do.
Just make sure you understand the consequences if you do.
