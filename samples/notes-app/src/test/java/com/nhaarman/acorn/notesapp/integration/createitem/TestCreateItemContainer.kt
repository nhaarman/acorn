/*
 *    Copyright 2018 Niek Haarman
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nhaarman.acorn.notesapp.integration.presentation.createitem

import com.nhaarman.acorn.notesapp.presentation.createitem.CreateItemContainer
import com.nhaarman.acorn.testing.RestorableTestContainer
import io.reactivex.subjects.PublishSubject

class TestCreateItemContainer : CreateItemContainer, RestorableTestContainer {

    override fun setInitialText(text: String?) {
    }

    override val textChanges = PublishSubject.create<String>()

    override val createClicks = PublishSubject.create<Unit>()
}
