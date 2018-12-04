# The Activity Lifecycle and Scene Transitions

The Activity lifecycle determines whether there is something to show to the user.
Especially, when the Activity is in the 'started' state (between onStart and
onStop), one can assume the Activity is visible to the user.
Whenever the Activity is 'stopped', the UI is not visible anymore and the UI
should be detached from the Scene.

The Navigator class can notify the Activity of a change in Scenes, upon
which it should react appropriately.
Most of the time, this involves replacing the current View by a new instance
suitable for the new active Scene.

## Managing Scene transitions

In a perfect world, view transitions are atomic operations. However, often these
transitions come with animations that take time, during which the Activity can
enter the 'stopped' state.
A naive implementation would attach the view to the Scene when a transition
animation has finished, without checking if the Activity is still available.

The `ActivityState` class provides a default implementation that can be used to
handle Scene transitions.

### Details of Scene transitions

A Scene transition typically consists of the following phases:

 - Detaching any view from the current Scene, if available;
 - Inflating the new Scene's layout and perform any animations on view;
 - Attach the newly created view to the new Scene.

Since animations do take time, a couple of scenarios come to mind:

 1. A Scene transition starts and ends during the Activity's 'Started' state;

    This is the 'happy' path of the transition: the new view can be attached to
    the view when the transition animation ends.

 2. A Scene transition start in the Activity's 'Started' state, but ends in its
   'Stopped' state;

    When this happens, the new view must _not_ be attached to the Scene at the
    end of the transition. If possible, the transition animation should be
    cancelled.

 3. A change of Scene occurs when the Activity is in the 'Stopped' state;

    In this case, no view inflation or transition animation occurs until the
    Activity enters the 'Started' state again.

 4. The Activity enters the 'stopped' state between a scene's transition
    notification and the start of the transition animation. This could happen
    when multiple Scene transitions are happening in a short time, causing the
    transition animations to be scheduled for some time in the future.

A couple of axioms should make this feasible:

 - When a Scene change occurs during the Activity's 'Started' state, the previous
   Scene gets its view detached.

 - When a Scene change occurs during the Activity's 'Started' state, an animation
   is started to show the new view.

 - When a transition animation ends in the Activity's 'Started' state, the new
   Scene gets its new view attached.

 - When the Activity stops during an animation, the animation is cancelled if
   possible. When the animation ends, the Scene does not get a view attached.

 - When the Activity starts, the view corresponding to the current active Scene
   is inflated and shown immediately, without any transition animation.

It is also possible that multiple Scene transitions happen in a short time,
causing transition animations to be queued.
Implementations can choose to drop Scene transitions in this case.


