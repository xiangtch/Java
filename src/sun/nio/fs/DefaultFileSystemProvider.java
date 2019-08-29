/*    */ package sun.nio.fs;
/*    */ 
/*    */ import java.nio.file.spi.FileSystemProvider;
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
/*    */ public class DefaultFileSystemProvider
/*    */ {
/*    */   public static FileSystemProvider create()
/*    */   {
/* 36 */     return new WindowsFileSystemProvider();
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\nio\fs\DefaultFileSystemProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */