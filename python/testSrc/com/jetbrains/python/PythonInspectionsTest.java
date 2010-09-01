package com.jetbrains.python;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ex.LocalInspectionToolWrapper;
import com.jetbrains.python.fixtures.PyLightFixtureTestCase;
import com.jetbrains.python.inspections.*;
import com.jetbrains.python.psi.LanguageLevel;
import com.jetbrains.python.psi.impl.PythonLanguageLevelPusher;

/**
 * @author yole
 */
public class PythonInspectionsTest extends PyLightFixtureTestCase {
  public void testReturnValueFromInit() {
    LocalInspectionTool inspection = new PyReturnFromInitInspection();
    doTest(getTestName(true), inspection);
  }

  private void doTest(String testName, LocalInspectionTool localInspectionTool) {
    myFixture.testInspection("inspections/" + testName, new LocalInspectionToolWrapper(localInspectionTool));
  }

  private void doTestWithPy3k(String testName, LocalInspectionTool localInspectionTool) {
    doTestWithLanguageLevel(testName, localInspectionTool, LanguageLevel.PYTHON30);
  }

  private void doTestWithLanguageLevel(String testName,
                                       LocalInspectionTool localInspectionTool,
                                       LanguageLevel languageLevel) {
    PythonLanguageLevelPusher.setForcedLanguageLevel(myFixture.getProject(), languageLevel);
    try {
      doTest(testName, localInspectionTool);
    }
    finally {
      PythonLanguageLevelPusher.setForcedLanguageLevel(myFixture.getProject(), null);
    }
  }

  public void testPyMethodFirstArgAssignmentInspection() {
    LocalInspectionTool inspection = new PyMethodFirstArgAssignmentInspection();
    doTest(getTestName(false), inspection);
  }

  public void testPyUnreachableCodeInspection() {
    LocalInspectionTool inspection = new PyUnreachableCodeInspection();
    doTest(getTestName(false), inspection);
  }

  public void testPyUnresolvedReferencesInspection() {
    LocalInspectionTool inspection = new PyUnresolvedReferencesInspection();
    doTest(getTestName(false), inspection);
  }

  public void testPyArgumentListInspection() {
    LocalInspectionTool inspection = new PyArgumentListInspection();
    doTest(getTestName(false), inspection);
  }

  public void testPyMethodParametersInspection() {
    LocalInspectionTool inspection = new PyMethodParametersInspection();
    doTest(getTestName(false), inspection);
  }

  public void testPyArgumentListInspection3K() {
    LocalInspectionTool inspection = new PyArgumentListInspection();
    doTestWithPy3k(getTestName(false), inspection);
  }

  public void testPyRedeclarationInspection() {
    LocalInspectionTool inspection = new PyRedeclarationInspection();
    doTest(getTestName(false), inspection);
  }

  public void testPyStringFormatInspection() {
    LocalInspectionTool inspection = new PyStringFormatInspection();
    doTest(getTestName(false), inspection);
  }

  public void testPyMethodOverridingInspection() {
    LocalInspectionTool inspection = new PyMethodOverridingInspection();
    doTest(getTestName(false), inspection);
  }

  public void testPyTrailingSemicolonInspection() {
    LocalInspectionTool inspection = new PyTrailingSemicolonInspection();
    doTest(getTestName(false), inspection);
  }

  public void testPyUnusedLocalVariableInspection() {
    PyUnusedLocalInspection inspection = new PyUnusedLocalInspection();
    inspection.ignoreTupleUnpacking = false;
    doTest(getTestName(false), inspection);
  }

  public void testPyUnusedVariableTupleUnpacking() {
    doHighlightingTest(PyUnusedLocalInspection.class);
  }

  public void testPyUnusedLocalFunctionInspection() {
    PyUnusedLocalInspection inspection = new PyUnusedLocalInspection();
    doTest(getTestName(false), inspection);
  }

  public void testPyDictCreationInspection() {
    doHighlightingTest(PyDictCreationInspection.class);
  }

  public void testPyDeprecatedModulesInspection() {
    PythonLanguageLevelPusher.setForcedLanguageLevel(myFixture.getProject(), LanguageLevel.PYTHON25);
    try {
      LocalInspectionTool inspection = new PyDeprecatedModulesInspection();
      doTest(getTestName(false), inspection);
    }
    finally {
      PythonLanguageLevelPusher.setForcedLanguageLevel(myFixture.getProject(), null);
    }
  }

  public void testPyTupleAssignmentBalanceInspection() {
    LocalInspectionTool inspection = new PyTupleAssignmentBalanceInspection();
    doTest(getTestName(false), inspection);
  }

  public void testPyTupleAssignmentBalanceInspection2() {
    LocalInspectionTool inspection = new PyTupleAssignmentBalanceInspection();
    doTestWithPy3k(getTestName(false), inspection);
  }

