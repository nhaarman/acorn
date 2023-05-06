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

package com.nhaarman.acorn.lint

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.intellij.psi.PsiClass
import org.jetbrains.uast.UBinaryExpression
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UMethod
import org.jetbrains.uast.UastBinaryOperator

/**
 * A Detector that detects usage of the `disposables` property of the
 * `com.nhaarman.acorn.presentation.RxScene` class.
 *
 * This detector is included in the `ext-acorn-android` module since normal
 * Lint doesn't support normal JVM modules.
 */
class RxSceneDisposablesUsageDetector : Detector(), Detector.UastScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> {
        return listOf(UBinaryExpression::class.java, UCallExpression::class.java)
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

            override fun visitCallExpression(node: UCallExpression) {
                if (node.methodIdentifier?.name !in listOf("plusAssign", "add")) {
                    return
                }

                val receiver = node.receiver
                if (receiver?.sourcePsi?.node?.chars != "disposables") {
                    return
                }

                val method = node.findContainingMethod()
                if (method?.name != "attach") {
                    return
                }

                val containingClass = node.findContainingClass() ?: return
                val allSupers = containingClass.psi.allSupers()
                if (allSupers.none { it.qualifiedName == "com.nhaarman.acorn.presentation.RxScene" }) {
                    return
                }

                context.report(
                    RxSceneDisposablesUsageDetector.issue,
                    receiver,
                    context.getLocation(receiver),
                    "`disposables` property must only be used from `onStart()`",
                )
            }

            override fun visitBinaryExpression(node: UBinaryExpression) {
                if (node.operator !is UastBinaryOperator.AssignOperator) {
                    return
                }

                if (node.leftOperand.sourcePsi?.node?.chars != "disposables") {
                    return
                }

                val method = node.findContainingMethod()
                if (method?.name != "attach") {
                    return
                }

                val containingClass = node.findContainingClass() ?: return
                val allSupers = containingClass.psi.allSupers()
                if (allSupers.none { it.qualifiedName == "com.nhaarman.acorn.presentation.RxScene" }) {
                    return
                }

                context.report(
                    RxSceneDisposablesUsageDetector.issue,
                    node.leftOperand,
                    context.getLocation(node.leftOperand),
                    "`disposables` property must only be used from `onStart()`",
                )
            }

            private fun UElement.findContainingMethod(): UMethod? {
                val parent = uastParent
                if (parent !is UMethod) {
                    return parent?.findContainingMethod()
                }

                return parent
            }

            private fun UElement.findContainingClass(): UClass? {
                val parent = uastParent
                if (parent !is UClass) {
                    return parent?.findContainingClass()
                }

                return parent
            }
        }
    }

    companion object {

        val issue = Issue.create(
            "Acorn_RxSceneDisposables",
            "Incorrect usage of RxScene#disposables",
            "The `disposables` property should only be used from `onStart()`. When used in other lifecycle methods, leaks may occur.",
            Category.CORRECTNESS,
            5,
            Severity.WARNING,
            Implementation(
                RxSceneDisposablesUsageDetector::class.java,
                Scope.JAVA_FILE_SCOPE,
            ),
        )
    }
}
