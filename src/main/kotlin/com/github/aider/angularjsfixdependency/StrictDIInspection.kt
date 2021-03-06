package com.github.aider.angularjsfixdependency

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.lang.javascript.JSTokenTypes
import com.intellij.lang.javascript.inspections.JSInspection
import com.intellij.lang.javascript.psi.JSArgumentList
import com.intellij.lang.javascript.psi.JSCallExpression
import com.intellij.lang.javascript.psi.JSElementVisitor
import com.intellij.lang.javascript.psi.JSFunctionExpression
import com.intellij.lang.javascript.psi.ecma6.TypeScriptFunctionExpression
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.SmartPointerManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.PsiUtilCore


class StrictDIInspection : JSInspection() {
    override fun createVisitor(holder: ProblemsHolder, session: LocalInspectionToolSession): PsiElementVisitor {
        return object : JSElementVisitor() {
            override fun visitJSArgumentList(jsArgumentList: JSArgumentList) {
                super.visitJSArgumentList(jsArgumentList)
                val parent = jsArgumentList.parent as JSCallExpression
//                JSFunction function = getFunction(parent);
                //                JSFunction function = getFunction(parent);
                val expression: JSFunctionExpression? =
                    (PsiTreeUtil.getChildOfType(jsArgumentList, TypeScriptFunctionExpression::class.java)
                        ?: PsiTreeUtil.getChildOfType(
                            jsArgumentList,
                            JSFunctionExpression::class.java
                        ))
                if (expression == null) {
                    return
                }
                val functionParameters = expression.parameters
                if (functionParameters.isEmpty()) {
                    return
                }

                if (isStatementWrappedWithBrackets(expression)) {
                    return;
                }

                val buf = StringBuilder()
                val buf2 = StringBuilder()
                for (functionParameter in functionParameters) {
                    buf.append(QUOTE).append(functionParameter.name).append(QUOTE).append(COMMA_SPACE)
                    buf2.append(functionParameter.name).append(COMMA_SPACE)
                }
                val text = parent.firstChild.text


                if (text.endsWith(CONTROLLER) ||
                    text.endsWith(CONSTANT) ||
                    text.endsWith(RUN) ||
                    text.endsWith(DIRECTIVE) ||
                    text.endsWith(FACTORY) ||
                    text.endsWith(SERVICE) ||
                    text.endsWith(CONFIG) ||
                    text.endsWith(VALUE) ||
                    text.endsWith(FILTER)
                ) {
                    if (!text.startsWith(ANGULAR) && !text.startsWith(APP) && !text.startsWith(MODULE)) {
                        return
                    }
                    if (buf2.isNotEmpty()) {
                        buf2.deleteCharAt(buf2.length - 1)
                    }
                    val descriptionTemplate = "Replace '($buf2) => {...' with '[$buf($buf2) => {...]'"
                    val pointer = SmartPointerManager.createPointer(expression)
                    val fix = StrictDiFix(pointer, buf, descriptionTemplate)
                    holder.registerProblem(expression, descriptionTemplate, fix)
                }

            }
        }
    }

    private fun isStatementWrappedWithBrackets(statement: PsiElement): Boolean {
        return PsiUtilCore.getElementType(statement.firstChild) === JSTokenTypes.LBRACKET || PsiUtilCore.getElementType(
            statement.lastChild
        ) === JSTokenTypes.RBRACKET
    }

    companion object {
        const val APP = "app."
        const val MODULE = "module."
        const val ANGULAR = "angular"
        const val CONTROLLER = "controller"
        const val CONSTANT = "constant"
        const val RUN = "run"
        const val DIRECTIVE = "directive"
        const val FACTORY = "factory"
        const val SERVICE = "service"
        const val CONFIG = "config"
        const val FILTER = "filter"
        const val VALUE = "VALUE"
        const val COMMA_SPACE = ", "
        const val QUOTE = "'"
    }
}