/*    */ package sun.applet;
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
/*    */ public class AppletIllegalArgumentException
/*    */   extends IllegalArgumentException
/*    */ {
/* 35 */   private String key = null;
/*    */   
/*    */   public AppletIllegalArgumentException(String paramString) {
/* 38 */     super(paramString);
/* 39 */     this.key = paramString;
/*    */   }
/*    */   
/*    */   public String getLocalizedMessage()
/*    */   {
/* 44 */     return amh.getMessage(this.key);
/*    */   }
/*    */   
/* 47 */   private static AppletMessageHandler amh = new AppletMessageHandler("appletillegalargumentexception");
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\applet\AppletIllegalArgumentException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */