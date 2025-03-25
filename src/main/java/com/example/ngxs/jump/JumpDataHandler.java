package com.example.ngxs.jump;

import com.intellij.psi.PsiFile;

import java.util.Objects;

public record JumpDataHandler(
        int position,
        String key,
        String filepath,
        String filename,
        PsiFile psiFile) {

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof JumpDataHandler that)) return false;
        return position() == that.position()
                && Objects.equals(key(), that.key())
                && Objects.equals(filepath(), that.filepath())
                && Objects.equals(filename(), that.filename());
    }

    @Override
    public int hashCode() {
        return Objects.hash(position(), key(), filepath(), filename());
    }
}
