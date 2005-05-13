package com.siyeh.ig.performance;

import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

class CanBeStaticVisitor extends PsiRecursiveElementVisitor{
    private boolean canBeStatic = true;

    public void visitElement(@NotNull PsiElement element){
        if(canBeStatic){
            super.visitElement(element);
        }
    }

    public void visitReferenceExpression(@NotNull PsiReferenceExpression ref){
        if(!canBeStatic){
            return;
        }
        super.visitReferenceExpression(ref);
        final PsiElement element = ref.resolve();
        if(element instanceof PsiField){
            final PsiField field = (PsiField) element;
            if(!field.hasModifierProperty(PsiModifier.STATIC)){
                canBeStatic = false;
            }
        } else if(element instanceof PsiVariable){
            //can happen with initializers of inner classes referencing
            //local variables or parameters from outer class
            canBeStatic = false;
        }
    }

    public boolean canBeStatic(){
        return canBeStatic;
    }
}
