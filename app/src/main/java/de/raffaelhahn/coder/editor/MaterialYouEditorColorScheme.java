package de.raffaelhahn.coder.editor;

import android.content.Context;
import android.graphics.Color;

import androidx.annotation.NonNull;

import com.google.android.material.color.MaterialColors;

import org.eclipse.tm4e.core.internal.theme.Theme;
import org.eclipse.tm4e.core.internal.theme.raw.IRawTheme;
import org.eclipse.tm4e.core.internal.theme.raw.RawTheme;
import org.eclipse.tm4e.core.registry.IThemeSource;

import java.util.List;

import de.raffaelhahn.coder.R;
import io.github.rosemoe.sora.langs.textmate.TextMateColorScheme;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.model.ThemeModel;
import io.github.rosemoe.sora.widget.CodeEditor;
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme;

public class MaterialYouEditorColorScheme  extends EditorColorScheme implements ThemeRegistry.ThemeChangeListener {

    private Theme theme;

    @Deprecated
    private IThemeSource themeSource;

    private ThemeModel currentTheme;

    private final ThemeRegistry themeRegistry;
    private static Context context;

    public MaterialYouEditorColorScheme(ThemeRegistry themeRegistry, ThemeModel themeModel) throws Exception {
        this.themeRegistry = themeRegistry;

        currentTheme = themeModel;
    }

    public static MaterialYouEditorColorScheme create(Context context) throws Exception {
        ThemeRegistry themeRegistry = ThemeRegistry.getInstance();
        MaterialYouEditorColorScheme.context = context;
        return new MaterialYouEditorColorScheme(themeRegistry, themeRegistry.getCurrentThemeModel());
    }

    public void setTheme(ThemeModel themeModel) {
        currentTheme = themeModel;
        super.colors.clear();
        this.theme = themeModel.getTheme();
        this.themeSource = themeModel.getThemeSource();
        applyDefault();
    }

    @Override
    public void onChangeTheme(ThemeModel newTheme) {
        setTheme(newTheme);
    }

    @Override
    public void applyDefault() {
        super.applyDefault();

        if (themeRegistry != null && !themeRegistry.hasListener(this)) {
            themeRegistry.addListener(this);
        }

        applyMaterialYouTheme();

    }

    private void applyMaterialYouTheme() {
        setColor(LINE_DIVIDER, Color.TRANSPARENT);

        setColor(SELECTION_INSERT, Color.parseColor("#ddbb88"));
        setColor(SELECTED_TEXT_BACKGROUND, Color.parseColor("#770811"));
        setColor(NON_PRINTABLE_CHAR, Color.parseColor("#103050"));
        setColor(CURRENT_LINE, Color.parseColor("#082050"));

        setColor(WHOLE_BACKGROUND, MaterialColors.getColorOrNull(context, com.google.android.material.R.attr.colorSurface));
        setColor(LINE_NUMBER_BACKGROUND, MaterialColors.getColorOrNull(context, com.google.android.material.R.attr.colorSurface));

        setColor(LINE_NUMBER, MaterialColors.getColorOrNull(context, com.google.android.material.R.attr.colorAccent));
        setColor(LINE_NUMBER_CURRENT, MaterialColors.getColorOrNull(context, com.google.android.material.R.attr.colorAccent));
        setColor(TEXT_NORMAL, Color.parseColor("#6688cc"));
        /*setColor(COMPLETION_WND_BACKGROUND, Color.parseColor(completionWindowBackground));
        setColor(COMPLETION_WND_ITEM_CURRENT, Color.parseColor(completionWindowBackgroundCurrent));
        setColor(HIGHLIGHTED_DELIMITERS_FOREGROUND, Color.parseColor(highlightedDelimetersForeground));
        setColor(DIAGNOSTIC_TOOLTIP_BACKGROUND, Color.parseColor(tooltipBackground));
        setColor(DIAGNOSTIC_TOOLTIP_BRIEF_MSG, Color.parseColor(tooltipBriefMessageColor));
        setColor(DIAGNOSTIC_TOOLTIP_DETAILED_MSG, Color.parseColor(tooltipDetailedMessageColor));
        setColor(DIAGNOSTIC_TOOLTIP_ACTION, Color.parseColor(tooltipActionColor));*/
        setColor(BLOCK_LINE, Color.parseColor("#002952"));
        setColor(BLOCK_LINE_CURRENT, Color.parseColor("#204972"));



    }

    private void applyMaterialYouThemeA() {
        setColor(LINE_DIVIDER, Color.TRANSPARENT);

        setColor(SELECTION_INSERT, Color.parseColor("#ddbb88"));
        setColor(SELECTED_TEXT_BACKGROUND, Color.parseColor("#770811"));
        setColor(NON_PRINTABLE_CHAR, Color.parseColor("#103050"));
        setColor(CURRENT_LINE, Color.parseColor("#082050"));

        setColor(WHOLE_BACKGROUND, Color.parseColor("#000c18"));
        setColor(LINE_NUMBER_BACKGROUND, Color.parseColor("#000c18"));

        setColor(LINE_NUMBER, Color.parseColor("#406385"));
        setColor(LINE_NUMBER_CURRENT, Color.parseColor("#80a2c2"));
        setColor(TEXT_NORMAL, Color.parseColor("#6688cc"));
        /*setColor(COMPLETION_WND_BACKGROUND, Color.parseColor(completionWindowBackground));
        setColor(COMPLETION_WND_ITEM_CURRENT, Color.parseColor(completionWindowBackgroundCurrent));
        setColor(HIGHLIGHTED_DELIMITERS_FOREGROUND, Color.parseColor(highlightedDelimetersForeground));
        setColor(DIAGNOSTIC_TOOLTIP_BACKGROUND, Color.parseColor(tooltipBackground));
        setColor(DIAGNOSTIC_TOOLTIP_BRIEF_MSG, Color.parseColor(tooltipBriefMessageColor));
        setColor(DIAGNOSTIC_TOOLTIP_DETAILED_MSG, Color.parseColor(tooltipDetailedMessageColor));
        setColor(DIAGNOSTIC_TOOLTIP_ACTION, Color.parseColor(tooltipActionColor));*/
        setColor(BLOCK_LINE, Color.parseColor("#002952"));
        setColor(BLOCK_LINE_CURRENT, Color.parseColor("#204972"));



    }

    @Override
    public boolean isDark() {
        var superIsDark = super.isDark();
        if (superIsDark) {
            return true;
        }
        if (currentTheme != null) {
            return currentTheme.isDark();
        }
        return false;
    }

    @Override
    public int getColor(int type) {
        if (type >= 255) {
            // Cache colors in super class
            var superColor = super.getColor(type);
            if (superColor == 0) {
                if (theme != null) {
                    String color = theme.getColor(type - 255);
                    var newColor = color != null ? Color.parseColor(color) : super.getColor(TEXT_NORMAL);
                    super.colors.put(type, newColor);
                    return newColor;
                }
                return super.getColor(TEXT_NORMAL);
            } else {
                return superColor;
            }
        }
        return super.getColor(type);
    }

    @Override
    public void detachEditor(@NonNull CodeEditor editor) {
        super.detachEditor(editor);
        themeRegistry.removeListener(this);
    }

    @Override
    public void attachEditor(@NonNull CodeEditor editor) {
        super.attachEditor(editor);
        try {
            themeRegistry.loadTheme(currentTheme);
        } catch (Exception e) {
            //throw new RuntimeException(e);
        }
        setTheme(currentTheme);
    }


    @Deprecated
    public IThemeSource getThemeSource() {
        return themeSource;
    }
}
