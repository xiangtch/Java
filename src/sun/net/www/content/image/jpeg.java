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
/*    */ 
/*    */ public class jpeg
/*    */   extends ContentHandler
/*    */ {
/*    */   public Object getContent(URLConnection paramURLConnection)
/*    */     throws IOException
/*    */   {
/* 36 */     return new URLImageSource(paramURLConnection);
/*    */   }
/*    */   
/*    */   public Object getContent(URLConnection paramURLConnection, Class[] paramArrayOfClass) throws IOException
/*    */   {
/* 41 */     Class[] arrayOfClass = paramArrayOfClass;
/* 42 */     for (int i = 0; i < arrayOfClass.length; i++) {
/* 43 */       if (arrayOfClass[i].isAssignableFrom(URLImageSource.class)) {
/* 44 */         return new URLImageSource(paramURLConnection);
/*    */       }
/* 46 */       if (arrayOfClass[i].isAssignableFrom(Image.class)) {
/* 47 */         Toolkit localToolkit = Toolkit.getDefaultToolkit();
/* 48 */         return localToolkit.createImage(new URLImageSource(paramURLConnection));
/*    */       }
/*    */     }
/* 51 */     return null;
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\net\www\content\image\jpeg.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */