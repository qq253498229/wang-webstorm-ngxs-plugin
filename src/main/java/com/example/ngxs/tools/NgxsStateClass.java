package com.example.ngxs.tools;

import com.intellij.lang.javascript.psi.ecma6.impl.TypeScriptFunctionImpl;
import com.intellij.lang.javascript.psi.ecmal4.JSAttributeList;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

import java.util.Arrays;
import java.util.List;

public class NgxsStateClass {
    private PsiFile psiFile;
    private PsiElement psiElement;

    public NgxsStateClass(PsiFile psiFile, PsiElement psiElement) {
        this.psiFile = psiFile;
        this.psiElement = psiElement;
    }

    public List<NgxsActionImpl> getAllNgxsActionImpl() {
        return Arrays.stream(psiElement.getChildren())
                .filter(s -> s instanceof TypeScriptFunctionImpl)
                .filter(s -> Arrays.stream(s.getChildren()).anyMatch(t -> {
                    if (t instanceof JSAttributeList attr) {
                        return attr.getText().contains("@Action");
                    }
                    return false;
                }))
                .map(s -> new NgxsActionImpl(psiFile, s))
                .toList();
    }
}