  public void testPyClassicStyleClassInspection() {
    LocalInspectionTool inspection = new PyClassicStyleClassInspection();
    doTest(getTestName(false), inspection);
  }

  public void testPyExceptClausesOrderInspection() {
    PythonLanguageLevelPusher.setForcedLanguageLevel(myFixture.getProject(), LanguageLevel.PYTHON26);
    try {
      myFixture.configureByFile("inspections/" + getTestName(false) + "/test.py");
      myFixture.enableInspections(PyExceptClausesOrderInspection.class);
      myFixture.checkHighlighting(true, false, false);
    }
    finally {
      PythonLanguageLevelPusher.setForcedLanguageLevel(myFixture.getProject(), null);
    }
  }

  public void testPyExceptionInheritInspection() {
    LocalInspectionTool inspection = new PyExceptionInheritInspection();
    doTest(getTestName(false), inspection);
  }

  public void testPyDefaultArgumentInspection() {
    LocalInspectionTool inspection = new PyDefaultArgumentInspection();
    doTest(getTestName(false), inspection);
  }

  public void testPyRaisingNewStyleClassInspection() {
    LocalInspectionTool inspection = new PyRaisingNewStyleClassInspection();
    doTestWithLanguageLevel(getTestName(false), inspection, LanguageLevel.PYTHON24);
  }

  public void testPyUnboundLocalVariableInspection() {
    LocalInspectionTool inspection = new PyUnboundLocalVariableInspection();
    doTest(getTestName(false), inspection);
  }

  public void testPyUnboundLocalVariableInspectionPy3k() {
    LocalInspectionTool inspection = new PyUnboundLocalVariableInspection();
    doTestWithPy3k(getTestName(false), inspection);
  }

  public void testPyDocstringInspection() {
    LocalInspectionTool inspection = new PyDocstringInspection();
    doTest(getTestName(false), inspection);
  }

  public void testPyStatementEffectInspection() {
    LocalInspectionTool inspection = new PyStatementEffectInspection();
    doTest(getTestName(false), inspection);
  }

  public void testPySimplifyBooleanCheckInspection() {
    doHighlightingTest(PySimplifyBooleanCheckInspection.class);
  }

  public void testPyFromFutureImportInspection() {
    doHighlightingTest(PyFromFutureImportInspection.class);
  }

  public void testPyFromFutureImportInspectionDocString() {
    myFixture.configureByFile("inspections/PyFromFutureImportInspection/module_docstring.py");
    myFixture.enableInspections(PyFromFutureImportInspection.class);
    myFixture.checkHighlighting(true, false, false);
  }

  public void testPyComparisonWithNoneInspection() {
    LocalInspectionTool inspection = new PyComparisonWithNoneInspection();
    doTest(getTestName(false), inspection);
  }

  public void testPyStringExceptionInspection() {
    LocalInspectionTool inspection = new PyStringExceptionInspection();
    doTest(getTestName(false), inspection);
  }

  public void testPySuperArgumentsInspection() {
    LocalInspectionTool inspection = new PySuperArgumentsInspection();
    doTest(getTestName(false), inspection);
  }

  public void testPyByteLiteralInspection() {
    LocalInspectionTool inspection = new PyByteLiteralInspection();
    doTest(getTestName(false), inspection);
  }

  public void testPyTupleItemAssignmentInspection() {
    LocalInspectionTool inspection = new PyTupleItemAssignmentInspection();
    doTest(getTestName(false), inspection);
  }

  public void testPyInitNewSignatureInspection() throws Exception {
    LocalInspectionTool inspection = new PyInitNewSignatureInspection();
    doTest(getTestName(false), inspection);
  }

  public void testPyCallingNonCallableInspection() throws Exception {
    doHighlightingTest(PyCallingNonCallableInspection.class);
  }

  private void doHighlightingTest(final Class<? extends PyInspection> inspectionClass) {
    myFixture.configureByFile("inspections/" + getTestName(true) + "/test.py");
    myFixture.enableInspections(inspectionClass);
    myFixture.checkHighlighting(true, false, false);
  }

  public void testPyPropertyAccessInspection() {
    doTestWithLanguageLevel(getTestName(false), new PyPropertyAccessInspection(), LanguageLevel.PYTHON26);
  }

  public void testPyPropertyDefinitionInspection25() {
    doTestWithLanguageLevel(getTestName(false), new PyPropertyDefinitionInspection(), LanguageLevel.PYTHON25);
  }

  public void testPyPropertyDefinitionInspection26() {
    doTestWithLanguageLevel(getTestName(false), new PyPropertyDefinitionInspection(), LanguageLevel.PYTHON26);
  }

  public void testInconsistentIndentation() {
    doHighlightingTest(PyInconsistentIndentationInspection.class);
  }
}
