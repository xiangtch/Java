/*    */ package sun.awt.image;
/*    */ 
/*    */ import java.io.BufferedInputStream;
/*    */ import java.io.ByteArrayInputStream;
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
/*    */ public class ByteArrayImageSource
/*    */   extends InputStreamImageSource
/*    */ {
/*    */   byte[] imagedata;
/*    */   int imageoffset;
/*    */   int imagelength;
/*    */   
/*    */   public ByteArrayImageSource(byte[] paramArrayOfByte)
/*    */   {
/* 38 */     this(paramArrayOfByte, 0, paramArrayOfByte.length);
/*    */   }
/*    */   
/*    */   public ByteArrayImageSource(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
/* 42 */     this.imagedata = paramArrayOfByte;
/* 43 */     this.imageoffset = paramInt1;
/* 44 */     this.imagelength = paramInt2;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   final boolean checkSecurity(Object paramObject, boolean paramBoolean)
/*    */   {
/* 51 */     return true;
/*    */   }
/*    */   
/*    */   protected ImageDecoder getDecoder() {
/* 55 */     BufferedInputStream localBufferedInputStream = new BufferedInputStream(new ByteArrayInputStream(this.imagedata, this.imageoffset, this.imagelength));
/*    */     
/*    */ 
/*    */ 
/* 59 */     return getDecoder(localBufferedInputStream);
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\image\ByteArrayImageSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */