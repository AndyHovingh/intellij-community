package com.jetbrains.python.inspections;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.jetbrains.python.PyTokenTypes;
import com.jetbrains.python.lexer.PythonIndentingLexer;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yole
 */
public class PyInconsistentIndentationInspection extends PyInspection {
  @Nls
  @NotNull
  @Override
  public String getDisplayName() {
    return "Inconsistent indentation";
  }

  @Override
  public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
    return new IndentValidator(file, manager, isOnTheFly).invoke();
  }

  private static class IndentValidator {
    private PsiFile myFile;
    private InspectionManager myManager;
    private boolean myOnTheFly;
    private List<ProblemDescriptor> myProblems;
    private int myLastTabs = 0;
    private int myLastSpaces = 0;

    public IndentValidator(PsiFile file, InspectionManager manager, boolean isOnTheFly) {
      myFile = file;
      myManager = manager;
      myOnTheFly = isOnTheFly;
      myProblems = new ArrayList<ProblemDescriptor>();
    }

    public ProblemDescriptor[] invoke() {
      PythonIndentingLexer lexer = new PythonIndentingLexer();
      final String text = myFile.getText();
      lexer.start(text);
      while (lexer.getTokenType() != null) {
        final IElementType tokenType = lexer.getTokenType();
        if (tokenType == PyTokenTypes.STATEMENT_BREAK) {
          lexer.advance();
          while(lexer.getTokenType() != null && lexer.getTokenType() != PyTokenTypes.LINE_BREAK) {
            lexer.advance();
          }
          if (lexer.getTokenType() == PyTokenTypes.LINE_BREAK) {
            String indent = text.substring(lexer.getTokenStart(), lexer.getTokenEnd());
            validateIndent(lexer.getTokenStart(), indent);
          }
        }
        lexer.advance();
      }
      return myProblems.toArray(new ProblemDescriptor[myProblems.size()]);
    }

    private void validateIndent(final int tokenStart, String indent) {
      int lastLF = indent.lastIndexOf('\n');
      String lastLineIndent = indent.substring(lastLF+1);
      int spaces = 0;
      int tabs = 0;
      final int length = lastLineIndent.length();
      for (int i = 0; i < length; i++) {
        final char c = lastLineIndent.charAt(i);
        if (c == ' ') spaces++;
        else if (c == '\t') tabs++;
      }
      final int problemStart = tokenStart + lastLF + 1;
      if (spaces > 0 && tabs > 0) {
        reportProblem("Inconsistent indentation: mix of tabs and spaces", problemStart, length);
      }
      else if (spaces > 0 && myLastTabs > 0) {
        reportProblem("Inconsistent indentation: previous line used tabs, this line uses spaces", problemStart, length);
      }
      else if (tabs > 0 && myLastSpaces > 0) {
        reportProblem("Inconsistent indentation: previous line used spaces, this line uses tabs", problemStart, length);
      }
      myLastTabs = tabs;
      myLastSpaces = spaces;
    }

    private void reportProblem(final String descriptionTemplate, final int problemStart, final int problemLength) {
      PsiElement elt = myFile.findElementAt(problemStart);
      int startOffset = problemStart - elt.getTextRange().getStartOffset();
      int endOffset = startOffset + problemLength;
      myProblems.add(myManager.createProblemDescriptor(elt, new TextRange(startOffset, endOffset),
                                                       descriptionTemplate,
                                                   ProblemHighlightType.GENERIC_ERROR_OR_WARNING, myOnTheFly));
    }
  }
}
