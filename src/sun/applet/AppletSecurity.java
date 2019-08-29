/*     */ package sun.applet;
/*     */ 
/*     */ import java.lang.reflect.Field;
/*     */ import java.net.URLClassLoader;
/*     */ import java.security.AccessControlContext;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.security.ProtectionDomain;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Properties;
/*     */ import sun.awt.AWTSecurityManager;
/*     */ import sun.awt.AppContext;
/*     */ import sun.security.util.SecurityConstants;
/*     */ import sun.security.util.SecurityConstants.AWT;
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
/*     */ public class AppletSecurity
/*     */   extends AWTSecurityManager
/*     */ {
/*  57 */   private static Field facc = null;
/*     */   
/*     */ 
/*  60 */   private static Field fcontext = null;
/*     */   
/*     */   static {
/*     */     try {
/*  64 */       facc = URLClassLoader.class.getDeclaredField("acc");
/*  65 */       facc.setAccessible(true);
/*  66 */       fcontext = AccessControlContext.class.getDeclaredField("context");
/*  67 */       fcontext.setAccessible(true);
/*     */     } catch (NoSuchFieldException localNoSuchFieldException) {
/*  69 */       throw new UnsupportedOperationException(localNoSuchFieldException);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public AppletSecurity()
/*     */   {
/*  78 */     reset();
/*     */   }
/*     */   
/*     */ 
/*  82 */   private HashSet restrictedPackages = new HashSet();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void reset()
/*     */   {
/*  90 */     this.restrictedPackages.clear();
/*     */     
/*  92 */     AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Object run()
/*     */       {
/*  96 */         Enumeration localEnumeration = System.getProperties().propertyNames();
/*     */         
/*  98 */         while (localEnumeration.hasMoreElements())
/*     */         {
/* 100 */           String str1 = (String)localEnumeration.nextElement();
/*     */           
/* 102 */           if ((str1 != null) && (str1.startsWith("package.restrict.access.")))
/*     */           {
/* 104 */             String str2 = System.getProperty(str1);
/*     */             
/* 106 */             if ((str2 != null) && (str2.equalsIgnoreCase("true")))
/*     */             {
/* 108 */               String str3 = str1.substring(24);
/*     */               
/*     */ 
/* 111 */               AppletSecurity.this.restrictedPackages.add(str3);
/*     */             }
/*     */           }
/*     */         }
/* 115 */         return null;
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private AppletClassLoader currentAppletClassLoader()
/*     */   {
/* 126 */     ClassLoader localClassLoader1 = currentClassLoader();
/*     */     
/* 128 */     if ((localClassLoader1 == null) || ((localClassLoader1 instanceof AppletClassLoader))) {
/* 129 */       return (AppletClassLoader)localClassLoader1;
/*     */     }
/*     */     
/* 132 */     Class[] arrayOfClass = getClassContext();
/* 133 */     for (int i = 0; i < arrayOfClass.length; i++) {
/* 134 */       localClassLoader1 = arrayOfClass[i].getClassLoader();
/* 135 */       if ((localClassLoader1 instanceof AppletClassLoader)) {
/* 136 */         return (AppletClassLoader)localClassLoader1;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 146 */     for (i = 0; i < arrayOfClass.length; i++) {
/* 147 */       final ClassLoader localClassLoader2 = arrayOfClass[i].getClassLoader();
/*     */       
/* 149 */       if ((localClassLoader2 instanceof URLClassLoader)) {
/* 150 */         localClassLoader1 = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
/*     */         {
/*     */           public Object run() {
/* 153 */             AccessControlContext localAccessControlContext = null;
/* 154 */             ProtectionDomain[] arrayOfProtectionDomain = null;
/*     */             try
/*     */             {
/* 157 */               localAccessControlContext = (AccessControlContext)AppletSecurity.facc.get(localClassLoader2);
/* 158 */               if (localAccessControlContext == null) {
/* 159 */                 return null;
/*     */               }
/*     */               
/* 162 */               arrayOfProtectionDomain = (ProtectionDomain[])AppletSecurity.fcontext.get(localAccessControlContext);
/* 163 */               if (arrayOfProtectionDomain == null) {
/* 164 */                 return null;
/*     */               }
/*     */             } catch (Exception localException) {
/* 167 */               throw new UnsupportedOperationException(localException);
/*     */             }
/*     */             
/* 170 */             for (int i = 0; i < arrayOfProtectionDomain.length; i++) {
/* 171 */               ClassLoader localClassLoader = arrayOfProtectionDomain[i].getClassLoader();
/*     */               
/* 173 */               if ((localClassLoader instanceof AppletClassLoader)) {
/* 174 */                 return localClassLoader;
/*     */               }
/*     */             }
/*     */             
/* 178 */             return null;
/*     */           }
/*     */         });
/*     */         
/* 182 */         if (localClassLoader1 != null) {
/* 183 */           return (AppletClassLoader)localClassLoader1;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 189 */     localClassLoader1 = Thread.currentThread().getContextClassLoader();
/* 190 */     if ((localClassLoader1 instanceof AppletClassLoader)) {
/* 191 */       return (AppletClassLoader)localClassLoader1;
/*     */     }
/*     */     
/* 194 */     return (AppletClassLoader)null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean inThreadGroup(ThreadGroup paramThreadGroup)
/*     */   {
/* 203 */     if (currentAppletClassLoader() == null) {
/* 204 */       return false;
/*     */     }
/* 206 */     return getThreadGroup().parentOf(paramThreadGroup);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean inThreadGroup(Thread paramThread)
/*     */   {
/* 214 */     return inThreadGroup(paramThread.getThreadGroup());
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
/*     */   public void checkAccess(Thread paramThread)
/*     */   {
/* 228 */     if ((paramThread.getState() != Thread.State.TERMINATED) && (!inThreadGroup(paramThread))) {
/* 229 */       checkPermission(SecurityConstants.MODIFY_THREAD_PERMISSION);
/*     */     }
/*     */   }
/*     */   
/* 233 */   private boolean inThreadGroupCheck = false;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void checkPackageAccess(String paramString)
/*     */   {
/* 281 */     super.checkPackageAccess(paramString);
/*     */     
/*     */ 
/* 284 */     for (Iterator localIterator = this.restrictedPackages.iterator(); localIterator.hasNext();)
/*     */     {
/* 286 */       String str = (String)localIterator.next();
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 291 */       if ((paramString.equals(str)) || (paramString.startsWith(str + ".")))
/*     */       {
/* 293 */         checkPermission(new RuntimePermission("accessClassInPackage." + paramString));
/*     */       }
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
/*     */   public void checkAwtEventQueueAccess()
/*     */   {
/* 310 */     AppContext localAppContext = AppContext.getAppContext();
/* 311 */     AppletClassLoader localAppletClassLoader = currentAppletClassLoader();
/*     */     
/* 313 */     if ((AppContext.isMainContext(localAppContext)) && (localAppletClassLoader != null))
/*     */     {
/*     */ 
/*     */ 
/* 317 */       super.checkPermission(SecurityConstants.AWT.CHECK_AWT_EVENTQUEUE_PERMISSION);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ThreadGroup getThreadGroup()
/*     */   {
/* 329 */     AppletClassLoader localAppletClassLoader = currentAppletClassLoader();
/*     */     
/* 331 */     ThreadGroup localThreadGroup = localAppletClassLoader == null ? null : localAppletClassLoader.getThreadGroup();
/* 332 */     if (localThreadGroup != null) {
/* 333 */       return localThreadGroup;
/*     */     }
/* 335 */     return super.getThreadGroup();
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
/*     */   public AppContext getAppContext()
/*     */   {
/* 352 */     AppletClassLoader localAppletClassLoader = currentAppletClassLoader();
/*     */     
/* 354 */     if (localAppletClassLoader == null) {
/* 355 */       return null;
/*     */     }
/* 357 */     AppContext localAppContext = localAppletClassLoader.getAppContext();
/*     */     
/*     */ 
/*     */ 
/* 361 */     if (localAppContext == null) {
/* 362 */       throw new SecurityException("Applet classloader has invalid AppContext");
/*     */     }
/*     */     
/* 365 */     return localAppContext;
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public synchronized void checkAccess(ThreadGroup paramThreadGroup)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 190	sun/applet/AppletSecurity:inThreadGroupCheck	Z
/*     */     //   4: ifeq +13 -> 17
/*     */     //   7: aload_0
/*     */     //   8: getstatic 194	sun/security/util/SecurityConstants:MODIFY_THREADGROUP_PERMISSION	Ljava/lang/RuntimePermission;
/*     */     //   11: invokevirtual 225	sun/applet/AppletSecurity:checkPermission	(Ljava/security/Permission;)V
/*     */     //   14: goto +39 -> 53
/*     */     //   17: aload_0
/*     */     //   18: iconst_1
/*     */     //   19: putfield 190	sun/applet/AppletSecurity:inThreadGroupCheck	Z
/*     */     //   22: aload_0
/*     */     //   23: aload_1
/*     */     //   24: invokevirtual 224	sun/applet/AppletSecurity:inThreadGroup	(Ljava/lang/ThreadGroup;)Z
/*     */     //   27: ifne +10 -> 37
/*     */     //   30: aload_0
/*     */     //   31: getstatic 194	sun/security/util/SecurityConstants:MODIFY_THREADGROUP_PERMISSION	Ljava/lang/RuntimePermission;
/*     */     //   34: invokevirtual 225	sun/applet/AppletSecurity:checkPermission	(Ljava/security/Permission;)V
/*     */     //   37: aload_0
/*     */     //   38: iconst_0
/*     */     //   39: putfield 190	sun/applet/AppletSecurity:inThreadGroupCheck	Z
/*     */     //   42: goto +11 -> 53
/*     */     //   45: astore_2
/*     */     //   46: aload_0
/*     */     //   47: iconst_0
/*     */     //   48: putfield 190	sun/applet/AppletSecurity:inThreadGroupCheck	Z
/*     */     //   51: aload_2
/*     */     //   52: athrow
/*     */     //   53: return
/*     */     // Line number table:
/*     */     //   Java source line #240	-> byte code offset #0
/*     */     //   Java source line #245	-> byte code offset #7
/*     */     //   Java source line #248	-> byte code offset #17
/*     */     //   Java source line #249	-> byte code offset #22
/*     */     //   Java source line #250	-> byte code offset #30
/*     */     //   Java source line #253	-> byte code offset #37
/*     */     //   Java source line #254	-> byte code offset #42
/*     */     //   Java source line #253	-> byte code offset #45
/*     */     //   Java source line #254	-> byte code offset #51
/*     */     //   Java source line #256	-> byte code offset #53
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	54	0	this	AppletSecurity
/*     */     //   0	54	1	paramThreadGroup	ThreadGroup
/*     */     //   45	7	2	localObject	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   17	37	45	finally
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\applet\AppletSecurity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */