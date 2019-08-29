/*    */ package sun.audio;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class ContinuousAudioDataStream
/*    */   extends AudioDataStream
/*    */ {
/*    */   public ContinuousAudioDataStream(AudioData paramAudioData)
/*    */   {
/* 53 */     super(paramAudioData);
/*    */   }
/*    */   
/*    */ 
/*    */   public int read()
/*    */   {
/* 59 */     int i = super.read();
/*    */     
/* 61 */     if (i == -1) {
/* 62 */       reset();
/* 63 */       i = super.read();
/*    */     }
/*    */     
/* 66 */     return i;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*    */   {
/* 74 */     for (int i = 0; i < paramInt2;) {
/* 75 */       int j = super.read(paramArrayOfByte, paramInt1 + i, paramInt2 - i);
/* 76 */       if (j >= 0) i += j; else {
/* 77 */         reset();
/*    */       }
/*    */     }
/* 80 */     return i;
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\audio\ContinuousAudioDataStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */