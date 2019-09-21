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


Acorn allows you to create finegrained little blocks for the navigational states in your application, 
and lets you compose them together into a well-defined tree structure.



Acorn turns your navigation back stack in a composable and decoupled navigation
structure.

Acorn allows you to create little blocks that represent the states of navigation in your application,
and create a tree-like navigational strucutre out of it.

Acorn allows you to create finegrained little blocks for the navigational states in your application, 
and lets you compose them together into a well-defined tree structure.

Acorn allows you to define the navigational structure of your application in finegrained,
composable blocks.

With acorn you can create finegrained 

.. you can structure the 



## Acorn's filosophy

At its core, Acorn is a set of interfaces that describe the basics of mobile
screen navigation. A {{ anchor('Scene') }} interface represents a screen and 
can be regarded as a basic building block for the application flow.
{{ anchor('Navigators','Navigator') }} control this application flow, and can
be composed together to form a large structure.
Finally, {{ anchor('Containers','Container') }} form the boundary between your
presentation layer and the UI elements.

![]({{ 'media/acorn_diagram.svg'|asset }})
{.image-preview}

On top of this, Acorn provides an extensive set of default implementations to
do the work for you: several base Scene implementations that provide the basics,
and a couple of Navigators that can be composed together to create the
navigational structure that you need.

Acorn decouples the UI from navigation, meaning the user interface becomes a
plugin into the navigational state.
This gives you full control over your view elements and especially your
transition animations.

In the {{ anchor('Wiki') }} section you can find information on several topics 
when working with Acorn.

_Note: this documentation website is work in progress, and some sections may be
missing or incomplete._
