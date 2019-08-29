/*    */ package sun.font;
/*    */ 
/*    */ import java.security.AccessController;
/*    */ import java.security.PrivilegedAction;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class FontManagerFactory
/*    */ {
/* 50 */   private static FontManager instance = null;
/*    */   private static final String DEFAULT_CLASS;
/*    */   
/*    */   static {
/* 54 */     if (FontUtilities.isWindows) {
/* 55 */       DEFAULT_CLASS = "sun.awt.Win32FontManager";
/* 56 */     } else if (FontUtilities.isMacOSX) {
/* 57 */       DEFAULT_CLASS = "sun.font.CFontManager";
/*    */     } else {
/* 59 */       DEFAULT_CLASS = "sun.awt.X11FontManager";
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static synchronized FontManager getInstance()
/*    */   {
/* 70 */     if (instance != null) {
/* 71 */       return instance;
/*    */     }
/*    */     
/* 74 */     AccessController.doPrivileged(new PrivilegedAction()
/*    */     {
/*    */       public Object run()
/*    */       {
/*    */         try {
/* 79 */           String str = System.getProperty("sun.font.fontmanager", 
/* 80 */             FontManagerFactory.DEFAULT_CLASS);
/* 81 */           ClassLoader localClassLoader = ClassLoader.getSystemClassLoader();
/* 82 */           Class localClass = Class.forName(str, true, localClassLoader);
/* 83 */           FontManagerFactory.access$102((FontManager)localClass.newInstance());
/*    */         }
/*    */         catch (ClassNotFoundException|InstantiationException|IllegalAccessException localClassNotFoundException)
/*    */         {
/* 87 */           throw new InternalError(localClassNotFoundException);
/*    */         }
/*    */         
/* 90 */         return null;
/*    */       }
/*    */       
/* 93 */     });
/* 94 */     return instance;
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\font\FontManagerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */