package com.example;

import com.intellij.lang.javascript.psi.ecma6.TypeScriptClass;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;

import java.util.List;

public class CustomJump extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        if (project == null || editor == null || psiFile == null) return;
        int offset = editor.getCaretModel().getOffset();
        PsiElement element = psiFile.findElementAt(offset);
        if (element == null) return;
        PsiElement parent = element.getParent();
        while (parent != null) {
            if (parent instanceof PsiReference reference) {
                PsiElement resolved = reference.resolve();
                if (resolved == null) return;
                if (resolved instanceof TypeScriptClass clazz) {
                    String qualifiedName = clazz.getQualifiedName();
                    if (qualifiedName == null) return;
                    List<PsiFile> files = FilenameIndex.getAllFilesByExt(project, "ts", GlobalSearchScope.projectScope(project))
                            .stream().map(s -> PsiManager.getInstance(project).findFile(s)).toList();
                    for (PsiFile file : files) {
                        String text = file.getText();
                        int idx = text.indexOf("@Action(" + qualifiedName + ")");
                        if (idx == -1) continue;
                        PsiElement elementAt = file.findElementAt(idx + qualifiedName.length());
                        if (elementAt instanceof Navigatable navigatable) {
                            navigatable.navigate(true);
                        }
                    }
                    return;
                }
            }
            parent = parent.getParent();
        }
    }

}
