package com.github.aider.angularjsfixdependency

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.lang.javascript.inspections.JSInspection
import com.intellij.lang.javascript.psi.*
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFile
import com.intellij.psi.SmartPointerManager
import com.intellij.psi.util.PsiTreeUtil


class WrapFunctionInspection : JSInspection() {
    override fun createVisitor(holder: ProblemsHolder, session: LocalInspectionToolSession): PsiElementVisitor {
        return object : JSElementVisitor() {
            override fun visitFile(file: PsiFile) {
                super.visitFile(file)
//                JSExpressionStatement

                var node: PsiElement? =
                    PsiTreeUtil.getChildOfType(file.originalElement, JSReferenceExpression::class.java)
                if (node == null) {
                    node = PsiTreeUtil.getChildOfType(file, JSExpressionStatement::class.java)
                }
                for (c in file.children) {
                    if (c is JSExpressionStatement && c.text.startsWith("angular.module")) {
                        node = c
                        break
                    }
                }
                val text = node?.text
                if (text?.startsWith("angular.module") == true) {
                    val callExpression = PsiTreeUtil.getParentOfType(node, JSCallExpression::class.java)?.parent
                    val jsReferenceExpression =
                        PsiTreeUtil.getTopmostParentOfType(callExpression, JSReferenceExpression::class.java)

                    val jsExpressionStatement =
                        PsiTreeUtil.getTopmostParentOfType(callExpression, JSExpressionStatement::class.java)
                    val jsParenthesizedExpression =
                        PsiTreeUtil.getTopmostParentOfType(node, JSParenthesizedExpression::class.java)
                    if (jsParenthesizedExpression != null) {
                        return
                    }
                    if (jsReferenceExpression != null && callExpression !is JSReferenceExpression) {
                        return
                    }
                    var wrapFnPoint: PsiElement? = null
                    if (jsExpressionStatement != null) {
                        wrapFnPoint = jsExpressionStatement
                    } else if (jsReferenceExpression != null) {
                        wrapFnPoint = jsReferenceExpression
                    } else if (callExpression != null) {
                        wrapFnPoint = callExpression.originalElement
                    } else if (node != null) {
                        wrapFnPoint = node.originalElement
                    }
                    if (wrapFnPoint != null) {
                        val fix = WrapFunctionFix(
                            SmartPointerManager.createPointer(wrapFnPoint),
                            DESCRIPTION_TEMPLATE
                        )
                        holder.registerProblem(wrapFnPoint, DESCRIPTION_TEMPLATE, fix)
                    }
                }
            }
        }
    }

    companion object {
        const val DESCRIPTION_TEMPLATE = "Wrap '(function () { ... })();"
    }
}