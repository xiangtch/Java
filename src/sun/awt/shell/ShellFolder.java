/*     */ package sun.awt.shell;
/*     */ 
/*     */ import java.awt.Image;
/*     */ import java.awt.Toolkit;
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectStreamException;
/*     */ import java.net.URI;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import java.util.Vector;
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
/*     */ public abstract class ShellFolder
/*     */   extends File
/*     */ {
/*     */   private static final String COLUMN_NAME = "FileChooser.fileNameHeaderText";
/*     */   private static final String COLUMN_SIZE = "FileChooser.fileSizeHeaderText";
/*     */   private static final String COLUMN_DATE = "FileChooser.fileDateHeaderText";
/*     */   protected ShellFolder parent;
/*     */   private static final ShellFolderManager shellFolderManager;
/*     */   
/*     */   ShellFolder(ShellFolder paramShellFolder, String paramString)
/*     */   {
/*  52 */     super(paramString != null ? paramString : "ShellFolder");
/*  53 */     this.parent = paramShellFolder;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isFileSystem()
/*     */   {
/*  60 */     return !getPath().startsWith("ShellFolder");
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getParent()
/*     */   {
/*  88 */     if ((this.parent == null) && (isFileSystem())) {
/*  89 */       return super.getParent();
/*     */     }
/*  91 */     if (this.parent != null) {
/*  92 */       return this.parent.getPath();
/*     */     }
/*  94 */     return null;
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
/*     */   public File getParentFile()
/*     */   {
/* 111 */     if (this.parent != null)
/* 112 */       return this.parent;
/* 113 */     if (isFileSystem()) {
/* 114 */       return super.getParentFile();
/*     */     }
/* 116 */     return null;
/*     */   }
/*     */   
/*     */   public File[] listFiles()
/*     */   {
/* 121 */     return listFiles(true);
/*     */   }
/*     */   
/*     */   public File[] listFiles(boolean paramBoolean) {
/* 125 */     File[] arrayOfFile = super.listFiles();
/*     */     
/* 127 */     if (!paramBoolean) {
/* 128 */       Vector localVector = new Vector();
/* 129 */       int i = arrayOfFile == null ? 0 : arrayOfFile.length;
/* 130 */       for (int j = 0; j < i; j++) {
/* 131 */         if (!arrayOfFile[j].isHidden()) {
/* 132 */           localVector.addElement(arrayOfFile[j]);
/*     */         }
/*     */       }
/* 135 */       arrayOfFile = (File[])localVector.toArray(new File[localVector.size()]);
/*     */     }
/*     */     
/* 138 */     return arrayOfFile;
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
/*     */   public int compareTo(File paramFile)
/*     */   {
/* 174 */     if ((paramFile == null) || (!(paramFile instanceof ShellFolder)) || (((paramFile instanceof ShellFolder)) && 
/* 175 */       (((ShellFolder)paramFile).isFileSystem())))
/*     */     {
/* 177 */       if (isFileSystem()) {
/* 178 */         return super.compareTo(paramFile);
/*     */       }
/* 180 */       return -1;
/*     */     }
/*     */     
/* 183 */     if (isFileSystem()) {
/* 184 */       return 1;
/*     */     }
/* 186 */     return getName().compareTo(paramFile.getName());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Image getIcon(boolean paramBoolean)
/*     */   {
/* 196 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static
/*     */   {
/* 208 */     String str = (String)Toolkit.getDefaultToolkit().getDesktopProperty("Shell.shellFolderManager");
/* 209 */     Class localClass = null;
/*     */     try {
/* 211 */       localClass = Class.forName(str, false, null);
/* 212 */       if (!ShellFolderManager.class.isAssignableFrom(localClass)) {
/* 213 */         localClass = null;
/*     */       }
/*     */     }
/*     */     catch (ClassNotFoundException localClassNotFoundException) {}catch (NullPointerException localNullPointerException) {}catch (SecurityException localSecurityException) {}
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 221 */     if (localClass == null) {
/* 222 */       localClass = ShellFolderManager.class;
/*     */     }
/*     */     try
/*     */     {
/* 226 */       shellFolderManager = (ShellFolderManager)localClass.newInstance();
/*     */     }
/*     */     catch (InstantiationException localInstantiationException) {
/* 229 */       throw new Error("Could not instantiate Shell Folder Manager: " + localClass.getName());
/*     */     }
/*     */     catch (IllegalAccessException localIllegalAccessException) {
/* 232 */       throw new Error("Could not access Shell Folder Manager: " + localClass.getName());
/*     */     } }
/*     */   
/* 235 */   private static final Invoker invoker = shellFolderManager.createInvoker();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ShellFolder getShellFolder(File paramFile)
/*     */     throws FileNotFoundException
/*     */   {
/* 243 */     if ((paramFile instanceof ShellFolder)) {
/* 244 */       return (ShellFolder)paramFile;
/*     */     }
/* 246 */     if (!paramFile.exists()) {
/* 247 */       throw new FileNotFoundException();
/*     */     }
/* 249 */     return shellFolderManager.createShellFolder(paramFile);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Object get(String paramString)
/*     */   {
/* 258 */     return shellFolderManager.get(paramString);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean isComputerNode(File paramFile)
/*     */   {
/* 266 */     return shellFolderManager.isComputerNode(paramFile);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static boolean isFileSystemRoot(File paramFile)
/*     */   {
/* 273 */     return shellFolderManager.isFileSystemRoot(paramFile);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static File getNormalizedFile(File paramFile)
/*     */     throws IOException
/*     */   {
/* 281 */     File localFile = paramFile.getCanonicalFile();
/* 282 */     if (paramFile.equals(localFile))
/*     */     {
/* 284 */       return localFile;
/*     */     }
/*     */     
/*     */ 
/* 288 */     return new File(paramFile.toURI().normalize());
/*     */   }
/*     */   
/*     */ 
/*     */   public static void sort(List<? extends File> paramList)
/*     */   {
/* 294 */     if ((paramList == null) || (paramList.size() <= 1)) {
/* 295 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 300 */     invoke(new Callable()
/*     */     {
/*     */ 
/*     */       public Void call()
/*     */       {
/* 305 */         Object localObject = null;
/*     */         
/* 307 */         for (File localFile1 : this.val$files) {
/* 308 */           File localFile2 = localFile1.getParentFile();
/*     */           
/* 310 */           if ((localFile2 == null) || (!(localFile1 instanceof ShellFolder))) {
/* 311 */             localObject = null;
/*     */             
/* 313 */             break;
/*     */           }
/*     */           
/* 316 */           if (localObject == null) {
/* 317 */             localObject = localFile2;
/*     */           }
/* 319 */           else if ((localObject != localFile2) && (!((File)localObject).equals(localFile2))) {
/* 320 */             localObject = null;
/*     */             
/* 322 */             break;
/*     */           }
/*     */         }
/*     */         
/*     */ 
/* 327 */         if ((localObject instanceof ShellFolder)) {
/* 328 */           ((ShellFolder)localObject).sortChildren(this.val$files);
/*     */         } else {
/* 330 */           Collections.sort(this.val$files, ShellFolder.FILE_COMPARATOR);
/*     */         }
/*     */         
/* 333 */         return null;
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   public void sortChildren(final List<? extends File> paramList)
/*     */   {
/* 341 */     invoke(new Callable() {
/*     */       public Void call() {
/* 343 */         Collections.sort(paramList, ShellFolder.FILE_COMPARATOR);
/*     */         
/* 345 */         return null;
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public boolean isAbsolute() {
/* 351 */     return (!isFileSystem()) || (super.isAbsolute());
/*     */   }
/*     */   
/*     */   public File getAbsoluteFile() {
/* 355 */     return isFileSystem() ? super.getAbsoluteFile() : this;
/*     */   }
/*     */   
/*     */   public boolean canRead() {
/* 359 */     return isFileSystem() ? super.canRead() : true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean canWrite()
/*     */   {
/* 368 */     return isFileSystem() ? super.canWrite() : false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean exists()
/*     */   {
/* 374 */     return (!isFileSystem()) || (isFileSystemRoot(this)) || (super.exists());
/*     */   }
/*     */   
/*     */   public boolean isDirectory() {
/* 378 */     return isFileSystem() ? super.isDirectory() : true;
/*     */   }
/*     */   
/*     */   public boolean isFile() {
/* 382 */     return !isDirectory() ? true : isFileSystem() ? super.isFile() : false;
/*     */   }
/*     */   
/*     */   public long lastModified() {
/* 386 */     return isFileSystem() ? super.lastModified() : 0L;
/*     */   }
/*     */   
/*     */   public long length() {
/* 390 */     return isFileSystem() ? super.length() : 0L;
/*     */   }
/*     */   
/*     */   public boolean createNewFile() throws IOException {
/* 394 */     return isFileSystem() ? super.createNewFile() : false;
/*     */   }
/*     */   
/*     */   public boolean delete() {
/* 398 */     return isFileSystem() ? super.delete() : false;
/*     */   }
/*     */   
/*     */   public void deleteOnExit() {
/* 402 */     if (isFileSystem()) {
/* 403 */       super.deleteOnExit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean mkdir()
/*     */   {
/* 410 */     return isFileSystem() ? super.mkdir() : false;
/*     */   }
/*     */   
/*     */   public boolean mkdirs() {
/* 414 */     return isFileSystem() ? super.mkdirs() : false;
/*     */   }
/*     */   
/*     */   public boolean renameTo(File paramFile) {
/* 418 */     return isFileSystem() ? super.renameTo(paramFile) : false;
/*     */   }
/*     */   
/*     */   public boolean setLastModified(long paramLong) {
/* 422 */     return isFileSystem() ? super.setLastModified(paramLong) : false;
/*     */   }
/*     */   
/*     */   public boolean setReadOnly() {
/* 426 */     return isFileSystem() ? super.setReadOnly() : false;
/*     */   }
/*     */   
/*     */   public String toString() {
/* 430 */     return isFileSystem() ? super.toString() : getDisplayName();
/*     */   }
/*     */   
/*     */   public static ShellFolderColumnInfo[] getFolderColumns(File paramFile) {
/* 434 */     ShellFolderColumnInfo[] arrayOfShellFolderColumnInfo = null;
/*     */     
/* 436 */     if ((paramFile instanceof ShellFolder)) {
/* 437 */       arrayOfShellFolderColumnInfo = ((ShellFolder)paramFile).getFolderColumns();
/*     */     }
/*     */     
/* 440 */     if (arrayOfShellFolderColumnInfo == null)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 449 */       arrayOfShellFolderColumnInfo = new ShellFolderColumnInfo[] { new ShellFolderColumnInfo("FileChooser.fileNameHeaderText", Integer.valueOf(150), Integer.valueOf(10), true, null, FILE_COMPARATOR), new ShellFolderColumnInfo("FileChooser.fileSizeHeaderText", Integer.valueOf(75), Integer.valueOf(4), true, null, DEFAULT_COMPARATOR, true), new ShellFolderColumnInfo("FileChooser.fileDateHeaderText", Integer.valueOf(130), Integer.valueOf(10), true, null, DEFAULT_COMPARATOR, true) };
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 454 */     return arrayOfShellFolderColumnInfo;
/*     */   }
/*     */   
/*     */   public ShellFolderColumnInfo[] getFolderColumns() {
/* 458 */     return null;
/*     */   }
/*     */   
/*     */   public static Object getFolderColumnValue(File paramFile, int paramInt) {
/* 462 */     if ((paramFile instanceof ShellFolder)) {
/* 463 */       Object localObject = ((ShellFolder)paramFile).getFolderColumnValue(paramInt);
/* 464 */       if (localObject != null) {
/* 465 */         return localObject;
/*     */       }
/*     */     }
/*     */     
/* 469 */     if ((paramFile == null) || (!paramFile.exists())) {
/* 470 */       return null;
/*     */     }
/*     */     
/* 473 */     switch (paramInt)
/*     */     {
/*     */     case 0: 
/* 476 */       return paramFile;
/*     */     
/*     */     case 1: 
/* 479 */       return paramFile.isDirectory() ? null : Long.valueOf(paramFile.length());
/*     */     
/*     */     case 2: 
/* 482 */       if (isFileSystemRoot(paramFile)) {
/* 483 */         return null;
/*     */       }
/* 485 */       long l = paramFile.lastModified();
/* 486 */       return l == 0L ? null : new Date(l);
/*     */     }
/*     */     
/* 489 */     return null;
/*     */   }
/*     */   
/*     */   public Object getFolderColumnValue(int paramInt)
/*     */   {
/* 494 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static <T> T invoke(Callable<T> paramCallable)
/*     */   {
/*     */     try
/*     */     {
/* 504 */       return (T)invoke(paramCallable, RuntimeException.class);
/*     */     } catch (InterruptedException localInterruptedException) {}
/* 506 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static <T, E extends Throwable> T invoke(Callable<T> paramCallable, Class<E> paramClass)
/*     */     throws InterruptedException, Throwable
/*     */   {
/*     */     try
/*     */     {
/* 518 */       return (T)invoker.invoke(paramCallable);
/*     */     } catch (Exception localException) {
/* 520 */       if ((localException instanceof RuntimeException))
/*     */       {
/* 522 */         throw ((RuntimeException)localException);
/*     */       }
/*     */       
/* 525 */       if ((localException instanceof InterruptedException))
/*     */       {
/* 527 */         Thread.currentThread().interrupt();
/*     */         
/*     */ 
/* 530 */         throw ((InterruptedException)localException);
/*     */       }
/*     */       
/* 533 */       if (paramClass.isInstance(localException)) {
/* 534 */         throw ((Throwable)paramClass.cast(localException));
/*     */       }
/*     */       
/* 537 */       throw new RuntimeException("Unexpected error", localException);
/*     */     }
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
/* 558 */   private static final Comparator DEFAULT_COMPARATOR = new Comparator()
/*     */   {
/*     */     public int compare(Object paramAnonymousObject1, Object paramAnonymousObject2) {
/*     */       int i;
/* 562 */       if ((paramAnonymousObject1 == null) && (paramAnonymousObject2 == null)) {
/* 563 */         i = 0;
/* 564 */       } else if ((paramAnonymousObject1 != null) && (paramAnonymousObject2 == null)) {
/* 565 */         i = 1;
/* 566 */       } else if ((paramAnonymousObject1 == null) && (paramAnonymousObject2 != null)) {
/* 567 */         i = -1;
/* 568 */       } else if ((paramAnonymousObject1 instanceof Comparable)) {
/* 569 */         i = ((Comparable)paramAnonymousObject1).compareTo(paramAnonymousObject2);
/*     */       } else {
/* 571 */         i = 0;
/*     */       }
/*     */       
/* 574 */       return i;
/*     */     }
/*     */   };
/*     */   
/* 578 */   private static final Comparator<File> FILE_COMPARATOR = new Comparator() {
/*     */     public int compare(File paramAnonymousFile1, File paramAnonymousFile2) {
/* 580 */       ShellFolder localShellFolder1 = null;
/* 581 */       ShellFolder localShellFolder2 = null;
/*     */       
/* 583 */       if ((paramAnonymousFile1 instanceof ShellFolder)) {
/* 584 */         localShellFolder1 = (ShellFolder)paramAnonymousFile1;
/* 585 */         if (localShellFolder1.isFileSystem()) {
/* 586 */           localShellFolder1 = null;
/*     */         }
/*     */       }
/* 589 */       if ((paramAnonymousFile2 instanceof ShellFolder)) {
/* 590 */         localShellFolder2 = (ShellFolder)paramAnonymousFile2;
/* 591 */         if (localShellFolder2.isFileSystem()) {
/* 592 */           localShellFolder2 = null;
/*     */         }
/*     */       }
/*     */       
/* 596 */       if ((localShellFolder1 != null) && (localShellFolder2 != null))
/* 597 */         return localShellFolder1.compareTo(localShellFolder2);
/* 598 */       if (localShellFolder1 != null)
/*     */       {
/* 600 */         return -1; }
/* 601 */       if (localShellFolder2 != null) {
/* 602 */         return 1;
/*     */       }
/* 604 */       String str1 = paramAnonymousFile1.getName();
/* 605 */       String str2 = paramAnonymousFile2.getName();
/*     */       
/*     */ 
/* 608 */       int i = str1.compareToIgnoreCase(str2);
/* 609 */       if (i != 0) {
/* 610 */         return i;
/*     */       }
/*     */       
/*     */ 
/* 614 */       return str1.compareTo(str2);
/*     */     }
/*     */   };
/*     */   
/*     */   protected abstract Object writeReplace()
/*     */     throws ObjectStreamException;
/*     */   
/*     */   public abstract boolean isLink();
/*     */   
/*     */   public abstract ShellFolder getLinkLocation()
/*     */     throws FileNotFoundException;
/*     */   
/*     */   public abstract String getDisplayName();
/*     */   
/*     */   public abstract String getFolderType();
/*     */   
/*     */   public abstract String getExecutableType();
/*     */   
/*     */   public static abstract interface Invoker
/*     */   {
/*     */     public abstract <T> T invoke(Callable<T> paramCallable)
/*     */       throws Exception;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\shell\ShellFolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */