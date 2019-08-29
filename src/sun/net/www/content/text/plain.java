/*    */ package sun.net.www.content.text;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.net.ContentHandler;
/*    */ import java.net.URLConnection;
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
/*    */ public class plain
/*    */   extends ContentHandler
/*    */ {
/*    */   public Object getContent(URLConnection paramURLConnection)
/*    */   {
/*    */     try
/*    */     {
/* 42 */       InputStream localInputStream = paramURLConnection.getInputStream();
/* 43 */       return new PlainTextInputStream(paramURLConnection.getInputStream());
/*    */     } catch (IOException localIOException) {
/* 45 */       return "Error reading document:\n" + localIOException.toString();
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\net\www\content\text\plain.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */