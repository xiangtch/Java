/*     */ package sun.awt.windows;
/*     */ 
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.util.Hashtable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class WFontMetrics
/*     */   extends FontMetrics
/*     */ {
/*     */   int[] widths;
/*     */   int ascent;
/*     */   int descent;
/*     */   int leading;
/*     */   int height;
/*     */   int maxAscent;
/*     */   int maxDescent;
/*     */   int maxHeight;
/*     */   int maxAdvance;
/*     */   
/*     */   public WFontMetrics(Font paramFont)
/*     */   {
/* 113 */     super(paramFont);
/* 114 */     init();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getLeading()
/*     */   {
/* 122 */     return this.leading;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getAscent()
/*     */   {
/* 130 */     return this.ascent;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getDescent()
/*     */   {
/* 138 */     return this.descent;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getHeight()
/*     */   {
/* 146 */     return this.height;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getMaxAscent()
/*     */   {
/* 154 */     return this.maxAscent;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getMaxDescent()
/*     */   {
/* 162 */     return this.maxDescent;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getMaxAdvance()
/*     */   {
/* 170 */     return this.maxAdvance;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public native int stringWidth(String paramString);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public native int charsWidth(char[] paramArrayOfChar, int paramInt1, int paramInt2);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public native int bytesWidth(byte[] paramArrayOfByte, int paramInt1, int paramInt2);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int[] getWidths()
/*     */   {
/* 196 */     return this.widths;
/*     */   }
/*     */   
/*     */   native void init();
/*     */   
/* 201 */   static Hashtable table = new Hashtable();
/*     */   
/*     */   static FontMetrics getFontMetrics(Font paramFont) {
/* 204 */     Object localObject = (FontMetrics)table.get(paramFont);
/* 205 */     if (localObject == null) {
/* 206 */       table.put(paramFont, localObject = new WFontMetrics(paramFont));
/*     */     }
/* 208 */     return (FontMetrics)localObject;
/*     */   }
/*     */   
/*     */   private static native void initIDs();
/*     */   
/*     */   static {}
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\windows\WFontMetrics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */