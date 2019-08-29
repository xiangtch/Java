/*     */ package sun.security.krb5.internal;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import sun.security.krb5.Asn1Exception;
/*     */ import sun.security.krb5.Checksum;
/*     */ import sun.security.krb5.EncryptionKey;
/*     */ import sun.security.krb5.KrbException;
/*     */ import sun.security.krb5.PrincipalName;
/*     */ import sun.security.krb5.Realm;
/*     */ import sun.security.krb5.RealmException;
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
/*     */ public class PAForUserEnc
/*     */ {
/*     */   public final PrincipalName name;
/*     */   private final EncryptionKey key;
/*     */   public static final String AUTH_PACKAGE = "Kerberos";
/*     */   
/*     */   public PAForUserEnc(PrincipalName paramPrincipalName, EncryptionKey paramEncryptionKey)
/*     */   {
/*  62 */     this.name = paramPrincipalName;
/*  63 */     this.key = paramEncryptionKey;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PAForUserEnc(DerValue paramDerValue, EncryptionKey paramEncryptionKey)
/*     */     throws Asn1Exception, KrbException, IOException
/*     */   {
/*  76 */     DerValue localDerValue = null;
/*  77 */     this.key = paramEncryptionKey;
/*     */     
/*  79 */     if (paramDerValue.getTag() != 48) {
/*  80 */       throw new Asn1Exception(906);
/*     */     }
/*     */     
/*     */ 
/*  84 */     PrincipalName localPrincipalName = null;
/*  85 */     localDerValue = paramDerValue.getData().getDerValue();
/*  86 */     if ((localDerValue.getTag() & 0x1F) == 0) {
/*     */       try {
/*  88 */         localPrincipalName = new PrincipalName(localDerValue.getData().getDerValue(), new Realm("PLACEHOLDER"));
/*     */ 
/*     */       }
/*     */       catch (RealmException localRealmException1) {}
/*     */     }
/*     */     else {
/*  94 */       throw new Asn1Exception(906);
/*     */     }
/*     */     
/*  97 */     localDerValue = paramDerValue.getData().getDerValue();
/*  98 */     if ((localDerValue.getTag() & 0x1F) == 1) {
/*     */       try {
/* 100 */         Realm localRealm = new Realm(localDerValue.getData().getDerValue());
/*     */         
/* 102 */         this.name = new PrincipalName(localPrincipalName.getNameType(), localPrincipalName.getNameStrings(), localRealm);
/*     */       } catch (RealmException localRealmException2) {
/* 104 */         throw new IOException(localRealmException2);
/*     */       }
/*     */     } else {
/* 107 */       throw new Asn1Exception(906);
/*     */     }
/*     */     
/* 110 */     localDerValue = paramDerValue.getData().getDerValue();
/* 111 */     if ((localDerValue.getTag() & 0x1F) != 2)
/*     */     {
/*     */ 
/* 114 */       throw new Asn1Exception(906);
/*     */     }
/*     */     
/* 117 */     localDerValue = paramDerValue.getData().getDerValue();
/* 118 */     if ((localDerValue.getTag() & 0x1F) == 3) {
/* 119 */       String str = new KerberosString(localDerValue.getData().getDerValue()).toString();
/* 120 */       if (!str.equalsIgnoreCase("Kerberos")) {
/* 121 */         throw new IOException("Incorrect auth-package");
/*     */       }
/*     */     } else {
/* 124 */       throw new Asn1Exception(906);
/*     */     }
/* 126 */     if (paramDerValue.getData().available() > 0)
/* 127 */       throw new Asn1Exception(906);
/*     */   }
/*     */   
/*     */   public byte[] asn1Encode() throws Asn1Exception, IOException {
/* 131 */     DerOutputStream localDerOutputStream1 = new DerOutputStream();
/* 132 */     localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), this.name.asn1Encode());
/* 133 */     localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)1), this.name.getRealm().asn1Encode());
/*     */     
/*     */ 
/*     */     try
/*     */     {
/* 138 */       Checksum localChecksum = new Checksum(65398, getS4UByteArray(), this.key, 17);
/*     */       
/*     */ 
/* 141 */       localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)2), localChecksum.asn1Encode());
/*     */     } catch (KrbException localKrbException) {
/* 143 */       throw new IOException(localKrbException);
/*     */     }
/*     */     
/* 146 */     DerOutputStream localDerOutputStream2 = new DerOutputStream();
/* 147 */     localDerOutputStream2.putDerValue(new KerberosString("Kerberos").toDerValue());
/* 148 */     localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)3), localDerOutputStream2);
/*     */     
/* 150 */     localDerOutputStream2 = new DerOutputStream();
/* 151 */     localDerOutputStream2.write((byte)48, localDerOutputStream1);
/* 152 */     return localDerOutputStream2.toByteArray();
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
/*     */   public byte[] getS4UByteArray()
/*     */   {
/*     */     try
/*     */     {
/* 167 */       ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
/* 168 */       localByteArrayOutputStream.write(new byte[4]);
/* 169 */       for (Object localObject2 : this.name.getNameStrings()) {
/* 170 */         localByteArrayOutputStream.write(((String)localObject2).getBytes("UTF-8"));
/*     */       }
/* 172 */       localByteArrayOutputStream.write(this.name.getRealm().toString().getBytes("UTF-8"));
/* 173 */       localByteArrayOutputStream.write("Kerberos".getBytes("UTF-8"));
/* 174 */       ??? = localByteArrayOutputStream.toByteArray();
/* 175 */       ??? = this.name.getNameType();
/* 176 */       ???[0] = ((byte)(??? & 0xFF));
/* 177 */       ???[1] = ((byte)(??? >> 8 & 0xFF));
/* 178 */       ???[2] = ((byte)(??? >> 16 & 0xFF));
/* 179 */       ???[3] = ((byte)(??? >> 24 & 0xFF));
/* 180 */       return (byte[])???;
/*     */     }
/*     */     catch (IOException localIOException) {
/* 183 */       throw new AssertionError("Cannot write ByteArrayOutputStream", localIOException);
/*     */     }
/*     */   }
/*     */   
/*     */   public String toString() {
/* 188 */     return "PA-FOR-USER: " + this.name;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\krb5\internal\PAForUserEnc.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */