package com.example.ngxs.jump;

import com.intellij.lang.javascript.TypeScriptFileType;
import com.intellij.psi.PsiFile;
import com.intellij.util.indexing.*;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            String content = new String(fileContent.getContent());
            final Map<String, JumpDataHandler> r = new HashMap<>();
            Pattern pattern = Pattern.compile("@Action\\((.*)\\)");
            Matcher matcher = pattern.matcher(content);
            boolean hasMatch = matcher.find();
            if (hasMatch) {
                String filename = psiFile.getName();
                String path = psiFile.getVirtualFile().getPath();
                do {
                    int start = matcher.start();
                    String key = matcher.group(1).trim();
                    JumpDataHandler handler = new JumpDataHandler(start, key, path, filename, psiFile);
                    r.put(key, handler);
                } while (matcher.find());
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
