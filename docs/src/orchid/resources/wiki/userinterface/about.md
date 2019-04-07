---
pageId: 'user_interface'
title: 'The User Interface'

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

The user interface provides the bridge between the user interacting with the
device and your application code.
It is typically implemented in a very platform specific manner using classes
provided by the platform.

At this point in time it is already a common practice for Android developers 
to protect their application code from the platform specific view code: logic is 
separated into different classes to provide a clear boundary between the 
application logic and the view.

In Acorn, the entire platform acts as a plugin to your application.
Instead of depending on the Android platform to provide Activities or Fragments
so you can start your logic, Acorn lets you view the Activity as a 'window' into 
your application.
Regardless of whether the Activity showing your application is currently in the
foreground or in the background, or whether the user rotates the device or 
changes the system language, your application and navigational state will just
keep running until the OS decides to kill the app.

![]({{ 'wiki/userinterface/media/acorn_diagram_userinterface.svg'|asset }})
{.image-preview}

To be able to do this, Acorn lets the Activity subscribe to changes in the
navigational state, and makes it its sole responsibility to provide the user 
interface.
Upon starting the Activity the Activity will receive the current navigational 
state from Acorn, display the proper user interface and attach it to the 
currently active {{ anchor('Scene') }}.
When the Activity disappears, albeit due to the user navigating to another 
application or rotating the device, it detaches the user interface from the
Scene and stops listening to the navigational state.

A couple of basic use cases should be able to demonstrate the simplicity of 
this. 
In the GIFs below, an Activity registers itself with a Navigator, which provides
the Activity with a screen (the green S).
The Activity has a reference to its layout, in which it can inflate and place 
views (V).
Whenever a screen becomes orange, it is being replaced; whenever an Activity 
becomes red, it is stopped.

### A new Activity instance

When starting an application, the Android system launches a new Activity 
instance that can be used to host the UI.
In our case, it registers a listener with our Navigator.
It will receive the current active screen and inflate the layout for it.
Finally, it will attach the layout to the active screen so it can start
displaying data.

![]({{ 'wiki/userinterface/media/new_activity/new_activity.gif'|asset }})
{.image-preview}

### Changing the active scene

When a new screen (say screen **B**) becomes active, the Activity detaches the 
current layout from the previous screen **A**.
It inflates the new layout for screen **B**, replaces layout **A** with layout 
**B** using a suitable transition animation, and attaches the new layout to the 
newly activated screen.

<!--TODO GIF-->

### Navigating away from and returning to the app

When the user navigates away from the application, the application ‘loses’ its 
window to the user.
The Activity is stopped and detaches the user interface from the active screen.
When the user returns to the application, the Activity is started again and can
simply attach its layout to the active screen again.

<!--TODO GIF-->

When the user returns to the application after the Activity was destroyed, it 
needs to inflate the active screen’s layout again and attach it to the screen.
This behavior is pretty much the same as our first case ‘A new Activity 
instance’.

<!--TODO GIF-->

### Changing configurations

When the user rotates the device, enters split screen mode or changes the system 
language, the system will destroy any active Activity instances and recreate 
them with the new configuration.
When this happens, the original Activity simply detaches its layout from the 
active screen, and the new Activity inflates the new layout and attaches it to 
the active screen.
From a screen perspective, changing configurations now simply means switching
out views.


