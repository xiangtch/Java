package sun.misc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Executable;
import java.security.AccessControlContext;
import java.util.Map;
import sun.nio.ch.Interruptible;
import sun.reflect.ConstantPool;
import sun.reflect.annotation.AnnotationType;

public abstract interface JavaLangAccess
{
  public abstract ConstantPool getConstantPool(Class<?> paramClass);
  
  public abstract boolean casAnnotationType(Class<?> paramClass, AnnotationType paramAnnotationType1, AnnotationType paramAnnotationType2);
  
  public abstract AnnotationType getAnnotationType(Class<?> paramClass);
  
  public abstract Map<Class<? extends Annotation>, Annotation> getDeclaredAnnotationMap(Class<?> paramClass);
  
  public abstract byte[] getRawClassAnnotations(Class<?> paramClass);
  
  public abstract byte[] getRawClassTypeAnnotations(Class<?> paramClass);
  
  public abstract byte[] getRawExecutableTypeAnnotations(Executable paramExecutable);
  
  public abstract <E extends Enum<E>> E[] getEnumConstantsShared(Class<E> paramClass);
  
  public abstract void blockedOn(Thread paramThread, Interruptible paramInterruptible);
  
  public abstract void registerShutdownHook(int paramInt, boolean paramBoolean, Runnable paramRunnable);
  
  public abstract int getStackTraceDepth(Throwable paramThrowable);
  
  public abstract StackTraceElement getStackTraceElement(Throwable paramThrowable, int paramInt);
  
  public abstract String newStringUnsafe(char[] paramArrayOfChar);
  
  public abstract Thread newThreadWithAcc(Runnable paramRunnable, AccessControlContext paramAccessControlContext);
  
  public abstract void invokeFinalize(Object paramObject)
    throws Throwable;
}


/* Location:              E:\java_source\rt.jar!\sun\misc\JavaLangAccess.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */