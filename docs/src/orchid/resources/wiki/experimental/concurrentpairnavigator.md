---
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

The {{ anchor('ConcurrentPairNavigator') }} is a stacking
{{ anchor('Navigator') }} that allows up to two 
{{ anchor('Scenes','Scene') }} in its stack.
Whenever a second Scene is stacked upon the initial Scene, both the initial
Scene and the second Scene will _simultaneously_ be in their 'started' states,
hereby differing from navigators such as the {{ anchor('StackNavigator') }} 
which only allow a single Scene in the 'started' state.

This Navigator can come in useful when implementing complex overlays that 
warrant their own Scene instance:

![]({{ 'wiki/experimental/media/overlay_example.gif'|asset }})
{.image-preview}

_A 'complex overlay' here means an overlay that is _not_ a simple dialog.
The ConcurrentPairNavigator allows for great flexibility to define your layout
and its transitions perfectly, but comes with a fair amount of overhead.
If you want to display a simple dialog showing a message or asking a question,
use an AlertDialog instead._

## Usage

_You can find a working sample project demonstrating usage of this class 
[here](https://github.com/nhaarman/Acorn/tree/master/samples/hello-concurrentpairnavigator)_

To be able to allow two {{ anchor('Scenes','Scene') }} to be active at once, the
{{ anchor('ConcurrentPairNavigator') }} wraps the two Scenes in a 
{{ anchor('CombinedScene') }} instance with the key of the second Scene.
This special Scene implementation ignores any lifecycle calls, and ensures both
Scenes receive the proper {{ anchor('Container') }} instances in `attach` and
`detach`.

It does this by accepting a special {{ anchor('CombinedContainer') }} 
specialization of the Container interface, which allows access to the two sub
Containers.
It is then the responsibility of the UI layer to properly provide instances of
the CombinedContainer interface.

In the following sections you can find out how to make use of the 
ConcurrentPairNavigator.

### The initial Scene

The {{ anchor('ConcurrentPairNavigator') }} requires a first, initial 
{{ anchor('Scene') }}. 
This Scene is just like any other regular Scene, and can implement 
{{ anchor('ProvidesView') }} if you want to. 

For example, we can create the first Scene from above using RxJava like this<sup>1</sup>:

{% highlight 'kotlin' %}
interface FirstSceneContainer : Container {

    var count: Long

    /** Registers a listener for when an action is clicked */
    fun onActionClicked(f: () -> Unit)
}

/**
 * Displays a counter value that continuously increases starting when this Scene
 * is started, until this Scene is destroyed.
 */
class FirstScene(
    private val listener: Events,
    scheduler: Scheduler = AndroidSchedulers.mainThread()
) : RxScene<FirstSceneContainer>(null), ProvidesView {

    /**
     * Emits a continuously increasing stream of Longs every 100 milliseconds,
     * starting at the first subscription, until the Scene is destroyed.
     */
    private val counter: Observable<Long> = Observable
        .interval(0, 100, TimeUnit.MILLISECONDS, scheduler)
        .replay(1).autoConnect(this)

    /**
     * Conveniently provides a View and ViewController for this Scene.
     */
    override fun createViewController(parent: ViewGroup): ViewController {
        return FirstSceneViewController(parent.inflate(R.layout.first_scene))
    }

    override fun onStart() {
        super.onStart()

        // Subscribe to the counter, updating the container when available.
        disposables += counter
            .combineWithLatestView()
            .subscribe { (count, container) ->
                container?.count = count
            }
    }

    override fun attach(v: FirstSceneContainer) {
        super.attach(v)
        
        // Registers a listener with the container.
        v.onActionClicked { listener.actionClicked() }
    }

    interface Events {

        /**
         * Invoked when this Scene's action is clicked.
         */
        fun actionClicked()
    }
}
{% endhighlight %}

As mentioned, there is nothing special about this Scene that makes it suitable
for the ConcurrentPairNavigator.

### The second Scene

In our example from above we introduce a new layout that partially overlays the
first Scene, while still keeping our first Scene active.
This overlay is backed by a second Scene, which can be implemented like so:

{% highlight 'kotlin' %}
interface SecondSceneContainer : Container {

    /** Registers a listener for when the 'back' action is clicked */
    fun onBackClicked(f: () -> Unit)
}

/**
 * Displays a simple 'back' button.
 */
class SecondScene(
    private val listener: Events
) : Scene<SecondSceneContainer> {

    override fun attach(v: SecondSceneContainer) {
    
        // Registers a listener with the container.
        v.onBackClicked { listener.onBackClicked() }
    }

    interface Events {

        /**
         * Invoked when this Scene's 'back' action is clicked.
         */ 
        fun onBackClicked()
    }
}
{% endhighlight %}

This Scene does **not** implement {{ anchor('ProvidesView') }}, as this Scene has
too little context about _how_ to provide this container.
For example, it actually doesn't know it is being displayed using an overlay, and
does not know how to provide the entire layout.
More on this in {{ anchor('"Providing the container"') }}.

### The ConcurrentPairNavigator 

Now we can implement the {{ anchor('ConcurrentPairNavigator') }} class to tie 
our Scenes together:

{% highlight 'kotlin' %}
class HelloConcurrentPairNavigator : ConcurrentPairNavigator(null) {

    /**
     * Creates the first Scene when this Navigator is initialized.
     */
    override fun createInitialScene(): Scene<out Container> {
        return FirstScene(FirstSceneListener())
    }

    override fun instantiateScene(sceneClass: KClass<out Scene<*>>, state: SceneState?): Scene<out Container> {
        // Normally you should implement this as well.
        // For this example however, restoring is omitted.
        error("Not supported")
    }

    private inner class FirstSceneListener : FirstScene.Events {

        /**
         * Pushes a SecondScene instance on the stack.
         */
        override fun actionClicked() {
            push(SecondScene(SecondSceneListener()))
        }
    }

    private inner class SecondSceneListener : SecondScene.Events {

        /**
         * Pops the SecondScene instance from the stack.
         */
        override fun onBackClicked() {
            pop()
        }
    }
}
{% endhighlight %}

Whenever the `FirstSceneListener`'s `actionClicked` function is invoked, it 
pushes a new `SecondScene` instance on the stack.
This triggers the Navigator to emit a `CombinedScene` instance to its listeners.

When the `SecondSceneListener`'s `onBackClicked` function is invoked, the 
`SecondScene` is popped from the stack—leading to the Navigator emitting
the `FirstScene` to its listeners again.

### Providing the layouts

Now this is where things get tricky and a bit cumbersome.
As with all Scenes, there are three use cases to support when displaying their 
layouts:

 - Displaying as part of a Scene transition from the _first_ to the _second_ Scene;
 - Displaying as part of a Scene transition from the _second_ to the _first_ Scene;
 - Displaying from scratch, for example when a fresh Activity appears.

Usually when creating a Scene you can cover both of these by providing a simple
ViewController, for example using the ProvidesView interface.
The second use case is automatically covered by a default transition which 
replaces the entire layout with the new one.
For the first Scene this is also the case.  
In the case of a CombinedScene however, using ProvidesView for the second 
Scene isn't gonna cut it: the second Scene's layout is only a subset of the 
actual layout that needs to be shown. In our example case this would be the
card at the bottom of the view.

Therefore all these use cases need to be handled manually.

#### - The ViewController

First though, we need to provide a ViewController implementation.
In this case we implement the CombinedContainer implementation:

{% highlight 'kotlin' %}
@OptIn(ExperimentalConcurrentPairNavigator::class)
class FirstSecondViewController(
    override val view: ViewGroup
) : ViewController, CombinedContainer {

    override val firstContainer: Container by lazy {
        FirstSceneViewController(view.firstSceneRoot)
    }

    override val secondContainer: Container by lazy {
        SecondSceneViewController(view.secondSceneRoot)
    }
}
{% endhighlight %}

This allows Acorn to bind the correct part of the layout to the proper
Scenes.

#### - Displaying as part from a Scene transition from the _first_ to the _second_ Scene

This is the case the user will see most of the time, when you call 
ConcurrentPairNavigator#push with your second Scene instance.
When this happens, the navigator will transition from the  `FirstScene` instance
to a CombinedScene instance. 
This CombinedScene takes the `key` from the `SecondScene`, so this can be used
to define a transition animation.

This transition animation can be implemented as any other transition animation.
The only difference is that you don't remove the first Scene's layout at the end
of the transition.
For example, the transition in the GIF above is implemented as such:

{% highlight 'kotlin' %}
object FirstSecondTransition : Transition {

    override fun execute(parent: ViewGroup, callback: Transition.Callback) {
        // Inflate and add the overlay to the layout
        val secondScene = parent.inflateView(R.layout.second_scene)
        parent.addView(secondScene)

        // Immediately create and attach the combined view controller.
        // This allows our first Scene to keep communicating with the
        // view seamlessly.
        val viewController = FirstSecondViewController(parent)
        callback.attach(viewController)

        parent.doOnPreDraw {
            // Animate the dark overlay
            secondScene.overlayView
                .apply {
                    alpha = 0f
                    animate().alpha(1f)
                }

            // Animate the card view
            secondScene.cardView.apply {
                translationY = height.toFloat()
                animate().translationY(0f)
                    .withEndAction {
                        // Complete the transition
                        callback.onComplete(viewController)
                    }
            }
        }
    }
}
{% endhighlight %}

This transition can be registered using the first and second Scene's key as
mentioned before, for example using the transitionFactory DSL:

{% highlight 'kotlin' %}
transitionFactory(viewControllerFactory) {
    (FirstScene.key to SecondScene.key) use FirstSecondTransition
}
{% endhighlight %}


#### - Displaying from scratch

Next to displaying the combination of the first and second layout 
through a transition, it can also occur that the entire layout needs to
be created from scratch.
This happens for example when a fresh Activity is presented, like after
an orientation change.

In this case we need to provide Acorn with a **single** View instance
that contains both layouts.
We can do this using a FrameLayout and the `<include>` tag.
In `first_and_second_scene.xml`:

{% highlight 'xml' %}
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/firstAndSecondRoot"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <include
        android:id="@+id/firstSceneRoot"
        layout="@layout/first_scene" />

    <include
        android:id="@+id/secondSceneRoot"
        layout="@layout/second_scene" />

</FrameLayout>
{% endhighlight %}

This will combine our two scene layouts in a single FrameLayout which
we can provide to Acorn:


{% highlight 'kotlin' %}
class MyViewControllerFactory : ViewControllerFactory {

    override fun supports(scene: Scene<*>): Boolean {
        return scene.key == SecondScene.key
    }

    override fun viewControllerFor(scene: Scene<*>, parent: ViewGroup): ViewController {
        return FirstSecondViewController(parent)
    }
}
{% endhighlight %}

#### - Displaying as part from a Scene transition from the _second_ to the _first_ Scene

This is also a case the user will see often.
However, we now have to take **two cases** into account: one where the 
layout is built up using the transition from first to second, and the
other where the layout is built up from scratch.
In the latter case we have an intermediate extra FrameLayout we need to 
deal with.

We can define a Transition just as before, but now need to take care
that we leave our layout in a proper state.
We can do this by checking what elements are present in the layout, in
particular whether the intermediate FrameLayout is there:

{% highlight 'kotlin' %}
object SecondFirstTransition : Transition {

    override fun execute(parent: ViewGroup, callback: Transition.Callback) {
        // The firstAndSecondRoot id is an indicator that our intermediate 
        // FrameLayout is present.
        val firstAndSecondRoot = parent.findViewById<ViewGroup>(R.id.firstAndSecondRoot)
        if (firstAndSecondRoot != null) {
            // 'Flatten' the layout to the 'normal' state 
            normalizeLayout(parent)
        }
        
        // Now we can execute the transition as usual.

        // Immediately create and attach the combined view controller.
        // This allows our first Scene to keep communicating with the
        // view seamlessly.
        val viewController = FirstSceneViewController(parent)
        callback.attach(viewController)

        // Animate the dark overlay
        parent.secondSceneRoot.overlayView
            .animate()
            .alpha(0f)

        // Animate the card view
        val cardView = parent.secondSceneRoot.cardView
        cardView.animate()
            .translationY(cardView.height.toFloat())
            .withEndAction {
                // Complete the transaction, taking care to remove any
                // obsolete views.
                parent.removeView(parent.secondSceneRoot)
                callback.onComplete(viewController)
            }
    }

    /**
     * 'Normalizes' the layout by removing the intermediate FrameLayout.
     * The resulting layout in [parent] contains the exact layout as it would
     * be when transitioning using [FirstSecondTransition].
     */
    private fun normalizeLayout(parent: ViewGroup) {
        val firstAndSecondRoot = parent.firstAndSecondRoot

        firstAndSecondRoot.firstSceneRoot.let {
            firstAndSecondRoot.removeView(it)
            parent.addView(it)
        }

        firstAndSecondRoot.secondSceneRoot.let {
            firstAndSecondRoot.removeView(it)
            parent.addView(it)
        }

        parent.removeView(firstAndSecondRoot)
    }
}
{% endhighlight %}


---

1: This example uses the {{ anchor('RxScene') }} class available in the 
`com.nhaarman.acorn.ext:acorn-rx` artifact.



