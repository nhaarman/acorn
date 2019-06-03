---
title: 'About'

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

![]({{ 'wiki/activity/media/acorn_diagram_userinterface.svg'|asset }})
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

The `ext-acorn-android` artifact provides several components that help bridge
the gap between bare navigational state and displaying the user interface on 
Android.
