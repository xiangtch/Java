/*    */ package sun.nio.fs;
/*    */ 
/*    */ import java.nio.file.spi.FileTypeDetector;
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
/*    */ public class DefaultFileTypeDetector
/*    */ {
/*    */   public static FileTypeDetector create()
/*    */   {
/* 34 */     return new RegistryFileTypeDetector();
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\nio\fs\DefaultFileTypeDetector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */