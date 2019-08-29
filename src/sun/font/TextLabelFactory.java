/*     */ package sun.font;
/*     */ 
/*     */ import java.awt.Font;
/*     */ import java.awt.font.FontRenderContext;
/*     */ import java.text.Bidi;
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
/*     */ public final class TextLabelFactory
/*     */ {
/*     */   private final FontRenderContext frc;
/*     */   private final char[] text;
/*     */   private final Bidi bidi;
/*     */   private Bidi lineBidi;
/*     */   private final int flags;
/*     */   private int lineStart;
/*     */   private int lineLimit;
/*     */   
/*     */   public TextLabelFactory(FontRenderContext paramFontRenderContext, char[] paramArrayOfChar, Bidi paramBidi, int paramInt)
/*     */   {
/*  71 */     this.frc = paramFontRenderContext;
/*  72 */     this.text = ((char[])paramArrayOfChar.clone());
/*  73 */     this.bidi = paramBidi;
/*  74 */     this.flags = paramInt;
/*  75 */     this.lineBidi = paramBidi;
/*  76 */     this.lineStart = 0;
/*  77 */     this.lineLimit = paramArrayOfChar.length;
/*     */   }
/*     */   
/*     */   public FontRenderContext getFontRenderContext() {
/*  81 */     return this.frc;
/*     */   }
/*     */   
/*     */   public Bidi getLineBidi() {
/*  85 */     return this.lineBidi;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setLineContext(int paramInt1, int paramInt2)
/*     */   {
/*  95 */     this.lineStart = paramInt1;
/*  96 */     this.lineLimit = paramInt2;
/*  97 */     if (this.bidi != null) {
/*  98 */       this.lineBidi = this.bidi.createLineBidi(paramInt1, paramInt2);
/*     */     }
/*     */   }
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
/*     */   public ExtendedTextLabel createExtended(Font paramFont, CoreMetrics paramCoreMetrics, Decoration paramDecoration, int paramInt1, int paramInt2)
/*     */   {
/* 122 */     if ((paramInt1 >= paramInt2) || (paramInt1 < this.lineStart) || (paramInt2 > this.lineLimit)) {
/* 123 */       throw new IllegalArgumentException("bad start: " + paramInt1 + " or limit: " + paramInt2);
/*     */     }
/*     */     
/* 126 */     int i = this.lineBidi == null ? 0 : this.lineBidi.getLevelAt(paramInt1 - this.lineStart);
/* 127 */     int j = (this.lineBidi == null) || (this.lineBidi.baseIsLeftToRight()) ? 0 : 1;
/* 128 */     int k = this.flags & 0xFFFFFFF6;
/* 129 */     if ((i & 0x1) != 0) k |= 0x1;
/* 130 */     if ((j & 0x1) != 0) { k |= 0x8;
/*     */     }
/* 132 */     StandardTextSource localStandardTextSource = new StandardTextSource(this.text, paramInt1, paramInt2 - paramInt1, this.lineStart, this.lineLimit - this.lineStart, i, k, paramFont, this.frc, paramCoreMetrics);
/* 133 */     return new ExtendedTextSourceLabel(localStandardTextSource, paramDecoration);
/*     */   }
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
/*     */   public TextLabel createSimple(Font paramFont, CoreMetrics paramCoreMetrics, int paramInt1, int paramInt2)
/*     */   {
/* 148 */     if ((paramInt1 >= paramInt2) || (paramInt1 < this.lineStart) || (paramInt2 > this.lineLimit)) {
/* 149 */       throw new IllegalArgumentException("bad start: " + paramInt1 + " or limit: " + paramInt2);
/*     */     }
/*     */     
/* 152 */     int i = this.lineBidi == null ? 0 : this.lineBidi.getLevelAt(paramInt1 - this.lineStart);
/* 153 */     int j = (this.lineBidi == null) || (this.lineBidi.baseIsLeftToRight()) ? 0 : 1;
/* 154 */     int k = this.flags & 0xFFFFFFF6;
/* 155 */     if ((i & 0x1) != 0) k |= 0x1;
/* 156 */     if ((j & 0x1) != 0) k |= 0x8;
/* 157 */     StandardTextSource localStandardTextSource = new StandardTextSource(this.text, paramInt1, paramInt2 - paramInt1, this.lineStart, this.lineLimit - this.lineStart, i, k, paramFont, this.frc, paramCoreMetrics);
/* 158 */     return new TextSourceLabel(localStandardTextSource);
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\font\TextLabelFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */