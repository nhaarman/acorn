# Deep Linking

The Android system allows apps to directly link to content in other apps, by
firing (implicit) `Intents`.
This can be used to for example open the dialer application with a phone number
already filled in, or to directly link to a product's detail screen.
Intents primarily consist of the general action to be performed,
such as `android.intent.ACTION_VIEW` and the data to operate on, which is
expressed in the form of a `Uri`.

## Handling 

Applications can register `IntentFilters` with the system to indicate that they
can handle certain intents.
Intent filters must specify at least one action they support, such as
`android.intent.action.MAIN` or `android.intent.action.VIEW`.

### Main application flow

One of the most used intent filters is one with action `android.intent.action.MAIN`
and category `android.intent.category.LAUNCHER`, which is used to let launcher
applications know that this is the default `Activity` to start.
When the Activity starts, some initial screen of your application is shown, 
kicking off the main application flow.

### Linking into your application

As mentioned before, you can also allow other apps to link into your application
using implicit intents.
This could be a link clicked in a browser, a general request to handle some data,
or a targeted intent that specifically requests your app to be started.

Usually these are secondary flows parallel to your main application flow.
For example, a user may have put the application in the background while
composing a message.
Another application may fire an intent that deep links into your application,
starting a completely different flow.
When this second flow is finished, it is expected that the main flow with the
message being composed is still intact.

Another possibility is to start your main application flow from a secondary entry
point.
One example is a user clicking a link in an email that automatically logs them in
in your application.
Instead of starting the flow from its usual entry point, for example the login
screen, the application starts in a screen that handles this link.
When the user is logged in successfully, the normal application flow takes over.
