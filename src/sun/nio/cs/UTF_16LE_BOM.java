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
/*    */ class UTF_16LE_BOM
/*    */   extends Unicode
/*    */ {
/*    */   public UTF_16LE_BOM()
/*    */   {
/* 36 */     super("x-UTF-16LE-BOM", StandardCharsets.aliases_UTF_16LE_BOM);
/*    */   }
/*    */   
/*    */   public String historicalName() {
/* 40 */     return "UnicodeLittle";
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
/* 54 */       super(0, 2);
/*    */     }
/*    */   }
/*    */   
/*    */   private static class Encoder extends UnicodeEncoder
/*    */   {
/*    */     public Encoder(Charset paramCharset) {
/* 61 */       super(1, true);
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\nio\cs\UTF_16LE_BOM.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */