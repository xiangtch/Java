/*     */ package sun.text.normalizer;
/*     */ 
/*     */ import java.util.Iterator;
/*     */ import java.util.TreeSet;
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
/*     */ public class UnicodeSetIterator
/*     */ {
/*  81 */   public static int IS_STRING = -1;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int codepoint;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int codepointEnd;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String string;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private UnicodeSet set;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public UnicodeSetIterator(UnicodeSet paramUnicodeSet)
/*     */   {
/* 115 */     reset(paramUnicodeSet);
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
/*     */ 
/*     */   public boolean nextRange()
/*     */   {
/* 139 */     if (this.nextElement <= this.endElement) {
/* 140 */       this.codepointEnd = this.endElement;
/* 141 */       this.codepoint = this.nextElement;
/* 142 */       this.nextElement = (this.endElement + 1);
/* 143 */       return true;
/*     */     }
/* 145 */     if (this.range < this.endRange) {
/* 146 */       loadRange(++this.range);
/* 147 */       this.codepointEnd = this.endElement;
/* 148 */       this.codepoint = this.nextElement;
/* 149 */       this.nextElement = (this.endElement + 1);
/* 150 */       return true;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 155 */     if (this.stringIterator == null) return false;
/* 156 */     this.codepoint = IS_STRING;
/* 157 */     this.string = ((String)this.stringIterator.next());
/* 158 */     if (!this.stringIterator.hasNext()) this.stringIterator = null;
/* 159 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void reset(UnicodeSet paramUnicodeSet)
/*     */   {
/* 170 */     this.set = paramUnicodeSet;
/* 171 */     reset();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void reset()
/*     */   {
/* 179 */     this.endRange = (this.set.getRangeCount() - 1);
/* 180 */     this.range = 0;
/* 181 */     this.endElement = -1;
/* 182 */     this.nextElement = 0;
/* 183 */     if (this.endRange >= 0) {
/* 184 */       loadRange(this.range);
/*     */     }
/* 186 */     this.stringIterator = null;
/* 187 */     if (this.set.strings != null) {
/* 188 */       this.stringIterator = this.set.strings.iterator();
/* 189 */       if (!this.stringIterator.hasNext()) { this.stringIterator = null;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/* 196 */   private int endRange = 0;
/* 197 */   private int range = 0;
/*     */   
/*     */ 
/*     */   protected int endElement;
/*     */   
/*     */ 
/*     */   protected int nextElement;
/*     */   
/*     */ 
/* 206 */   private Iterator<String> stringIterator = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void loadRange(int paramInt)
/*     */   {
/* 216 */     this.nextElement = this.set.getRangeStart(paramInt);
/* 217 */     this.endElement = this.set.getRangeEnd(paramInt);
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\text\normalizer\UnicodeSetIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */