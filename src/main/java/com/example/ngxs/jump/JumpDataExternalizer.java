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

public class JumpDataExternalizer implements DataExternalizer<JumpDataHandler> {
    public static final JumpDataExternalizer INSTANCE = new JumpDataExternalizer();

    @Override
    public void save(@NotNull DataOutput out, JumpDataHandler handler) throws IOException {
        out.writeInt(handler.position());
        out.writeUTF(handler.key());
        out.writeUTF(handler.filepath());
        out.writeUTF(handler.filename());
    }

    @Override
    public JumpDataHandler read(@NotNull DataInput in) throws IOException {
        int position = in.readInt();
        String key = in.readUTF();
        String filepath = in.readUTF();
        String filename = in.readUTF();
        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(filepath);
        if (virtualFile == null) return null;
        Project project = ProjectManager.getInstance().getDefaultProject();
        PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
        return new JumpDataHandler(position, key, filepath, filename, psiFile);
    }
}
