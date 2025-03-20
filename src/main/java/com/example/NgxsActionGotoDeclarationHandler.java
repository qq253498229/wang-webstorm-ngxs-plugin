package com.example;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.Nullable;

public class NgxsActionGotoDeclarationHandler implements GotoDeclarationHandler {
    @Override
    public PsiElement @Nullable [] getGotoDeclarationTargets(
            @Nullable PsiElement psiElement, int i, Editor editor) {
        if (psiElement == null) {
            return null;
        }
        // 判断点击的文本是否为 Loading（可根据实际情况细化判断逻辑，比如检查引用类型、上下文等）
        if (!"Loading".equals(psiElement.getText())) {
            return null;
        }
        Project project = psiElement.getProject();
        PsiElement targetAction = findTargetElement(project, "system.action.ts", "export class Loading");
        PsiElement targetState = findTargetElement(project, "system.state.ts", "@Action(SystemAction.Loading)");

        if (targetAction == null && targetState == null) {
            return null;
        }
        if (targetAction != null && targetState != null) {
            return new PsiElement[]{
                    new CustomNavigateItem(targetAction, "Action"),
                    new CustomNavigateItem(targetState, "Implement"),
            };
        } else if (targetAction != null) {
            return new PsiElement[]{new CustomNavigateItem(targetAction, "Action")};
        } else {
            return new PsiElement[]{new CustomNavigateItem(targetState, "Implement")};
        }
    }


    /**
     * 根据文件名和关键字查找目标 PsiElement
     * 实际实现中可以通过 FileBasedIndex 或者直接遍历项目文件来查找目标文件，然后利用 PsiFile 查找关键字位置
     */
    @Nullable
    private PsiElement findTargetElement(Project project, String fileName, String searchText) {
        // 这里只是示例逻辑，实际实现需要利用 PsiManager、FilenameIndex 等 API 获取对应文件，然后遍历查找匹配的 PsiElement
        PsiFile[] files = FilenameIndex.getFilesByName(project, fileName, GlobalSearchScope.projectScope(project));
        for (PsiFile file : files) {
            // 简单使用文本查找（实际建议基于 PSI 结构遍历查找更准确）
            String fileText = file.getText();
            int index = fileText.indexOf(searchText);
            if (index >= 0) {
                return file.findElementAt(index);
            }
        }
        return null;
    }
}
