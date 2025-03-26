package com.example.ngxs.jump;

import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;

import java.util.Objects;

public record JumpDataHandler(
        int position,
        String key,
        String filepath,
        String filename,
        PsiFile psiFile,
        Project project,
        String projectName,
        String projectPath) {

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof JumpDataHandler that)) return false;
        return position() == that.position()
                && Objects.equals(key(), that.key())
                && Objects.equals(filepath(), that.filepath())
                && Objects.equals(filename(), that.filename())
                && Objects.equals(projectName(), that.projectName())
                && Objects.equals(projectPath(), that.projectPath())
                ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(position(), key(), filepath(), filename(), projectName(), projectPath());
    }

    public void navigate() {
        VirtualFile virtualFile = psiFile.getVirtualFile();
        OpenFileDescriptor descriptor = new OpenFileDescriptor(project, virtualFile, position);
        descriptor.navigate(true);
    }
}
