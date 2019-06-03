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

People seem to have difficulties grasping the concept of lifecycles, or how to
deal with it.
However, these lifecycles are very useful, and quite necessary to actually be
able to do something.
When a screen becomes active in the application, it may want to start calling
use cases in the business layer to retrieve some data.
When it becomes visible to the user, it may want to register listeners to the UI
to react to events.
When it is replaced by another screen, it may need to stop listening for
updates, and when it is destroyed (by popping it from a back stack for example),
it may need to do some cleanup.
Lifecycle callbacks are an excellent way to go and handle these scenarios.

The Activity indeed has a somewhat complicated lifecycle in the sense that it
gets destroyed on configuration changes, such as device rotation.
For Scenes this is not the case: the UI gets attached to the container when
available, and detached when it becomes unavailable again.
When the device gets rotated from portrait to landscape, this can be seen as the
disappearance of the portrait UI, and the appearance of the landscape UI.

### Scene lifecycle

In the basis, a {{ anchor('Scene') }} can be 'started' or 'stopped', and
generally there is only one active Scene at a time.
To be able to interact with the user, a Scene can define a
{{ anchor('Container') }} type that can be attached to it.
This Container represents the View through which a Scene can display data to the
user and receive input events.

{% highlight 'kotlin' %}

interface Scene<V : Container> {

    fun onStart() {}

    fun attach(v: V) {}
    fun detach(v: V) {}

    fun onStop() {}
    fun onDestroy() {}
}

{% endhighlight %}


The lifecycle of a {{ anchor('Scene') }} is very simple:

 - 'stopped'  : The Scene is dormant, waiting to be started or to be destroyed.
 - 'started'  : The Scene is started.
 - 'destroyed': The Scene is destroyed and will not be started anymore

During the lifetime of a Scene it can go from 'stopped' to 'started' and vice
versa multiple times, until it reaches the 'destroyed' state.

![]({{site.baseUrl}}/assets/media/scene_lifecycle.png)

Next to this, the Scene provides the `attach` and `detach` methods through which
{{ anchor('Container') }} instances can be attached and detached.
During the lifetime of a Scene, it is possible that multiple Containers are
attached to it, though only one at a time.

For example, a simple "Hello World!" screen could be implemented as follows:

{% highlight 'kotlin' %}
interface HelloWorldContainer : Container {

    var text: String
}

class HelloWorldScene : Scene<HelloWorldContainer> {

    override fun attach(v : HelloWorldContainer) {
        v.text = "Hello World!"
    }
}
{% endhighlight %}

For more information about the Scene's interface, see
{{ anchor('The Scene Interface', 'pageId=scene_interface') }}.
