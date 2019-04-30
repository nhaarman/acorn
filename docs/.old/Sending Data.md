Android has a great mechanism that allows applications to share data
with each other.
Web pages can be opened in a browser application, applications can
request a camera application to take a picture for them, and plain text
and image media can be shared to social media applications.

## Sending Data

Your application can share data with other applications.
You can allow the user to share a picture with a messaging app, or you
can start the Google Maps app to start driving to the nearest pizza
place.
Android supports this through the use of `Intent`s: specialized
messaging objects that can be used to request an action from another app
component.
You can send an Intent to the Android system and the system will start
the component that can handle the Intent.

### Navigation

In terms of your application's navigation state, sharing data by starting
a third party application is just another destination in your app.
You can navigate to a `Scene` that represents sharing the data.

```kotlin
class ShareTextScene(
  val text: String,
  private val listener: Events
) : Scene<Nothing> {

  fun finished() {
    listener.finished()
  }

  interface Events {
    fun finished()
  }
}
```

Since your application has no UI to show for this Scene, you can just
pass `Nothing` as its container type.
At the UI-layer of your application, instead of inflating a layout, you
create an Intent to send to the Android system.
Next to a `ViewFactory` instance, the `AcornActivityDelegate` accepts an
`IntentProvider` instance you can implement for these scenario's.
By returning a valid Intent from the IntentProvider, Acorn will route that intent
to `Activity#startActivityForResult` instead of inflating a layout.

```kotlin
object ShareTextIntentProvider: IntentProvider {

  override fun intentFor(scene: Scene<out Container>, data: TransitionData?): Intent? {
    if (scene !is ShareTextScene) return null

    return Intent().apply {
        action = Intent.ACTION_SEND
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, scene.text)
    }
  }

  override fun onActivityResult(scene: Scene<out Container>, resultCode: Int, data: Intent?): Boolean {
    if (scene !is ShareTextScene) return false

    scene.finished()
    return true
  }
}
```

Acorn uses `startActivityForResult` to be able to get notified when the external
application finishes.
In `IntentProvider#onActivityResult` you can let the Scene know it is finished,
and cause a change in navigation state again.

### Requesting Data

Your application can also request data from other applications.
It can request a contact from the Contacts app, or ask the camera app
to take a picture.
For this, you can use the same mechanism as described above.