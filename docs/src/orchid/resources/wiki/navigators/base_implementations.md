---
---

There are some base {{ anchor('Navigator') }} implementations to help you get started. 
Each of these implementations provide a default `saveInstanceState()` 
implementation allow their state to be saved, and each has their own strategy to
do this.
None of them actually implement {{ anchor('SavableNavigator') }} however, you 
can opt into their state saving ability by explicitly implementing the 
SavableNavigator interface.

### {{ anchor('SingleSceneNavigator') }}<sup>1</sup>

The {{ anchor('SingleSceneNavigator') }} is a very basic Navigator that is only 
able to host a single {{ anchor('Scene') }} during its lifetime, without ever 
navigating to a different Scene. 
This class can sometimes come in useful when composing Navigators.

The SingleSceneNavigator provides a single abstract `createScene` method that
needs to be overridden, which provides the Scene to use in this Navigator.
If the Navigator is restored from a saved state and the Scene implements
{{ anchor('SavableScene') }}, the `createScene` method will be called with the 
instance that was returned in 
[SavableScene#saveInstanceState]({{site.baseUrl}}/com/nhaarman/acorn/presentation/SavableScene#method__abstract_fun_saveInstanceState____SceneState).

{% highlight 'kotlin' %}
class MyHelloWorldNavigator(
    savedState: NavigatorState?
) : SingleSceneNavigator(savedState) {

    override fun createScene(state: SceneState?) : Scene<out Container> {
        return HelloWorldScene(state)
    }
}
{% endhighlight %}

The SingleSceneNavigator will automatically save its own and the Scene's
instance state when necessary, and reconstruct itself from any saved state
passed to its constructor.

### {{ anchor('ReplacingNavigator') }}<sup>1</sup>

The {{ anchor('ReplacingNavigator') }} is a Navigator that can switch between 
several different Scenes, but has no back behavior.
When the user presses the back button, the Navigator will always directly finish
regardless of how many Scenes it has seen.

The ReplacingNavigator provides an abstract method 
[initialScene]({{site.baseUrl}}/com/nhaarman/acorn/navigation/ReplacingNavigator#method__protected_abstract_fun_initialScene____Scene) 
that needs to be overridden, which provides the initial Scene to use when not 
restored from a saved state.
When the ReplacingNavigator is restored from a saved state, this method will not
be called.

Instead to support state restoration, this {{ anchor('ReplacingNavigator') }} 
provides an abstract 
[instantiateScene]({{site.baseUrl}}/com/nhaarman/acorn/navigation/ReplacingNavigator#method__protected_abstract_fun_instantiateScene_sceneClass__KClass__state__SceneState____Scene) 
method which takes in a `KClass<Scene<*>>` and an optional 
{{ anchor('SceneState') }} instance.
Since state saving can occur at any time, users of the ReplacingNavigator must
be able to handle all Scenes used in it, even if the Scene itself does not
implement {{ anchor('SavableScene') }}.

{% highlight 'kotlin' %}
class MyNavigator(
    savedState: NavigatorState?
) : ReplacingNavigator(savedState) {

    override fun initialScene() : Scene<out Container> {
        return MyFirstScene()
    }

    fun onEvent() {
        replace(MySecondScene())
    }

    override fun instantiateScene(
        sceneClass: KClass<out Scene<*>>,
        state: SceneState?
    ) : Scene<out Container> {
        return when(sceneClass) {
            MyFirstScene::class -> MyFirstScene(state)
            MySecondScene::class -> MySecondScene(state)
            else -> error("Unknown scene class: $sceneClass")
        }
    }
}
{% endhighlight %}

To switch Scenes in the {{ anchor('ReplacingNavigator') }}, the class provides a 
`replace` method.
When calling this method, the ReplacingNavigator will handle the previous and
new Scenes' lifecycle methods appropriately and notify any listeners of the
change in scenery.

### {{ anchor('StackNavigator') }}<sup>1</sup>

The {{ anchor('StackNavigator') }} base class is the class you'll feel most 
familiar with, as it uses a stack to model its internal state.
You can push Scenes on the stack, pop them off, or replace the top Scene with
another one.
Pressing the back button will pop the top Scene off the stack.

The {{ anchor('StackNavigator') }} provides an abstract method 
[initialStack]({{site.baseUrl}}/com/nhaarman/acorn/navigation/StackNavigator#method__protected_abstract_fun_initialStack____List) 
that needs to
be overridden, which provides the initial Scene stack to use when not restored
from a saved state.
When the StackNavigator is restored from a saved state, this method will not
be called.

Instead to support state restoration, this {{ anchor('StackNavigator') }} provides an
abstract 
[instantiateScene]({{site.baseUrl}}/com/nhaarman/acorn/navigation/StackNavigator#method__protected_abstract_fun_instantiateScene_sceneClass__KClass__state__SceneState____Scene)
method which takes in a `KClass<Scene<*>>` and an
optional {{ anchor('SceneState') }} instance.
Since state saving can occur at any time, users of the StackNavigator must be 
able to handle all Scenes used in it, even if the Scene itself does not
implement {{ anchor('SavableScene') }}.
The StackNavigator will take care of preserving the order of the stack.

{% highlight 'kotlin' %}
class MyNavigator(
    savedState: NavigatorState?
) : StackNavigator(savedState) {

    override fun initialStack() : List<Scene<out Container>> {
        return listOf(MyFirstScene())
    }

    fun onEvent() {
        push(MySecondScene())
    }

    override fun instantiateScene(
        sceneClass: KClass<out Scene<*>>,
        state: SceneState?
    ) : Scene<out Container> {
        return when(sceneClass) {
            MyFirstScene::class -> MyFirstScene(state)
            MySecondScene::class -> MySecondScene(state)
            else -> error("Unknown scene class: $sceneClass")
        }
    }
}
{% endhighlight %}

To manipulate the stack in the {{ anchor('StackNavigator') }}, the class 
provides three methods: `push`, `pop`, and `replace`.
When calling one of these methods, the StackNavigator will handle the Scenes'
lifecycle methods appropriately and will notify any listeners of the change in
scenery.

----

1: This class is available in the `ext-acorn` artifact.
