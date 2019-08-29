/*     */ package sun.security.krb5.internal;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import sun.security.krb5.Asn1Exception;
/*     */ import sun.security.krb5.Config;
/*     */ import sun.security.krb5.KrbException;
/*     */ import sun.security.krb5.internal.util.KerberosFlags;
/*     */ import sun.security.util.BitArray;
/*     */ import sun.security.util.DerInputStream;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class KDCOptions
/*     */   extends KerberosFlags
/*     */ {
/*     */   private static final int KDC_OPT_PROXIABLE = 268435456;
/*     */   private static final int KDC_OPT_RENEWABLE_OK = 16;
/*     */   private static final int KDC_OPT_FORWARDABLE = 1073741824;
/*     */   public static final int RESERVED = 0;
/*     */   public static final int FORWARDABLE = 1;
/*     */   public static final int FORWARDED = 2;
/*     */   public static final int PROXIABLE = 3;
/*     */   public static final int PROXY = 4;
/*     */   public static final int ALLOW_POSTDATE = 5;
/*     */   public static final int POSTDATED = 6;
/*     */   public static final int UNUSED7 = 7;
/*     */   public static final int RENEWABLE = 8;
/*     */   public static final int UNUSED9 = 9;
/*     */   public static final int UNUSED10 = 10;
/*     */   public static final int UNUSED11 = 11;
/*     */   public static final int CNAME_IN_ADDL_TKT = 14;
/*     */   public static final int RENEWABLE_OK = 27;
/*     */   public static final int ENC_TKT_IN_SKEY = 28;
/*     */   public static final int RENEW = 30;
/*     */   public static final int VALIDATE = 31;
/* 148 */   private static final String[] names = { "RESERVED", "FORWARDABLE", "FORWARDED", "PROXIABLE", "PROXY", "ALLOW_POSTDATE", "POSTDATED", "UNUSED7", "RENEWABLE", "UNUSED9", "UNUSED10", "UNUSED11", null, null, "CNAME_IN_ADDL_TKT", null, null, null, null, null, null, null, null, null, null, null, null, "RENEWABLE_OK", "ENC_TKT_IN_SKEY", null, "RENEW", "VALIDATE" };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 171 */   private boolean DEBUG = Krb5.DEBUG;
/*     */   
/*     */   public static KDCOptions with(int... paramVarArgs) {
/* 174 */     KDCOptions localKDCOptions = new KDCOptions();
/* 175 */     for (int k : paramVarArgs) {
/* 176 */       localKDCOptions.set(k, true);
/*     */     }
/* 178 */     return localKDCOptions;
/*     */   }
/*     */   
/*     */   public KDCOptions() {
/* 182 */     super(32);
/* 183 */     setDefault();
/*     */   }
/*     */   
/*     */   public KDCOptions(int paramInt, byte[] paramArrayOfByte) throws Asn1Exception {
/* 187 */     super(paramInt, paramArrayOfByte);
/* 188 */     if ((paramInt > paramArrayOfByte.length * 8) || (paramInt > 32)) {
/* 189 */       throw new Asn1Exception(502);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public KDCOptions(boolean[] paramArrayOfBoolean)
/*     */     throws Asn1Exception
/*     */   {
/* 201 */     super(paramArrayOfBoolean);
/* 202 */     if (paramArrayOfBoolean.length > 32) {
/* 203 */       throw new Asn1Exception(502);
/*     */     }
/*     */   }
/*     */   
/*     */   public KDCOptions(DerValue paramDerValue) throws Asn1Exception, IOException {
/* 208 */     this(paramDerValue.getUnalignedBitString(true).toBooleanArray());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public KDCOptions(byte[] paramArrayOfByte)
/*     */   {
/* 218 */     super(paramArrayOfByte.length * 8, paramArrayOfByte);
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
/*     */   public static KDCOptions parse(DerInputStream paramDerInputStream, byte paramByte, boolean paramBoolean)
/*     */     throws Asn1Exception, IOException
/*     */   {
/* 237 */     if ((paramBoolean) && (((byte)paramDerInputStream.peekByte() & 0x1F) != paramByte))
/* 238 */       return null;
/* 239 */     DerValue localDerValue1 = paramDerInputStream.getDerValue();
/* 240 */     if (paramByte != (localDerValue1.getTag() & 0x1F)) {
/* 241 */       throw new Asn1Exception(906);
/*     */     }
/* 243 */     DerValue localDerValue2 = localDerValue1.getData().getDerValue();
/* 244 */     return new KDCOptions(localDerValue2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void set(int paramInt, boolean paramBoolean)
/*     */     throws ArrayIndexOutOfBoundsException
/*     */   {
/* 257 */     super.set(paramInt, paramBoolean);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean get(int paramInt)
/*     */     throws ArrayIndexOutOfBoundsException
/*     */   {
/* 270 */     return super.get(paramInt);
/*     */   }
/*     */   
/*     */   public String toString() {
/* 274 */     StringBuilder localStringBuilder = new StringBuilder();
/* 275 */     localStringBuilder.append("KDCOptions: ");
/* 276 */     for (int i = 0; i < 32; i++) {
/* 277 */       if (get(i)) {
/* 278 */         if (names[i] != null) {
/* 279 */           localStringBuilder.append(names[i]).append(",");
/*     */         } else {
/* 281 */           localStringBuilder.append(i).append(",");
/*     */         }
/*     */       }
/*     */     }
/* 285 */     return localStringBuilder.toString();
/*     */   }
/*     */   
/*     */   private void setDefault()
/*     */   {
/*     */     try {
/* 291 */       Config localConfig = Config.getInstance();
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 296 */       int i = localConfig.getIntValue(new String[] { "libdefaults", "kdc_default_options" });
/*     */       
/*     */ 
/* 299 */       if ((i & 0x10) == 16) {
/* 300 */         set(27, true);
/*     */       }
/* 302 */       else if (localConfig.getBooleanValue(new String[] { "libdefaults", "renewable" })) {
/* 303 */         set(27, true);
/*     */       }
/*     */       
/* 306 */       if ((i & 0x10000000) == 268435456) {
/* 307 */         set(3, true);
/*     */       }
/* 309 */       else if (localConfig.getBooleanValue(new String[] { "libdefaults", "proxiable" })) {
/* 310 */         set(3, true);
/*     */       }
/*     */       
/*     */ 
/* 314 */       if ((i & 0x40000000) == 1073741824) {
/* 315 */         set(1, true);
/*     */       }
/* 317 */       else if (localConfig.getBooleanValue(new String[] { "libdefaults", "forwardable" })) {
/* 318 */         set(1, true);
/*     */       }
/*     */     }
/*     */     catch (KrbException localKrbException) {
/* 322 */       if (this.DEBUG) {
/* 323 */         System.out.println("Exception in getting default values for KDC Options from the configuration ");
/*     */         
/* 325 */         localKrbException.printStackTrace();
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\krb5\internal\KDCOptions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */