package de.raffaelhahn.coder;

import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.widget.EditText;
import android.widget.Toast;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.BitSet;

import de.raffaelhahn.coder.antlr.HTMLLexer;

public class CodeTextWatcher implements TextWatcher {

    private EditText editText;

    public CodeTextWatcher(EditText editText) {
        this.editText = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        try {
            HTMLLexer lexer = new HTMLLexer(CharStreams.fromReader(new StringReader(s.toString())));
            lexer.addErrorListener(new ANTLRErrorListener() {
                @Override
                public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                    Toast.makeText(editText.getContext(), msg, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, boolean exact, BitSet ambigAlts, ATNConfigSet configs) {
                    Toast.makeText(editText.getContext(), "msg", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void reportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex, int stopIndex, BitSet conflictingAlts, ATNConfigSet configs) {
                    Toast.makeText(editText.getContext(), "msg", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void reportContextSensitivity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, int prediction, ATNConfigSet configs) {
                    Toast.makeText(editText.getContext(), "msg", Toast.LENGTH_SHORT).show();
                }
            });

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
            editText.removeTextChangedListener(this);
            int selectionStart = editText.getSelectionStart();
            editText.setText(builder);
            editText.addTextChangedListener(this);
            editText.setSelection(selectionStart);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
