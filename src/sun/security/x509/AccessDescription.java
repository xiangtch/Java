/*     */ package sun.security.x509;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import sun.security.util.DerInputStream;
/*     */ import sun.security.util.DerOutputStream;
/*     */ import sun.security.util.DerValue;
/*     */ import sun.security.util.ObjectIdentifier;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class AccessDescription
/*     */ {
/*  38 */   private int myhash = -1;
/*     */   
/*     */ 
/*     */   private ObjectIdentifier accessMethod;
/*     */   
/*     */   private GeneralName accessLocation;
/*     */   
/*  45 */   public static final ObjectIdentifier Ad_OCSP_Id = ObjectIdentifier.newInternal(new int[] { 1, 3, 6, 1, 5, 5, 7, 48, 1 });
/*     */   
/*     */ 
/*  48 */   public static final ObjectIdentifier Ad_CAISSUERS_Id = ObjectIdentifier.newInternal(new int[] { 1, 3, 6, 1, 5, 5, 7, 48, 2 });
/*     */   
/*     */ 
/*  51 */   public static final ObjectIdentifier Ad_TIMESTAMPING_Id = ObjectIdentifier.newInternal(new int[] { 1, 3, 6, 1, 5, 5, 7, 48, 3 });
/*     */   
/*     */ 
/*  54 */   public static final ObjectIdentifier Ad_CAREPOSITORY_Id = ObjectIdentifier.newInternal(new int[] { 1, 3, 6, 1, 5, 5, 7, 48, 5 });
/*     */   
/*     */   public AccessDescription(ObjectIdentifier paramObjectIdentifier, GeneralName paramGeneralName) {
/*  57 */     this.accessMethod = paramObjectIdentifier;
/*  58 */     this.accessLocation = paramGeneralName;
/*     */   }
/*     */   
/*     */   public AccessDescription(DerValue paramDerValue) throws IOException {
/*  62 */     DerInputStream localDerInputStream = paramDerValue.getData();
/*  63 */     this.accessMethod = localDerInputStream.getOID();
/*  64 */     this.accessLocation = new GeneralName(localDerInputStream.getDerValue());
/*     */   }
/*     */   
/*     */   public ObjectIdentifier getAccessMethod() {
/*  68 */     return this.accessMethod;
/*     */   }
/*     */   
/*     */   public GeneralName getAccessLocation() {
/*  72 */     return this.accessLocation;
/*     */   }
/*     */   
/*     */   public void encode(DerOutputStream paramDerOutputStream) throws IOException {
/*  76 */     DerOutputStream localDerOutputStream = new DerOutputStream();
/*  77 */     localDerOutputStream.putOID(this.accessMethod);
/*  78 */     this.accessLocation.encode(localDerOutputStream);
/*  79 */     paramDerOutputStream.write((byte)48, localDerOutputStream);
/*     */   }
/*     */   
/*     */   public int hashCode() {
/*  83 */     if (this.myhash == -1) {
/*  84 */       this.myhash = (this.accessMethod.hashCode() + this.accessLocation.hashCode());
/*     */     }
/*  86 */     return this.myhash;
/*     */   }
/*     */   
/*     */   public boolean equals(Object paramObject) {
/*  90 */     if ((paramObject == null) || (!(paramObject instanceof AccessDescription))) {
/*  91 */       return false;
/*     */     }
/*  93 */     AccessDescription localAccessDescription = (AccessDescription)paramObject;
/*     */     
/*  95 */     if (this == localAccessDescription) {
/*  96 */       return true;
/*     */     }
/*  98 */     return (this.accessMethod.equals(localAccessDescription.getAccessMethod())) && 
/*  99 */       (this.accessLocation.equals(localAccessDescription.getAccessLocation()));
/*     */   }
/*     */   
/*     */   public String toString() {
/* 103 */     String str = null;
/* 104 */     if (this.accessMethod.equals(Ad_CAISSUERS_Id)) {
/* 105 */       str = "caIssuers";
/* 106 */     } else if (this.accessMethod.equals(Ad_CAREPOSITORY_Id)) {
/* 107 */       str = "caRepository";
/* 108 */     } else if (this.accessMethod.equals(Ad_TIMESTAMPING_Id)) {
/* 109 */       str = "timeStamping";
/* 110 */     } else if (this.accessMethod.equals(Ad_OCSP_Id)) {
/* 111 */       str = "ocsp";
/*     */     } else {
/* 113 */       str = this.accessMethod.toString();
/*     */     }
/* 115 */     return 
/* 116 */       "\n   accessMethod: " + str + "\n   accessLocation: " + this.accessLocation.toString() + "\n";
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\x509\AccessDescription.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */