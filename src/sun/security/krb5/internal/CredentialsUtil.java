/*     */ package sun.security.krb5.internal;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import sun.security.krb5.Credentials;
/*     */ import sun.security.krb5.KrbException;
/*     */ import sun.security.krb5.KrbTgsReq;
/*     */ import sun.security.krb5.PrincipalName;
/*     */ import sun.security.krb5.Realm;
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
/*     */ public class CredentialsUtil
/*     */ {
/*  44 */   private static boolean DEBUG = Krb5.DEBUG;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Credentials acquireS4U2selfCreds(PrincipalName paramPrincipalName, Credentials paramCredentials)
/*     */     throws KrbException, IOException
/*     */   {
/*  55 */     String str1 = paramPrincipalName.getRealmString();
/*  56 */     String str2 = paramCredentials.getClient().getRealmString();
/*  57 */     if (!str1.equals(str2))
/*     */     {
/*  59 */       throw new KrbException("Cross realm impersonation not supported");
/*     */     }
/*  61 */     if (!paramCredentials.isForwardable()) {
/*  62 */       throw new KrbException("S4U2self needs a FORWARDABLE ticket");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  69 */     KrbTgsReq localKrbTgsReq = new KrbTgsReq(paramCredentials, paramCredentials.getClient(), new PAData(129, new PAForUserEnc(paramPrincipalName, paramCredentials.getSessionKey()).asn1Encode()));
/*  70 */     Credentials localCredentials = localKrbTgsReq.sendAndGetCreds();
/*  71 */     if (!localCredentials.getClient().equals(paramPrincipalName)) {
/*  72 */       throw new KrbException("S4U2self request not honored by KDC");
/*     */     }
/*  74 */     if (!localCredentials.isForwardable()) {
/*  75 */       throw new KrbException("S4U2self ticket must be FORWARDABLE");
/*     */     }
/*  77 */     return localCredentials;
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
/*     */   public static Credentials acquireS4U2proxyCreds(String paramString, Ticket paramTicket, PrincipalName paramPrincipalName, Credentials paramCredentials)
/*     */     throws KrbException, IOException
/*     */   {
/*  92 */     KrbTgsReq localKrbTgsReq = new KrbTgsReq(paramCredentials, paramTicket, new PrincipalName(paramString));
/*     */     
/*     */ 
/*     */ 
/*  96 */     Credentials localCredentials = localKrbTgsReq.sendAndGetCreds();
/*  97 */     if (!localCredentials.getClient().equals(paramPrincipalName)) {
/*  98 */       throw new KrbException("S4U2proxy request not honored by KDC");
/*     */     }
/* 100 */     return localCredentials;
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
/*     */   public static Credentials acquireServiceCreds(String paramString, Credentials paramCredentials)
/*     */     throws KrbException, IOException
/*     */   {
/* 117 */     PrincipalName localPrincipalName = new PrincipalName(paramString);
/* 118 */     String str1 = localPrincipalName.getRealmString();
/* 119 */     String str2 = paramCredentials.getClient().getRealmString();
/*     */     
/* 121 */     if (str2.equals(str1)) {
/* 122 */       if (DEBUG) {
/* 123 */         System.out.println(">>> Credentials acquireServiceCreds: same realm");
/*     */       }
/*     */       
/* 126 */       return serviceCreds(localPrincipalName, paramCredentials);
/*     */     }
/* 128 */     Credentials localCredentials1 = null;
/*     */     
/* 130 */     boolean[] arrayOfBoolean = new boolean[1];
/* 131 */     Credentials localCredentials2 = getTGTforRealm(str2, str1, paramCredentials, arrayOfBoolean);
/*     */     
/* 133 */     if (localCredentials2 != null) {
/* 134 */       if (DEBUG) {
/* 135 */         System.out.println(">>> Credentials acquireServiceCreds: got right tgt");
/*     */         
/* 137 */         System.out.println(">>> Credentials acquireServiceCreds: obtaining service creds for " + localPrincipalName);
/*     */       }
/*     */       
/*     */       try
/*     */       {
/* 142 */         localCredentials1 = serviceCreds(localPrincipalName, localCredentials2);
/*     */       } catch (Exception localException) {
/* 144 */         if (DEBUG) {
/* 145 */           System.out.println(localException);
/*     */         }
/* 147 */         localCredentials1 = null;
/*     */       }
/*     */     }
/*     */     
/* 151 */     if (localCredentials1 != null) {
/* 152 */       if (DEBUG) {
/* 153 */         System.out.println(">>> Credentials acquireServiceCreds: returning creds:");
/*     */         
/* 155 */         Credentials.printDebug(localCredentials1);
/*     */       }
/* 157 */       if (arrayOfBoolean[0] == 0) {
/* 158 */         localCredentials1.resetDelegate();
/*     */       }
/* 160 */       return localCredentials1;
/*     */     }
/* 162 */     throw new KrbApErrException(63, "No service creds");
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
/*     */   private static Credentials getTGTforRealm(String paramString1, String paramString2, Credentials paramCredentials, boolean[] paramArrayOfBoolean)
/*     */     throws KrbException
/*     */   {
/* 181 */     String[] arrayOfString = Realm.getRealmsList(paramString1, paramString2);
/*     */     
/* 183 */     int i = 0;int j = 0;
/* 184 */     Object localObject = null;Credentials localCredentials1 = null;Credentials localCredentials2 = null;
/* 185 */     PrincipalName localPrincipalName = null;
/* 186 */     String str = null;
/*     */     
/* 188 */     paramArrayOfBoolean[0] = true;
/* 189 */     localObject = paramCredentials; for (i = 0; i < arrayOfString.length;) {
/* 190 */       localPrincipalName = PrincipalName.tgsService(paramString2, arrayOfString[i]);
/*     */       
/* 192 */       if (DEBUG) {
/* 193 */         System.out.println(">>> Credentials acquireServiceCreds: main loop: [" + i + "] tempService=" + localPrincipalName);
/*     */       }
/*     */       
/*     */ 
/*     */       try
/*     */       {
/* 199 */         localCredentials1 = serviceCreds(localPrincipalName, (Credentials)localObject);
/*     */       } catch (Exception localException1) {
/* 201 */         localCredentials1 = null;
/*     */       }
/*     */       
/* 204 */       if (localCredentials1 == null) {
/* 205 */         if (DEBUG) {
/* 206 */           System.out.println(">>> Credentials acquireServiceCreds: no tgt; searching thru capath");
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 213 */         localCredentials1 = null; for (j = i + 1; 
/* 214 */             (localCredentials1 == null) && (j < arrayOfString.length); j++) {
/* 215 */           localPrincipalName = PrincipalName.tgsService(arrayOfString[j], arrayOfString[i]);
/* 216 */           if (DEBUG) {
/* 217 */             System.out.println(">>> Credentials acquireServiceCreds: inner loop: [" + j + "] tempService=" + localPrincipalName);
/*     */           }
/*     */           
/*     */ 
/*     */           try
/*     */           {
/* 223 */             localCredentials1 = serviceCreds(localPrincipalName, (Credentials)localObject);
/*     */           } catch (Exception localException2) {
/* 225 */             localCredentials1 = null;
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 230 */       if (localCredentials1 == null) {
/* 231 */         if (!DEBUG) break;
/* 232 */         System.out.println(">>> Credentials acquireServiceCreds: no tgt; cannot get creds"); break;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 242 */       str = localCredentials1.getServer().getInstanceComponent();
/* 243 */       if ((paramArrayOfBoolean[0] != 0) && (!localCredentials1.checkDelegate())) {
/* 244 */         if (DEBUG) {
/* 245 */           System.out.println(">>> Credentials acquireServiceCreds: global OK-AS-DELEGATE turned off at " + localCredentials1
/*     */           
/* 247 */             .getServer());
/*     */         }
/* 249 */         paramArrayOfBoolean[0] = false;
/*     */       }
/*     */       
/* 252 */       if (DEBUG) {
/* 253 */         System.out.println(">>> Credentials acquireServiceCreds: got tgt");
/*     */       }
/*     */       
/*     */ 
/* 257 */       if (str.equals(paramString2))
/*     */       {
/* 259 */         localCredentials2 = localCredentials1;
/* 260 */         break;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 268 */       for (j = i + 1; j < arrayOfString.length; j++) {
/* 269 */         if (str.equals(arrayOfString[j])) {
/*     */           break;
/*     */         }
/*     */       }
/*     */       
/* 274 */       if (j >= arrayOfString.length) {
/*     */         break;
/*     */       }
/*     */       
/*     */ 
/* 279 */       i = j;
/* 280 */       localObject = localCredentials1;
/*     */       
/* 282 */       if (DEBUG) {
/* 283 */         System.out.println(">>> Credentials acquireServiceCreds: continuing with main loop counter reset to " + i);
/*     */       }
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
/* 299 */     return localCredentials2;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static Credentials serviceCreds(PrincipalName paramPrincipalName, Credentials paramCredentials)
/*     */     throws KrbException, IOException
/*     */   {
/* 308 */     return new KrbTgsReq(paramCredentials, paramPrincipalName).sendAndGetCreds();
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\krb5\internal\CredentialsUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */