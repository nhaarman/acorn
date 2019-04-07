# Background

Building modular, stable and testable applications is difficult when using the 
components provided by the Android framework.  
Activities and Fragments carry a lot of responsibilities: they are designed to
handle the lifecycle, the UI, permission callbacks, navigation to other screens
and much more.  
Screen navigation is typically handled by the screens themselves using
`Activity.startActivity` or dealing with the `FragmentManager`, leading to 
extensive coupling.

Testing code that uses these framework-provided components isn't trivial either:
often you need to run platform code which isn't available on the JVM. 
This requires you to execute the tests on an actual device or an emulator, which 
can be painfully slow.

Activities and Fragments also don't provide the flexibility you need to create
your own screen transition animations.

This section covers some background: what are the characteristics of mobile 
applications and how can all this be modeled in a modular and testable way?

 - [Mobile Applications](mobile_applications)
 - [Application Flow](application_flow)
 - [Saving and restoring state](saving_and_restoring_state)
 - [Deep Linking](deep_linking)
