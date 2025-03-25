package com.example.ngxs.jump;

import com.intellij.lang.javascript.psi.ecma6.TypeScriptClass;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.FileBasedIndex;

import java.util.List;

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
                if (resolved == null) return;
                if (resolved instanceof TypeScriptClass clazz) {
                    String qualifiedName = clazz.getQualifiedName();
                    if (qualifiedName == null) return;
                    GlobalSearchScope scope = GlobalSearchScope.projectScope(project);
                    FileBasedIndex index = FileBasedIndex.getInstance();
                    List<JumpDataHandler> handlers = index.getValues(INDEXER_JUMP, qualifiedName, scope);
                    for (JumpDataHandler handler : handlers) {
                        // 这里要根据handler中的属性跳转到文件的对应位置
                        PsiFile psiFile1 = handler.psiFile();
                        VirtualFile virtualFile = psiFile1.getVirtualFile();
                        OpenFileDescriptor descriptor = new OpenFileDescriptor(project, virtualFile, handler.position());
                        descriptor.navigate(true);
                    }
                    return;
                }
            }
            parent = parent.getParent();
        }
    }

}
