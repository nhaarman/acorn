Basically Fragments, but rewritten. 
 
 - Still has all responsibilities in a Controller instance:
   - Creating views
   - Handling routing
   - Has access to `Activity`
   - Callbacks when attached to window
   - Handles options menu
   - Permissions result
   - >1337 lines
 - Controller instances do not accept non-serializable dependencies such as repositories
   - Enforces field injection at some point in the lifecycle
 - Embracing lifecycles (e.g. do stuff in this lifecycle method)
   - https://github.com/bluelinelabs/Conductor/issues/34
 - No separation of view / logic