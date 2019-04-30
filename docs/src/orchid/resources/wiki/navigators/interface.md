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

![]({{ 'wiki/navigators/media/acorn_diagram_navigator_interface.svg'|asset }})
{.image-preview}

Just as with Scenes, you can implement the {{ anchor('Navigator') }} interface 
to create your own Navigator.
This time however, this is actually discouraged: it can be tricky to properly
deal with managing the different lifecycles.
There are a couple of excellent base classes available however that provide most
of the basic implementations you'll need.
If you still need to implement your own Navigator, have a look at 
{{ anchor('Scene Management','pageId=scene_management') }}.

The {{ anchor('Navigator') }} interface describes the basic functionality for a 
Navigator.
It provides several lifecycle describing methods, and allows interested parties
to register themselves to be notified of {{ anchor('Scene') }} changes.

{% highlight 'kotlin' %}
interface Navigator {

    fun onStart()
    fun onStop()
    fun onDestroy()
    
    fun isDestroyed(): Boolean
    
    fun addNavigatorEventsListener(listener: Navigator.Events): DisposableHandle

    interface Events {

        fun scene(scene: Scene<out Container>, data: TransitionData? = null)
        fun finished()
    }
}

{% endhighlight %}

The first three methods describe the {{ anchor('Navigator') }}'s lifecycle: 

 - `onStart()`: Called when the Navigator is started;
 - `onStop()`: Called when the Navigator is stopped;
 - `onDestroy()`: Called when the Navigator gets destroyed;

As a user of one of the base classes mentioned above you usually won't have to
override these methods, although you can hook into them when you need it.

The {{ anchor('Navigator.Events', 'Events') }} interface can be implemented to 
get notifications of Scene changes.
