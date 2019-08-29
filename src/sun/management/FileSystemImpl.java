/*    */ package sun.management;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ import java.security.AccessController;
/*    */ import java.security.PrivilegedAction;
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
/*    */ public class FileSystemImpl
/*    */   extends FileSystem
/*    */ {
/*    */   public boolean supportsFileSecurity(File paramFile)
/*    */     throws IOException
/*    */   {
/* 37 */     return isSecuritySupported0(paramFile.getAbsolutePath());
/*    */   }
/*    */   
/*    */   public boolean isAccessUserOnly(File paramFile) throws IOException {
/* 41 */     String str = paramFile.getAbsolutePath();
/* 42 */     if (!isSecuritySupported0(str)) {
/* 43 */       throw new UnsupportedOperationException("File system does not support file security");
/*    */     }
/* 45 */     return isAccessUserOnly0(str);
/*    */   }
/*    */   
/*    */ 
/*    */   static native void init0();
/*    */   
/*    */   static native boolean isSecuritySupported0(String paramString)
/*    */     throws IOException;
/*    */   
/*    */   static native boolean isAccessUserOnly0(String paramString)
/*    */     throws IOException;
/*    */   
/*    */   static
/*    */   {
/* 59 */     AccessController.doPrivileged(new PrivilegedAction()
/*    */     {
/*    */       public Void run() {
/* 62 */         System.loadLibrary("management");
/* 63 */         return null;
/*    */       }
/* 65 */     });
/* 66 */     init0();
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\management\FileSystemImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */