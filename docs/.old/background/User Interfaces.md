In Android, the Activity is the 'window' between the application and the user by
hosting the user interface and intercepting input events.
The user interface is typically implemented using View instances, which are
attached to the Activity.
Views can be instantiated manually, or inflated from an xml resource, after
which it can be attached to the Activity.

## The Activity as the UI layer

Multitasking in Android means that you can switch quickly between different
applications.

During the life of an Activity, it goes through several phases.
Most interestingly, when the Activity is in its 'started' or 'resumed' state,
one can assume the Activity is at least visible to the user.
Activities can enter their 'started' or 'stopped' state any number of times,
until it is destroyed by the system.



Once an Activity instance is destroyed, a new instance can be recreated and
restored to the previous state.
This typically happens on configuration changes, such as device rotation.




