package com.example.ngxs.tools;

import com.intellij.lang.javascript.psi.ecma6.TypeScriptClass;
import com.intellij.lang.javascript.psi.ecmal4.JSAttributeList;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

import java.util.Arrays;
import java.util.List;

public class JsResolver {
    private PsiFile psiFile;

    public JsResolver() {
    }

    public JsResolver(PsiFile psiFile) {
        this.psiFile = psiFile;
    }

    /// 判断文件包不包含ngxs的state
    public boolean isNgxsState() {
        PsiElement[] children = psiFile.getChildren();
        return Arrays.stream(children).anyMatch(e -> e instanceof TypeScriptClass
                && e.getChildren()[0] instanceof JSAttributeList
                && e.getText().contains("@State")
        );
    }

    /// 获取文件内全部state
    public List<NgxsStateClass> getAllStateClass() {
        PsiElement[] children = psiFile.getChildren();
        List<PsiElement> list = Arrays.stream(children).filter(e -> e instanceof TypeScriptClass
                && Arrays.stream(e.getChildren()).anyMatch(c -> c instanceof JSAttributeList)
                && e.getText().contains("@State")).toList();
        return list.stream().map(s -> new NgxsStateClass(psiFile, s)).toList();
    }

    /// 获取文件内全部Action实现
    public List<NgxsActionImpl> getAllNgxsActionImpl() {
        List<NgxsStateClass> allStateClass = getAllStateClass();
        return allStateClass.stream().flatMap(s -> s.getAllNgxsActionImpl().stream()).toList();
    }
}
