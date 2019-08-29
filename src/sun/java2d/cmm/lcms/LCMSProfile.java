/*     */ package sun.java2d.cmm.lcms;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import sun.java2d.cmm.Profile;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class LCMSProfile
/*     */   extends Profile
/*     */ {
/*     */   private final TagCache tagCache;
/*     */   private final Object disposerReferent;
/*     */   
/*     */   LCMSProfile(long paramLong, Object paramObject)
/*     */   {
/*  39 */     super(paramLong);
/*     */     
/*  41 */     this.disposerReferent = paramObject;
/*     */     
/*  43 */     this.tagCache = new TagCache(this);
/*     */   }
/*     */   
/*     */   final long getLcmsPtr() {
/*  47 */     return getNativePtr();
/*     */   }
/*     */   
/*     */   TagData getTag(int paramInt) {
/*  51 */     return this.tagCache.getTag(paramInt);
/*     */   }
/*     */   
/*     */   void clearTagCache() {
/*  55 */     this.tagCache.clear();
/*     */   }
/*     */   
/*     */   static class TagCache {
/*     */     final LCMSProfile profile;
/*     */     private HashMap<Integer, TagData> tags;
/*     */     
/*     */     TagCache(LCMSProfile paramLCMSProfile) {
/*  63 */       this.profile = paramLCMSProfile;
/*  64 */       this.tags = new HashMap();
/*     */     }
/*     */     
/*     */     TagData getTag(int paramInt) {
/*  68 */       TagData localTagData = (TagData)this.tags.get(Integer.valueOf(paramInt));
/*  69 */       if (localTagData == null) {
/*  70 */         byte[] arrayOfByte = LCMS.getTagNative(this.profile.getNativePtr(), paramInt);
/*  71 */         if (arrayOfByte != null) {
/*  72 */           localTagData = new TagData(paramInt, arrayOfByte);
/*  73 */           this.tags.put(Integer.valueOf(paramInt), localTagData);
/*     */         }
/*     */       }
/*  76 */       return localTagData;
/*     */     }
/*     */     
/*     */     void clear() {
/*  80 */       this.tags.clear();
/*     */     }
/*     */   }
/*     */   
/*     */   static class TagData {
/*     */     private int signature;
/*     */     private byte[] data;
/*     */     
/*     */     TagData(int paramInt, byte[] paramArrayOfByte) {
/*  89 */       this.signature = paramInt;
/*  90 */       this.data = paramArrayOfByte;
/*     */     }
/*     */     
/*     */     int getSize() {
/*  94 */       return this.data.length;
/*     */     }
/*     */     
/*     */     byte[] getData() {
/*  98 */       return Arrays.copyOf(this.data, this.data.length);
/*     */     }
/*     */     
/*     */     void copyDataTo(byte[] paramArrayOfByte) {
/* 102 */       System.arraycopy(this.data, 0, paramArrayOfByte, 0, this.data.length);
/*     */     }
/*     */     
/*     */     int getSignature() {
/* 106 */       return this.signature;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\java2d\cmm\lcms\LCMSProfile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */