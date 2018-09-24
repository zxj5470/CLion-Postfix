package com.github.zxj5470.clpstfx

import com.github.zxj5470.clpstfx.ClionPostfixTemplateProvider.Companion.selectorTopmost
import com.intellij.codeInsight.template.postfix.templates.*
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.Condition
import com.intellij.openapi.util.Conditions
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.containers.ContainerUtil
import com.jetbrains.cidr.lang.psi.OCExpression

class ClionPostfixTemplateProvider : PostfixTemplateProvider {
    private val myTemplates: Set<PostfixTemplate>

    companion object {
        fun selectorTopmost(): PostfixTemplateExpressionSelector {
            return selectorTopmost(Conditions.alwaysTrue())
        }

        fun selectorTopmost(additionalFilter: Condition<PsiElement>): PostfixTemplateExpressionSelector {
            return object : PostfixTemplateExpressionSelectorBase(additionalFilter) {
                override fun getNonFilteredExpressions(psiElement: PsiElement, document: Document, i: Int): List<PsiElement> {
                    val stat = PsiTreeUtil.getNonStrictParentOfType(psiElement, OCExpression::class.java)
                    return ContainerUtil.createMaybeSingletonList(stat)
                }
            }
        }
    }

    init {
        myTemplates = ContainerUtil.newHashSet(
                CLionPrintfPostfixTemplate(this),
                CLionCoutPostfixTemplate(this)
        )
    }

    override fun getTemplates(): Set<PostfixTemplate> = myTemplates
    override fun isTerminalSymbol(currentChar: Char) = currentChar == '.'
    override fun afterExpand(file: PsiFile, editor: Editor) = Unit
    override fun preCheck(copyFile: PsiFile, realEditor: Editor, currentOffset: Int): PsiFile = copyFile
    override fun preExpand(file: PsiFile, editor: Editor) = Unit
}

class CLionPrintfPostfixTemplate(provider: PostfixTemplateProvider) :
        StringBasedPostfixTemplate("printf", "printf(expr)", selectorTopmost(), provider) {
    override fun getTemplateString(psiElement: PsiElement): String? = "printf(\"%\$END\$\", \$expr\$);"
    override fun getElementToRemove(expr: PsiElement): PsiElement = expr
}

class CLionCoutPostfixTemplate(provider: PostfixTemplateProvider) :
        StringBasedPostfixTemplate("cout", "std::cout << expr;", selectorTopmost(), provider) {
    override fun getTemplateString(psiElement: PsiElement): String? = "std::cout << \$expr\$;"
    override fun getElementToRemove(expr: PsiElement): PsiElement = expr
}