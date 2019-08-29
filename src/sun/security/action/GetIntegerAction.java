/*     */ package sun.security.action;
/*     */ 
/*     */ import java.security.PrivilegedAction;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class GetIntegerAction
/*     */   implements PrivilegedAction<Integer>
/*     */ {
/*     */   private String theProp;
/*     */   private int defaultVal;
/*  70 */   private boolean defaultSet = false;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public GetIntegerAction(String paramString)
/*     */   {
/*  79 */     this.theProp = paramString;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public GetIntegerAction(String paramString, int paramInt)
/*     */   {
/*  90 */     this.theProp = paramString;
/*  91 */     this.defaultVal = paramInt;
/*  92 */     this.defaultSet = true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Integer run()
/*     */   {
/* 108 */     Integer localInteger = Integer.getInteger(this.theProp);
/* 109 */     if ((localInteger == null) && (this.defaultSet))
/* 110 */       return new Integer(this.defaultVal);
/* 111 */     return localInteger;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\action\GetIntegerAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */