/*     */ package sun.security.krb5.internal.tools;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.util.Arrays;
/*     */ import javax.security.auth.kerberos.KeyTab;
/*     */ import sun.security.krb5.Config;
/*     */ import sun.security.krb5.KrbAsReqBuilder;
/*     */ import sun.security.krb5.KrbException;
/*     */ import sun.security.krb5.PrincipalName;
/*     */ import sun.security.krb5.RealmException;
/*     */ import sun.security.krb5.internal.HostAddresses;
/*     */ import sun.security.krb5.internal.KDCOptions;
/*     */ import sun.security.krb5.internal.Krb5;
/*     */ import sun.security.krb5.internal.ccache.Credentials;
/*     */ import sun.security.krb5.internal.ccache.CredentialsCache;
/*     */ import sun.security.util.Password;
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
/*     */ public class Kinit
/*     */ {
/*     */   private KinitOptions options;
/*  52 */   private static final boolean DEBUG = Krb5.DEBUG;
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
/*     */   public static void main(String[] paramArrayOfString)
/*     */   {
/*     */     try
/*     */     {
/* 113 */       Kinit localKinit = new Kinit(paramArrayOfString);
/*     */     }
/*     */     catch (Exception localException) {
/* 116 */       String str = null;
/* 117 */       if ((localException instanceof KrbException))
/*     */       {
/* 119 */         str = ((KrbException)localException).krbErrorMessage() + " " + ((KrbException)localException).returnCodeMessage();
/*     */       } else {
/* 121 */         str = localException.getMessage();
/*     */       }
/* 123 */       if (str != null) {
/* 124 */         System.err.println("Exception: " + str);
/*     */       } else {
/* 126 */         System.out.println("Exception: " + localException);
/*     */       }
/* 128 */       localException.printStackTrace();
/* 129 */       System.exit(-1);
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
/*     */   private Kinit(String[] paramArrayOfString)
/*     */     throws IOException, RealmException, KrbException
/*     */   {
/* 144 */     if ((paramArrayOfString == null) || (paramArrayOfString.length == 0)) {
/* 145 */       this.options = new KinitOptions();
/*     */     } else {
/* 147 */       this.options = new KinitOptions(paramArrayOfString);
/*     */     }
/* 149 */     String str1 = null;
/* 150 */     PrincipalName localPrincipalName1 = this.options.getPrincipal();
/* 151 */     if (localPrincipalName1 != null) {
/* 152 */       str1 = localPrincipalName1.toString();
/*     */     }
/*     */     
/* 155 */     if (DEBUG) {
/* 156 */       System.out.println("Principal is " + localPrincipalName1);
/*     */     }
/* 158 */     char[] arrayOfChar = this.options.password;
/* 159 */     boolean bool = this.options.useKeytabFile();
/* 160 */     KrbAsReqBuilder localKrbAsReqBuilder; if (!bool) {
/* 161 */       if (str1 == null) {
/* 162 */         throw new IllegalArgumentException(" Can not obtain principal name");
/*     */       }
/*     */       
/* 165 */       if (arrayOfChar == null) {
/* 166 */         System.out.print("Password for " + str1 + ":");
/* 167 */         System.out.flush();
/* 168 */         arrayOfChar = Password.readPassword(System.in);
/* 169 */         if (DEBUG) {
/* 170 */           System.out.println(">>> Kinit console input " + new String(arrayOfChar));
/*     */         }
/*     */       }
/*     */       
/* 174 */       localKrbAsReqBuilder = new KrbAsReqBuilder(localPrincipalName1, arrayOfChar);
/*     */     } else {
/* 176 */       if (DEBUG) {
/* 177 */         System.out.println(">>> Kinit using keytab");
/*     */       }
/* 179 */       if (str1 == null) {
/* 180 */         throw new IllegalArgumentException("Principal name must be specified.");
/*     */       }
/*     */       
/* 183 */       localObject = this.options.keytabFileName();
/* 184 */       if ((localObject != null) && 
/* 185 */         (DEBUG)) {
/* 186 */         System.out.println(">>> Kinit keytab file name: " + (String)localObject);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 193 */       localKrbAsReqBuilder = new KrbAsReqBuilder(localPrincipalName1, localObject == null ? KeyTab.getInstance() : KeyTab.getInstance(new File((String)localObject)));
/*     */     }
/*     */     
/* 196 */     Object localObject = new KDCOptions();
/* 197 */     setOptions(1, this.options.forwardable, (KDCOptions)localObject);
/* 198 */     setOptions(3, this.options.proxiable, (KDCOptions)localObject);
/* 199 */     localKrbAsReqBuilder.setOptions((KDCOptions)localObject);
/* 200 */     String str2 = this.options.getKDCRealm();
/* 201 */     if (str2 == null) {
/* 202 */       str2 = Config.getInstance().getDefaultRealm();
/*     */     }
/*     */     
/* 205 */     if (DEBUG) {
/* 206 */       System.out.println(">>> Kinit realm name is " + str2);
/*     */     }
/*     */     
/* 209 */     PrincipalName localPrincipalName2 = PrincipalName.tgsService(str2, str2);
/* 210 */     localKrbAsReqBuilder.setTarget(localPrincipalName2);
/*     */     
/* 212 */     if (DEBUG) {
/* 213 */       System.out.println(">>> Creating KrbAsReq");
/*     */     }
/*     */     
/* 216 */     if (this.options.getAddressOption()) {
/* 217 */       localKrbAsReqBuilder.setAddresses(HostAddresses.getLocalAddresses());
/*     */     }
/* 219 */     localKrbAsReqBuilder.action();
/*     */     
/*     */ 
/* 222 */     Credentials localCredentials = localKrbAsReqBuilder.getCCreds();
/* 223 */     localKrbAsReqBuilder.destroy();
/*     */     
/*     */ 
/*     */ 
/* 227 */     CredentialsCache localCredentialsCache = CredentialsCache.create(localPrincipalName1, this.options.cachename);
/* 228 */     if (localCredentialsCache == null) {
/* 229 */       throw new IOException("Unable to create the cache file " + this.options.cachename);
/*     */     }
/*     */     
/* 232 */     localCredentialsCache.update(localCredentials);
/* 233 */     localCredentialsCache.save();
/*     */     
/* 235 */     if (this.options.password == null)
/*     */     {
/* 237 */       System.out.println("New ticket is stored in cache file " + this.options.cachename);
/*     */     }
/*     */     else {
/* 240 */       Arrays.fill(this.options.password, '0');
/*     */     }
/*     */     
/*     */ 
/* 244 */     if (arrayOfChar != null) {
/* 245 */       Arrays.fill(arrayOfChar, '0');
/*     */     }
/* 247 */     this.options = null;
/*     */   }
/*     */   
/*     */   private static void setOptions(int paramInt1, int paramInt2, KDCOptions paramKDCOptions) {
/* 251 */     switch (paramInt2) {
/*     */     case 0: 
/*     */       break;
/*     */     case -1: 
/* 255 */       paramKDCOptions.set(paramInt1, false);
/* 256 */       break;
/*     */     case 1: 
/* 258 */       paramKDCOptions.set(paramInt1, true);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\krb5\internal\tools\Kinit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */