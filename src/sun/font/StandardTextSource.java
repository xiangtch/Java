/*     */ package sun.font;
/*     */ 
/*     */ import java.awt.Font;
/*     */ import java.awt.font.FontRenderContext;
/*     */ import java.awt.font.LineMetrics;
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
/*     */ final class StandardTextSource
/*     */   extends TextSource
/*     */ {
/*     */   private final char[] chars;
/*     */   private final int start;
/*     */   private final int len;
/*     */   private final int cstart;
/*     */   private final int clen;
/*     */   private final int level;
/*     */   private final int flags;
/*     */   private final Font font;
/*     */   private final FontRenderContext frc;
/*     */   private final CoreMetrics cm;
/*     */   
/*     */   StandardTextSource(char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, Font paramFont, FontRenderContext paramFontRenderContext, CoreMetrics paramCoreMetrics)
/*     */   {
/*  73 */     if (paramArrayOfChar == null) {
/*  74 */       throw new IllegalArgumentException("bad chars: null");
/*     */     }
/*  76 */     if (paramInt3 < 0) {
/*  77 */       throw new IllegalArgumentException("bad cstart: " + paramInt3);
/*     */     }
/*  79 */     if (paramInt1 < paramInt3) {
/*  80 */       throw new IllegalArgumentException("bad start: " + paramInt1 + " for cstart: " + paramInt3);
/*     */     }
/*  82 */     if (paramInt4 < 0) {
/*  83 */       throw new IllegalArgumentException("bad clen: " + paramInt4);
/*     */     }
/*  85 */     if (paramInt3 + paramInt4 > paramArrayOfChar.length) {
/*  86 */       throw new IllegalArgumentException("bad clen: " + paramInt4 + " cstart: " + paramInt3 + " for array len: " + paramArrayOfChar.length);
/*     */     }
/*  88 */     if (paramInt2 < 0) {
/*  89 */       throw new IllegalArgumentException("bad len: " + paramInt2);
/*     */     }
/*  91 */     if (paramInt1 + paramInt2 > paramInt3 + paramInt4) {
/*  92 */       throw new IllegalArgumentException("bad len: " + paramInt2 + " start: " + paramInt1 + " for cstart: " + paramInt3 + " clen: " + paramInt4);
/*     */     }
/*  94 */     if (paramFont == null) {
/*  95 */       throw new IllegalArgumentException("bad font: null");
/*     */     }
/*  97 */     if (paramFontRenderContext == null) {
/*  98 */       throw new IllegalArgumentException("bad frc: null");
/*     */     }
/*     */     
/* 101 */     this.chars = paramArrayOfChar;
/* 102 */     this.start = paramInt1;
/* 103 */     this.len = paramInt2;
/* 104 */     this.cstart = paramInt3;
/* 105 */     this.clen = paramInt4;
/* 106 */     this.level = paramInt5;
/* 107 */     this.flags = paramInt6;
/* 108 */     this.font = paramFont;
/* 109 */     this.frc = paramFontRenderContext;
/*     */     
/* 111 */     if (paramCoreMetrics != null) {
/* 112 */       this.cm = paramCoreMetrics;
/*     */     } else {
/* 114 */       LineMetrics localLineMetrics = paramFont.getLineMetrics(paramArrayOfChar, paramInt3, paramInt4, paramFontRenderContext);
/* 115 */       this.cm = ((FontLineMetrics)localLineMetrics).cm;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public char[] getChars()
/*     */   {
/* 122 */     return this.chars;
/*     */   }
/*     */   
/*     */   public int getStart() {
/* 126 */     return this.start;
/*     */   }
/*     */   
/*     */   public int getLength() {
/* 130 */     return this.len;
/*     */   }
/*     */   
/*     */   public int getContextStart() {
/* 134 */     return this.cstart;
/*     */   }
/*     */   
/*     */   public int getContextLength() {
/* 138 */     return this.clen;
/*     */   }
/*     */   
/*     */   public int getLayoutFlags() {
/* 142 */     return this.flags;
/*     */   }
/*     */   
/*     */   public int getBidiLevel() {
/* 146 */     return this.level;
/*     */   }
/*     */   
/*     */   public Font getFont() {
/* 150 */     return this.font;
/*     */   }
/*     */   
/*     */   public FontRenderContext getFRC() {
/* 154 */     return this.frc;
/*     */   }
/*     */   
/*     */   public CoreMetrics getCoreMetrics() {
/* 158 */     return this.cm;
/*     */   }
/*     */   
/*     */   public TextSource getSubSource(int paramInt1, int paramInt2, int paramInt3) {
/* 162 */     if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 + paramInt2 > this.len)) {
/* 163 */       throw new IllegalArgumentException("bad start (" + paramInt1 + ") or length (" + paramInt2 + ")");
/*     */     }
/*     */     
/* 166 */     int i = this.level;
/* 167 */     if (paramInt3 != 2) {
/* 168 */       int j = (this.flags & 0x8) == 0 ? 1 : 0;
/* 169 */       if (((paramInt3 != 0) || (j == 0)) && ((paramInt3 != 1) || (j != 0)))
/*     */       {
/* 171 */         throw new IllegalArgumentException("direction flag is invalid");
/*     */       }
/* 173 */       i = j != 0 ? 0 : 1;
/*     */     }
/*     */     
/* 176 */     return new StandardTextSource(this.chars, this.start + paramInt1, paramInt2, this.cstart, this.clen, i, this.flags, this.font, this.frc, this.cm);
/*     */   }
/*     */   
/*     */   public String toString() {
/* 180 */     return toString(true);
/*     */   }
/*     */   
/*     */   public String toString(boolean paramBoolean) {
/* 184 */     StringBuffer localStringBuffer = new StringBuffer(super.toString());
/* 185 */     localStringBuffer.append("[start:");
/* 186 */     localStringBuffer.append(this.start);
/* 187 */     localStringBuffer.append(", len:");
/* 188 */     localStringBuffer.append(this.len);
/* 189 */     localStringBuffer.append(", cstart:");
/* 190 */     localStringBuffer.append(this.cstart);
/* 191 */     localStringBuffer.append(", clen:");
/* 192 */     localStringBuffer.append(this.clen);
/* 193 */     localStringBuffer.append(", chars:\"");
/*     */     int i;
/* 195 */     int j; if (paramBoolean == true) {
/* 196 */       i = this.cstart;
/* 197 */       j = this.cstart + this.clen;
/*     */     }
/*     */     else {
/* 200 */       i = this.start;
/* 201 */       j = this.start + this.len;
/*     */     }
/* 203 */     for (int k = i; k < j; k++) {
/* 204 */       if (k > i) {
/* 205 */         localStringBuffer.append(" ");
/*     */       }
/* 207 */       localStringBuffer.append(Integer.toHexString(this.chars[k]));
/*     */     }
/* 209 */     localStringBuffer.append("\"");
/* 210 */     localStringBuffer.append(", level:");
/* 211 */     localStringBuffer.append(this.level);
/* 212 */     localStringBuffer.append(", flags:");
/* 213 */     localStringBuffer.append(this.flags);
/* 214 */     localStringBuffer.append(", font:");
/* 215 */     localStringBuffer.append(this.font);
/* 216 */     localStringBuffer.append(", frc:");
/* 217 */     localStringBuffer.append(this.frc);
/* 218 */     localStringBuffer.append(", cm:");
/* 219 */     localStringBuffer.append(this.cm);
/* 220 */     localStringBuffer.append("]");
/*     */     
/* 222 */     return localStringBuffer.toString();
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\font\StandardTextSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */