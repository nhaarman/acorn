# Concepts

Acorn separates the view from the navigation layer by letting the view layer 
subscribe to screen changes in the navigation layer.
The navigation layer reacts to user input to modify its navigation state and
publishes the new screen object.
The view layer observes these screen changes and changes its UI conform to the
new screen.

In Acorn, screens are represented by Scenes and Navigators control the 
navigation state.  
On these pages you can learn more about the concepts of Acorn.

* [Scenes](scenes)
* [Navigators](navigators)
* [The view layer](view_layer)
