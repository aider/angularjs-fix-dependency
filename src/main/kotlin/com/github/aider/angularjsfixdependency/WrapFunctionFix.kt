package com.github.aider.angularjsfixdependency

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.util.IntentionFamilyName
import com.intellij.lang.javascript.JSTokenTypes
import com.intellij.lang.javascript.psi.JSStatement
import com.intellij.lang.javascript.psi.impl.JSPsiElementFactory
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.SmartPsiElementPointer
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.psi.util.PsiUtilCore

class WrapFunctionFix(
    private val expression: SmartPsiElementPointer<PsiElement?>,
//    private val buf: StringBuilder,
    private val descriptionTemplate: String
) : LocalQuickFix {
    private fun addBracketsToStatement(smartPsiElementPointer: SmartPsiElementPointer<PsiElement?>?) {
        if (smartPsiElementPointer == null) {
            return
        }
        val elem = smartPsiElementPointer.element
        if (elem != null) {
            val text = elem.text
            val newText = "(function () {\n$text\n})();"
            replaceAndReformat(elem, newText)
        }
    }

    override fun getFamilyName(): @IntentionFamilyName String {
        return descriptionTemplate
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        addBracketsToStatement(expression)
    }

    companion object {
        const val COMMA_SPACE = ", "
        const val QUOTE = "'"
        const val LBRACKET = "["
        const val RBRACKET = "]"
        private fun isStatementWrappedWithBrackets(statement: PsiElement): Boolean {
            return PsiUtilCore.getElementType(statement.firstChild) === JSTokenTypes.LBRACKET || PsiUtilCore.getElementType(
                statement.lastChild
            ) === JSTokenTypes.RBRACKET
        }

        private fun replaceAndReformat(statement: PsiElement, text: String) {
            val codeStyleManager = CodeStyleManager.getInstance(statement.project)
            val newStatement = codeStyleManager.performActionWithFormatterDisabled<PsiElement> {
                statement.replace(
                    JSPsiElementFactory.createJSStatement(
                        text,
                        statement
                    )
                )
            } as JSStatement
            val formatted = codeStyleManager.reformat(newStatement)
            codeStyleManager.reformatNewlyAddedElement(formatted.node.treeParent, formatted.node)
        }
    }
}