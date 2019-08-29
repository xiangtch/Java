/*    */ package sun.awt.windows;
/*    */ 
/*    */ import java.awt.Desktop.Action;
/*    */ import java.awt.peer.DesktopPeer;
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ import java.net.URI;
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
/*    */ final class WDesktopPeer
/*    */   implements DesktopPeer
/*    */ {
/* 44 */   private static String ACTION_OPEN_VERB = "open";
/* 45 */   private static String ACTION_EDIT_VERB = "edit";
/* 46 */   private static String ACTION_PRINT_VERB = "print";
/*    */   
/*    */ 
/*    */   public boolean isSupported(Desktop.Action paramAction)
/*    */   {
/* 51 */     return true;
/*    */   }
/*    */   
/*    */   public void open(File paramFile) throws IOException
/*    */   {
/* 56 */     ShellExecute(paramFile, ACTION_OPEN_VERB);
/*    */   }
/*    */   
/*    */   public void edit(File paramFile) throws IOException
/*    */   {
/* 61 */     ShellExecute(paramFile, ACTION_EDIT_VERB);
/*    */   }
/*    */   
/*    */   public void print(File paramFile) throws IOException
/*    */   {
/* 66 */     ShellExecute(paramFile, ACTION_PRINT_VERB);
/*    */   }
/*    */   
/*    */   public void mail(URI paramURI) throws IOException
/*    */   {
/* 71 */     ShellExecute(paramURI, ACTION_OPEN_VERB);
/*    */   }
/*    */   
/*    */   public void browse(URI paramURI) throws IOException
/*    */   {
/* 76 */     ShellExecute(paramURI, ACTION_OPEN_VERB);
/*    */   }
/*    */   
/*    */   private void ShellExecute(File paramFile, String paramString) throws IOException {
/* 80 */     String str = ShellExecute(paramFile.getAbsolutePath(), paramString);
/* 81 */     if (str != null) {
/* 82 */       throw new IOException("Failed to " + paramString + " " + paramFile + ". Error message: " + str);
/*    */     }
/*    */   }
/*    */   
/*    */   private void ShellExecute(URI paramURI, String paramString) throws IOException {
/* 87 */     String str = ShellExecute(paramURI.toString(), paramString);
/*    */     
/* 89 */     if (str != null) {
/* 90 */       throw new IOException("Failed to " + paramString + " " + paramURI + ". Error message: " + str);
/*    */     }
/*    */   }
/*    */   
/*    */   private static native String ShellExecute(String paramString1, String paramString2);
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\windows\WDesktopPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */