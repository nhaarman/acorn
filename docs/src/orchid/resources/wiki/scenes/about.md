---
pageId: 'about_scenes'

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

In a typical mobile application, the user can navigate from one destination to
another.
Each of these destinations can be regarded as a ‘screen’: a set of components
that fulfill a very specific use case.
For example, the main screen of a Twitter client may show a list of tweets to
the user.

![]({{ 'wiki/scenes/media/acorn_diagram_scene.svg'|asset }})
{.image-preview}

In Acorn these screens are represented by {{ anchor('Scene') }} 
objects<sup>1</sup>.
A Scene represents a destination in an application the user can navigate to.
A Scene is usually a screen in your application, but may also be a dialog or
even something entirely else.
A single traditional 'screen' could perhaps even consist of two separate Scenes,
for example when you enter an 'edit mode' for the screen.

### A note on Scenes versus Activities or Fragments

In a sense, Scenes are similar to Activities or Fragments: when used as a
primary navigational unit, they both represent a screen in an application.

A couple of key differences are:

 - Scenes are not created by the system, but can accept dependencies in their
   constructors;
 - Scenes only have one responsibility: connecting the UI to the business logic.
   They do not:
    - Create any views
    - Control application flow directly
    - Have system callbacks (like permissions)
 - Scenes always survive configuration changes.
 
----

1: Note that Acorn's `Scene` objects are a completely different concept than Android's [Scenes used for transitions](https://developer.android.com/reference/android/transition/Scene).
