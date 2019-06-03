---
pageId: 'scene_interface'

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

![]({{ 'wiki/scenes/media/acorn_diagram_scene_interface.svg'|asset }})
{.image-preview}

The [`Scene`]({{site.baseUrl}}/com/nhaarman/acorn/presentation/Scene)
interface describes the basic functionality for a screen.
Firstly, it supplies a `SceneKey` property that can be used to identify separate
instances.
Next to that, it provides several lifecycle describing methods: 

{% highlight 'kotlin' %}
interface Scene<V : Container> {

    val key: SceneKey get() = SceneKey.from(javaClass)

    fun onStart() {}
    fun onStop() {}
    fun onDestroy() {}
    
    fun attach(v: V) {}
    fun detach(v: V) {}
}
{% endhighlight %} 

The first three methods describe the Scene's own lifecycle:

 - `onStart()`: Called when the Scene is started;
 - `onStop()`: Called when the Scene is stopped;
 - `onDestroy()`: Called when the Scene gets destroyed.

These allow you to start or stop doing work at the appropriate times.  
To be able to access the user interface, the Scene also provides the `attach`
and `detach` methods:
 
 - `attach(V)`: Attaches the Container of type `V` to the Scene;
 - `detach(V)`: Detaches the Container of type `V` from the Scene;


#### `onStart` / `onStop`

These two methods mark the active stage of the Scene.
You could for example choose to register to location updates when the Scene 
becomes active, and cancel the registration when the Scene becomes inactive:

{% highlight 'kotlin' %}
interface MyContainer : Container

class MyScene(
    private val locationProvider: LocationProvider
): Scene<MyContainer> {

    private val listener = { location: Location? ->
        // Process location update
    }

    override fun onStart() {
        locationProvider.registerLocationUpdates(listener)
    }

    override fun onStop() {
        locationProvider.unregisterLocationUpdates(listener)
    }
}
{% endhighlight %}

#### `attach`  / `detach`

These two methods give you access to the user interface.
You can grab a reference to the Container instance in `attach` to be able
to pass data to it.
However, you must make sure to remove the reference in `detach` to prevent
memory leaks.
For example, if we expand on the previous example:

{% highlight 'kotlin' %}
interface MyContainer : Container {

    var location : Location?
}

class MyScene(
    private val locationProvider: LocationProvider
): Scene<MyContainer> {

    private var view: MyContainer? = null

    private val listener = { location: Location? ->
        view?.location = location
    }

    override fun onStart() {
        locationProvider.registerLocationUpdates(listener)
    }

    override fun attach(v: MyContainer) {
        this.view = v
    }

    override fun detach(v: MyContainer) {
        this.view = null
    }

    override fun onStop() {
        locationProvider.unregisterLocationUpdates(listener)
    }
}
{% endhighlight %}

This implementation will start to listen to location updates as soon as the
Scene becomes active.
When a view is attached, the location updates will be passed on to it.
When the view is detached, the reference to the view is removed, but the Scene
will still listen to location updates.
Finally, When the Scene becomes inactive, it will stop listening to location
updates as well.


#### `onDestroy()`

`onDestroy()` will be called once and only once at the end of the lifetime of
the Scene.
When this method is called, the Scene must be regarded as destroyed and no more
calls to its lifecycle methods will be made.
You can use this callback to release resources if you already haven't done so.
