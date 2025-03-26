package com.example.ngxs.tools;

import com.intellij.lang.javascript.psi.JSArgumentList;
import com.intellij.lang.javascript.psi.ecma6.ES6Decorator;
import com.intellij.lang.javascript.psi.ecmal4.JSAttributeList;
import com.intellij.lang.javascript.psi.impl.JSCallExpressionImpl;
import com.intellij.lang.javascript.psi.impl.JSReferenceExpressionImpl;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

public class NgxsActionImpl {
    private PsiFile psiFile;
    private PsiElement psiElement;
    public int position;
    public String key;

    public NgxsActionImpl(PsiFile psiFile, PsiElement psiElement) {
        this.psiFile = psiFile;
        this.psiElement = psiElement;
        this.position = psiElement.getTextRange().getStartOffset();
        for (PsiElement child : psiElement.getChildren()) {
            if (child instanceof JSAttributeList attr) {
                for (ES6Decorator decorator : attr.getDecorators()) {
                    if ("Action".equals(decorator.getDecoratorName()) && decorator.getExpression() instanceof JSCallExpressionImpl exp) {
                        JSArgumentList argumentList = exp.getArgumentList();
                        if (argumentList == null) continue;
                        for (PsiElement param : argumentList.getChildren()) {
                            if (param instanceof JSReferenceExpressionImpl p) {
                                this.key = p.getText();
                            }
                        }
                    }
                }
            }
        }
    }
}
