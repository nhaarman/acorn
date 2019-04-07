---
---

By default, a {{ anchor('Navigator') }} does not support state saving, although 
each of the base implementations below do.

To support {{ anchor('Navigator') }} state saving, it can implement the 
{{ anchor('SavableNavigator') }} interface.
In this method, the Navigator implementation must include everything necessary
to be able to reconstruct itself from a serialized state.
This includes the Navigator's own internal state, but also that of its 
{{ anchor('Scenes', 'Scene') }}.
When using one of the base implementations this is done mostly for you, but you
can also choose to override the `saveInstanceState` method to include your own
data.

{% highlight 'kotlin' %}
class MyNavigator(
    private val userId: String,
    savedState: NavigatorState?
) : SingleSceneNavigator(savedState) {

    override fun saveInstanceState() : NavigatorState {
        return super.saveInstanceState().also {
            it["user_id"] = userId
        }
    }

    companion object {

        fun from(savedState: NavigatorState) : MyNavigator {
            return MyNavigator(
                userId = savedState["user_id"],
                savedState = savedState
            )
        }
    }
}
{% endhighlight %}

This snippet uses an existing {{ anchor('SingleSceneNavigator') }} class which 
implements the {{ anchor('SavableNavigator') }} interface.
When the Navigator needs to have its state saved, the implementation hooks into 
the `saveInstanceState()` method and includes the `userId` in the resulting state.

Similarly when restoring the Navigator, it uses a previously saved state to 
retrieve the value of the `userId`.
