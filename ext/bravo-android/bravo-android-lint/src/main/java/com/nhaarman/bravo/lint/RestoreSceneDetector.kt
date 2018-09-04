package com.nhaarman.bravo.lint

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Context
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiType
import com.intellij.psi.search.GlobalSearchScope
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UMethod
import org.jetbrains.uast.getContainingUClass
import org.jetbrains.uast.util.isConstructorCall
import org.jetbrains.uast.visitor.AbstractUastVisitor

class RestoreSceneDetector : Detector(), Detector.UastScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>>? {
        return listOf(
            UMethod::class.java,
            UCallExpression::class.java
        )
    }

    private val constructedScenes = mutableSetOf<ConstructedScene>()
    private val referencedScenes = mutableSetOf<PsiType>()

    private fun PsiClass.allSupers(): List<PsiClass> {
        return supers
            .flatMap {
                it.allSupers() + it
            }
            .distinct()
    }

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return object : UElementHandler() {

            val sceneType = PsiType.getTypeByName(
                "com.nhaarman.bravo.presentation.Scene",
                context.uastContext.project,
                GlobalSearchScope.everythingScope(context.uastContext.project)
            )

            private val supportedNavigatorFQNs = listOf(
                "com.nhaarman.bravo.navigation.StackNavigator",
                "com.nhaarman.bravo.navigation.ReplacingNavigator",
                "com.nhaarman.bravo.navigation.WizardNavigator"
            )

            override fun visitCallExpression(node: UCallExpression) {
                if (!node.isConstructorCall()) return

                val containingClass = node.getContainingUClass() ?: return
                if (containingClass.allSupers().none { it.qualifiedName in supportedNavigatorFQNs }) {
                    return
                }

                val returnType = node.returnType ?: return

                val isScene = sceneType.isAssignableFrom(returnType)
                if (!isScene) {
                    println("${returnType.canonicalText} !is Scene")
                    return
                }

                constructedScenes += ConstructedScene(returnType, node)
            }

            override fun visitMethod(node: UMethod) {
                if (node.name != "instantiateScene") return

                val body = node.uastBody ?: return
                body.accept(object : AbstractUastVisitor() {

                    override fun visitCallExpression(node: UCallExpression): Boolean {
                        val type = node.returnType ?: return false

                        val isScene = sceneType.isAssignableFrom(type)
                        if (!isScene) return false

                        referencedScenes += type

                        return true
                    }
                })
            }
        }
    }

    override fun afterCheckFile(context: Context) {
        constructedScenes
            .filter { it.type !in referencedScenes }
            .forEach {
                (context as JavaContext).report(
                    RestoreSceneDetector.issue,
                    it.element,
                    context.getNameLocation(it.element),
                    "Scene is not restored"
                )
            }

        constructedScenes.clear()
        referencedScenes.clear()
    }

    private data class ConstructedScene(val type: PsiType, val element: UElement)

    companion object {

        val issue = Issue.create(
            "Bravo_RestoreScene",
            "Scene created but not restored",
            "Scenes used in this Navigator should be restored in instantiateScene.",
            Category.CORRECTNESS,
            5,
            Severity.ERROR,
            Implementation(
                RestoreSceneDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }
}