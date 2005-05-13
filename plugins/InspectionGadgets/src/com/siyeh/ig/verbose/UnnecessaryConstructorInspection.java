package com.siyeh.ig.verbose;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.siyeh.ig.*;
import org.jetbrains.annotations.NotNull;

public class UnnecessaryConstructorInspection extends ClassInspection {
    private final UnnecessaryConstructorFix fix = new UnnecessaryConstructorFix();

    public String getID(){
        return "RedundantNoArgConstructor";
    }
    public String getDisplayName() {
        return "Redundant no-arg constructor";
    }

    public String getGroupDisplayName() {
        return GroupNames.VERBOSE_GROUP_NAME;
    }

    public String buildErrorString(PsiElement location) {
        return "No-arg constructor #ref is unnecessary #loc";
    }

    public BaseInspectionVisitor createVisitor(InspectionManager inspectionManager, boolean onTheFly) {
        return new UnnecessaryConstructorVisitor(this, inspectionManager, onTheFly);
    }

    public InspectionGadgetsFix buildFix(PsiElement location) {
        return fix;
    }

    private static class UnnecessaryConstructorFix extends InspectionGadgetsFix {
        public String getName() {
            return "Remove redundant constructor";
        }

        public void applyFix(Project project, ProblemDescriptor descriptor) {
            if(isQuickFixOnReadOnlyFile(descriptor)) return;
            final PsiElement nameIdentifier = descriptor.getPsiElement();
            final PsiElement constructor = nameIdentifier.getParent();
            deleteElement(constructor);
        }

    }

    private static class UnnecessaryConstructorVisitor extends BaseInspectionVisitor {
        private UnnecessaryConstructorVisitor(BaseInspection inspection, InspectionManager inspectionManager, boolean isOnTheFly) {
            super(inspection, inspectionManager, isOnTheFly);
        }

        public void visitClass(@NotNull PsiClass aClass) {

            final PsiMethod[] constructors = aClass.getConstructors();
            if (constructors == null) {
                return;
            }
            if (constructors.length != 1) {
                return;
            }
            final PsiMethod constructor = constructors[0];
            if (!constructor.hasModifierProperty(PsiModifier.PUBLIC)) {
                return;
            }
            final PsiParameterList parameterList = constructor.getParameterList();
            if (parameterList == null) {
                return;
            }
            if (parameterList.getParameters().length != 0) {
                return;
            }
            final PsiReferenceList throwsList = constructor.getThrowsList();
            if(throwsList!=null)
            {
                final PsiJavaCodeReferenceElement[] elements = throwsList.getReferenceElements();
                if(elements.length!=0)
                {
                    return;
                }
            }
            final PsiCodeBlock body = constructor.getBody();
            if (body == null) {
                return;
            }
            final PsiStatement[] statements = body.getStatements();
            if (statements == null) {
                return;
            }
            if (statements.length == 0) {
                registerMethodError(constructor);
            }else if(statements.length == 1)
            {
                final PsiStatement statement = statements[0];
                if("super();".equals(statement.getText()))
                {
                    registerMethodError(constructor);
                }
            }
        }
    }
}
