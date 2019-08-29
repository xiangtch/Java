/*     */ package sun.security.action;
/*     */ 
/*     */ import java.security.AccessController;
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
/*     */ public class GetPropertyAction
/*     */   implements PrivilegedAction<String>
/*     */ {
/*     */   private String theProp;
/*     */   private String defaultVal;
/*     */   
/*     */   public GetPropertyAction(String paramString)
/*     */   {
/*  63 */     this.theProp = paramString;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public GetPropertyAction(String paramString1, String paramString2)
/*     */   {
/*  74 */     this.theProp = paramString1;
/*  75 */     this.defaultVal = paramString2;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String run()
/*     */   {
/*  86 */     String str = System.getProperty(this.theProp);
/*  87 */     return str == null ? this.defaultVal : str;
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
/*     */ 
/*     */   public static String privilegedGetProperty(String paramString)
/*     */   {
/* 104 */     if (System.getSecurityManager() == null) {
/* 105 */       return System.getProperty(paramString);
/*     */     }
/* 107 */     return (String)AccessController.doPrivileged(new GetPropertyAction(paramString));
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\action\GetPropertyAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */