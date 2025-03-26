package com.example.ngxs.jump;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.io.DataExternalizer;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

public class JumpDataExternalizer implements DataExternalizer<JumpDataHandler> {
    public static final JumpDataExternalizer INSTANCE = new JumpDataExternalizer();

    @Override
    public void save(@NotNull DataOutput out, JumpDataHandler handler) throws IOException {
        out.writeInt(handler.position());
        out.writeUTF(handler.key());
        out.writeUTF(handler.filepath());
        out.writeUTF(handler.filename());
        out.writeUTF(handler.projectName());
        out.writeUTF(handler.projectPath());
    }

    @Override
    public JumpDataHandler read(@NotNull DataInput in) throws IOException {
        int position = in.readInt();
        String key = in.readUTF();
        String filepath = in.readUTF();
        String filename = in.readUTF();
        String projectName = in.readUTF();
        String projectPath = in.readUTF();
        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(filepath);
        if (virtualFile == null) return null;
        ProjectManager projectManager = ProjectManager.getInstance();
        @NotNull Project[] openProjects = projectManager.getOpenProjects();
        Optional<@NotNull Project> currentFirst = Arrays.stream(openProjects)
                .filter(s -> projectName.equals(s.getName()) && projectPath.equals(s.getBasePath()))
                .findFirst();
        if (currentFirst.isEmpty()) return null;
        Project project = currentFirst.get();
        PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
        return new JumpDataHandler(position, key, filepath, filename, psiFile, project, projectName, projectPath);
    }
}
