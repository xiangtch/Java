/*    */ package sun.java2d.pipe;
/*    */ 
/*    */ import sun.font.GlyphList;
/*    */ import sun.java2d.SunGraphics2D;
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
/*    */ 
/*    */ public class LCDTextRenderer
/*    */   extends GlyphListLoopPipe
/*    */ {
/*    */   protected void drawGlyphList(SunGraphics2D paramSunGraphics2D, GlyphList paramGlyphList)
/*    */   {
/* 42 */     paramSunGraphics2D.loops.drawGlyphListLCDLoop.DrawGlyphListLCD(paramSunGraphics2D, paramSunGraphics2D.surfaceData, paramGlyphList);
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\java2d\pipe\LCDTextRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */