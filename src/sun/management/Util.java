/*    */ package sun.management;
/*    */ 
/*    */ import java.lang.management.ManagementPermission;
/*    */ import java.util.List;
/*    */ import javax.management.MalformedObjectNameException;
/*    */ import javax.management.ObjectName;
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
/*    */ public class Util
/*    */ {
/*    */   static RuntimeException newException(Exception paramException)
/*    */   {
/* 38 */     throw new RuntimeException(paramException);
/*    */   }
/*    */   
/* 41 */   private static final String[] EMPTY_STRING_ARRAY = new String[0];
/*    */   
/* 43 */   static String[] toStringArray(List<String> paramList) { return (String[])paramList.toArray(EMPTY_STRING_ARRAY); }
/*    */   
/*    */   public static ObjectName newObjectName(String paramString1, String paramString2)
/*    */   {
/* 47 */     return newObjectName(paramString1 + ",name=" + paramString2);
/*    */   }
/*    */   
/*    */   public static ObjectName newObjectName(String paramString) {
/*    */     try {
/* 52 */       return ObjectName.getInstance(paramString);
/*    */     } catch (MalformedObjectNameException localMalformedObjectNameException) {
/* 54 */       throw new IllegalArgumentException(localMalformedObjectNameException);
/*    */     }
/*    */   }
/*    */   
/* 58 */   private static ManagementPermission monitorPermission = new ManagementPermission("monitor");
/*    */   
/* 60 */   private static ManagementPermission controlPermission = new ManagementPermission("control");
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
/*    */   static void checkAccess(ManagementPermission paramManagementPermission)
/*    */     throws SecurityException
/*    */   {
/* 75 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 76 */     if (localSecurityManager != null) {
/* 77 */       localSecurityManager.checkPermission(paramManagementPermission);
/*    */     }
/*    */   }
/*    */   
/*    */   static void checkMonitorAccess() throws SecurityException {
/* 82 */     checkAccess(monitorPermission);
/*    */   }
/*    */   
/* 85 */   static void checkControlAccess() throws SecurityException { checkAccess(controlPermission); }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\management\Util.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */