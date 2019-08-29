/*     */ package sun.security.krb5;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.security.AccessController;
/*     */ import java.util.LinkedList;
/*     */ import sun.security.action.GetBooleanAction;
/*     */ import sun.security.krb5.internal.util.KerberosString;
/*     */ import sun.security.util.DerInputStream;
/*     */ import sun.security.util.DerOutputStream;
/*     */ import sun.security.util.DerValue;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Realm
/*     */   implements Cloneable
/*     */ {
/*  52 */   public static final boolean AUTODEDUCEREALM = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.security.krb5.autodeducerealm"))).booleanValue();
/*     */   
/*     */   private final String realm;
/*     */   
/*     */   public Realm(String paramString)
/*     */     throws RealmException
/*     */   {
/*  59 */     this.realm = parseRealm(paramString);
/*     */   }
/*     */   
/*     */   public static Realm getDefault() throws RealmException {
/*     */     try {
/*  64 */       return new Realm(Config.getInstance().getDefaultRealm());
/*     */     } catch (RealmException localRealmException) {
/*  66 */       throw localRealmException;
/*     */     } catch (KrbException localKrbException) {
/*  68 */       throw new RealmException(localKrbException);
/*     */     }
/*     */   }
/*     */   
/*     */   public Object clone()
/*     */   {
/*  74 */     return this;
/*     */   }
/*     */   
/*     */   public boolean equals(Object paramObject) {
/*  78 */     if (this == paramObject) {
/*  79 */       return true;
/*     */     }
/*     */     
/*  82 */     if (!(paramObject instanceof Realm)) {
/*  83 */       return false;
/*     */     }
/*     */     
/*  86 */     Realm localRealm = (Realm)paramObject;
/*  87 */     return this.realm.equals(localRealm.realm);
/*     */   }
/*     */   
/*     */   public int hashCode() {
/*  91 */     return this.realm.hashCode();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Realm(DerValue paramDerValue)
/*     */     throws Asn1Exception, RealmException, IOException
/*     */   {
/* 103 */     if (paramDerValue == null) {
/* 104 */       throw new IllegalArgumentException("encoding can not be null");
/*     */     }
/* 106 */     this.realm = new KerberosString(paramDerValue).toString();
/* 107 */     if ((this.realm == null) || (this.realm.length() == 0))
/* 108 */       throw new RealmException(601);
/* 109 */     if (!isValidRealmString(this.realm))
/* 110 */       throw new RealmException(600);
/*     */   }
/*     */   
/*     */   public String toString() {
/* 114 */     return this.realm;
/*     */   }
/*     */   
/*     */   public static String parseRealmAtSeparator(String paramString)
/*     */     throws RealmException
/*     */   {
/* 120 */     if (paramString == null) {
/* 121 */       throw new IllegalArgumentException("null input name is not allowed");
/*     */     }
/*     */     
/* 124 */     String str1 = new String(paramString);
/* 125 */     String str2 = null;
/* 126 */     int i = 0;
/* 127 */     while (i < str1.length()) {
/* 128 */       if ((str1.charAt(i) == '@') && (
/* 129 */         (i == 0) || (str1.charAt(i - 1) != '\\'))) {
/* 130 */         if (i + 1 < str1.length()) {
/* 131 */           str2 = str1.substring(i + 1, str1.length()); break;
/*     */         }
/* 133 */         throw new IllegalArgumentException("empty realm part not allowed");
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 139 */       i++;
/*     */     }
/* 141 */     if (str2 != null) {
/* 142 */       if (str2.length() == 0)
/* 143 */         throw new RealmException(601);
/* 144 */       if (!isValidRealmString(str2))
/* 145 */         throw new RealmException(600);
/*     */     }
/* 147 */     return str2;
/*     */   }
/*     */   
/*     */   public static String parseRealmComponent(String paramString) {
/* 151 */     if (paramString == null) {
/* 152 */       throw new IllegalArgumentException("null input name is not allowed");
/*     */     }
/*     */     
/* 155 */     String str1 = new String(paramString);
/* 156 */     String str2 = null;
/* 157 */     int i = 0;
/* 158 */     while (i < str1.length()) {
/* 159 */       if ((str1.charAt(i) == '.') && (
/* 160 */         (i == 0) || (str1.charAt(i - 1) != '\\'))) {
/* 161 */         if (i + 1 >= str1.length()) break;
/* 162 */         str2 = str1.substring(i + 1, str1.length()); break;
/*     */       }
/*     */       
/*     */ 
/* 166 */       i++;
/*     */     }
/* 168 */     return str2;
/*     */   }
/*     */   
/*     */   protected static String parseRealm(String paramString) throws RealmException {
/* 172 */     String str = parseRealmAtSeparator(paramString);
/* 173 */     if (str == null)
/* 174 */       str = paramString;
/* 175 */     if ((str == null) || (str.length() == 0))
/* 176 */       throw new RealmException(601);
/* 177 */     if (!isValidRealmString(str))
/* 178 */       throw new RealmException(600);
/* 179 */     return str;
/*     */   }
/*     */   
/*     */ 
/*     */   protected static boolean isValidRealmString(String paramString)
/*     */   {
/* 185 */     if (paramString == null)
/* 186 */       return false;
/* 187 */     if (paramString.length() == 0)
/* 188 */       return false;
/* 189 */     for (int i = 0; i < paramString.length(); i++) {
/* 190 */       if ((paramString.charAt(i) == '/') || 
/* 191 */         (paramString.charAt(i) == ':') || 
/* 192 */         (paramString.charAt(i) == 0)) {
/* 193 */         return false;
/*     */       }
/*     */     }
/* 196 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] asn1Encode()
/*     */     throws Asn1Exception, IOException
/*     */   {
/* 207 */     DerOutputStream localDerOutputStream = new DerOutputStream();
/* 208 */     localDerOutputStream.putDerValue(new KerberosString(this.realm).toDerValue());
/* 209 */     return localDerOutputStream.toByteArray();
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
/*     */   public static Realm parse(DerInputStream paramDerInputStream, byte paramByte, boolean paramBoolean)
/*     */     throws Asn1Exception, IOException, RealmException
/*     */   {
/* 227 */     if ((paramBoolean) && (((byte)paramDerInputStream.peekByte() & 0x1F) != paramByte)) {
/* 228 */       return null;
/*     */     }
/* 230 */     DerValue localDerValue1 = paramDerInputStream.getDerValue();
/* 231 */     if (paramByte != (localDerValue1.getTag() & 0x1F)) {
/* 232 */       throw new Asn1Exception(906);
/*     */     }
/* 234 */     DerValue localDerValue2 = localDerValue1.getData().getDerValue();
/* 235 */     return new Realm(localDerValue2);
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
/*     */   public static String[] getRealmsList(String paramString1, String paramString2)
/*     */   {
/*     */     try
/*     */     {
/* 259 */       return parseCapaths(paramString1, paramString2);
/*     */     }
/*     */     catch (KrbException localKrbException) {}
/* 262 */     return parseHierarchy(paramString1, paramString2);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static String[] parseCapaths(String paramString1, String paramString2)
/*     */     throws KrbException
/*     */   {
/* 316 */     Config localConfig = Config.getInstance();
/*     */     
/* 318 */     if (!localConfig.exists(new String[] { "capaths", paramString1, paramString2 })) {
/* 319 */       throw new KrbException("No conf");
/*     */     }
/*     */     
/* 322 */     LinkedList localLinkedList = new LinkedList();
/*     */     
/* 324 */     String str1 = paramString2;
/*     */     for (;;) {
/* 326 */       String str2 = localConfig.getAll(new String[] { "capaths", paramString1, str1 });
/* 327 */       if (str2 == null) {
/*     */         break;
/*     */       }
/* 330 */       String[] arrayOfString = str2.split("\\s+");
/* 331 */       int i = 0;
/* 332 */       for (int j = arrayOfString.length - 1; j >= 0; j--)
/* 333 */         if ((!localLinkedList.contains(arrayOfString[j])) && 
/* 334 */           (!arrayOfString[j].equals(".")) && 
/* 335 */           (!arrayOfString[j].equals(paramString1)) && 
/* 336 */           (!arrayOfString[j].equals(paramString2)) && 
/* 337 */           (!arrayOfString[j].equals(str1)))
/*     */         {
/*     */ 
/*     */ 
/* 341 */           i = 1;
/* 342 */           localLinkedList.addFirst(arrayOfString[j]);
/*     */         }
/* 344 */       if (i == 0) break;
/* 345 */       str1 = (String)localLinkedList.getFirst();
/*     */     }
/* 347 */     localLinkedList.addFirst(paramString1);
/* 348 */     return (String[])localLinkedList.toArray(new String[localLinkedList.size()]);
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
/*     */   private static String[] parseHierarchy(String paramString1, String paramString2)
/*     */   {
/* 361 */     String[] arrayOfString1 = paramString1.split("\\.");
/* 362 */     String[] arrayOfString2 = paramString2.split("\\.");
/*     */     
/* 364 */     int i = arrayOfString1.length;
/* 365 */     int j = arrayOfString2.length;
/*     */     
/* 367 */     int k = 0;
/* 368 */     j--; for (i--; (j >= 0) && (i >= 0) && 
/* 369 */           (arrayOfString2[j].equals(arrayOfString1[i])); 
/* 370 */         i--) {
/* 371 */       k = 1;j--;
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
/* 384 */     LinkedList localLinkedList = new LinkedList();
/*     */     
/*     */ 
/* 387 */     for (int m = 0; m <= i; m++) {
/* 388 */       localLinkedList.addLast(subStringFrom(arrayOfString1, m));
/*     */     }
/*     */     
/*     */ 
/* 392 */     if (k != 0) {
/* 393 */       localLinkedList.addLast(subStringFrom(arrayOfString1, i + 1));
/*     */     }
/*     */     
/*     */ 
/* 397 */     for (m = j; m >= 0; m--) {
/* 398 */       localLinkedList.addLast(subStringFrom(arrayOfString2, m));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 403 */     localLinkedList.removeLast();
/*     */     
/* 405 */     return (String[])localLinkedList.toArray(new String[localLinkedList.size()]);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static String subStringFrom(String[] paramArrayOfString, int paramInt)
/*     */   {
/* 413 */     StringBuilder localStringBuilder = new StringBuilder();
/* 414 */     for (int i = paramInt; i < paramArrayOfString.length; i++) {
/* 415 */       if (localStringBuilder.length() != 0) localStringBuilder.append('.');
/* 416 */       localStringBuilder.append(paramArrayOfString[i]);
/*     */     }
/* 418 */     return localStringBuilder.toString();
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\krb5\Realm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */