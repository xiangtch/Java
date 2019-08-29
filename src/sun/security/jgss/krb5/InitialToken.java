/*     */ package sun.security.jgss.krb5;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.Inet4Address;
/*     */ import java.net.Inet6Address;
/*     */ import java.net.InetAddress;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.util.Arrays;
/*     */ import javax.security.auth.kerberos.DelegationPermission;
/*     */ import org.ietf.jgss.ChannelBinding;
/*     */ import org.ietf.jgss.GSSException;
/*     */ import sun.security.jgss.GSSToken;
/*     */ import sun.security.krb5.Checksum;
/*     */ import sun.security.krb5.Credentials;
/*     */ import sun.security.krb5.EncryptionKey;
/*     */ import sun.security.krb5.KrbCred;
/*     */ import sun.security.krb5.KrbException;
/*     */ import sun.security.krb5.PrincipalName;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ abstract class InitialToken
/*     */   extends Krb5Token
/*     */ {
/*     */   private static final int CHECKSUM_TYPE = 32771;
/*     */   private static final int CHECKSUM_LENGTH_SIZE = 4;
/*     */   private static final int CHECKSUM_BINDINGS_SIZE = 16;
/*     */   private static final int CHECKSUM_FLAGS_SIZE = 4;
/*     */   private static final int CHECKSUM_DELEG_OPT_SIZE = 2;
/*     */   private static final int CHECKSUM_DELEG_LGTH_SIZE = 2;
/*     */   private static final int CHECKSUM_DELEG_FLAG = 1;
/*     */   private static final int CHECKSUM_MUTUAL_FLAG = 2;
/*     */   private static final int CHECKSUM_REPLAY_FLAG = 4;
/*     */   private static final int CHECKSUM_SEQUENCE_FLAG = 8;
/*     */   private static final int CHECKSUM_CONF_FLAG = 16;
/*     */   private static final int CHECKSUM_INTEG_FLAG = 32;
/*  57 */   private final byte[] CHECKSUM_FIRST_BYTES = { 16, 0, 0, 0 };
/*     */   
/*     */   private static final int CHANNEL_BINDING_AF_INET = 2;
/*     */   
/*     */   private static final int CHANNEL_BINDING_AF_INET6 = 24;
/*     */   
/*     */   private static final int CHANNEL_BINDING_AF_NULL_ADDR = 255;
/*     */   private static final int Inet4_ADDRSZ = 4;
/*     */   private static final int Inet6_ADDRSZ = 16;
/*     */   
/*     */   protected class OverloadedChecksum
/*     */   {
/*  69 */     private byte[] checksumBytes = null;
/*  70 */     private Credentials delegCreds = null;
/*  71 */     private int flags = 0;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public OverloadedChecksum(Krb5Context paramKrb5Context, Credentials paramCredentials1, Credentials paramCredentials2)
/*     */       throws KrbException, IOException, GSSException
/*     */     {
/*  82 */       byte[] arrayOfByte = null;
/*  83 */       int i = 0;
/*  84 */       int j = 24;
/*     */       
/*     */ 
/*  87 */       if (!paramCredentials1.isForwardable()) {
/*  88 */         paramKrb5Context.setCredDelegState(false);
/*  89 */         paramKrb5Context.setDelegPolicyState(false);
/*  90 */       } else if (paramKrb5Context.getCredDelegState()) {
/*  91 */         if ((paramKrb5Context.getDelegPolicyState()) && 
/*  92 */           (!paramCredentials2.checkDelegate()))
/*     */         {
/*  94 */           paramKrb5Context.setDelegPolicyState(false);
/*     */         }
/*     */       }
/*  97 */       else if (paramKrb5Context.getDelegPolicyState()) {
/*  98 */         if (paramCredentials2.checkDelegate()) {
/*  99 */           paramKrb5Context.setCredDelegState(true);
/*     */         } else {
/* 101 */           paramKrb5Context.setDelegPolicyState(false);
/*     */         }
/*     */       }
/*     */       
/* 105 */       if (paramKrb5Context.getCredDelegState()) {
/* 106 */         localObject1 = null;
/*     */         
/* 108 */         localObject2 = paramKrb5Context.getCipherHelper(paramCredentials2.getSessionKey());
/* 109 */         if (useNullKey((CipherHelper)localObject2)) {
/* 110 */           localObject1 = new KrbCred(paramCredentials1, paramCredentials2, EncryptionKey.NULL_KEY);
/*     */         }
/*     */         else
/*     */         {
/* 114 */           localObject1 = new KrbCred(paramCredentials1, paramCredentials2, paramCredentials2.getSessionKey());
/*     */         }
/* 116 */         arrayOfByte = ((KrbCred)localObject1).getMessage();
/* 117 */         j += 4 + arrayOfByte.length;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 122 */       this.checksumBytes = new byte[j];
/*     */       
/* 124 */       this.checksumBytes[(i++)] = InitialToken.this.CHECKSUM_FIRST_BYTES[0];
/* 125 */       this.checksumBytes[(i++)] = InitialToken.this.CHECKSUM_FIRST_BYTES[1];
/* 126 */       this.checksumBytes[(i++)] = InitialToken.this.CHECKSUM_FIRST_BYTES[2];
/* 127 */       this.checksumBytes[(i++)] = InitialToken.this.CHECKSUM_FIRST_BYTES[3];
/*     */       
/* 129 */       Object localObject1 = paramKrb5Context.getChannelBinding();
/* 130 */       if (localObject1 != null)
/*     */       {
/* 132 */         localObject2 = InitialToken.this.computeChannelBinding(paramKrb5Context.getChannelBinding());
/* 133 */         System.arraycopy(localObject2, 0, this.checksumBytes, i, localObject2.length);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 139 */       i += 16;
/*     */       
/* 141 */       if (paramKrb5Context.getCredDelegState())
/* 142 */         this.flags |= 0x1;
/* 143 */       if (paramKrb5Context.getMutualAuthState())
/* 144 */         this.flags |= 0x2;
/* 145 */       if (paramKrb5Context.getReplayDetState())
/* 146 */         this.flags |= 0x4;
/* 147 */       if (paramKrb5Context.getSequenceDetState())
/* 148 */         this.flags |= 0x8;
/* 149 */       if (paramKrb5Context.getIntegState())
/* 150 */         this.flags |= 0x20;
/* 151 */       if (paramKrb5Context.getConfState()) {
/* 152 */         this.flags |= 0x10;
/*     */       }
/* 154 */       Object localObject2 = new byte[4];
/* 155 */       GSSToken.writeLittleEndian(this.flags, (byte[])localObject2);
/* 156 */       this.checksumBytes[(i++)] = localObject2[0];
/* 157 */       this.checksumBytes[(i++)] = localObject2[1];
/* 158 */       this.checksumBytes[(i++)] = localObject2[2];
/* 159 */       this.checksumBytes[(i++)] = localObject2[3];
/*     */       
/* 161 */       if (paramKrb5Context.getCredDelegState())
/*     */       {
/*     */ 
/* 164 */         PrincipalName localPrincipalName = paramCredentials2.getServer();
/*     */         
/*     */ 
/* 167 */         StringBuffer localStringBuffer = new StringBuffer("\"");
/* 168 */         localStringBuffer.append(localPrincipalName.getName()).append('"');
/* 169 */         String str = localPrincipalName.getRealmAsString();
/* 170 */         localStringBuffer.append(" \"krbtgt/").append(str).append('@');
/* 171 */         localStringBuffer.append(str).append('"');
/* 172 */         SecurityManager localSecurityManager = System.getSecurityManager();
/* 173 */         if (localSecurityManager != null)
/*     */         {
/* 175 */           DelegationPermission localDelegationPermission = new DelegationPermission(localStringBuffer.toString());
/* 176 */           localSecurityManager.checkPermission(localDelegationPermission);
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 185 */         this.checksumBytes[(i++)] = 1;
/* 186 */         this.checksumBytes[(i++)] = 0;
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 193 */         if (arrayOfByte.length > 65535) {
/* 194 */           throw new GSSException(11, -1, "Incorrect message length");
/*     */         }
/*     */         
/* 197 */         GSSToken.writeLittleEndian(arrayOfByte.length, (byte[])localObject2);
/* 198 */         this.checksumBytes[(i++)] = localObject2[0];
/* 199 */         this.checksumBytes[(i++)] = localObject2[1];
/* 200 */         System.arraycopy(arrayOfByte, 0, this.checksumBytes, i, arrayOfByte.length);
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
/*     */     public OverloadedChecksum(Krb5Context paramKrb5Context, Checksum paramChecksum, EncryptionKey paramEncryptionKey1, EncryptionKey paramEncryptionKey2)
/*     */       throws GSSException, KrbException, IOException
/*     */     {
/* 217 */       int i = 0;
/*     */       
/* 219 */       if (paramChecksum == null) {
/* 220 */         localObject1 = new GSSException(11, -1, "No cksum in AP_REQ's authenticator");
/*     */         
/* 222 */         ((GSSException)localObject1).initCause(new KrbException(50));
/* 223 */         throw ((Throwable)localObject1);
/*     */       }
/* 225 */       this.checksumBytes = paramChecksum.getBytes();
/*     */       
/* 227 */       if ((this.checksumBytes[0] != InitialToken.this.CHECKSUM_FIRST_BYTES[0]) || 
/* 228 */         (this.checksumBytes[1] != InitialToken.this.CHECKSUM_FIRST_BYTES[1]) || 
/* 229 */         (this.checksumBytes[2] != InitialToken.this.CHECKSUM_FIRST_BYTES[2]) || 
/* 230 */         (this.checksumBytes[3] != InitialToken.this.CHECKSUM_FIRST_BYTES[3])) {
/* 231 */         throw new GSSException(11, -1, "Incorrect checksum");
/*     */       }
/*     */       
/*     */ 
/* 235 */       Object localObject1 = paramKrb5Context.getChannelBinding();
/*     */       
/*     */ 
/*     */ 
/*     */       byte[] arrayOfByte2;
/*     */       
/*     */ 
/*     */ 
/*     */       Object localObject2;
/*     */       
/*     */ 
/* 246 */       if (localObject1 != null) {
/* 247 */         byte[] arrayOfByte1 = new byte[16];
/* 248 */         System.arraycopy(this.checksumBytes, 4, arrayOfByte1, 0, 16);
/*     */         
/*     */ 
/* 251 */         arrayOfByte2 = new byte[16];
/* 252 */         if (!Arrays.equals(arrayOfByte2, arrayOfByte1))
/*     */         {
/* 254 */           localObject2 = InitialToken.this.computeChannelBinding((ChannelBinding)localObject1);
/* 255 */           if (!Arrays.equals((byte[])localObject2, arrayOfByte1))
/*     */           {
/* 257 */             throw new GSSException(1, -1, "Bytes mismatch!");
/*     */           }
/*     */         }
/*     */         else {
/* 261 */           throw new GSSException(1, -1, "Token missing ChannelBinding!");
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 266 */       this.flags = GSSToken.readLittleEndian(this.checksumBytes, 20, 4);
/*     */       
/* 268 */       if ((this.flags & 0x1) > 0)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 276 */         int j = GSSToken.readLittleEndian(this.checksumBytes, 26, 2);
/* 277 */         arrayOfByte2 = new byte[j];
/* 278 */         System.arraycopy(this.checksumBytes, 28, arrayOfByte2, 0, j);
/*     */         
/*     */         try
/*     */         {
/* 282 */           localObject2 = new KrbCred(arrayOfByte2, paramEncryptionKey1);
/*     */         } catch (KrbException localKrbException) {
/* 284 */           if (paramEncryptionKey2 != null) {
/* 285 */             localObject2 = new KrbCred(arrayOfByte2, paramEncryptionKey2);
/*     */           } else {
/* 287 */             throw localKrbException;
/*     */           }
/*     */         }
/* 290 */         this.delegCreds = localObject2.getDelegatedCreds()[0];
/*     */       }
/*     */     }
/*     */     
/*     */     private boolean useNullKey(CipherHelper paramCipherHelper)
/*     */     {
/* 296 */       boolean bool = true;
/*     */       
/* 298 */       if ((paramCipherHelper.getProto() == 1) || (paramCipherHelper.isArcFour())) {
/* 299 */         bool = false;
/*     */       }
/* 301 */       return bool;
/*     */     }
/*     */     
/*     */     public Checksum getChecksum() throws KrbException {
/* 305 */       return new Checksum(this.checksumBytes, 32771);
/*     */     }
/*     */     
/*     */     public Credentials getDelegatedCreds() {
/* 309 */       return this.delegCreds;
/*     */     }
/*     */     
/*     */ 
/*     */     public void setContextFlags(Krb5Context paramKrb5Context)
/*     */     {
/* 315 */       if ((this.flags & 0x1) > 0) {
/* 316 */         paramKrb5Context.setCredDelegState(true);
/*     */       }
/* 318 */       if ((this.flags & 0x2) == 0) {
/* 319 */         paramKrb5Context.setMutualAuthState(false);
/*     */       }
/* 321 */       if ((this.flags & 0x4) == 0) {
/* 322 */         paramKrb5Context.setReplayDetState(false);
/*     */       }
/* 324 */       if ((this.flags & 0x8) == 0) {
/* 325 */         paramKrb5Context.setSequenceDetState(false);
/*     */       }
/* 327 */       if ((this.flags & 0x10) == 0) {
/* 328 */         paramKrb5Context.setConfState(false);
/*     */       }
/* 330 */       if ((this.flags & 0x20) == 0) {
/* 331 */         paramKrb5Context.setIntegState(false);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private int getAddrType(InetAddress paramInetAddress) {
/* 337 */     int i = 255;
/*     */     
/* 339 */     if ((paramInetAddress instanceof Inet4Address)) {
/* 340 */       i = 2;
/* 341 */     } else if ((paramInetAddress instanceof Inet6Address))
/* 342 */       i = 24;
/* 343 */     return i;
/*     */   }
/*     */   
/*     */   private byte[] getAddrBytes(InetAddress paramInetAddress) throws GSSException {
/* 347 */     int i = getAddrType(paramInetAddress);
/* 348 */     byte[] arrayOfByte = paramInetAddress.getAddress();
/* 349 */     if (arrayOfByte != null) {
/* 350 */       switch (i) {
/*     */       case 2: 
/* 352 */         if (arrayOfByte.length != 4) {
/* 353 */           throw new GSSException(11, -1, "Incorrect AF-INET address length in ChannelBinding.");
/*     */         }
/*     */         
/* 356 */         return arrayOfByte;
/*     */       case 24: 
/* 358 */         if (arrayOfByte.length != 16) {
/* 359 */           throw new GSSException(11, -1, "Incorrect AF-INET6 address length in ChannelBinding.");
/*     */         }
/*     */         
/* 362 */         return arrayOfByte;
/*     */       }
/* 364 */       throw new GSSException(11, -1, "Cannot handle non AF-INET addresses in ChannelBinding.");
/*     */     }
/*     */     
/*     */ 
/* 368 */     return null;
/*     */   }
/*     */   
/*     */   private byte[] computeChannelBinding(ChannelBinding paramChannelBinding)
/*     */     throws GSSException
/*     */   {
/* 374 */     InetAddress localInetAddress1 = paramChannelBinding.getInitiatorAddress();
/* 375 */     InetAddress localInetAddress2 = paramChannelBinding.getAcceptorAddress();
/* 376 */     int i = 20;
/*     */     
/* 378 */     int j = getAddrType(localInetAddress1);
/* 379 */     int k = getAddrType(localInetAddress2);
/*     */     
/* 381 */     byte[] arrayOfByte1 = null;
/* 382 */     if (localInetAddress1 != null) {
/* 383 */       arrayOfByte1 = getAddrBytes(localInetAddress1);
/* 384 */       i += arrayOfByte1.length;
/*     */     }
/*     */     
/* 387 */     byte[] arrayOfByte2 = null;
/* 388 */     if (localInetAddress2 != null) {
/* 389 */       arrayOfByte2 = getAddrBytes(localInetAddress2);
/* 390 */       i += arrayOfByte2.length;
/*     */     }
/*     */     
/* 393 */     byte[] arrayOfByte3 = paramChannelBinding.getApplicationData();
/* 394 */     if (arrayOfByte3 != null) {
/* 395 */       i += arrayOfByte3.length;
/*     */     }
/*     */     
/* 398 */     byte[] arrayOfByte4 = new byte[i];
/*     */     
/* 400 */     int m = 0;
/*     */     
/* 402 */     writeLittleEndian(j, arrayOfByte4, m);
/* 403 */     m += 4;
/*     */     
/* 405 */     if (arrayOfByte1 != null) {
/* 406 */       writeLittleEndian(arrayOfByte1.length, arrayOfByte4, m);
/* 407 */       m += 4;
/* 408 */       System.arraycopy(arrayOfByte1, 0, arrayOfByte4, m, arrayOfByte1.length);
/*     */       
/* 410 */       m += arrayOfByte1.length;
/*     */     }
/*     */     else {
/* 413 */       m += 4;
/*     */     }
/*     */     
/* 416 */     writeLittleEndian(k, arrayOfByte4, m);
/* 417 */     m += 4;
/*     */     
/* 419 */     if (arrayOfByte2 != null) {
/* 420 */       writeLittleEndian(arrayOfByte2.length, arrayOfByte4, m);
/* 421 */       m += 4;
/* 422 */       System.arraycopy(arrayOfByte2, 0, arrayOfByte4, m, arrayOfByte2.length);
/*     */       
/* 424 */       m += arrayOfByte2.length;
/*     */     }
/*     */     else {
/* 427 */       m += 4;
/*     */     }
/*     */     
/* 430 */     if (arrayOfByte3 != null) {
/* 431 */       writeLittleEndian(arrayOfByte3.length, arrayOfByte4, m);
/* 432 */       m += 4;
/* 433 */       System.arraycopy(arrayOfByte3, 0, arrayOfByte4, m, arrayOfByte3.length);
/*     */       
/* 435 */       m += arrayOfByte3.length;
/*     */     }
/*     */     else {
/* 438 */       m += 4;
/*     */     }
/*     */     try
/*     */     {
/* 442 */       MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
/* 443 */       return localMessageDigest.digest(arrayOfByte4);
/*     */     }
/*     */     catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
/*     */     {
/* 447 */       throw new GSSException(11, -1, "Could not get MD5 Message Digest - " + localNoSuchAlgorithmException.getMessage());
/*     */     }
/*     */   }
/*     */   
/*     */   public abstract byte[] encode()
/*     */     throws IOException;
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\jgss\krb5\InitialToken.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */