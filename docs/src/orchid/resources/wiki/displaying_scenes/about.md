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

Acorn regards the Activity as a window into the application.
In fact, the Activity provides a Window instance that is used to display the user
interface to the user.  
The decoupling of navigation from the user interface allows us the Activity as a
plugin in the application: an Activity appearing registers itself as a listener
with the {{ anchor('Navigator') }} to indicate that its interested in 
{{ anchor('Scene') }} events.
Upon receiving these events, it can act appropriately and display the proper user
interface, attaching it to the Scene as necessary.

In the following couple of sections the concepts of displaying scenes are
explained:

 - [The ViewController](viewcontroller)
 - [Mapping Scenes to layouts](mapping_scenes_layouts)
 - [Transition Animations](transition_animations)

