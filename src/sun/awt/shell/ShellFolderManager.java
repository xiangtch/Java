/*     */ package sun.awt.shell;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.util.concurrent.Callable;
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
/*     */ class ShellFolderManager
/*     */ {
/*     */   public ShellFolder createShellFolder(File paramFile)
/*     */     throws FileNotFoundException
/*     */   {
/*  43 */     return new DefaultShellFolder(null, paramFile);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object get(String paramString)
/*     */   {
/*  67 */     if (paramString.equals("fileChooserDefaultFolder"))
/*     */     {
/*  69 */       File localFile = new File(System.getProperty("user.home"));
/*     */       try {
/*  71 */         return createShellFolder(localFile);
/*     */       } catch (FileNotFoundException localFileNotFoundException) {
/*  73 */         return localFile;
/*     */       } }
/*  75 */     if (paramString.equals("roots"))
/*     */     {
/*  77 */       return File.listRoots(); }
/*  78 */     if (paramString.equals("fileChooserComboBoxFolders"))
/*     */     {
/*     */ 
/*  81 */       return get("roots"); }
/*  82 */     if (paramString.equals("fileChooserShortcutPanelFolders"))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*  87 */       return new File[] { (File)get("fileChooserDefaultFolder") };
/*     */     }
/*  89 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isComputerNode(File paramFile)
/*     */   {
/*  97 */     return false;
/*     */   }
/*     */   
/*     */   public boolean isFileSystemRoot(File paramFile) {
/* 101 */     if (((paramFile instanceof ShellFolder)) && (!((ShellFolder)paramFile).isFileSystem())) {
/* 102 */       return false;
/*     */     }
/* 104 */     return paramFile.getParentFile() == null;
/*     */   }
/*     */   
/*     */   protected ShellFolder.Invoker createInvoker() {
/* 108 */     return new DirectInvoker(null);
/*     */   }
/*     */   
/*     */   private static class DirectInvoker implements ShellFolder.Invoker {
/*     */     public <T> T invoke(Callable<T> paramCallable) throws Exception {
/* 113 */       return (T)paramCallable.call();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\shell\ShellFolderManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */