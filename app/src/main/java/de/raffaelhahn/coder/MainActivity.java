package de.raffaelhahn.coder;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;

import java.io.IOException;
import java.io.StringReader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        try {
            HTMLLexer lexer = new HTMLLexer(CharStreams.fromReader(new StringReader("<html><head><title>Test</title></head><body><h1>Test</h1></body></html>")));

            SpannableStringBuilder builder = new SpannableStringBuilder();
            Token token;
            while ((token = lexer.nextToken()) != null) {
                if(token.getType() == HTMLLexer.EOF) break;
                switch (token.getType()) {

                    case HTMLLexer.TAG_OPEN:
                        builder.append(token.getText(), new ForegroundColorSpan(Color.RED), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        break;
                    case HTMLLexer.TAG_CLOSE:
                        builder.append(token.getText(), new ForegroundColorSpan(Color.RED), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        break;
                    case HTMLLexer.TAG_SLASH:
                        builder.append(token.getText(), new ForegroundColorSpan(Color.RED), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        break;
                    case HTMLLexer.TAG_NAME:
                        builder.append(token.getText(), new ForegroundColorSpan(Color.RED), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        break;
                    case HTMLLexer.TAG_SLASH_CLOSE:
                        builder.append(token.getText(), new ForegroundColorSpan(Color.RED), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        break;
                    case HTMLLexer.TAG_EQUALS:
                        builder.append(token.getText(), new ForegroundColorSpan(Color.RED), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        break;
                    default:
                        builder.append("<font color=\"#0000FF\">");
                        break;
                }
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}