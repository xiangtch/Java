/*    */ package sun.misc;
/*    */ 
/*    */ import java.io.EOFException;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.util.Arrays;
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
/*    */ public class IOUtils
/*    */ {
/*    */   public static byte[] readFully(InputStream paramInputStream, int paramInt, boolean paramBoolean)
/*    */     throws IOException
/*    */   {
/* 52 */     byte[] arrayOfByte = new byte[0];
/* 53 */     if (paramInt == -1) paramInt = Integer.MAX_VALUE;
/* 54 */     int i = 0;
/* 55 */     while (i < paramInt) {
/*    */       int j;
/* 57 */       if (i >= arrayOfByte.length) {
/* 58 */         j = Math.min(paramInt - i, arrayOfByte.length + 1024);
/* 59 */         if (arrayOfByte.length < i + j) {
/* 60 */           arrayOfByte = Arrays.copyOf(arrayOfByte, i + j);
/*    */         }
/*    */       } else {
/* 63 */         j = arrayOfByte.length - i;
/*    */       }
/* 65 */       int k = paramInputStream.read(arrayOfByte, i, j);
/* 66 */       if (k < 0) {
/* 67 */         if ((paramBoolean) && (paramInt != Integer.MAX_VALUE)) {
/* 68 */           throw new EOFException("Detect premature EOF");
/*    */         }
/* 70 */         if (arrayOfByte.length == i) break;
/* 71 */         arrayOfByte = Arrays.copyOf(arrayOfByte, i); break;
/*    */       }
/*    */       
/*    */ 
/*    */ 
/* 76 */       i += k;
/*    */     }
/* 78 */     return arrayOfByte;
/*    */   }
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
/*    */   public static byte[] readNBytes(InputStream paramInputStream, int paramInt)
/*    */     throws IOException
/*    */   {
/* 93 */     if (paramInt < 0) {
/* 94 */       throw new IOException("length cannot be negative: " + paramInt);
/*    */     }
/* 96 */     return readFully(paramInputStream, paramInt, true);
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\misc\IOUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */