/*    */ package sun.util.locale.provider;
/*    */ 
/*    */ import java.lang.reflect.InvocationTargetException;
/*    */ import java.lang.reflect.Method;
/*    */ import java.util.spi.LocaleServiceProvider;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class HostLocaleProviderAdapter
/*    */   extends AuxLocaleProviderAdapter
/*    */ {
/*    */   public Type getAdapterType()
/*    */   {
/* 45 */     return Type.HOST;
/*    */   }
/*    */   
/*    */   protected <P extends LocaleServiceProvider> P findInstalledProvider(Class<P> paramClass)
/*    */   {
/*    */     try
/*    */     {
/* 52 */       Method localMethod = HostLocaleProviderAdapterImpl.class.getMethod("get" + paramClass
/* 53 */         .getSimpleName(), (Class[])null);
/* 54 */       return (LocaleServiceProvider)localMethod.invoke(null, (Object[])null);
/*    */ 
/*    */     }
/*    */     catch (NoSuchMethodException|IllegalAccessException|IllegalArgumentException|InvocationTargetException localNoSuchMethodException)
/*    */     {
/* 59 */       LocaleServiceProviderPool.config(HostLocaleProviderAdapter.class, localNoSuchMethodException.toString());
/*    */     }
/* 61 */     return null;
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\util\locale\provider\HostLocaleProviderAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */