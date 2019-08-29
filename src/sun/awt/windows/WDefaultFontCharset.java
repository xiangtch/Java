/*    */ package sun.awt.windows;
/*    */ 
/*    */ import java.nio.charset.Charset;
/*    */ import java.nio.charset.CharsetEncoder;
/*    */ import sun.awt.AWTCharset;
/*    */ import sun.awt.AWTCharset.Encoder;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ final class WDefaultFontCharset
/*    */   extends AWTCharset
/*    */ {
/*    */   private String fontName;
/*    */   
/*    */   WDefaultFontCharset(String paramString)
/*    */   {
/* 40 */     super("WDefaultFontCharset", Charset.forName("windows-1252"));
/* 41 */     this.fontName = paramString;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/* 46 */   public CharsetEncoder newEncoder() { return new Encoder(null); }
/*    */   
/*    */   private class Encoder extends AWTCharset.Encoder {
/* 49 */     private Encoder() { super(); }
/*    */     
/*    */     public boolean canEncode(char paramChar) {
/* 52 */       return WDefaultFontCharset.this.canConvert(paramChar);
/*    */     }
/*    */   }
/*    */   
/*    */   private synchronized native boolean canConvert(char paramChar);
/*    */   
/*    */   private static native void initIDs();
/*    */   
/*    */   static {}
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\windows\WDefaultFontCharset.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */