package de.raffaelhahn.coder.parsers;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;

import java.io.StringReader;

import de.raffaelhahn.coder.Colors;
import de.raffaelhahn.coder.antlr.HTMLLexer;

public class HTMLParser extends AbstractParser{


    @Override
    public SpannableStringBuilder addSyntaxHighlight(String text) {
        HTMLLexer lexer = new HTMLLexer(createCharStream(text));

        SpannableStringBuilder builder = new SpannableStringBuilder();
        Token token;
        while ((token = lexer.nextToken()) != null) {
            System.out.println(token.getType());
            if(token.getType() == HTMLLexer.EOF) break;
            switch (token.getType()) {

                // KEYWORDS
                case HTMLLexer.TAG:
                case HTMLLexer.TAG_CLOSE:
                case HTMLLexer.TAG_EQUALS:
                case HTMLLexer.TAG_NAME:
                case HTMLLexer.TAG_OPEN:
                case HTMLLexer.TAG_SLASH:
                case HTMLLexer.TAG_SLASH_CLOSE:
                    builder.append(token.getText(), new ForegroundColorSpan(Colors.keyword), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;

                //attribute
                case HTMLLexer.ATTRIBUTE:
                    builder.append(token.getText(), new ForegroundColorSpan(Colors.attribute), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;

                //STRING
                case HTMLLexer.SCRIPT:
                case HTMLLexer.SCRIPT_OPEN:
                case HTMLLexer.SCRIPT_BODY:
                    builder.append(token.getText(), new ForegroundColorSpan(Colors.string), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                case HTMLLexer.ATTVALUE_VALUE:
                    builder.append(token.getText(), new ForegroundColorSpan(Colors.string), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                default:
                    builder.append(token.getText(), new ForegroundColorSpan(Colors.variable), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
            }
        }
        return builder;
    }
}
