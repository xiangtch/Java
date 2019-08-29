/*    */ package sun.audio;
/*    */ 
/*    */ import java.io.InputStream;
/*    */ import java.io.SequenceInputStream;
/*    */ import java.util.Enumeration;
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
/*    */ public final class AudioStreamSequence
/*    */   extends SequenceInputStream
/*    */ {
/*    */   Enumeration e;
/*    */   InputStream in;
/*    */   
/*    */   public AudioStreamSequence(Enumeration paramEnumeration)
/*    */   {
/* 56 */     super(paramEnumeration);
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\audio\AudioStreamSequence.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */