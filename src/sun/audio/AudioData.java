/*    */ package sun.audio;
/*    */ 
/*    */ import java.io.ByteArrayInputStream;
/*    */ import java.io.IOException;
/*    */ import java.util.Arrays;
/*    */ import javax.sound.sampled.AudioFormat;
/*    */ import javax.sound.sampled.AudioFormat.Encoding;
/*    */ import javax.sound.sampled.AudioInputStream;
/*    */ import javax.sound.sampled.AudioSystem;
/*    */ import javax.sound.sampled.UnsupportedAudioFileException;
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
/*    */ public final class AudioData
/*    */ {
/* 55 */   private static final AudioFormat DEFAULT_FORMAT = new AudioFormat(Encoding.ULAW, 8000.0F, 8, 1, 1, 8000.0F, true);
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   AudioFormat format;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   byte[] buffer;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public AudioData(byte[] paramArrayOfByte)
/*    */   {
/* 74 */     this(DEFAULT_FORMAT, paramArrayOfByte);
/*    */     
/*    */     try
/*    */     {
/* 78 */       AudioInputStream localAudioInputStream = AudioSystem.getAudioInputStream(new ByteArrayInputStream(paramArrayOfByte));
/* 79 */       this.format = localAudioInputStream.getFormat();
/* 80 */       localAudioInputStream.close();
/*    */     }
/*    */     catch (IOException localIOException) {}catch (UnsupportedAudioFileException localUnsupportedAudioFileException) {}
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
/*    */   AudioData(AudioFormat paramAudioFormat, byte[] paramArrayOfByte)
/*    */   {
/* 95 */     this.format = paramAudioFormat;
/* 96 */     if (paramArrayOfByte != null) {
/* 97 */       this.buffer = Arrays.copyOf(paramArrayOfByte, paramArrayOfByte.length);
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\audio\AudioData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */