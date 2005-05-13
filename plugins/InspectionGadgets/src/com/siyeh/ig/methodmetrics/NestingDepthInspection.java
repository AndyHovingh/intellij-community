package com.siyeh.ig.methodmetrics;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.siyeh.ig.BaseInspection;
import com.siyeh.ig.BaseInspectionVisitor;
import com.siyeh.ig.GroupNames;
import org.jetbrains.annotations.NotNull;

public class NestingDepthInspection extends MethodMetricInspection {
    public String getID(){
        return "OverlyNestedMethod";
    }
    public String getDisplayName() {
        return "Overly nested method";
    }

    public String getGroupDisplayName() {
        return GroupNames.METHODMETRICS_GROUP_NAME;
    }

    protected int getDefaultLimit() {
        return 5;
    }

    protected String getConfigurationLabel() {
        return "Nesting depth limit:";
    }

    public String buildErrorString(PsiElement location) {
        final PsiMethod method = (PsiMethod) location.getParent();
        final NestingDepthVisitor visitor = new NestingDepthVisitor();
        method.accept(visitor);
        final int nestingDepth = visitor.getMaximumDepth();
        return "#ref is overly nested (maximum nesting depth = " + nestingDepth + ") #loc";
    }

    public BaseInspectionVisitor createVisitor(InspectionManager inspectionManager, boolean onTheFly) {
        return new NestingDepthMethodVisitor(this, inspectionManager, onTheFly);
    }

    private class NestingDepthMethodVisitor extends BaseInspectionVisitor {
        private NestingDepthMethodVisitor(BaseInspection inspection, InspectionManager inspectionManager, boolean isOnTheFly) {
            super(inspection, inspectionManager, isOnTheFly);
        }

        public void visitMethod(@NotNull PsiMethod method) {
            // note: no call to super
            final NestingDepthVisitor visitor = new NestingDepthVisitor();
            method.accept(visitor);
            final int count = visitor.getMaximumDepth();

            if (count <= getLimit()) {
                return;
            }
            registerMethodError(method);
        }
    }

}
