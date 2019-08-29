/*     */ package sun.security.jgss.wrapper;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.security.Provider;
/*     */ import java.util.HashMap;
/*     */ import org.ietf.jgss.Oid;
/*     */ import sun.security.action.PutAllAction;
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
/*     */ public final class SunNativeProvider
/*     */   extends Provider
/*     */ {
/*     */   static boolean DEBUG;
/*  56 */   static final Provider INSTANCE = new SunNativeProvider();
/*     */   
/*     */   static void debug(String paramString) {
/*  59 */     if (DEBUG) {
/*  60 */       if (paramString == null) {
/*  61 */         throw new NullPointerException();
/*     */       }
/*  63 */       System.out.println("SunNativeGSS: " + paramString);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*  69 */   private static HashMap<String, String> MECH_MAP = (HashMap)AccessController.doPrivileged(new PrivilegedAction()
/*     */   {
/*     */ 
/*     */     public HashMap<String, String> run()
/*     */     {
/*  73 */       SunNativeProvider.DEBUG = Boolean.parseBoolean(System.getProperty("sun.security.nativegss.debug"));
/*     */       try {
/*  75 */         System.loadLibrary("j2gss");
/*     */       } catch (Error localError) {
/*  77 */         SunNativeProvider.debug("No j2gss library found!");
/*  78 */         if (SunNativeProvider.DEBUG) localError.printStackTrace();
/*  79 */         return null;
/*     */       }
/*  81 */       String[] arrayOfString = new String[0];
/*  82 */       String str1 = System.getProperty("sun.security.jgss.lib");
/*  83 */       Object localObject; if ((str1 == null) || (str1.trim().equals(""))) {
/*  84 */         localObject = System.getProperty("os.name");
/*  85 */         if (((String)localObject).startsWith("SunOS")) {
/*  86 */           arrayOfString = new String[] { "libgss.so" };
/*  87 */         } else if (((String)localObject).startsWith("Linux")) {
/*  88 */           arrayOfString = new String[] { "libgssapi.so", "libgssapi_krb5.so", "libgssapi_krb5.so.2" };
/*     */ 
/*     */ 
/*     */ 
/*     */         }
/*  93 */         else if (((String)localObject).contains("OS X")) {
/*  94 */           arrayOfString = new String[] { "libgssapi_krb5.dylib", "/usr/lib/sasl2/libgssapiv2.2.so" };
/*     */         }
/*     */         
/*     */       }
/*     */       else
/*     */       {
/* 100 */         arrayOfString = new String[] { str1 };
/*     */       }
/* 102 */       for (String str2 : arrayOfString) {
/* 103 */         if (GSSLibStub.init(str2, SunNativeProvider.DEBUG)) {
/* 104 */           SunNativeProvider.debug("Loaded GSS library: " + str2);
/* 105 */           Oid[] arrayOfOid = GSSLibStub.indicateMechs();
/* 106 */           HashMap localHashMap = new HashMap();
/*     */           
/* 108 */           for (int k = 0; k < arrayOfOid.length; k++) {
/* 109 */             SunNativeProvider.debug("Native MF for " + arrayOfOid[k]);
/* 110 */             localHashMap.put("GssApiMechanism." + arrayOfOid[k], "sun.security.jgss.wrapper.NativeGSSFactory");
/*     */           }
/*     */           
/* 113 */           return localHashMap;
/*     */         }
/*     */       }
/* 116 */       return null;
/*     */     }
/*  69 */   });
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final String DEBUG_PROP = "sun.security.nativegss.debug";
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final String LIB_PROP = "sun.security.jgss.lib";
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final String MF_CLASS = "sun.security.jgss.wrapper.NativeGSSFactory";
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final String INFO = "Sun Native GSS provider";
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final String NAME = "SunNativeGSS";
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final long serialVersionUID = -238911724858694204L;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public SunNativeProvider()
/*     */   {
/* 123 */     super("SunNativeGSS", 1.8D, "Sun Native GSS provider");
/*     */     
/* 125 */     if (MECH_MAP != null) {
/* 126 */       AccessController.doPrivileged(new PutAllAction(this, MECH_MAP));
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\jgss\wrapper\SunNativeProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */