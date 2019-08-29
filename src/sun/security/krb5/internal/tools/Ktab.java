/*     */ package sun.security.krb5.internal.tools;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.PrintStream;
/*     */ import java.text.DateFormat;
/*     */ import java.util.Arrays;
/*     */ import java.util.Date;
/*     */ import java.util.Locale;
/*     */ import sun.security.krb5.EncryptionKey;
/*     */ import sun.security.krb5.KrbException;
/*     */ import sun.security.krb5.PrincipalName;
/*     */ import sun.security.krb5.internal.KerberosTime;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Ktab
/*     */ {
/*     */   KeyTab table;
/*     */   char action;
/*     */   String name;
/*     */   String principal;
/*     */   boolean showEType;
/*     */   boolean showTime;
/*  61 */   int etype = -1;
/*  62 */   char[] password = null;
/*     */   
/*  64 */   boolean forced = false;
/*  65 */   boolean append = false;
/*  66 */   int vDel = -1;
/*  67 */   int vAdd = -1;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void main(String[] paramArrayOfString)
/*     */   {
/*  74 */     Ktab localKtab = new Ktab();
/*  75 */     if ((paramArrayOfString.length == 1) && (paramArrayOfString[0].equalsIgnoreCase("-help"))) {
/*  76 */       localKtab.printHelp();
/*  77 */       return; }
/*  78 */     if ((paramArrayOfString == null) || (paramArrayOfString.length == 0)) {
/*  79 */       localKtab.action = 'l';
/*     */     } else {
/*  81 */       localKtab.processArgs(paramArrayOfString);
/*     */     }
/*  83 */     localKtab.table = KeyTab.getInstance(localKtab.name);
/*  84 */     if ((localKtab.table.isMissing()) && (localKtab.action != 'a')) {
/*  85 */       if (localKtab.name == null) {
/*  86 */         System.out.println("No default key table exists.");
/*     */       } else {
/*  88 */         System.out.println("Key table " + localKtab.name + " does not exist.");
/*     */       }
/*     */       
/*  91 */       System.exit(-1);
/*     */     }
/*  93 */     if (!localKtab.table.isValid()) {
/*  94 */       if (localKtab.name == null) {
/*  95 */         System.out.println("The format of the default key table  is incorrect.");
/*     */       }
/*     */       else {
/*  98 */         System.out.println("The format of key table " + localKtab.name + " is incorrect.");
/*     */       }
/*     */       
/* 101 */       System.exit(-1);
/*     */     }
/* 103 */     switch (localKtab.action) {
/*     */     case 'l': 
/* 105 */       localKtab.listKt();
/* 106 */       break;
/*     */     case 'a': 
/* 108 */       localKtab.addEntry();
/* 109 */       break;
/*     */     case 'd': 
/* 111 */       localKtab.deleteEntry();
/* 112 */       break;
/*     */     default: 
/* 114 */       localKtab.error(new String[] { "A command must be provided" });
/*     */     }
/*     */     
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
/*     */   void processArgs(String[] paramArrayOfString)
/*     */   {
/* 139 */     int i = 0;
/* 140 */     for (int j = 0; j < paramArrayOfString.length; j++) {
/* 141 */       if (paramArrayOfString[j].startsWith("-")) {
/* 142 */         switch (paramArrayOfString[j].toLowerCase(Locale.US))
/*     */         {
/*     */ 
/*     */         case "-l": 
/* 146 */           this.action = 'l';
/* 147 */           break;
/*     */         case "-a": 
/* 149 */           this.action = 'a';
/* 150 */           j++; if ((j >= paramArrayOfString.length) || (paramArrayOfString[j].startsWith("-"))) {
/* 151 */             error(new String[] { "A principal name must be specified after -a" });
/*     */           }
/* 153 */           this.principal = paramArrayOfString[j];
/* 154 */           break;
/*     */         case "-d": 
/* 156 */           this.action = 'd';
/* 157 */           j++; if ((j >= paramArrayOfString.length) || (paramArrayOfString[j].startsWith("-"))) {
/* 158 */             error(new String[] { "A principal name must be specified after -d" });
/*     */           }
/* 160 */           this.principal = paramArrayOfString[j];
/* 161 */           break;
/*     */         
/*     */ 
/*     */         case "-e": 
/* 165 */           if (this.action == 'l') {
/* 166 */             this.showEType = true;
/* 167 */           } else if (this.action == 'd') {
/* 168 */             j++; if ((j >= paramArrayOfString.length) || (paramArrayOfString[j].startsWith("-"))) {
/* 169 */               error(new String[] { "An etype must be specified after -e" });
/*     */             }
/*     */             try {
/* 172 */               this.etype = Integer.parseInt(paramArrayOfString[j]);
/* 173 */               if (this.etype <= 0) {
/* 174 */                 throw new NumberFormatException();
/*     */               }
/*     */             } catch (NumberFormatException localNumberFormatException1) {
/* 177 */               error(new String[] { paramArrayOfString[j] + " is not a valid etype" });
/*     */             }
/*     */           } else {
/* 180 */             error(new String[] { paramArrayOfString[j] + " is not valid after -" + this.action });
/*     */           }
/* 182 */           break;
/*     */         case "-n": 
/* 184 */           j++; if ((j >= paramArrayOfString.length) || (paramArrayOfString[j].startsWith("-"))) {
/* 185 */             error(new String[] { "A KVNO must be specified after -n" });
/*     */           }
/*     */           try {
/* 188 */             this.vAdd = Integer.parseInt(paramArrayOfString[j]);
/* 189 */             if (this.vAdd < 0) {
/* 190 */               throw new NumberFormatException();
/*     */             }
/*     */           } catch (NumberFormatException localNumberFormatException2) {
/* 193 */             error(new String[] { paramArrayOfString[j] + " is not a valid KVNO" });
/*     */           }
/*     */         
/*     */         case "-k": 
/* 197 */           j++; if ((j >= paramArrayOfString.length) || (paramArrayOfString[j].startsWith("-"))) {
/* 198 */             error(new String[] { "A keytab name must be specified after -k" });
/*     */           }
/* 200 */           if ((paramArrayOfString[j].length() >= 5) && 
/* 201 */             (paramArrayOfString[j].substring(0, 5).equalsIgnoreCase("FILE:"))) {
/* 202 */             this.name = paramArrayOfString[j].substring(5);
/*     */           } else {
/* 204 */             this.name = paramArrayOfString[j];
/*     */           }
/* 206 */           break;
/*     */         case "-t": 
/* 208 */           this.showTime = true;
/* 209 */           break;
/*     */         case "-f": 
/* 211 */           this.forced = true;
/* 212 */           break;
/*     */         case "-append": 
/* 214 */           this.append = true;
/* 215 */           break;
/*     */         default: 
/* 217 */           error(new String[] { "Unknown command: " + paramArrayOfString[j] });
/*     */         }
/*     */         
/*     */       } else {
/* 221 */         if (i != 0) {
/* 222 */           error(new String[] { "Useless extra argument " + paramArrayOfString[j] });
/*     */         }
/* 224 */         if (this.action == 'a') {
/* 225 */           this.password = paramArrayOfString[j].toCharArray();
/* 226 */         } else if (this.action == 'd') {
/* 227 */           switch (paramArrayOfString[j]) {
/* 228 */           case "all":  this.vDel = -1; break;
/* 229 */           case "old":  this.vDel = -2; break;
/*     */           default: 
/*     */             try {
/* 232 */               this.vDel = Integer.parseInt(paramArrayOfString[j]);
/* 233 */               if (this.vDel < 0) {
/* 234 */                 throw new NumberFormatException();
/*     */               }
/*     */             } catch (NumberFormatException localNumberFormatException3) {
/* 237 */               error(new String[] { paramArrayOfString[j] + " is not a valid KVNO" });
/*     */             }
/*     */           }
/*     */           
/*     */         } else {
/* 242 */           error(new String[] { "Useless extra argument " + paramArrayOfString[j] });
/*     */         }
/* 244 */         i = 1;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   void addEntry()
/*     */   {
/* 255 */     PrincipalName localPrincipalName = null;
/*     */     try {
/* 257 */       localPrincipalName = new PrincipalName(this.principal);
/*     */     } catch (KrbException localKrbException1) {
/* 259 */       System.err.println("Failed to add " + this.principal + " to keytab.");
/*     */       
/* 261 */       localKrbException1.printStackTrace();
/* 262 */       System.exit(-1);
/*     */     }
/* 264 */     if (this.password == null) {
/*     */       try {
/* 266 */         BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(System.in));
/*     */         
/* 268 */         System.out.print("Password for " + localPrincipalName.toString() + ":");
/* 269 */         System.out.flush();
/* 270 */         this.password = localBufferedReader.readLine().toCharArray();
/*     */       } catch (IOException localIOException1) {
/* 272 */         System.err.println("Failed to read the password.");
/* 273 */         localIOException1.printStackTrace();
/* 274 */         System.exit(-1);
/*     */       }
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 280 */       this.table.addEntry(localPrincipalName, this.password, this.vAdd, this.append);
/* 281 */       Arrays.fill(this.password, '0');
/*     */       
/* 283 */       this.table.save();
/* 284 */       System.out.println("Done!");
/* 285 */       System.out.println("Service key for " + this.principal + " is saved in " + this.table
/* 286 */         .tabName());
/*     */     }
/*     */     catch (KrbException localKrbException2) {
/* 289 */       System.err.println("Failed to add " + this.principal + " to keytab.");
/* 290 */       localKrbException2.printStackTrace();
/* 291 */       System.exit(-1);
/*     */     } catch (IOException localIOException2) {
/* 293 */       System.err.println("Failed to save new entry.");
/* 294 */       localIOException2.printStackTrace();
/* 295 */       System.exit(-1);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   void listKt()
/*     */   {
/* 303 */     System.out.println("Keytab name: " + this.table.tabName());
/* 304 */     KeyTabEntry[] arrayOfKeyTabEntry = this.table.getEntries();
/* 305 */     if ((arrayOfKeyTabEntry != null) && (arrayOfKeyTabEntry.length > 0)) {
/* 306 */       String[][] arrayOfString = new String[arrayOfKeyTabEntry.length + 1][this.showTime ? 3 : 2];
/* 307 */       int i = 0;
/* 308 */       arrayOfString[0][(i++)] = "KVNO";
/* 309 */       if (this.showTime) arrayOfString[0][(i++)] = "Timestamp";
/* 310 */       arrayOfString[0][(i++)] = "Principal";
/* 311 */       int m; for (int j = 0; j < arrayOfKeyTabEntry.length; j++) {
/* 312 */         i = 0;
/* 313 */         arrayOfString[(j + 1)][(i++)] = arrayOfKeyTabEntry[j].getKey()
/* 314 */           .getKeyVersionNumber().toString();
/* 315 */         if (this.showTime)
/*     */         {
/* 317 */           arrayOfString[(j + 1)][(i++)] = DateFormat.getDateTimeInstance(3, 3).format(new Date(arrayOfKeyTabEntry[j]
/* 318 */             .getTimeStamp().getTime())); }
/* 319 */         String str = arrayOfKeyTabEntry[j].getService().toString();
/* 320 */         if (this.showEType) {
/* 321 */           m = arrayOfKeyTabEntry[j].getKey().getEType();
/* 322 */           arrayOfString[(j + 1)][(i++)] = 
/* 323 */             (str + " (" + m + ":" + EType.toString(m) + ")");
/*     */         } else {
/* 325 */           arrayOfString[(j + 1)][(i++)] = str;
/*     */         }
/*     */       }
/* 328 */       int[] arrayOfInt = new int[i];
/* 329 */       for (int k = 0; k < i; k++) {
/* 330 */         for (m = 0; m <= arrayOfKeyTabEntry.length; m++) {
/* 331 */           if (arrayOfString[m][k].length() > arrayOfInt[k]) {
/* 332 */             arrayOfInt[k] = arrayOfString[m][k].length();
/*     */           }
/*     */         }
/* 335 */         if (k != 0) arrayOfInt[k] = (-arrayOfInt[k]);
/*     */       }
/* 337 */       for (k = 0; k < i; k++) {
/* 338 */         System.out.printf("%" + arrayOfInt[k] + "s ", new Object[] { arrayOfString[0][k] });
/*     */       }
/* 340 */       System.out.println();
/* 341 */       for (k = 0; k < i; k++) {
/* 342 */         for (m = 0; m < Math.abs(arrayOfInt[k]); m++) System.out.print("-");
/* 343 */         System.out.print(" ");
/*     */       }
/* 345 */       System.out.println();
/* 346 */       for (k = 0; k < arrayOfKeyTabEntry.length; k++) {
/* 347 */         for (m = 0; m < i; m++) {
/* 348 */           System.out.printf("%" + arrayOfInt[m] + "s ", new Object[] { arrayOfString[(k + 1)][m] });
/*     */         }
/* 350 */         System.out.println();
/*     */       }
/*     */     } else {
/* 353 */       System.out.println("0 entry.");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   void deleteEntry()
/*     */   {
/* 361 */     PrincipalName localPrincipalName = null;
/*     */     try {
/* 363 */       localPrincipalName = new PrincipalName(this.principal);
/* 364 */       if (!this.forced)
/*     */       {
/* 366 */         BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(System.in));
/*     */         
/* 368 */         System.out.print("Are you sure you want to delete service key(s) for " + localPrincipalName
/* 369 */           .toString() + " (" + (this.etype == -1 ? "all etypes" : new StringBuilder().append("etype=").append(this.etype).toString()) + ", " + (this.vDel == -2 ? "old kvno" : this.vDel == -1 ? "all kvno" : new StringBuilder().append("kvno=").append(this.vDel).toString()) + ") in " + this.table
/*     */           
/*     */ 
/* 372 */           .tabName() + "? (Y/[N]): ");
/*     */         
/* 374 */         System.out.flush();
/* 375 */         String str = localBufferedReader.readLine();
/* 376 */         if ((!str.equalsIgnoreCase("Y")) && 
/* 377 */           (!str.equalsIgnoreCase("Yes")))
/*     */         {
/*     */ 
/* 380 */           System.exit(0);
/*     */         }
/*     */       }
/*     */     } catch (KrbException localKrbException) {
/* 384 */       System.err.println("Error occurred while deleting the entry. Deletion failed.");
/*     */       
/* 386 */       localKrbException.printStackTrace();
/* 387 */       System.exit(-1);
/*     */     } catch (IOException localIOException1) {
/* 389 */       System.err.println("Error occurred while deleting the entry.  Deletion failed.");
/*     */       
/* 391 */       localIOException1.printStackTrace();
/* 392 */       System.exit(-1);
/*     */     }
/*     */     
/* 395 */     int i = this.table.deleteEntries(localPrincipalName, this.etype, this.vDel);
/*     */     
/* 397 */     if (i == 0) {
/* 398 */       System.err.println("No matched entry in the keytab. Deletion fails.");
/*     */       
/* 400 */       System.exit(-1);
/*     */     } else {
/*     */       try {
/* 403 */         this.table.save();
/*     */       } catch (IOException localIOException2) {
/* 405 */         System.err.println("Error occurs while saving the keytab. Deletion fails.");
/*     */         
/* 407 */         localIOException2.printStackTrace();
/* 408 */         System.exit(-1);
/*     */       }
/* 410 */       System.out.println("Done! " + i + " entries removed.");
/*     */     }
/*     */   }
/*     */   
/*     */   void error(String... paramVarArgs) {
/* 415 */     for (String str : paramVarArgs) {
/* 416 */       System.out.println("Error: " + str + ".");
/*     */     }
/* 418 */     printHelp();
/* 419 */     System.exit(-1);
/*     */   }
/*     */   
/*     */ 
/*     */   void printHelp()
/*     */   {
/* 425 */     System.out.println("\nUsage: ktab <commands> <options>");
/* 426 */     System.out.println();
/* 427 */     System.out.println("Available commands:");
/* 428 */     System.out.println();
/* 429 */     System.out.println("-l [-e] [-t]\n    list the keytab name and entries. -e with etype, -t with timestamp.");
/*     */     
/* 431 */     System.out.println("-a <principal name> [<password>] [-n <kvno>] [-append]\n    add new key entries to the keytab for the given principal name with\n    optional <password>. If a <kvno> is specified, new keys' Key Version\n    Numbers equal to the value, otherwise, automatically incrementing\n    the Key Version Numbers. If -append is specified, new keys are\n    appended to the keytab, otherwise, old keys for the\n    same principal are removed.");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 438 */     System.out.println("-d <principal name> [-f] [-e <etype>] [<kvno> | all | old]\n    delete key entries from the keytab for the specified principal. If\n    <kvno> is specified, delete keys whose Key Version Numbers match\n    kvno. If \"all\" is specified, delete all keys. If \"old\" is specified,\n    delete all keys except those with the highest kvno. Default action\n    is \"all\". If <etype> is specified, only keys of this encryption type\n    are deleted. <etype> should be specified as the numberic value etype\n    defined in RFC 3961, section 8. A prompt to confirm the deletion is\n    displayed unless -f is specified.");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 447 */     System.out.println();
/* 448 */     System.out.println("Common option(s):");
/* 449 */     System.out.println();
/* 450 */     System.out.println("-k <keytab name>\n    specify keytab name and path with prefix FILE:");
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\krb5\internal\tools\Ktab.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */