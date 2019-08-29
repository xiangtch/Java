/*     */ package sun.java2d.cmm.kcms;
/*     */ 
/*     */ import java.awt.color.CMMException;
/*     */ import java.awt.color.ICC_Profile;
/*     */ import java.awt.color.ProfileDataException;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import sun.java2d.cmm.ColorTransform;
/*     */ import sun.java2d.cmm.PCMM;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CMM
/*     */   implements PCMM
/*     */ {
/*  44 */   private static long ID = 0L;
/*     */   
/*     */   static final int cmmStatSuccess = 0;
/*     */   
/*     */   static final int cmmStatBadProfile = 503;
/*     */   
/*     */   static final int cmmStatBadTagData = 504;
/*     */   
/*     */   static final int cmmStatBadTagType = 505;
/*     */   
/*     */   static final int cmmStatBadTagId = 506;
/*     */   static final int cmmStatBadXform = 507;
/*     */   static final int cmmStatXformNotActive = 508;
/*     */   static final int cmmStatOutOfRange = 518;
/*     */   static final int cmmStatTagNotFound = 519;
/*     */   
/*     */   static native int cmmLoadProfile(byte[] paramArrayOfByte, long[] paramArrayOfLong);
/*     */   
/*     */   static native int cmmFreeProfile(long paramLong);
/*     */   
/*     */   static native int cmmGetProfileSize(long paramLong, int[] paramArrayOfInt);
/*     */   
/*     */   static native int cmmGetProfileData(long paramLong, byte[] paramArrayOfByte);
/*     */   
/*     */   static native int cmmGetTagSize(long paramLong, int paramInt, int[] paramArrayOfInt);
/*     */   
/*     */   static native int cmmGetTagData(long paramLong, int paramInt, byte[] paramArrayOfByte);
/*     */   
/*     */   static native int cmmSetTagData(long paramLong, int paramInt, byte[] paramArrayOfByte);
/*     */   
/*     */   static native int cmmGetTransform(ICC_Profile paramICC_Profile, int paramInt1, int paramInt2, ICC_Transform paramICC_Transform);
/*     */   
/*     */   static native int cmmCombineTransforms(ICC_Transform[] paramArrayOfICC_Transform, ICC_Transform paramICC_Transform);
/*     */   
/*     */   static native int cmmFreeTransform(long paramLong);
/*     */   
/*     */   static native int cmmGetNumComponents(long paramLong, int[] paramArrayOfInt);
/*     */   
/*     */   static native int cmmColorConvert(long paramLong, CMMImageLayout paramCMMImageLayout1, CMMImageLayout paramCMMImageLayout2);
/*     */   
/*     */   private long getKcmsPtr(Profile paramProfile)
/*     */   {
/*  86 */     if ((paramProfile instanceof KcmsProfile)) {
/*  87 */       return ((KcmsProfile)paramProfile).getKcmsPtr();
/*     */     }
/*  89 */     throw new CMMException("Invalid profile");
/*     */   }
/*     */   
/*     */   public Profile loadProfile(byte[] paramArrayOfByte)
/*     */   {
/*  94 */     long[] arrayOfLong = new long[1];
/*  95 */     checkStatus(cmmLoadProfile(paramArrayOfByte, arrayOfLong));
/*     */     
/*  97 */     if (arrayOfLong[0] != 0L) {
/*  98 */       return new KcmsProfile(arrayOfLong[0]);
/*     */     }
/* 100 */     return null;
/*     */   }
/*     */   
/*     */   public void freeProfile(Profile paramProfile) {
/* 104 */     checkStatus(cmmFreeProfile(getKcmsPtr(paramProfile)));
/*     */   }
/*     */   
/*     */   public int getProfileSize(Profile paramProfile) {
/* 108 */     int[] arrayOfInt = new int[1];
/* 109 */     checkStatus(cmmGetProfileSize(getKcmsPtr(paramProfile), arrayOfInt));
/* 110 */     return arrayOfInt[0];
/*     */   }
/*     */   
/*     */   public void getProfileData(Profile paramProfile, byte[] paramArrayOfByte) {
/* 114 */     checkStatus(cmmGetProfileData(getKcmsPtr(paramProfile), paramArrayOfByte));
/*     */   }
/*     */   
/*     */   public int getTagSize(Profile paramProfile, int paramInt) {
/* 118 */     int[] arrayOfInt = new int[1];
/* 119 */     checkStatus(cmmGetTagSize(getKcmsPtr(paramProfile), paramInt, arrayOfInt));
/* 120 */     return arrayOfInt[0];
/*     */   }
/*     */   
/*     */   public void getTagData(Profile paramProfile, int paramInt, byte[] paramArrayOfByte) {
/* 124 */     checkStatus(cmmGetTagData(getKcmsPtr(paramProfile), paramInt, paramArrayOfByte));
/*     */   }
/*     */   
/*     */   public void setTagData(Profile paramProfile, int paramInt, byte[] paramArrayOfByte) {
/* 128 */     int i = cmmSetTagData(getKcmsPtr(paramProfile), paramInt, paramArrayOfByte);
/*     */     
/* 130 */     switch (i) {
/*     */     case 504: 
/*     */     case 505: 
/*     */     case 519: 
/* 134 */       throw new IllegalArgumentException("Can not write tag data.");
/*     */     }
/* 136 */     checkStatus(i);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ColorTransform createTransform(ICC_Profile paramICC_Profile, int paramInt1, int paramInt2)
/*     */   {
/* 145 */     ICC_Transform localICC_Transform = new ICC_Transform();
/* 146 */     checkStatus(cmmGetTransform(paramICC_Profile, paramInt1, paramInt2, localICC_Transform));
/* 147 */     return localICC_Transform;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public ColorTransform createTransform(ColorTransform[] paramArrayOfColorTransform)
/*     */   {
/* 154 */     ICC_Transform localICC_Transform = new ICC_Transform();
/* 155 */     ICC_Transform[] arrayOfICC_Transform = new ICC_Transform[paramArrayOfColorTransform.length];
/* 156 */     for (int i = 0; i < paramArrayOfColorTransform.length; i++) {
/* 157 */       arrayOfICC_Transform[i] = ((ICC_Transform)paramArrayOfColorTransform[i]);
/*     */     }
/* 159 */     i = cmmCombineTransforms(arrayOfICC_Transform, localICC_Transform);
/* 160 */     if ((i != 0) || (localICC_Transform.getID() == 0L)) {
/* 161 */       throw new ProfileDataException("Invalid profile sequence");
/*     */     }
/* 163 */     return localICC_Transform;
/*     */   }
/*     */   
/*     */   static native int cmmInit();
/*     */   
/*     */   static native int cmmTerminate();
/*     */   
/* 170 */   private static CMM theKcms = null;
/*     */   
/*     */   static synchronized PCMM getModule() {
/* 173 */     if (theKcms != null) {
/* 174 */       return theKcms;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 179 */     AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Void run() {
/* 182 */         System.loadLibrary("kcms");
/* 183 */         return null;
/*     */       }
/* 185 */     });
/* 186 */     int i = cmmInit();
/*     */     
/* 188 */     checkStatus(i);
/*     */     
/* 190 */     theKcms = new CMM();
/*     */     
/* 192 */     return theKcms;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void finalize()
/*     */   {
/* 201 */     checkStatus(cmmTerminate());
/*     */   }
/*     */   
/*     */   public static void checkStatus(int paramInt)
/*     */   {
/* 206 */     if (paramInt != 0) {
/* 207 */       throw new CMMException(errorString(paramInt));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   static String errorString(int paramInt)
/*     */   {
/* 214 */     switch (paramInt) {
/*     */     case 0: 
/* 216 */       return "Success";
/*     */     
/*     */     case 519: 
/* 219 */       return "No such tag";
/*     */     
/*     */     case 503: 
/* 222 */       return "Invalid profile data";
/*     */     
/*     */     case 504: 
/* 225 */       return "Invalid tag data";
/*     */     
/*     */     case 505: 
/* 228 */       return "Invalid tag type";
/*     */     
/*     */     case 506: 
/* 231 */       return "Invalid tag signature";
/*     */     
/*     */     case 507: 
/* 234 */       return "Invlaid transform";
/*     */     
/*     */     case 508: 
/* 237 */       return "Transform is not active";
/*     */     
/*     */     case 518: 
/* 240 */       return "Invalid image format";
/*     */     }
/*     */     
/* 243 */     return "General CMM error" + paramInt;
/*     */   }
/*     */   
/*     */   final class KcmsProfile extends Profile {
/*     */     KcmsProfile(long paramLong) {
/* 248 */       super();
/*     */     }
/*     */     
/*     */     long getKcmsPtr() {
/* 252 */       return getNativePtr();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\java2d\cmm\kcms\CMM.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */