package com.siyeh.ig.psiutils;

import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ExceptionUtils{
    private ExceptionUtils(){
        super();
    }

    private static final Set<String> s_genericExceptionTypes = new HashSet<String>(4);

    static {
        s_genericExceptionTypes.add("java.lang.Throwable");
        s_genericExceptionTypes.add("java.lang.Exception");
        s_genericExceptionTypes.add("java.lang.RuntimeException");
        s_genericExceptionTypes.add("java.lang.Error");
    }

    private static Set<PsiType> getExceptionTypesHandled(@NotNull PsiTryStatement statement){
        final Set<PsiType> out = new HashSet<PsiType>(5);
        final PsiParameter[] params = statement.getCatchBlockParameters();
        for(PsiParameter param : params){
            final PsiType type = param.getType();
            out.add(type);
        }
        return out;
    }

    public static Set<PsiType> calculateExceptionsThrown(@NotNull PsiElement statement){
        final ExceptionsThrownVisitor visitor = new ExceptionsThrownVisitor();
        statement.accept(visitor);
        return visitor.getExceptionsThrown();
    }

    public static boolean isGenericExceptionClass(@Nullable PsiType exceptionType){
        if(!(exceptionType instanceof PsiClassType)){
            return false;
        }
        final PsiClassType classType = (PsiClassType) exceptionType;
        final String className = classType.getClassName();
        return s_genericExceptionTypes.contains(className);
    }

    private static class ExceptionsThrownVisitor
                                                 extends PsiRecursiveElementVisitor{
        private final Set<PsiType> m_exceptionsThrown = new HashSet<PsiType>(4);

        public void visitMethodCallExpression(@NotNull PsiMethodCallExpression expression){
            super.visitMethodCallExpression(expression);
            final PsiMethod method = expression.resolveMethod();
            if(method == null){
                return;
            }
            final PsiReferenceList throwsList = method.getThrowsList();
            if(throwsList == null){
                return;
            }
            final PsiManager psiManager = expression.getManager();
            final PsiElementFactory factory = psiManager.getElementFactory();
            final PsiJavaCodeReferenceElement[] list = throwsList.getReferenceElements();
            for(final PsiJavaCodeReferenceElement referenceElement : list){
                final PsiClass exceptionClass = (PsiClass) referenceElement.resolve();
                if(exceptionClass != null){
                    final PsiClassType exceptionType = factory.createType(exceptionClass);
                    m_exceptionsThrown.add(exceptionType);
                }
            }
        }

        public void visitNewExpression(@NotNull PsiNewExpression expression){
            super.visitNewExpression(expression);
            final PsiMethod method = expression.resolveMethod();
            if(method == null){
                return;
            }
            final PsiReferenceList throwsList = method.getThrowsList();
            if(throwsList == null){
                return;
            }
            final PsiManager psiManager = expression.getManager();
            final PsiElementFactory factory = psiManager .getElementFactory();
            final PsiJavaCodeReferenceElement[] list = throwsList.getReferenceElements();
            for(final PsiJavaCodeReferenceElement referenceElement : list){
                final PsiClass exceptionClass = (PsiClass) referenceElement.resolve();
                if(exceptionClass != null){
                    final PsiClassType exceptionType = factory.createType(exceptionClass);
                    m_exceptionsThrown.add(exceptionType);
                }
            }
        }

        public void visitThrowStatement(PsiThrowStatement statement){
            super.visitThrowStatement(statement);
            final PsiExpression exception = statement.getException();
            if(exception == null){
                return;
            }
            final PsiType type = exception.getType();
            if(type == null){
                return;
            }
            m_exceptionsThrown.add(type);
        }

        public void visitTryStatement(@NotNull PsiTryStatement statement){
            final PsiCodeBlock tryBlock = statement.getTryBlock();
            final Set<PsiType> exceptionsThrown = m_exceptionsThrown;
            if(tryBlock != null){
                final Set<PsiType> tryExceptions = calculateExceptionsThrown(tryBlock);
                final Set<PsiType> exceptionsHandled = ExceptionUtils.getExceptionTypesHandled(statement);
                for(Object tryException : tryExceptions){
                    final PsiType thrownType = (PsiType) tryException;
                    if(!isExceptionHandled(exceptionsHandled, thrownType)){
                        exceptionsThrown.add(thrownType);
                    }
                }
            }
            final PsiCodeBlock finallyBlock = statement.getFinallyBlock();
            if(finallyBlock != null){
                final Set<PsiType> finallyExceptions = calculateExceptionsThrown(finallyBlock);
                exceptionsThrown.addAll(finallyExceptions);
            }

            final PsiCodeBlock[] catchBlocks = statement.getCatchBlocks();
            for(PsiCodeBlock catchBlock : catchBlocks){
                final Set<PsiType> catchExceptions = calculateExceptionsThrown(catchBlock);
                exceptionsThrown.addAll(catchExceptions);
            }
        }

        private static boolean isExceptionHandled(Set<PsiType> exceptionHandled,
                                                  PsiType thrownType){
            for(Object aExceptionHandled : exceptionHandled){
                final PsiType handledType = (PsiType) aExceptionHandled;
                if(handledType.isAssignableFrom(thrownType)){
                    return true;
                }
            }
            return false;
        }

        private Set<PsiType> getExceptionsThrown(){
            return Collections.unmodifiableSet(m_exceptionsThrown);
        }
    }
}