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
import de.raffaelhahn.coder.parsers.HTMLParser;

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
            /*lexer.addErrorListener(new ANTLRErrorListener() {
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
            });*/

            SpannableStringBuilder builder = new HTMLParser().addSyntaxHighlight(s.toString());

            editText.removeTextChangedListener(this);
            int selectionStart = editText.getSelectionStart();
            editText.setText(builder);
            editText.addTextChangedListener(this);
            editText.setSelection(selectionStart);


    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
