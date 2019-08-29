/*     */ package sun.security.krb5;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Collection;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Vector;
/*     */ import sun.security.krb5.internal.Krb5;
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
/*     */ public class SCDynamicStoreConfig
/*     */ {
/*  37 */   private static boolean DEBUG = Krb5.DEBUG;
/*     */   
/*     */   static {
/*  40 */     boolean bool = ((Boolean)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Boolean run() {
/*  43 */         String str = System.getProperty("os.name");
/*  44 */         if (str.contains("OS X")) {
/*  45 */           System.loadLibrary("osx");
/*  46 */           return Boolean.valueOf(true);
/*     */         }
/*  48 */         return Boolean.valueOf(false);
/*     */       }
/*     */     })).booleanValue();
/*  51 */     if (bool) installNotificationCallback();
/*     */   }
/*     */   
/*     */   private static Vector<String> unwrapHost(Collection<Hashtable<String, String>> paramCollection)
/*     */   {
/*  56 */     Vector localVector = new Vector();
/*  57 */     for (Hashtable localHashtable : paramCollection) {
/*  58 */       localVector.add(localHashtable.get("host"));
/*     */     }
/*  60 */     return localVector;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static Hashtable<String, Object> convertRealmConfigs(Hashtable<String, ?> paramHashtable)
/*     */   {
/*  71 */     Hashtable localHashtable1 = new Hashtable();
/*     */     
/*  73 */     for (String str : paramHashtable.keySet())
/*     */     {
/*     */ 
/*  76 */       Hashtable localHashtable2 = (Hashtable)paramHashtable.get(str);
/*  77 */       Hashtable localHashtable3 = new Hashtable();
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*  82 */       Collection localCollection1 = (Collection)localHashtable2.get("kdc");
/*  83 */       if (localCollection1 != null) { localHashtable3.put("kdc", unwrapHost(localCollection1));
/*     */       }
/*     */       
/*     */ 
/*  87 */       Collection localCollection2 = (Collection)localHashtable2.get("kadmin");
/*  88 */       if (localCollection2 != null) { localHashtable3.put("admin_server", unwrapHost(localCollection2));
/*     */       }
/*     */       
/*  91 */       localHashtable1.put(str, localHashtable3);
/*     */     }
/*     */     
/*  94 */     return localHashtable1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Hashtable<String, Object> getConfig()
/*     */     throws IOException
/*     */   {
/* 105 */     Hashtable localHashtable = getKerberosConfig();
/* 106 */     if (localHashtable == null) {
/* 107 */       throw new IOException("Could not load configuration from SCDynamicStore");
/*     */     }
/*     */     
/* 110 */     if (DEBUG) System.out.println("Raw map from JNI: " + localHashtable);
/* 111 */     return convertNativeConfig(localHashtable);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static Hashtable<String, Object> convertNativeConfig(Hashtable<String, Object> paramHashtable)
/*     */   {
/* 119 */     Hashtable localHashtable1 = (Hashtable)paramHashtable.get("realms");
/* 120 */     if (localHashtable1 != null) {
/* 121 */       paramHashtable.remove("realms");
/* 122 */       Hashtable localHashtable2 = convertRealmConfigs(localHashtable1);
/* 123 */       paramHashtable.put("realms", localHashtable2);
/*     */     }
/* 125 */     WrapAllStringInVector(paramHashtable);
/* 126 */     if (DEBUG) System.out.println("stanzaTable : " + paramHashtable);
/* 127 */     return paramHashtable;
/*     */   }
/*     */   
/*     */ 
/*     */   private static void WrapAllStringInVector(Hashtable<String, Object> paramHashtable)
/*     */   {
/* 133 */     for (String str : paramHashtable.keySet()) {
/* 134 */       Object localObject = paramHashtable.get(str);
/* 135 */       if ((localObject instanceof Hashtable)) {
/* 136 */         WrapAllStringInVector((Hashtable)localObject);
/* 137 */       } else if ((localObject instanceof String)) {
/* 138 */         Vector localVector = new Vector();
/* 139 */         localVector.add((String)localObject);
/* 140 */         paramHashtable.put(str, localVector);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private static native void installNotificationCallback();
/*     */   
/*     */   private static native Hashtable<String, Object> getKerberosConfig();
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\krb5\SCDynamicStoreConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */