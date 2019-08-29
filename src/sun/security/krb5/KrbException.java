/*     */ package sun.security.krb5;
/*     */ 
/*     */ import sun.security.krb5.internal.KRBError;
/*     */ import sun.security.krb5.internal.Krb5;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class KrbException
/*     */   extends Exception
/*     */ {
/*     */   private static final long serialVersionUID = -4993302876451928596L;
/*     */   private int returnCode;
/*     */   private KRBError error;
/*     */   
/*     */   public KrbException(String paramString)
/*     */   {
/*  45 */     super(paramString);
/*     */   }
/*     */   
/*     */   public KrbException(Throwable paramThrowable) {
/*  49 */     super(paramThrowable);
/*     */   }
/*     */   
/*     */   public KrbException(int paramInt) {
/*  53 */     this.returnCode = paramInt;
/*     */   }
/*     */   
/*     */   public KrbException(int paramInt, String paramString) {
/*  57 */     this(paramString);
/*  58 */     this.returnCode = paramInt;
/*     */   }
/*     */   
/*     */   public KrbException(KRBError paramKRBError) {
/*  62 */     this.returnCode = paramKRBError.getErrorCode();
/*  63 */     this.error = paramKRBError;
/*     */   }
/*     */   
/*     */   public KrbException(KRBError paramKRBError, String paramString) {
/*  67 */     this(paramString);
/*  68 */     this.returnCode = paramKRBError.getErrorCode();
/*  69 */     this.error = paramKRBError;
/*     */   }
/*     */   
/*     */   public KRBError getError() {
/*  73 */     return this.error;
/*     */   }
/*     */   
/*     */   public int returnCode()
/*     */   {
/*  78 */     return this.returnCode;
/*     */   }
/*     */   
/*     */   public String returnCodeSymbol() {
/*  82 */     return returnCodeSymbol(this.returnCode);
/*     */   }
/*     */   
/*     */   public static String returnCodeSymbol(int paramInt) {
/*  86 */     return "not yet implemented";
/*     */   }
/*     */   
/*     */   public String returnCodeMessage() {
/*  90 */     return Krb5.getErrorMessage(this.returnCode);
/*     */   }
/*     */   
/*     */   public static String errorMessage(int paramInt) {
/*  94 */     return Krb5.getErrorMessage(paramInt);
/*     */   }
/*     */   
/*     */   public String krbErrorMessage()
/*     */   {
/*  99 */     StringBuffer localStringBuffer = new StringBuffer("krb_error " + this.returnCode);
/* 100 */     String str = getMessage();
/* 101 */     if (str != null) {
/* 102 */       localStringBuffer.append(" ");
/* 103 */       localStringBuffer.append(str);
/*     */     }
/* 105 */     return localStringBuffer.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getMessage()
/*     */   {
/* 115 */     StringBuffer localStringBuffer = new StringBuffer();
/* 116 */     int i = returnCode();
/* 117 */     if (i != 0) {
/* 118 */       localStringBuffer.append(returnCodeMessage());
/* 119 */       localStringBuffer.append(" (").append(returnCode()).append(')');
/*     */     }
/* 121 */     String str = super.getMessage();
/* 122 */     if ((str != null) && (str.length() != 0)) {
/* 123 */       if (i != 0)
/* 124 */         localStringBuffer.append(" - ");
/* 125 */       localStringBuffer.append(str);
/*     */     }
/* 127 */     return localStringBuffer.toString();
/*     */   }
/*     */   
/*     */   public String toString() {
/* 131 */     return "KrbException: " + getMessage();
/*     */   }
/*     */   
/*     */   public int hashCode() {
/* 135 */     int i = 17;
/* 136 */     i = 37 * i + this.returnCode;
/* 137 */     if (this.error != null) {
/* 138 */       i = 37 * i + this.error.hashCode();
/*     */     }
/* 140 */     return i;
/*     */   }
/*     */   
/*     */   public boolean equals(Object paramObject) {
/* 144 */     if (this == paramObject) {
/* 145 */       return true;
/*     */     }
/*     */     
/* 148 */     if (!(paramObject instanceof KrbException)) {
/* 149 */       return false;
/*     */     }
/*     */     
/* 152 */     KrbException localKrbException = (KrbException)paramObject;
/* 153 */     if (this.returnCode != localKrbException.returnCode) {
/* 154 */       return false;
/*     */     }
/* 156 */     return this.error == null ? false : localKrbException.error == null ? true : this.error
/* 157 */       .equals(localKrbException.error);
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\krb5\KrbException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */