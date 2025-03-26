package com.example.ngxs.jump;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.lang.javascript.psi.ecma6.TypeScriptClass;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.FileBasedIndex;

import java.util.List;

import static com.example.ngxs.Message.message;
import static com.example.ngxs.jump.JumpStateIndexer.INDEXER_JUMP;

public class JumpAction extends AnAction {

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
                if (resolved == null) break;
                if (resolved instanceof TypeScriptClass clazz) {
                    String qualifiedName = clazz.getQualifiedName();
                    if (qualifiedName == null) break;
                    GlobalSearchScope scope = GlobalSearchScope.projectScope(project);
                    FileBasedIndex index = FileBasedIndex.getInstance();
                    List<JumpDataHandler> handlers = index.getValues(INDEXER_JUMP, qualifiedName, scope);
                    for (JumpDataHandler handler : handlers) {
                        handler.navigate();
                    }
                    break;
                }
            }
            parent = parent.getParent();
        }
        String message = message("action.jumpToNgxsState.notFound");
        // 在光标处显示提示信息
        HintManager.getInstance().showInformationHint(editor, message);
    }

}
