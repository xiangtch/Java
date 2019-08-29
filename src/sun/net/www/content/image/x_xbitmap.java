/*    */ package sun.net.www.content.image;
/*    */ 
/*    */ import java.awt.Image;
/*    */ import java.awt.Toolkit;
/*    */ import java.io.IOException;
/*    */ import java.net.ContentHandler;
/*    */ import java.net.URLConnection;
/*    */ import sun.awt.image.URLImageSource;
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
/*    */ public class x_xbitmap
/*    */   extends ContentHandler
/*    */ {
/*    */   public Object getContent(URLConnection paramURLConnection)
/*    */     throws IOException
/*    */   {
/* 35 */     return new URLImageSource(paramURLConnection);
/*    */   }
/*    */   
/*    */   public Object getContent(URLConnection paramURLConnection, Class[] paramArrayOfClass) throws IOException
/*    */   {
/* 40 */     Class[] arrayOfClass = paramArrayOfClass;
/* 41 */     for (int i = 0; i < arrayOfClass.length; i++) {
/* 42 */       if (arrayOfClass[i].isAssignableFrom(URLImageSource.class)) {
/* 43 */         return new URLImageSource(paramURLConnection);
/*    */       }
/* 45 */       if (arrayOfClass[i].isAssignableFrom(Image.class)) {
/* 46 */         Toolkit localToolkit = Toolkit.getDefaultToolkit();
/* 47 */         return localToolkit.createImage(new URLImageSource(paramURLConnection));
/*    */       }
/*    */     }
/* 50 */     return null;
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\net\www\content\image\x_xbitmap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */