/*     */ package sun.security.jgss;
/*     */ 
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.HashMap;
/*     */ import javax.security.auth.login.AppConfigurationEntry;
/*     */ import javax.security.auth.login.AppConfigurationEntry.LoginModuleControlFlag;
/*     */ import javax.security.auth.login.Configuration;
/*     */ import org.ietf.jgss.Oid;
/*     */ import sun.security.action.GetPropertyAction;
/*     */ import sun.security.util.Debug;
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
/*     */ public class LoginConfigImpl
/*     */   extends Configuration
/*     */ {
/*     */   private final Configuration config;
/*     */   private final GSSCaller caller;
/*     */   private final String mechName;
/*  46 */   private static final Debug debug = Debug.getInstance("gssloginconfig", "\t[GSS LoginConfigImpl]");
/*     */   
/*     */   public static final boolean HTTP_USE_GLOBAL_CREDS;
/*     */   
/*     */   static
/*     */   {
/*  52 */     String str = GetPropertyAction.privilegedGetProperty("http.use.global.creds");
/*     */     
/*  54 */     HTTP_USE_GLOBAL_CREDS = !"false".equalsIgnoreCase(str);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public LoginConfigImpl(GSSCaller paramGSSCaller, Oid paramOid)
/*     */   {
/*  66 */     this.caller = paramGSSCaller;
/*     */     
/*  68 */     if (paramOid.equals(GSSUtil.GSS_KRB5_MECH_OID)) {
/*  69 */       this.mechName = "krb5";
/*     */     } else {
/*  71 */       throw new IllegalArgumentException(paramOid.toString() + " not supported");
/*     */     }
/*     */     
/*  74 */     this.config = ((Configuration)AccessController.doPrivileged(new PrivilegedAction() {
/*     */       public Configuration run() {
/*  76 */         return Configuration.getConfiguration();
/*     */       }
/*     */     }));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public AppConfigurationEntry[] getAppConfigurationEntry(String paramString)
/*     */   {
/*  88 */     AppConfigurationEntry[] arrayOfAppConfigurationEntry = null;
/*     */     
/*     */ 
/*  91 */     if ("OTHER".equalsIgnoreCase(paramString)) {
/*  92 */       return null;
/*     */     }
/*     */     
/*  95 */     String[] arrayOfString1 = null;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 101 */     if ("krb5".equals(this.mechName)) {
/* 102 */       if (this.caller == GSSCaller.CALLER_INITIATE) {
/* 103 */         arrayOfString1 = new String[] { "com.sun.security.jgss.krb5.initiate", "com.sun.security.jgss.initiate" };
/*     */ 
/*     */ 
/*     */       }
/* 107 */       else if (this.caller == GSSCaller.CALLER_ACCEPT) {
/* 108 */         arrayOfString1 = new String[] { "com.sun.security.jgss.krb5.accept", "com.sun.security.jgss.accept" };
/*     */ 
/*     */ 
/*     */       }
/* 112 */       else if (this.caller == GSSCaller.CALLER_SSL_CLIENT) {
/* 113 */         arrayOfString1 = new String[] { "com.sun.security.jgss.krb5.initiate", "com.sun.net.ssl.client" };
/*     */ 
/*     */ 
/*     */       }
/* 117 */       else if (this.caller == GSSCaller.CALLER_SSL_SERVER) {
/* 118 */         arrayOfString1 = new String[] { "com.sun.security.jgss.krb5.accept", "com.sun.net.ssl.server" };
/*     */ 
/*     */ 
/*     */       }
/* 122 */       else if ((this.caller instanceof HttpCaller)) {
/* 123 */         arrayOfString1 = new String[] { "com.sun.security.jgss.krb5.initiate" };
/*     */ 
/*     */       }
/* 126 */       else if (this.caller == GSSCaller.CALLER_UNKNOWN) {
/* 127 */         throw new AssertionError("caller not defined");
/*     */       }
/*     */     } else {
/* 130 */       throw new IllegalArgumentException(this.mechName + " not supported");
/*     */     }
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
/* 155 */     for (String str : arrayOfString1) {
/* 156 */       arrayOfAppConfigurationEntry = this.config.getAppConfigurationEntry(str);
/* 157 */       if (debug != null) {
/* 158 */         debug.println("Trying " + str + (arrayOfAppConfigurationEntry == null ? ": does not exist." : ": Found!"));
/*     */       }
/*     */       
/* 161 */       if (arrayOfAppConfigurationEntry != null) {
/*     */         break;
/*     */       }
/*     */     }
/*     */     
/* 166 */     if (arrayOfAppConfigurationEntry == null) {
/* 167 */       if (debug != null) {
/* 168 */         debug.println("Cannot read JGSS entry, use default values instead.");
/*     */       }
/* 170 */       arrayOfAppConfigurationEntry = getDefaultConfigurationEntry();
/*     */     }
/* 172 */     return arrayOfAppConfigurationEntry;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private AppConfigurationEntry[] getDefaultConfigurationEntry()
/*     */   {
/* 180 */     HashMap localHashMap = new HashMap(2);
/*     */     
/* 182 */     if ((this.mechName == null) || (this.mechName.equals("krb5"))) {
/* 183 */       if (isServerSide(this.caller))
/*     */       {
/*     */ 
/* 186 */         localHashMap.put("useKeyTab", "true");
/* 187 */         localHashMap.put("storeKey", "true");
/* 188 */         localHashMap.put("doNotPrompt", "true");
/* 189 */         localHashMap.put("principal", "*");
/* 190 */         localHashMap.put("isInitiator", "false");
/*     */       } else {
/* 192 */         if (((this.caller instanceof HttpCaller)) && (!HTTP_USE_GLOBAL_CREDS)) {
/* 193 */           localHashMap.put("useTicketCache", "false");
/*     */         } else {
/* 195 */           localHashMap.put("useTicketCache", "true");
/*     */         }
/* 197 */         localHashMap.put("doNotPrompt", "false");
/*     */       }
/* 199 */       return new AppConfigurationEntry[] { new AppConfigurationEntry("com.sun.security.auth.module.Krb5LoginModule", LoginModuleControlFlag.REQUIRED, localHashMap) };
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 206 */     return null;
/*     */   }
/*     */   
/*     */   private static boolean isServerSide(GSSCaller paramGSSCaller) {
/* 210 */     return (GSSCaller.CALLER_ACCEPT == paramGSSCaller) || (GSSCaller.CALLER_SSL_SERVER == paramGSSCaller);
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\jgss\LoginConfigImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */