package com.example.ngxs.jump;

import com.example.ngxs.tools.JsResolver;
import com.example.ngxs.tools.NgxsActionImpl;
import com.intellij.lang.javascript.TypeScriptFileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.indexing.*;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JumpStateIndexer extends FileBasedIndexExtension<String, JumpDataHandler> {
    public static final ID<String, JumpDataHandler> INDEXER_JUMP = ID.create("com.example.indexes.INDEX_JUMP_STATE");
    private final KeyDescriptor<String> myKeyDescriptor = new EnumeratorStringDescriptor();

    @Override
    public @NotNull ID<String, JumpDataHandler> getName() {
        return INDEXER_JUMP;
    }


    @Override
    public @NotNull DataIndexer<String, JumpDataHandler, FileContent> getIndexer() {
        return fileContent -> {
            PsiFile psiFile = fileContent.getPsiFile();
            final Map<String, JumpDataHandler> r = new HashMap<>();
            // js解析器
            JsResolver resolver = new JsResolver(psiFile);
            if (!resolver.isNgxsState()) return r;
            List<NgxsActionImpl> allNgxsActionImpl = resolver.getAllNgxsActionImpl();
            if (!allNgxsActionImpl.isEmpty()) {
                Project project = psiFile.getProject();
                String projectName = project.getName();
                String projectPath = project.getBasePath();
                for (NgxsActionImpl impl : allNgxsActionImpl) {
                    int position = impl.position;
                    String key = impl.key;
                    String path = psiFile.getVirtualFile().getPath();
                    String filename = psiFile.getName();
                    JumpDataHandler handler = new JumpDataHandler(position, key, path, filename, psiFile, project, projectName, projectPath);
                    r.put(key, handler);
                }
            }
            return r;
        };
    }

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }

    @Override
    public FileBasedIndex.@NotNull InputFilter getInputFilter() {
        return virtualFile -> {
            if (virtualFile.getFileType() != TypeScriptFileType.INSTANCE) return false;
            if (virtualFile.getName().endsWith(".d.ts")) return false;
            if (virtualFile.getPath().contains("/node_modules/")) return false;
            try {
                String text = new String(virtualFile.contentsToByteArray());
                if (!text.contains("@Action(")) return false;
            } catch (IOException e) {
                return false;
            }
            return true;
        };
    }

    @Override
    public @NotNull KeyDescriptor<String> getKeyDescriptor() {
        return this.myKeyDescriptor;
    }

    @Override
    public @NotNull DataExternalizer<JumpDataHandler> getValueExternalizer() {
        return JumpDataExternalizer.INSTANCE;
    }
}
