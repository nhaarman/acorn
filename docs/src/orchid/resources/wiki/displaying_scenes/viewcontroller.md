---
title: 'The ViewController'

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

Scenes are platform agnostic, and cannot (at least, should not) refer to 
Android's `View` classes directly.
Instead, specialized {{ anchor('Container') }} interfaces form a boundary 
between the Navigational layer on the one side, and the Android View layer on 
the other side.
This View layer needs to implement the Container, usually using `View` classes.  
Often, it is not feasible or wanted to let the the root View of the layout 
implement this container directly, thus the need for an adapter-like class 
arises: The {{ anchor('ViewController') }}.

![]({{ 'wiki/displaying_scenes/media/acorn_diagram_viewcontroller.svg'|asset }})
{.image-preview}

The ViewController acts as the bridge between the Scene and the View: it
'controls' the View if you like.
It implements the commands the Scene executes on the Container, and redirects
them to the view:

{% highlight 'kotlin' %}
class MyScene<MyContainer> : Scene {

    override fun attach(myContainer: MyContainer) {
        myContainer.text = "Hello, world!"
    }
}

interface MyContainer: Container {
    
    var text: String
}

class MyViewController(override val view: View) : ViewController, MyContainer {

    override var text: String = ""
        set(value) {
            view.findViewById<TextView>(R.id.myTextView).text = value
        }
}
{% endhighlight %}
