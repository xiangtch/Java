/*    */ package sun.nio.cs;
/*    */ 
/*    */ import java.nio.charset.Charset;
/*    */ import java.nio.charset.CharsetDecoder;
/*    */ import java.nio.charset.CharsetEncoder;
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
/*    */ class UTF_16
/*    */   extends Unicode
/*    */ {
/*    */   public UTF_16()
/*    */   {
/* 36 */     super("UTF-16", StandardCharsets.aliases_UTF_16);
/*    */   }
/*    */   
/*    */   public String historicalName() {
/* 40 */     return "UTF-16";
/*    */   }
/*    */   
/*    */   public CharsetDecoder newDecoder() {
/* 44 */     return new Decoder(this);
/*    */   }
/*    */   
/*    */   public CharsetEncoder newEncoder() {
/* 48 */     return new Encoder(this);
/*    */   }
/*    */   
/*    */   private static class Decoder extends UnicodeDecoder
/*    */   {
/*    */     public Decoder(Charset paramCharset) {
/* 54 */       super(0);
/*    */     }
/*    */   }
/*    */   
/*    */   private static class Encoder extends UnicodeEncoder
/*    */   {
/*    */     public Encoder(Charset paramCharset) {
/* 61 */       super(0, true);
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\nio\cs\UTF_16.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */