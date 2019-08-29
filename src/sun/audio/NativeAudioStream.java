/*    */ package sun.audio;
/*    */ 
/*    */ import java.io.FilterInputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class NativeAudioStream
/*    */   extends FilterInputStream
/*    */ {
/*    */   public NativeAudioStream(InputStream paramInputStream)
/*    */     throws IOException
/*    */   {
/* 53 */     super(paramInputStream);
/*    */   }
/*    */   
/*    */   public int getLength() {
/* 57 */     return 0;
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\audio\NativeAudioStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */