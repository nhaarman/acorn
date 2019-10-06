
Navigation on Android has always been a heikel punt.
There's Activities, its back stack, tasks, configuration changes,
OEMs..

Fragments were introduced to aid tablet development.
However, there use also made people vision a 'single Activity system', 
where navigation happened inside a single Activity.
Not much later Square was one of the first to opt for View based solutions 
instead of Fragments.

All these solutions seemed to build _on top_ of the Android ecosystem.
They made use of behavior in the framework, like the FragmentManager or
some Android View being attached to the Activity's window.
Building solutions on top of frameworks easily result in the needing of 
'hacks' to make the solution work.
Instead of working with the system you're fighting it.


## Acorn

Acorn turns the problem upside down, by taking a hard look at the foundational 
responsibilities of several components.
such as the Activity, navigational components, lifecycles and screens.

### Screens

Screens form the basic building blocks for navigation through applications.
They can usually work in isolation, meaning they can fulfill a very specific role in the application.

## Lifecycles

Lifecycles are what drives these screens. 
They tell screens when they should start and stop the work they're designed to do.

## Navigation

Navigational components take care of navigational state in the application. 
They keep track of where you are, and where you've been, and can decide where to go to when the user presses a certain button.

## The Activity
Activities come in last. 
Comparing to traditional (or prehistoric) Android development, we've already stripped away 75% of the responsibilities they used to have.
The only major thing that is left, is _displaying_ screens.

