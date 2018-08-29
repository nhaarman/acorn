package com.nhaarman.bravo.notesapp.android

// abstract class NotesAppActivity : BravoAppCompatActivity<Any>() {
//
//    override val viewFactory by lazy {
//        bindViews {
//
//            bindViewGroup(
//                ItemListScene.key,
//                R.layout.itemlist_scene
//            ) {
//                ItemListView(it)
//            }
//
//            bindViewGroup(
//                CreateItemScene.key,
//                R.layout.createitem_scene
//            ) {
//                CreateItemView(it)
//            }
//
//            bindViewGroup(
//                EditItemScene.key,
//                R.layout.edititem_scene
//            ) {
//                EditItemView(it)
//            }
//        }
//    }
//
//    override val transitionFactory: TransitionFactory by lazy {
//        transitionFactory(viewFactory) {
//            val fadeOutToBottom =
//                FadeOutToBottomTransition.from(viewFactory)
//                    .hideKeyboardOnStart()
//
//            (CreateItemScene::class.java to ItemListScene::class.java) use fadeOutToBottom
//            (EditItemScene::class.java to ItemListScene::class.java) use fadeOutToBottom
//        }
//    }
// }