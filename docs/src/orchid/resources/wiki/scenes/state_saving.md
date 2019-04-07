---
---

By default, a {{ anchor('Scene') }} does not support state saving, nor does it 
save or restore view hierarchy state such as user input or scroll positions.
From a user's perspective however, it is important that you _do_ save your 
Scene's state: not only can an Android application be killed at any time after which it
should be properly restored, but a lost scroll position on orientation change 
can also be very annoying.

### View hierarchy state saving

During the lifetime of a {{ anchor('Scene') }} it can happen that it receives 
multiple calls to `attach` and `detach`.
Often, subsequent calls to `attach` will have fresh instances of the 
{{ anchor('Container') }} passed to it, losing any view hierarchy state.
The Activity being recreated due to a device orientation change is one example 
of this.

The Scene can save and restore the container state between these subsequent
calls to `attach` if the Container type implements the 
{{ anchor('RestorableContainer') }} interface.
This interface provides a `saveInstanceState` method and a
`restoreInstanceState` method, which the Scene can use to restore the container 
state:

{% highlight 'kotlin' %}
interface MyContainer: RestorableContainer

class MyScene : Scene<MyContainer> {

    private var containerState: ContainerState? = null

    override fun attach(v: MyContainer) {
        containerState?.let { v.restoreInstanceState(it) }
    }

    override fun detach(v: MyContainer) {
        containerState = v.saveInstanceState()
    }
}
{% endhighlight %}

This {{ anchor('Scene') }} will now save the {{ anchor('Container') }}'s 
instance state when it gets detached from the Scene.
A new Container instance that gets attached to the Scene will receive the 
previous Container's state and can restore the view hierarchy.

### Scene state saving

To have your {{ anchor('Scene') }}'s state saved to prepare for process deaths, 
implement the {{ anchor('SavableScene') }} interface.
This interface provides a `saveInstanceState()` method that will be called at
appropriate times.  
When saving a Scene, you generally want to save as little as possible, but just
enough to be able to reconstruct it after process death.
Think of saving a `userId` value, but not the entire `User` instance.  

Next to this, you can also choose to save the view hierarchy state with it, if
the {{ anchor('Container') }} supports it:

{% highlight 'kotlin' %}
interface MyContainer: RestorableContainer

class MyScene(
    private val userId: String
) : Scene<MyContainer>, SavableScene {

    private var containerState: ContainerState? = null

    override fun attach(v: MyContainer) {
        containerState?.let { v.restoreInstanceState(it) }
    }

    override fun detach(v: MyContainer) {
        containerState = v.saveInstanceState()
    }

    override fun saveInstanceState(): SceneState {
        return sceneState {
            it["user_id"] = userId
            it["container_state"] = containerState
        }
    }
}
{% endhighlight %}

This snippet extends the previous snippet by implementing the `SavableScene`
interface and overriding the `saveInstanceState()` method.

### Scene state restoration

After a process death, a {{ anchor('Navigator') }} class can ask you to restore 
your {{ anchor('Scene') }} from a saved state 
(see {{ anchor('Navigators', 'pageId=navigators') }}).  
If your Scene implements {{ anchor('SavableScene') }}, you will be passed the 
{{ anchor('SceneState') }} instance that you returned from `saveInstanceState()`.
You can then pull out everything you need to be able to restore the Scene.

If we again expand on the previous example, we can add restoration support by
implementing a `create` method in the Scene's `companion object` as follows:

{% highlight 'kotlin' %}
interface MyContainer: RestorableContainer

class MyScene(
    private val userId: String,
    savedState: SceneState? = null
) : Scene<MyContainer>, SavableScene {

    private var containerState: ContainerState? = savedState?.get("container_state")

    override fun attach(v: MyContainer) {
        containerState?.let { v.restoreInstanceState(it) }
    }

    override fun detach(v: MyContainer) {
        containerState = v.saveInstanceState()
    }

    override fun saveInstanceState(): SceneState {
        return sceneState {
            it["user_id"] = userId
            it["container_state"] = containerState
        }
    }

    companion object {

        fun create(savedState: SceneState) : MyScene {
            return MyScene(
                savedState["user_id"],
                savedState
            )
        }
    }
}
{% endhighlight %}

Now, we have restored our `userId` from the saved state, as well as any view
state that was saved with it.
