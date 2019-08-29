/*    */ package sun.reflect.annotation;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class EnumConstantNotPresentExceptionProxy
/*    */   extends ExceptionProxy
/*    */ {
/*    */   private static final long serialVersionUID = -604662101303187330L;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   Class<? extends Enum<?>> enumType;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   String constName;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public EnumConstantNotPresentExceptionProxy(Class<? extends Enum<?>> paramClass, String paramString)
/*    */   {
/* 41 */     this.enumType = paramClass;
/* 42 */     this.constName = paramString;
/*    */   }
/*    */   
/*    */   protected RuntimeException generateException() {
/* 46 */     return new EnumConstantNotPresentException(this.enumType, this.constName);
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\reflect\annotation\EnumConstantNotPresentExceptionProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */