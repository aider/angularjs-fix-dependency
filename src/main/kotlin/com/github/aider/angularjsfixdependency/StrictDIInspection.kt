package com.github.aider.angularjsfixdependency

import com.intellij.lang.javascript.inspections.JSInspection
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.psi.PsiElementVisitor
import com.intellij.lang.javascript.psi.JSElementVisitor
import com.intellij.lang.javascript.psi.JSArgumentList
import com.intellij.lang.javascript.psi.JSCallExpression
import com.intellij.lang.javascript.psi.ecma6.TypeScriptFunctionExpression
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.lang.javascript.psi.JSParameterListElement
import java.lang.StringBuilder
import com.github.aider.angularjsfixdependency.StrictDIInspection
import com.github.aider.angularjsfixdependency.StrictDiFix
import com.intellij.psi.SmartPointerManager

class StrictDIInspection : JSInspection() {
    override fun createVisitor(holder: ProblemsHolder, session: LocalInspectionToolSession): PsiElementVisitor {
        return object : JSElementVisitor() {
            override fun visitJSArgumentList(jsArgumentList: JSArgumentList) {
                super.visitJSArgumentList(jsArgumentList)
                val parent = jsArgumentList.parent as JSCallExpression
                val text = parent.firstChild.text
                val expression = PsiTreeUtil.getChildOfType(jsArgumentList, TypeScriptFunctionExpression::class.java)
                    ?: return
                val functionParameters = expression.parameters
                val buf = StringBuilder()
                val buf2 = StringBuilder()
                for (functionParameter in functionParameters) {
                    buf.append(QUOTE).append(functionParameter.name).append(QUOTE).append(COMMA_SPACE)
                    buf2.append(functionParameter.name).append(COMMA_SPACE)
                }
                if (text.endsWith("controller") || text.endsWith("constant") || text.endsWith("run") || text.endsWith("directive") || text.endsWith(
                        "factory"
                    ) || text.endsWith("service") || text.endsWith("config") || text.endsWith("filter")
                ) {
                    if (!text.startsWith("angular") && !text.startsWith("app.")) {
                        return
                    }
                    if (buf2.length > 0) {
                        buf2.deleteCharAt(buf2.length - 1)
                    }
                    val descriptionTemplate = "Replace '($buf2) => {...' with '[$buf($buf2) => {...]'"
                    val fix = StrictDiFix(SmartPointerManager.createPointer(expression), buf, descriptionTemplate)
                    println(descriptionTemplate)
                    holder.registerProblem(expression, descriptionTemplate, fix)
                }
            }
        }
    }

    companion object {
        const val COMMA_SPACE = ", "
        const val QUOTE = "'"
    }
}