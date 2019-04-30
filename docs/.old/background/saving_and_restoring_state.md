# Saving and Restoring State

Saving and restoring state is an important issue for Android applications.
Due to the nature of the Android system, activities can be destroyed and
recreated at any time during an application's lifecycle.
When this happens, it is vital that the application's is restored to the state
it was in before it was killed, and that any input the user has entered into the
system is not lost.

This can be something like text entered into an `EditText`, or the scroll
position of a scrollable widget, but also any navigational state like the state
of the back stack.

## Moments of state storing

Historically, state is stored when an Activity is stopped but not finished,
which means as much as "we might return to it later".
This occurs in two ways:

 - The user leaves the Activity by not pressing the back button, such as navigating
   to another app or pressing the home button;
 - The Activity is being recreated due to a configuration change, such as a screen
   orientation change.

Whenever this happens, the system calls `Activity.onSaveInstanceState`, which
by default automatically includes the view hierarchy in the resulting state.
The system also keeps track of the Activities in the stack, and ensure that the
state is properly restored.

## Single Activity solutions

When using a single screen per Activity, the system handles the navigational and
view hierarchy state saving for you.
However, when using a single Activity to navigate through multiple screens,
you're stepping away from the Activity back stack and with that opt out of the
automatic state saving the system provides, leaving you to deal with this
yourself.

Properly managing application state now consists of two things:

 - Not only do you have to save the entire navigational state including the
   view hierarchies for each individual screen,
 - You must also handle view state restoration when navigation _back_ to a
   screen.

When using Fragments, the FragmentManager takes on the responsibility of doing
all this.