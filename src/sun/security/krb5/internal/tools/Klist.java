/*     */ package sun.security.krb5.internal.tools;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.net.InetAddress;
/*     */ import java.util.Date;
/*     */ import sun.security.krb5.EncryptionKey;
/*     */ import sun.security.krb5.PrincipalName;
/*     */ import sun.security.krb5.RealmException;
/*     */ import sun.security.krb5.internal.KerberosTime;
/*     */ import sun.security.krb5.internal.Krb5;
/*     */ import sun.security.krb5.internal.TicketFlags;
/*     */ import sun.security.krb5.internal.ccache.CredentialsCache;
/*     */ import sun.security.krb5.internal.crypto.EType;
/*     */ import sun.security.krb5.internal.ktab.KeyTab;
/*     */ import sun.security.krb5.internal.ktab.KeyTabEntry;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Klist
/*     */ {
/*     */   Object target;
/*  51 */   char[] options = new char[4];
/*     */   
/*     */   String name;
/*     */   char action;
/*  55 */   private static boolean DEBUG = Krb5.DEBUG;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*  76 */     Klist localKlist = new Klist();
/*  77 */     if ((paramArrayOfString == null) || (paramArrayOfString.length == 0)) {
/*  78 */       localKlist.action = 'c';
/*     */     } else {
/*  80 */       localKlist.processArgs(paramArrayOfString);
/*     */     }
/*  82 */     switch (localKlist.action) {
/*     */     case 'c': 
/*  84 */       if (localKlist.name == null) {
/*  85 */         localKlist.target = CredentialsCache.getInstance();
/*  86 */         localKlist.name = CredentialsCache.cacheName();
/*     */       } else {
/*  88 */         localKlist.target = CredentialsCache.getInstance(localKlist.name);
/*     */       }
/*  90 */       if (localKlist.target != null) {
/*  91 */         localKlist.displayCache();
/*     */       } else {
/*  93 */         localKlist.displayMessage("Credentials cache");
/*  94 */         System.exit(-1);
/*     */       }
/*  96 */       break;
/*     */     case 'k': 
/*  98 */       KeyTab localKeyTab = KeyTab.getInstance(localKlist.name);
/*  99 */       if (localKeyTab.isMissing()) {
/* 100 */         System.out.println("KeyTab " + localKlist.name + " not found.");
/* 101 */         System.exit(-1);
/* 102 */       } else if (!localKeyTab.isValid()) {
/* 103 */         System.out.println("KeyTab " + localKlist.name + " format not supported.");
/*     */         
/* 105 */         System.exit(-1);
/*     */       }
/* 107 */       localKlist.target = localKeyTab;
/* 108 */       localKlist.name = localKeyTab.tabName();
/* 109 */       localKlist.displayTab();
/* 110 */       break;
/*     */     default: 
/* 112 */       if (localKlist.name != null) {
/* 113 */         localKlist.printHelp();
/* 114 */         System.exit(-1);
/*     */       } else {
/* 116 */         localKlist.target = CredentialsCache.getInstance();
/* 117 */         localKlist.name = CredentialsCache.cacheName();
/* 118 */         if (localKlist.target != null) {
/* 119 */           localKlist.displayCache();
/*     */         } else {
/* 121 */           localKlist.displayMessage("Credentials cache");
/* 122 */           System.exit(-1);
/*     */         }
/*     */       }
/*     */       
/*     */       break;
/*     */     }
/*     */     
/*     */   }
/*     */   
/*     */   void processArgs(String[] paramArrayOfString)
/*     */   {
/* 133 */     for (int i = 0; i < paramArrayOfString.length; i++) { Character localCharacter;
/* 134 */       if ((paramArrayOfString[i].length() >= 2) && (paramArrayOfString[i].startsWith("-")))
/* 135 */         localCharacter = new Character(paramArrayOfString[i].charAt(1));
/* 136 */       switch (localCharacter.charValue()) {
/*     */       case 'c': 
/* 138 */         this.action = 'c';
/* 139 */         break;
/*     */       case 'k': 
/* 141 */         this.action = 'k';
/* 142 */         break;
/*     */       case 'a': 
/* 144 */         this.options[2] = 'a';
/* 145 */         break;
/*     */       case 'n': 
/* 147 */         this.options[3] = 'n';
/* 148 */         break;
/*     */       case 'f': 
/* 150 */         this.options[1] = 'f';
/* 151 */         break;
/*     */       case 'e': 
/* 153 */         this.options[0] = 'e';
/* 154 */         break;
/*     */       case 'K': 
/* 156 */         this.options[1] = 'K';
/* 157 */         break;
/*     */       case 't': 
/* 159 */         this.options[2] = 't';
/* 160 */         break;
/*     */       default: 
/* 162 */         printHelp();
/* 163 */         System.exit(-1); continue;
/*     */         
/*     */ 
/*     */ 
/* 167 */         if ((!paramArrayOfString[i].startsWith("-")) && (i == paramArrayOfString.length - 1))
/*     */         {
/* 169 */           this.name = paramArrayOfString[i];
/* 170 */           localCharacter = null;
/*     */         } else {
/* 172 */           printHelp();
/* 173 */           System.exit(-1);
/*     */         }
/*     */         break; }
/*     */     }
/*     */   }
/*     */   
/*     */   void displayTab() {
/* 180 */     KeyTab localKeyTab = (KeyTab)this.target;
/* 181 */     KeyTabEntry[] arrayOfKeyTabEntry = localKeyTab.getEntries();
/* 182 */     if (arrayOfKeyTabEntry.length == 0) {
/* 183 */       System.out.println("\nKey tab: " + this.name + ",  0 entries found.\n");
/*     */     }
/*     */     else {
/* 186 */       if (arrayOfKeyTabEntry.length == 1) {
/* 187 */         System.out.println("\nKey tab: " + this.name + ", " + arrayOfKeyTabEntry.length + " entry found.\n");
/*     */       }
/*     */       else {
/* 190 */         System.out.println("\nKey tab: " + this.name + ", " + arrayOfKeyTabEntry.length + " entries found.\n");
/*     */       }
/* 192 */       for (int i = 0; i < arrayOfKeyTabEntry.length; i++) {
/* 193 */         System.out.println("[" + (i + 1) + "] Service principal: " + arrayOfKeyTabEntry[i]
/*     */         
/* 195 */           .getService().toString());
/* 196 */         System.out.println("\t KVNO: " + arrayOfKeyTabEntry[i]
/* 197 */           .getKey().getKeyVersionNumber());
/* 198 */         EncryptionKey localEncryptionKey; if (this.options[0] == 'e') {
/* 199 */           localEncryptionKey = arrayOfKeyTabEntry[i].getKey();
/* 200 */           System.out.println("\t Key type: " + localEncryptionKey
/* 201 */             .getEType());
/*     */         }
/* 203 */         if (this.options[1] == 'K') {
/* 204 */           localEncryptionKey = arrayOfKeyTabEntry[i].getKey();
/* 205 */           System.out.println("\t Key: " + arrayOfKeyTabEntry[i]
/* 206 */             .getKeyString());
/*     */         }
/* 208 */         if (this.options[2] == 't') {
/* 209 */           System.out.println("\t Time stamp: " + 
/* 210 */             format(arrayOfKeyTabEntry[i].getTimeStamp()));
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   void displayCache() {
/* 217 */     CredentialsCache localCredentialsCache = (CredentialsCache)this.target;
/*     */     
/* 219 */     sun.security.krb5.internal.ccache.Credentials[] arrayOfCredentials = localCredentialsCache.getCredsList();
/* 220 */     if (arrayOfCredentials == null) {
/* 221 */       System.out.println("No credentials available in the cache " + this.name);
/*     */       
/* 223 */       System.exit(-1);
/*     */     }
/* 225 */     System.out.println("\nCredentials cache: " + this.name);
/* 226 */     String str1 = localCredentialsCache.getPrimaryPrincipal().toString();
/* 227 */     int i = arrayOfCredentials.length;
/*     */     
/* 229 */     if (i == 1) {
/* 230 */       System.out.println("\nDefault principal: " + str1 + ", " + arrayOfCredentials.length + " entry found.\n");
/*     */     }
/*     */     else
/*     */     {
/* 234 */       System.out.println("\nDefault principal: " + str1 + ", " + arrayOfCredentials.length + " entries found.\n");
/*     */     }
/*     */     
/* 237 */     if (arrayOfCredentials != null) {
/* 238 */       for (int j = 0; j < arrayOfCredentials.length; j++) {
/*     */         try
/*     */         {
/*     */           String str2;
/*     */           
/*     */ 
/* 244 */           if (arrayOfCredentials[j].getStartTime() != null) {
/* 245 */             str2 = format(arrayOfCredentials[j].getStartTime());
/*     */           } else {
/* 247 */             str2 = format(arrayOfCredentials[j].getAuthTime());
/*     */           }
/* 249 */           String str3 = format(arrayOfCredentials[j].getEndTime());
/*     */           
/* 251 */           String str5 = arrayOfCredentials[j].getServicePrincipal().toString();
/* 252 */           System.out.println("[" + (j + 1) + "]  Service Principal:  " + str5);
/*     */           
/*     */ 
/* 255 */           System.out.println("     Valid starting:     " + str2);
/* 256 */           System.out.println("     Expires:            " + str3);
/* 257 */           if (arrayOfCredentials[j].getRenewTill() != null) {
/* 258 */             String str4 = format(arrayOfCredentials[j].getRenewTill());
/* 259 */             System.out.println("     Renew until:        " + str4);
/*     */           }
/*     */           Object localObject1;
/* 262 */           if (this.options[0] == 'e') {
/* 263 */             String str6 = EType.toString(arrayOfCredentials[j].getEType());
/* 264 */             localObject1 = EType.toString(arrayOfCredentials[j].getTktEType());
/* 265 */             System.out.println("     EType (skey, tkt):  " + str6 + ", " + (String)localObject1);
/*     */           }
/*     */           
/* 268 */           if (this.options[1] == 'f') {
/* 269 */             System.out.println("     Flags:              " + arrayOfCredentials[j]
/* 270 */               .getTicketFlags().toString());
/*     */           }
/* 272 */           if (this.options[2] == 'a') {
/* 273 */             int k = 1;
/*     */             
/* 275 */             localObject1 = arrayOfCredentials[j].setKrbCreds().getClientAddresses();
/* 276 */             if (localObject1 != null) {
/* 277 */               for (Object localObject3 : localObject1) {
/*     */                 String str7;
/* 279 */                 if (this.options[3] == 'n') {
/* 280 */                   str7 = ((InetAddress)localObject3).getHostAddress();
/*     */                 } else {
/* 282 */                   str7 = ((InetAddress)localObject3).getCanonicalHostName();
/*     */                 }
/* 284 */                 System.out.println("     " + (k != 0 ? "Addresses:" : "          ") + "       " + str7);
/*     */                 
/*     */ 
/* 287 */                 k = 0;
/*     */               }
/*     */             } else {
/* 290 */               System.out.println("     [No host addresses info]");
/*     */             }
/*     */           }
/*     */         } catch (RealmException localRealmException) {
/* 294 */           System.out.println("Error reading principal from the entry.");
/*     */           
/* 296 */           if (DEBUG) {
/* 297 */             localRealmException.printStackTrace();
/*     */           }
/* 299 */           System.exit(-1);
/*     */         }
/*     */       }
/*     */     } else {
/* 303 */       System.out.println("\nNo entries found.");
/*     */     }
/*     */   }
/*     */   
/*     */   void displayMessage(String paramString) {
/* 308 */     if (this.name == null) {
/* 309 */       System.out.println("Default " + paramString + " not found.");
/*     */     } else {
/* 311 */       System.out.println(paramString + " " + this.name + " not found.");
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
/*     */   private String format(KerberosTime paramKerberosTime)
/*     */   {
/* 325 */     String str = paramKerberosTime.toDate().toString();
/* 326 */     return str.substring(4, 7) + " " + str.substring(8, 10) + ", " + str
/* 327 */       .substring(24) + " " + str
/* 328 */       .substring(11, 19);
/*     */   }
/*     */   
/*     */ 
/*     */   void printHelp()
/*     */   {
/* 334 */     System.out.println("\nUsage: klist [[-c] [-f] [-e] [-a [-n]]] [-k [-t] [-K]] [name]");
/*     */     
/* 336 */     System.out.println("   name\t name of credentials cache or  keytab with the prefix. File-based cache or keytab's prefix is FILE:.");
/*     */     
/*     */ 
/* 339 */     System.out.println("   -c specifies that credential cache is to be listed");
/*     */     
/* 341 */     System.out.println("   -k specifies that key tab is to be listed");
/* 342 */     System.out.println("   options for credentials caches:");
/* 343 */     System.out.println("\t-f \t shows credentials flags");
/* 344 */     System.out.println("\t-e \t shows the encryption type");
/* 345 */     System.out.println("\t-a \t shows addresses");
/* 346 */     System.out.println("\t  -n \t   do not reverse-resolve addresses");
/* 347 */     System.out.println("   options for keytabs:");
/* 348 */     System.out.println("\t-t \t shows keytab entry timestamps");
/* 349 */     System.out.println("\t-K \t shows keytab entry key value");
/* 350 */     System.out.println("\t-e \t shows keytab entry key type");
/* 351 */     System.out.println("\nUsage: java sun.security.krb5.tools.Klist -help for help.");
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\krb5\internal\tools\Klist.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */