package com.siyeh.ig.style;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.siyeh.ig.*;
import com.siyeh.ig.fixes.NormalizeDeclarationFix;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MultipleTypedDeclarationInspection extends VariableInspection {
    private final NormalizeDeclarationFix fix = new NormalizeDeclarationFix();

    public String getID(){
        return "VariablesOfDifferentTypesInDeclaration";
    }
    public String getDisplayName() {
        return "Variables of different types in one declaration";
    }

    public String getGroupDisplayName() {
        return GroupNames.STYLE_GROUP_NAME;
    }

    public String buildErrorString(PsiElement location) {
        return "Variables of different types in one declaration #loc";
    }

    public BaseInspectionVisitor createVisitor(InspectionManager inspectionManager, boolean onTheFly) {
        return new MultiplyTypedDeclarationVisitor(this, inspectionManager,
                onTheFly);
    }

    public InspectionGadgetsFix buildFix(PsiElement location) {
        return fix;
    }

    private static class MultiplyTypedDeclarationVisitor
            extends BaseInspectionVisitor {
        private MultiplyTypedDeclarationVisitor(BaseInspection inspection,
                                                InspectionManager inspectionManager,
                                                boolean isOnTheFly) {
            super(inspection, inspectionManager, isOnTheFly);
        }

        public void visitDeclarationStatement(PsiDeclarationStatement statement) {
            super.visitDeclarationStatement(statement);
            final PsiElement[] elements = statement.getDeclaredElements();
            if (elements.length > 1) {
                final PsiType baseType = ((PsiVariable) elements[0]).getType();
                boolean hasMultipleTypes = false;
                for (int i = 1; i < elements.length; i++) {
                    final PsiLocalVariable var = (PsiLocalVariable) elements[i];
                    final PsiType variableType = var.getType();
                    if (!variableType.equals(baseType)) {
                        hasMultipleTypes = true;
                    }
                }
                if (hasMultipleTypes) {
                    for (int i = 1; i < elements.length; i++) {
                        final PsiLocalVariable var = (PsiLocalVariable) elements[i];
                        registerVariableError(var);
                    }
                }
            }
        }

        public void visitField(@NotNull PsiField field) {
            super.visitField(field);
            if (!childrenContainTypeElement(field)) {
                return;
            }
            final List<PsiField> fields = getSiblingFields(field);

            if (fields.size() > 1) {
                final PsiType baseType = ((PsiVariable) fields.get(0)).getType();
                boolean hasMultipleTypes = false;
                for (int i = 1; i < fields.size(); i++) {
                    final PsiField var = fields.get(i);
                    final PsiType varType = var.getType();
                    if (!varType.equals(baseType)) {
                        hasMultipleTypes = true;
                    }
                }
                if (hasMultipleTypes) {
                    for (int i = 1; i < fields.size(); i++) {
                        final PsiField var = fields.get(i);
                        registerVariableError(var);
                    }
                }
            }
        }

        public static List<PsiField> getSiblingFields(PsiField field) {
            final List<PsiField> out = new ArrayList<PsiField>(5);
            out.add(field);
            PsiField nextfield =
                    PsiTreeUtil.getNextSiblingOfType(field,
                            PsiField.class);
            while (nextfield != null &&
                    nextfield.getTypeElement().equals(field.getTypeElement())) {
                out.add(nextfield);
                nextfield =
                        PsiTreeUtil.getNextSiblingOfType(nextfield,
                                PsiField.class);
            }

            return out;
        }

        public static boolean childrenContainTypeElement(PsiElement field) {
            final PsiElement[] children = field.getChildren();
            for(PsiElement aChildren : children){
                if(aChildren instanceof PsiTypeElement){
                    return true;
                }
            }
            return false;
        }

    }

}
