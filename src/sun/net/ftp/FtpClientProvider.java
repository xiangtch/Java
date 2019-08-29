/*     */ package sun.net.ftp;
/*     */ 
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.ServiceConfigurationError;
/*     */ import sun.net.ftp.impl.DefaultFtpClientProvider;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class FtpClientProvider
/*     */ {
/*  48 */   private static final Object lock = new Object();
/*  49 */   private static FtpClientProvider provider = null;
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract FtpClient createFtpClient();
/*     */   
/*     */ 
/*     */   protected FtpClientProvider()
/*     */   {
/*  58 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  59 */     if (localSecurityManager != null) {
/*  60 */       localSecurityManager.checkPermission(new RuntimePermission("ftpClientProvider"));
/*     */     }
/*     */   }
/*     */   
/*     */   private static boolean loadProviderFromProperty() {
/*  65 */     String str = System.getProperty("sun.net.ftpClientProvider");
/*  66 */     if (str == null) {
/*  67 */       return false;
/*     */     }
/*     */     try {
/*  70 */       Class localClass = Class.forName(str, true, null);
/*  71 */       provider = (FtpClientProvider)localClass.newInstance();
/*  72 */       return true;
/*     */ 
/*     */     }
/*     */     catch (ClassNotFoundException|IllegalAccessException|InstantiationException|SecurityException localClassNotFoundException)
/*     */     {
/*  77 */       throw new ServiceConfigurationError(localClassNotFoundException.toString());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static boolean loadProviderAsService()
/*     */   {
/*  98 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static FtpClientProvider provider()
/*     */   {
/* 137 */     synchronized (lock) {
/* 138 */       if (provider != null) {
/* 139 */         return provider;
/*     */       }
/* 141 */       (FtpClientProvider)AccessController.doPrivileged(new PrivilegedAction()
/*     */       {
/*     */         public Object run()
/*     */         {
/* 145 */           if (FtpClientProvider.access$000()) {
/* 146 */             return FtpClientProvider.provider;
/*     */           }
/* 148 */           if (FtpClientProvider.access$200()) {
/* 149 */             return FtpClientProvider.provider;
/*     */           }
/* 151 */           FtpClientProvider.access$102(new DefaultFtpClientProvider());
/* 152 */           return FtpClientProvider.provider;
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\net\ftp\FtpClientProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */