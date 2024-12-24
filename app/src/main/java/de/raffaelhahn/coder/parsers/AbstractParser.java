package de.raffaelhahn.coder.parsers;

import android.text.SpannableStringBuilder;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointBuffer;
import org.antlr.v4.runtime.CodePointCharStream;

import java.io.IOException;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public abstract class AbstractParser {

    public abstract SpannableStringBuilder addSyntaxHighlight(String text);

    protected CharStream createCharStream(String text) {
        try {
            return CharStreams.fromReader(new StringReader(text));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
