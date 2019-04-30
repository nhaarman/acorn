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

Acorn is a carefully designed library that brings true modularity to 
navigation flow and allows you to have full control over your transition
animations.

Acorn's main principles lie around two interfaces: The 
{{ anchor('Scene') }} and the {{ anchor('Navigator') }}.
The Scene represents a screen in an application and can be regarded 
as a basic building block for the application flow.
The Navigator controls the application flow and determines which screen
is presented to the user.

![]({{ 'media/acorn_diagram.svg'|asset }})
{.image-preview}

A Scene class can be viewed as the simplest form of what a screen can be.
It has a simple lifecycle describing the four stages necessary to interact with
your application logic: 'created' - 'started' - 'stopped' - 'destroyed'.  
In addition the user interface gets 'attached' to and 'detached' from the Scene, allowing
you to interact with the user.  
This separation of the lifecycle protects your presentation layer from
recreation of the Android Activity: Scenes survive configuration changes such
as rotation of the device.
See {{ anchor('Scenes') }} for more information.

The Navigator controls the navigational state of the application and notifies the
UI layer of the active Scene.
A Navigator is completely free to choose how it is implemented: it does not 
necessarily have to use a back-stack like structure to model the application 
flow, but can use any data structure they like.
Navigators can even be composed together to combine several sub-flows into one 
major application flow.
See {{ anchor('Navigators') }} for more information.

In Acorn, the Activity is regarded as a 'window' to the user.
Its only responsibility is to react to screen changes and show the proper
user interface. 
This decoupling of navigation and the user interface results in an excellent
way to do transition animations: whenever the screen changes you get full
control over the root ViewGroup in the Activity, allowing you to do anything
you want.

In the {{ anchor('Wiki') }} section you can find information on several topics 
when working with Acorn.

_Note: this documentation website is work in progress, and some sections may be
missing or incomplete._
