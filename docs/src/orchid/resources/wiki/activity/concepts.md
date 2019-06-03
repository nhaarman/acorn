---
title: 'The Activity as a Plugin'

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

A couple of basic use cases should be able to demonstrate the simplicity of 
the Activity as a plugin.
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

![]({{ 'wiki/activity/media/new_activity/new_activity.gif'|asset }})
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

