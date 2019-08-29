/*     */ package sun.security.jgss.spnego;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.ietf.jgss.GSSException;
/*     */ import org.ietf.jgss.Oid;
/*     */ import sun.security.jgss.GSSToken;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ abstract class SpNegoToken
/*     */   extends GSSToken
/*     */ {
/*     */   static final int NEG_TOKEN_INIT_ID = 0;
/*     */   static final int NEG_TOKEN_TARG_ID = 1;
/*     */   private int tokenType;
/*     */   
/*     */   static enum NegoResult
/*     */   {
/*  53 */     ACCEPT_COMPLETE, 
/*  54 */     ACCEPT_INCOMPLETE, 
/*  55 */     REJECT;
/*     */     
/*     */ 
/*     */     private NegoResult() {}
/*     */   }
/*     */   
/*  61 */   static final boolean DEBUG = SpNegoContext.DEBUG;
/*     */   
/*     */ 
/*     */   public static ObjectIdentifier OID;
/*     */   
/*     */ 
/*     */ 
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/*  72 */       OID = new ObjectIdentifier(SpNegoMechFactory.GSS_SPNEGO_MECH_OID.toString());
/*     */     }
/*     */     catch (IOException localIOException) {}
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected SpNegoToken(int paramInt)
/*     */   {
/*  82 */     this.tokenType = paramInt;
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
/*     */   byte[] getEncoded()
/*     */     throws IOException, GSSException
/*     */   {
/* 103 */     DerOutputStream localDerOutputStream1 = new DerOutputStream();
/* 104 */     localDerOutputStream1.write(encode());
/*     */     
/*     */ 
/* 107 */     switch (this.tokenType)
/*     */     {
/*     */     case 0: 
/* 110 */       DerOutputStream localDerOutputStream2 = new DerOutputStream();
/* 111 */       localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), localDerOutputStream1);
/*     */       
/* 113 */       return localDerOutputStream2.toByteArray();
/*     */     
/*     */ 
/*     */     case 1: 
/* 117 */       DerOutputStream localDerOutputStream3 = new DerOutputStream();
/* 118 */       localDerOutputStream3.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)1), localDerOutputStream1);
/*     */       
/* 120 */       return localDerOutputStream3.toByteArray();
/*     */     }
/* 122 */     return localDerOutputStream1.toByteArray();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   final int getType()
/*     */   {
/* 132 */     return this.tokenType;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static String getTokenName(int paramInt)
/*     */   {
/* 142 */     switch (paramInt) {
/*     */     case 0: 
/* 144 */       return "SPNEGO NegTokenInit";
/*     */     case 1: 
/* 146 */       return "SPNEGO NegTokenTarg";
/*     */     }
/* 148 */     return "SPNEGO Mechanism Token";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static NegoResult getNegoResultType(int paramInt)
/*     */   {
/* 159 */     switch (paramInt) {
/*     */     case 0: 
/* 161 */       return NegoResult.ACCEPT_COMPLETE;
/*     */     case 1: 
/* 163 */       return NegoResult.ACCEPT_INCOMPLETE;
/*     */     case 2: 
/* 165 */       return NegoResult.REJECT;
/*     */     }
/*     */     
/* 168 */     return NegoResult.ACCEPT_COMPLETE;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static String getNegoResultString(int paramInt)
/*     */   {
/* 179 */     switch (paramInt) {
/*     */     case 0: 
/* 181 */       return "Accept Complete";
/*     */     case 1: 
/* 183 */       return "Accept InComplete";
/*     */     case 2: 
/* 185 */       return "Reject";
/*     */     }
/* 187 */     return "Unknown Negotiated Result: " + paramInt;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static int checkNextField(int paramInt1, int paramInt2)
/*     */     throws GSSException
/*     */   {
/* 200 */     if (paramInt1 < paramInt2) {
/* 201 */       return paramInt2;
/*     */     }
/* 203 */     throw new GSSException(10, -1, "Invalid SpNegoToken token : wrong order");
/*     */   }
/*     */   
/*     */   abstract byte[] encode()
/*     */     throws GSSException;
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\jgss\spnego\SpNegoToken.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */