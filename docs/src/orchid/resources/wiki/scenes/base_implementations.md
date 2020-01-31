---
---

The samples in the previous article show how you can create 
{{ anchor('Scenes', 'Scene') }} with just the basic interfaces, but there is a 
lot of boilerplate setup taking place.  
Fortunately, there are some base implementations that take some of this
boilerplate out of your hands.

### {{ anchor('BasicScene') }}<sup>1</sup>

The {{ anchor('BasicScene') }} class is a basic abstract Scene class that 
provides most common functionality, such as providing a handle to the currently
attached view, saving view state between subsequent `attach` calls, and provides
a default `saveInstanceState()` implementation for easy state saving.
It does not implement {{ anchor('SavableScene') }} by itself, but subclasses of 
BasicScene can opt in to this default implementation by explicitly implementing
the 'SavableScene' interface.
If we take the sample from Scene state restoration before and re-implement it
using the BasicScene class, we get the following:

{% highlight 'kotlin' %}
interface MyContainer: RestorableContainer

class MyScene(
    private val userId: String,
    savedState: SceneState? = null
) : BasicScene<MyContainer>(savedState),
    SavableScene {

    override fun saveInstanceState(): SceneState {
        return super.saveInstanceState().also {
            it["user_id"] = userId
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

We now only have to deal with saving and restoring our `userId`, and let the
BaseSavableScene handle the rest.

### {{ anchor('RxScene') }}<sup>2</sup>

The {{ anchor('RxScene') }} abstract class extends the 
{{ anchor('BasicScene') }} class and provides helper functions for working 
with Rx streams.

### {{ anchor('LifecycleScene') }}<sup>3</sup>

The {{ anchor('LifecycleScene') }} abstract class extends the 
{{ anchor('BasicScene') }} class and implements the 
`androidx.lifecycle.LifecycleOwner` interface.

----

1: This class is available in the `ext-acorn` artifact.  
2: This class is available in the `ext-acorn-rx` artifact.  
3: This class is available in the `ext-acorn-android-lifecycle` artifact.  
