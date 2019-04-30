---
---

One of the strengths of {{ anchor('Navigators', 'Navigator') }} is that they can 
be composed into a larger structure.
This allows for modular and reusable blocks in your application.
When composing Navigators, you have the same freedom as you have with regular
Navigators which means that you can choose your own internal model to represent
the state.

When composing Navigators, you usually create a {{ anchor('Navigator') }} that exclusively deals
with other Navigator instances directly, instead of mixing Scenes and Navigators
together.

The base implementations below show an overview of the default composite
Navigator classes.

### {{ anchor('CompositeReplacingNavigator') }}<sup>1</sup>

The {{ anchor('CompositeReplacingNavigator') }} class is the composite version 
of the {{ anchor('ReplacingNavigator') }}, and can switch between several child 
navigators.

{% highlight 'kotlin' %}
class MyCompositeNavigator(
    savedState: NavigatorState?
) : CompositeReplacingNavigator(savedState) {

    override fun initialNavigator() : Navigator {
        return MyFirstNavigator()
    }

    fun onEvent() {
        replace(MySecondNavigator())
    }

    override fun instantiateNavigator(
        navigatorClass: KClass<out Navigator<*>>,
        state: NavigatorState?
    ) : Navigator<out Container> {
        return when(navigatorClass) {
            MyFirstNavigator::class -> MyFirstNavigator(state)
            MySecondNavigator::class -> MySecondNavigator(state)
            else -> error("Unknown navigator class: $navigatorClass")
        }
    }
}
{% endhighlight %}

### {{ anchor('CompositeStackNavigator') }}<sup>1</sup>

The {{ anchor('CompositeStackNavigator') }} class is the composite version of 
the {{ anchor('StackNavigator') }}, and uses a stack to model its internal state.

{% highlight 'kotlin' %}
class MyCompositeNavigator(
    savedState: NavigatorState?
) : CompositeStackNavigator(savedState) {

    override fun initialStack() : List<Navigator> {
        return listOf(MyFirstNavigator())
    }

    fun onEvent() {
        push(MySecondNavigator())
    }

    override fun instantiateNavigator(
        navigatorClass: KClass<out Navigator<*>>,
        state: NavigatorState?
    ) : Navigator<out Container> {
        return when(navigatorClass) {
            MyFirstNavigator::class -> MyFirstNavigator(state)
            MySecondNavigator::class -> MySecondNavigator(state)
            else -> error("Unknown navigator class: $navigatorClass")
        }
    }
}
{% endhighlight %}

----

1: This class is available in the `ext-acorn` artifact.
