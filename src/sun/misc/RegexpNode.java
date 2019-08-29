/*     */ package sun.misc;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class RegexpNode
/*     */ {
/*     */   char c;
/*     */   RegexpNode firstchild;
/*     */   RegexpNode nextsibling;
/*     */   int depth;
/*     */   boolean exact;
/*     */   Object result;
/* 268 */   String re = null;
/*     */   
/*     */   RegexpNode() {
/* 271 */     this.c = '#';
/* 272 */     this.depth = 0;
/*     */   }
/*     */   
/* 275 */   RegexpNode(char paramChar, int paramInt) { this.c = paramChar;
/* 276 */     this.depth = paramInt;
/*     */   }
/*     */   
/* 279 */   RegexpNode add(char paramChar) { RegexpNode localRegexpNode = this.firstchild;
/* 280 */     if (localRegexpNode == null) {
/* 281 */       localRegexpNode = new RegexpNode(paramChar, this.depth + 1);
/*     */     } else {
/* 283 */       while (localRegexpNode != null) {
/* 284 */         if (localRegexpNode.c == paramChar) {
/* 285 */           return localRegexpNode;
/*     */         }
/* 287 */         localRegexpNode = localRegexpNode.nextsibling; }
/* 288 */       localRegexpNode = new RegexpNode(paramChar, this.depth + 1);
/* 289 */       localRegexpNode.nextsibling = this.firstchild;
/*     */     }
/* 291 */     this.firstchild = localRegexpNode;
/* 292 */     return localRegexpNode;
/*     */   }
/*     */   
/* 295 */   RegexpNode find(char paramChar) { for (RegexpNode localRegexpNode = this.firstchild; 
/* 296 */         localRegexpNode != null; 
/* 297 */         localRegexpNode = localRegexpNode.nextsibling)
/* 298 */       if (localRegexpNode.c == paramChar)
/* 299 */         return localRegexpNode;
/* 300 */     return null;
/*     */   }
/*     */   
/* 303 */   void print(PrintStream paramPrintStream) { if (this.nextsibling != null) {
/* 304 */       RegexpNode localRegexpNode = this;
/* 305 */       paramPrintStream.print("(");
/* 306 */       while (localRegexpNode != null) {
/* 307 */         paramPrintStream.write(localRegexpNode.c);
/* 308 */         if (localRegexpNode.firstchild != null)
/* 309 */           localRegexpNode.firstchild.print(paramPrintStream);
/* 310 */         localRegexpNode = localRegexpNode.nextsibling;
/* 311 */         paramPrintStream.write(localRegexpNode != null ? 124 : 41);
/*     */       }
/*     */     } else {
/* 314 */       paramPrintStream.write(this.c);
/* 315 */       if (this.firstchild != null) {
/* 316 */         this.firstchild.print(paramPrintStream);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\misc\RegexpNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */