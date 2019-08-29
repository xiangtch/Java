/*    */ package sun.audio;
/*    */ 
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
/*    */ public class AudioDataStream
/*    */   extends ByteArrayInputStream
/*    */ {
/*    */   private final AudioData ad;
/*    */   
/*    */   public AudioDataStream(AudioData paramAudioData)
/*    */   {
/* 47 */     super(paramAudioData.buffer);
/* 48 */     this.ad = paramAudioData;
/*    */   }
/*    */   
/*    */   final AudioData getAudioData() {
/* 52 */     return this.ad;
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\audio\AudioDataStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */