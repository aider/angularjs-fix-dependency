package com.github.aider.angularjsfixdependency

import com.intellij.psi.SmartPsiElementPointer
import com.intellij.lang.javascript.psi.ecma6.TypeScriptFunctionExpression
import java.lang.StringBuilder
import com.intellij.codeInspection.LocalQuickFix
import com.github.aider.angularjsfixdependency.StrictDiFix
import com.intellij.codeInspection.util.IntentionFamilyName
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiUtilCore
import com.intellij.lang.javascript.JSTokenTypes
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.lang.javascript.psi.JSStatement
import com.intellij.lang.javascript.psi.impl.JSPsiElementFactory
import com.intellij.openapi.project.Project

class StrictDiFix(
    private val expression: SmartPsiElementPointer<TypeScriptFunctionExpression?>,
    private val buf: StringBuilder,
    private val descriptionTemplate: String
) : LocalQuickFix {
    private fun addBracketsToStatement(smartPsiElementPointer: SmartPsiElementPointer<TypeScriptFunctionExpression?>?) {
        if (smartPsiElementPointer == null) {
            return
        }
        val function = smartPsiElementPointer.element
        if (function != null) {
//            JSParameterListElement[] functionParameters = function.getParameters();
//            StringBuilder buf = new StringBuilder();
//            for (JSParameterListElement functionParameter : functionParameters) {
//                buf.append(QUOTE).append(functionParameter.getName()).append(QUOTE).append(COMMA_SPACE);
//            }
            val text = function.text
            val newText = LBRACKET + buf + text + RBRACKET
            replaceAndReformat(function, newText)
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