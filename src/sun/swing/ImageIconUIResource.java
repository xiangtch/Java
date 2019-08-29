/*    */ package sun.swing;
/*    */ 
/*    */ import java.awt.Image;
/*    */ import javax.swing.ImageIcon;
/*    */ import javax.swing.plaf.UIResource;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ImageIconUIResource
/*    */   extends ImageIcon
/*    */   implements UIResource
/*    */ {
/*    */   public ImageIconUIResource(byte[] paramArrayOfByte)
/*    */   {
/* 47 */     super(paramArrayOfByte);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public ImageIconUIResource(Image paramImage)
/*    */   {
/* 57 */     super(paramImage);
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\swing\ImageIconUIResource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */