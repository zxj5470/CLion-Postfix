//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.jetbrains.cidr.lang;

import com.intellij.injected.editor.VirtualFileWindow;
import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.registry.Registry;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.PlatformUtils;
import com.intellij.util.containers.FactoryMap;
import com.jetbrains.cidr.lang.editor.colors.OCFileHighlighter;
import com.jetbrains.cidr.lang.lexer.OCLexerSettings;
import com.jetbrains.cidr.lang.psi.OCFile;
import com.jetbrains.cidr.lang.workspace.OCLanguageKindCalculator;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public class OCLanguage extends Language {
    public static final Key<Boolean> LANGUAGE_SUPPORT_DISABLED;
    private static final OCLanguage ourInstance;

    public static boolean enableObjectiveCSettings() {
        return PlatformUtils.isAppCode() || Registry.is("clion.enable.objc.settings");
    }

    @NotNull
    public static OCLanguage getInstance() {
        return ourInstance;
    }

    public OCLanguage() {
        super("ObjectiveC");
        SyntaxHighlighterFactory.LANGUAGE_FACTORY.addExplicitExtension(this, new SyntaxHighlighterFactory() {
            private Map<OCLexerSettings, OCFileHighlighter> myHighlighters = FactoryMap.create(OCFileHighlighter::new);

            @NotNull
            public SyntaxHighlighter getSyntaxHighlighter(Project var1, VirtualFile var2) {
                OCLanguageKind var3 = CLanguageKind.maxLanguage(var1);
                if (var2 != null && !(var2 instanceof VirtualFileWindow) && var2.isValid() && var1 != null) {
                    PsiFile var4 = PsiManager.getInstance(var1).findFile(var2);
                    OCFile var5 = var4 instanceof OCFile ? (OCFile)var4 : null;
                    if (var5 != null) {
                        var3 = OCLanguageKindCalculator.calculateLanguageKindFast(var5);
                    }
                }

                OCLexerSettings var6 = OCLexerSettings.forLanguage(var3).forHighlighting().allowNullabilityKeywords().allowGccAutoType().allowAvailabilityExpression().allowMsvcExtensions().build();
                return this.myHighlighters.get(var6);
            }
        });
    }

    public boolean isCaseSensitive() {
        return true;
    }

    @NotNull
    public String getDisplayName() {
        return enableObjectiveCSettings() ? "C/C++/Objective-C" : "C/C++";
    }

    static {
        LANGUAGE_SUPPORT_DISABLED = Key.create("CPP_SUPPORT_DISABLED");
        ourInstance = new OCLanguage();
    }
}
