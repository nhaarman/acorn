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

Often, certain events happen in a screen that should cause a transition to
another screen.
This could be a user having logged in successfully, or pressing on an item to
view its detail screen.
A {{ anchor('Scene') }} however should have no knowledge of navigation flow, 
meaning it cannot tell the system to go to another Scene.
Instead, it can notify the component that _does_ have control over navigation
flow that an event has happened, like "this item was clicked!".

A typical pattern for this is to define an interface for these events, and let
the Scene accept an instance of this interface in its constructor.
The listener can then act on these events accordingly, such as navigating to 
another Scene.

{% highlight 'kotlin' %}
class LoginScene(
    private val loginInteractor: LoginInteractor,
    private val listener: Events
) : Scene<LoginContainer> {

    /* ... */

    private fun doLogin(username: String, password: String) {
        loginInteractor.login(username, password) { user : User ->
            listener.onLoggedIn(user)
        }
    }

    interface Events {

        /** Called when this Scene is done logging in given user. */
        fun onLoggedIn(user: User)
    }
}
{% endhighlight %}

This technique decouples the actual act of navigating to another screen from the
Scene implementation, allowing for a modular and reusable component:

![]({{ 'wiki/scenes/media/scene_events.svg'|asset }})
{.image-preview}

For more information about navigation, see 
[Navigators]({{ link('pageId=navigators') }}).
