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
/*    */ 
/*    */ public class gif
/*    */   extends ContentHandler
/*    */ {
/*    */   public Object getContent(URLConnection paramURLConnection)
/*    */     throws IOException
/*    */   {
/* 37 */     return new URLImageSource(paramURLConnection);
/*    */   }
/*    */   
/*    */   public Object getContent(URLConnection paramURLConnection, Class[] paramArrayOfClass) throws IOException
/*    */   {
/* 42 */     Class[] arrayOfClass = paramArrayOfClass;
/* 43 */     for (int i = 0; i < arrayOfClass.length; i++) {
/* 44 */       if (arrayOfClass[i].isAssignableFrom(URLImageSource.class)) {
/* 45 */         return new URLImageSource(paramURLConnection);
/*    */       }
/* 47 */       if (arrayOfClass[i].isAssignableFrom(Image.class)) {
/* 48 */         Toolkit localToolkit = Toolkit.getDefaultToolkit();
/* 49 */         return localToolkit.createImage(new URLImageSource(paramURLConnection));
/*    */       }
/*    */     }
/* 52 */     return null;
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\net\www\content\image\gif.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */