/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pgxp.pto.constants;

/**
 *
 * @author 70744416353
 */
import java.nio.file.Path;

/**
 * A data object for mapping words to file paths.
 */
public class Word {

    private Path pth;
    private String wrd;

    public static Builder builder() {
        return new Builder();
    }

    public Path path() {
        return this.pth;
    }

    public String word() {
        return this.wrd;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (!(other instanceof Word)) {
            return false;
        }
        Word otherWord = (Word) other;
        return this.path().equals(otherWord.path()) && this.word().equals(otherWord.word());
    }

    @Override
    public int hashCode() {
        return this.word().hashCode() ^ this.path().hashCode();
    }

    public static class Builder {

        private Path pth;
        private String wrd;

        Builder() {
        }

        public Builder path(Path path) {
            this.pth = path;
            return this;
        }

        public Builder word(String word) {
            this.wrd = word;
            return this;
        }

        public Word build() {
            Word out = new Word();
            out.pth = this.pth;
            out.wrd = this.wrd;
            return out;
        }
    }
}
