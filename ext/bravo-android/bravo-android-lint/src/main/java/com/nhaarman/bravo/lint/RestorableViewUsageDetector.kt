/*
 * Bravo - Decoupling navigation from Android
 * Copyright (C) 2018 Niek Haarman
 *
 * Bravo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bravo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Bravo.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.nhaarman.bravo.lint

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.intellij.psi.PsiClass
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement

class RestorableViewUsageDetector : Detector(), Detector.UastScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> {
        return listOf(UClass::class.java)
    }

    private fun PsiClass.allSupers(): List<PsiClass> {
        return supers
            .flatMap {
                it.allSupers() + it
            }
            .distinct()
    }

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return object : UElementHandler() {

            private val restorableViewFQN = "com.nhaarman.bravo.android.presentation.RestorableView"
            private val androidViewFQN = "android.view.View"

            override fun visitClass(node: UClass) {
                if (node.isInterface) return

                val supers = node.supers
                if (supers.any { it.qualifiedName == restorableViewFQN }) {
                    if (supers.none { it.qualifiedName == androidViewFQN } && node.allSupers().none { it.qualifiedName == androidViewFQN }) {
                        val element = node.uastSuperTypes.first { it.getQualifiedName() == restorableViewFQN }
                        context.report(
                            issue,
                            node,
                            context.getNameLocation(element),
                            "${node.name} implements RestorableView but is not a subclass of View"
                        )
                    }

                    return
                }

                val allSupers = node.allSupers()
                if (allSupers.none { it.qualifiedName == restorableViewFQN }) {
                    return
                }

                if (allSupers.none { it.qualifiedName == "android.view.View" }) {
                    val element = supers
                        .first { superClass ->
                            superClass.allSupers().any { it.qualifiedName == restorableViewFQN }
                        }
                        .let { superClass ->
                            node.uastSuperTypes.first { it.getQualifiedName() == superClass.qualifiedName }
                        }

                    context.report(
                        issue,
                        node,
                        context.getNameLocation(element),
                        "${node.name} transitively implements RestorableView but is not a subclass of View"
                    )
                }
            }
        }
    }

    companion object {

        val issue = Issue.create(
            "Bravo_RestorableViewUsage",
            "Incorrect usage of RestorableView",
            "Classes implementing RestorableView must extend from android.view.View, otherwise a RuntimeException may be thrown.",
            Category.CORRECTNESS,
            10,
            Severity.FATAL,
            Implementation(
                RestorableViewUsageDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }
}