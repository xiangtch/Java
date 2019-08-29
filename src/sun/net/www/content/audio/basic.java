/*    */ package sun.net.www.content.audio;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.net.ContentHandler;
/*    */ import java.net.URLConnection;
/*    */ import sun.applet.AppletAudioClip;
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
/*    */ public class basic
/*    */   extends ContentHandler
/*    */ {
/*    */   public Object getContent(URLConnection paramURLConnection)
/*    */     throws IOException
/*    */   {
/* 43 */     return new AppletAudioClip(paramURLConnection);
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\net\www\content\audio\basic.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */