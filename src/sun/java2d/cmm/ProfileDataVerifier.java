/*     */ package sun.java2d.cmm;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ProfileDataVerifier
/*     */ {
/*     */   private static final int MAX_TAG_COUNT = 100;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final int HEADER_SIZE = 128;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final int TOC_OFFSET = 132;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final int TOC_RECORD_SIZE = 12;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final int PROFILE_FILE_SIGNATURE = 1633907568;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void verify(byte[] paramArrayOfByte)
/*     */   {
/*  36 */     if (paramArrayOfByte == null) {
/*  37 */       throw new IllegalArgumentException("Invalid ICC Profile Data");
/*     */     }
/*     */     
/*  40 */     if (paramArrayOfByte.length < 132)
/*     */     {
/*  42 */       throw new IllegalArgumentException("Invalid ICC Profile Data");
/*     */     }
/*     */     
/*     */ 
/*  46 */     int i = readInt32(paramArrayOfByte, 0);
/*  47 */     int j = readInt32(paramArrayOfByte, 128);
/*     */     
/*  49 */     if ((j < 0) || (j > 100)) {
/*  50 */       throw new IllegalArgumentException("Invalid ICC Profile Data");
/*     */     }
/*     */     
/*  53 */     if ((i < 132 + j * 12) || (i > paramArrayOfByte.length))
/*     */     {
/*     */ 
/*  56 */       throw new IllegalArgumentException("Invalid ICC Profile Data");
/*     */     }
/*     */     
/*  59 */     int k = readInt32(paramArrayOfByte, 36);
/*     */     
/*  61 */     if (1633907568 != k) {
/*  62 */       throw new IllegalArgumentException("Invalid ICC Profile Data");
/*     */     }
/*     */     
/*     */ 
/*  66 */     for (int m = 0; m < j; m++) {
/*  67 */       int n = getTagOffset(m, paramArrayOfByte);
/*  68 */       int i1 = getTagSize(m, paramArrayOfByte);
/*     */       
/*  70 */       if ((n < 132) || (n > i)) {
/*  71 */         throw new IllegalArgumentException("Invalid ICC Profile Data");
/*     */       }
/*     */       
/*  74 */       if ((i1 < 0) || (i1 > Integer.MAX_VALUE - n) || (i1 + n > i))
/*     */       {
/*     */ 
/*     */ 
/*  78 */         throw new IllegalArgumentException("Invalid ICC Profile Data");
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private static int getTagOffset(int paramInt, byte[] paramArrayOfByte) {
/*  84 */     int i = 132 + paramInt * 12 + 4;
/*  85 */     return readInt32(paramArrayOfByte, i);
/*     */   }
/*     */   
/*     */   private static int getTagSize(int paramInt, byte[] paramArrayOfByte) {
/*  89 */     int i = 132 + paramInt * 12 + 8;
/*  90 */     return readInt32(paramArrayOfByte, i);
/*     */   }
/*     */   
/*     */   private static int readInt32(byte[] paramArrayOfByte, int paramInt) {
/*  94 */     int i = 0;
/*  95 */     for (int j = 0; j < 4; j++) {
/*  96 */       i <<= 8;
/*     */       
/*  98 */       i |= 0xFF & paramArrayOfByte[(paramInt++)];
/*     */     }
/* 100 */     return i;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\java2d\cmm\ProfileDataVerifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */