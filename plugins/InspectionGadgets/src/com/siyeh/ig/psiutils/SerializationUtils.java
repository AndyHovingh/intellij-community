package com.siyeh.ig.psiutils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.InheritanceUtil;
import com.intellij.psi.util.TypeConversionUtil;
import org.jetbrains.annotations.NotNull;

public class SerializationUtils{
    private static final String SERIALIZABLE_CLASS_NAME = "java.io.Serializable";
    private static final String EXTERNALIZABLE_CLASS_NAME = "java.io.Externalizable";

    private SerializationUtils(){
        super();
    }

    public static boolean isSerializable(@NotNull PsiClass aClass){
        final PsiManager manager = aClass.getManager();
        final Project project = manager.getProject();
        final GlobalSearchScope scope = GlobalSearchScope.allScope(project);
        final PsiClass serializable = manager.findClass(
                SERIALIZABLE_CLASS_NAME, scope);
        return InheritanceUtil.isInheritorOrSelf(aClass, serializable, true);
    }

    public static boolean isExternalizable(@NotNull PsiClass aClass){
        final PsiManager manager = aClass.getManager();
        final Project project = manager.getProject();
        final GlobalSearchScope scope = GlobalSearchScope.allScope(project);
        final PsiClass serializable = manager.findClass(
                EXTERNALIZABLE_CLASS_NAME, scope);
        return InheritanceUtil.isInheritorOrSelf(aClass, serializable, true);
    }

    public static boolean isDirectlySerializable(@NotNull PsiClass aClass){
        final PsiReferenceList implementsList = aClass.getImplementsList();
        if(implementsList != null){
            final PsiJavaCodeReferenceElement[] interfaces = implementsList.getReferenceElements();
            for(PsiJavaCodeReferenceElement aInterfaces : interfaces){
                final PsiClass implemented = (PsiClass) aInterfaces.resolve();
                if(implemented != null){
                    final String name = implemented.getQualifiedName();
                    if(SERIALIZABLE_CLASS_NAME.equals(name)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean hasReadObject(@NotNull PsiClass aClass){
        final PsiMethod[] methods = aClass.getMethods();
        for(final PsiMethod method : methods){
            if(isReadObject(method)){
                return true;
            }
        }
        return false;
    }

    public static boolean hasWriteObject(@NotNull PsiClass aClass){
        final PsiMethod[] methods = aClass.getMethods();
        for(final PsiMethod method : methods){
            if(isWriteObject(method)){
                return true;
            }
        }
        return false;
    }

    public static boolean isReadObject(@NotNull PsiMethod method){
        final String methodName = method.getName();
        if(!"readObject".equals(methodName)){
            return false;
        }
        final PsiParameterList parameterList = method.getParameterList();
        final PsiParameter[] parameters = parameterList.getParameters();
        if(parameters.length != 1){
            return false;
        }
        final PsiType argType = parameters[0].getType();
        if(!TypeUtils.typeEquals("java.io.ObjectInputStream", argType)){
            return false;
        }
        final PsiType returnType = method.getReturnType();
        return TypeUtils.typeEquals("void", returnType);
    }

    public static boolean isWriteObject(@NotNull PsiMethod method){
        final String methodName = method.getName();
        if(!"writeObject".equals(methodName)){
            return false;
        }
        final PsiParameterList parameterList = method.getParameterList();
        final PsiParameter[] parameters = parameterList.getParameters();
        if(parameters.length != 1){
            return false;
        }
        final PsiType argType = parameters[0].getType();
        if(!TypeUtils.typeEquals("java.io.ObjectOutputStream", argType)){
            return false;
        }
        final PsiType returnType = method.getReturnType();
        return TypeUtils.typeEquals("void", returnType);
    }

    public static boolean isReadResolve(@NotNull PsiMethod method){
        final String methodName = method.getName();
        if(!"readResolve".equals(methodName)){
            return false;
        }
        final PsiParameterList parameterList = method.getParameterList();
        final PsiParameter[] parameters = parameterList.getParameters();
        if(parameters.length != 0){
            return false;
        }
        final PsiType returnType = method.getReturnType();
        return TypeUtils.isJavaLangObject(returnType);
    }

    public static boolean isWriteReplace(@NotNull PsiMethod method){
        final String methodName = method.getName();
        if(!"writeReplace".equals(methodName)){
            return false;
        }
        final PsiParameterList parameterList = method.getParameterList();
        final PsiParameter[] parameters = parameterList.getParameters();
        if(parameters.length != 0){
            return false;
        }
        final PsiType returnType = method.getReturnType();
        return TypeUtils.isJavaLangObject(returnType);
    }

    public static boolean typeIsSerializable(@NotNull PsiType type){
        final PsiType componentType = type.getDeepComponentType();
        if(TypeConversionUtil.isPrimitiveAndNotNull(componentType)){
            return true;
        }
        final PsiType[] superTypes = componentType.getSuperTypes();
        for(PsiType superType : superTypes){
            if(TypeUtils.typeEquals(SERIALIZABLE_CLASS_NAME, superType)){
                return true;
            }
        }
        return false;
    }
}
