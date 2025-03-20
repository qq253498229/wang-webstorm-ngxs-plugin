package com.example;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.PsiNavigationSupport;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.FakePsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CustomNavigateItem extends FakePsiElement implements NavigationItem {
    private final PsiElement targetElement;
    private final String customName;
    private final Project project;

    public CustomNavigateItem(@NotNull PsiElement targetElement, @NotNull String customName) {
        this.targetElement = targetElement;
        this.customName = customName;
        this.project = targetElement.getProject();
    }

    @Override
    public @Nullable @NlsSafe String getName() {
        return customName;
    }

    @Override
    public @Nullable ItemPresentation getPresentation() {
        String location = targetElement.getContainingFile() != null
                ? targetElement.getContainingFile().getName()
                : "";
        return new PresentationData(customName, location, null, null);
    }

    @Override
    public void navigate(boolean requestFocus) {
        if (targetElement instanceof Navigatable) {
            ((Navigatable) targetElement).navigate(requestFocus);
        } else {
            PsiNavigationSupport.getInstance().createNavigatable(project, targetElement.getContainingFile().getVirtualFile(), targetElement.getTextOffset()).navigate(requestFocus);
        }
    }

    @Override
    public boolean canNavigate() {
        return targetElement instanceof Navigatable && ((Navigatable) targetElement).canNavigate();
    }

    @Override
    public boolean canNavigateToSource() {
        return canNavigate();
    }

    @Override
    public PsiElement getParent() {
        return targetElement.getParent();
    }
}
