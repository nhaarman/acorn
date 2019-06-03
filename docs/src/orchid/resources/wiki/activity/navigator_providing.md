---
title: 'Providing the Navigator'

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

From the Android's perspective, the Activity class is one of the main entry
points in an application.
When the user taps an application's launcher icon an Activity instance gets 
spawned and provides a Window to display the user interface.
This Activity instance however doesn't live long: changes in configuration
such as rotating the device or resizing the window cause the Activity to be 
destroyed and re-created.  
The Android system also provides several methods on the Activity class to 
be able to navigate to different Activities, using `Activity.startActivity`
or `Activity.startActivityForResult`.

Next to this, when an Activity is stopped it is able to save its instance state
so it can be restored properly when a new Activity is re-created.
This saved state even survives process deaths, which is especially important to
esnure an optimal user experience.

Acorn takes the entire navigational state over using its 
{{ anchor('Navigators','Navigator') }}.
At the root of the navigational tree there is usually a single Navigator
instance providing the applications navigational state. 
This instance needs to be shared across Activity instances, to ensure 
configuration changes don't cause the navigational state to get lost.
Furthermore, the navigational state needs to be saved in the Activity's 
`onSaveInstanceState` method to ensure proper behavior across app restarts.


## The {{ anchor('NavigatorProvider') }}

The `ext-acorn-android` artifact provides a NavigatorProvider interface
that exposes an access point for Activities to retrieve the Navigator instance
from.
The Activity can invoke `NavigatorProvider.navigatorFor(NavigatorState?)` 
when its `onCreate` method is invoked to retrieve the Navigator instance it can
register with.    
When the Activity's `onSaveInstanceState` method is invoked by the system, it 
needs to save the Navigator's state with it.
The NavigatorProvider interface also exposes a 
`saveNavigatorState(): NavigatorState` function for this.


### Implementing the NavigatorProvider

The `NavigatorProvider` needs to take care to cache the `Navigator` instance
across its `navigatorFor` invocations.
Furthermore, it can apply additional logic to determine whether or not to 
recreate the Navigator from the saved state. 

The {{ anchor('AbstractNavigatorProvider') }} abstract class provides a base 
implementation you can use to handle some of the boilerplate for you, such as
automatically caching the initial Navigator throughout its lifetime.
On top of that it includes a 30 minute timeout into the saved state to avoid
stale state from being restored.

When using the AbstractNavigatorProvider, you only need to implement the 
`createNavigator(NavigatorState)` function:

{% highlight 'kotlin' %}
class MyNavigatorProvider: AbstractNavigatorProvider<MyNavigator>() {

    override fun createNavigator(savedState: NavigatorState?): MyNavigator {
        return MyNavigator.from(savedState)
    }
}
{% endhighlight %}

### Providing the NavigatorProvider

To ensure the proper Navigator instance is used throughout your application's
lifetime, the NavigatorProvider instance needs to be shared between Activity
restarts.
The `Application` class is a perfect place for creating and caching this 
instance.
For example, you can do:

{% highlight 'kotlin' %}
val Context.navigatorProvider 
    get() = (applicationContext as MyApplication).navigatorProvider
    
class MyApplication: Application() {

    val navigatorProvider by lazy {
        MyNavigatorProvider()
    }
}
{% endhighlight %}
