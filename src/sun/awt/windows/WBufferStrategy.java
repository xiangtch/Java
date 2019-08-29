/*    */ package sun.awt.windows;
/*    */ 
/*    */ import java.awt.Component;
/*    */ import java.awt.Image;
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
/*    */ 
/*    */ public final class WBufferStrategy
/*    */ {
/*    */   private static native void initIDs(Class<?> paramClass);
/*    */   
/*    */   public static native Image getDrawBuffer(Component paramComponent);
/*    */   
/*    */   static
/*    */   {
/* 44 */     initIDs(Component.class);
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\windows\WBufferStrategy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */