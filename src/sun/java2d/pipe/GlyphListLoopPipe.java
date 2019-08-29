/*    */ package sun.java2d.pipe;
/*    */ 
/*    */ import sun.font.GlyphList;
/*    */ import sun.java2d.SunGraphics2D;
/*    */ import sun.java2d.loops.DrawGlyphList;
/*    */ import sun.java2d.loops.DrawGlyphListAA;
/*    */ import sun.java2d.loops.DrawGlyphListLCD;
/*    */ import sun.java2d.loops.RenderLoops;
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
/*    */ public abstract class GlyphListLoopPipe
/*    */   extends GlyphListPipe
/*    */   implements LoopBasedPipe
/*    */ {
/*    */   protected void drawGlyphList(SunGraphics2D paramSunGraphics2D, GlyphList paramGlyphList, int paramInt)
/*    */   {
/* 44 */     switch (paramInt)
/*    */     {
/*    */     case 1: 
/* 47 */       paramSunGraphics2D.loops.drawGlyphListLoop.DrawGlyphList(paramSunGraphics2D, paramSunGraphics2D.surfaceData, paramGlyphList);
/* 48 */       return;
/*    */     
/*    */     case 2: 
/* 51 */       paramSunGraphics2D.loops.drawGlyphListAALoop.DrawGlyphListAA(paramSunGraphics2D, paramSunGraphics2D.surfaceData, paramGlyphList);
/* 52 */       return;
/*    */     
/*    */     case 4: 
/*    */     case 6: 
/* 56 */       paramSunGraphics2D.loops.drawGlyphListLCDLoop.DrawGlyphListLCD(paramSunGraphics2D, paramSunGraphics2D.surfaceData, paramGlyphList);
/* 57 */       return;
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\java2d\pipe\GlyphListLoopPipe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */