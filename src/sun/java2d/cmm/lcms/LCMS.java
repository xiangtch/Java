/*     */ package sun.java2d.cmm.lcms;
/*     */ 
/*     */ import java.awt.color.CMMException;
/*     */ import java.awt.color.ICC_Profile;
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
/*     */ public class LCMS
/*     */   implements PCMM
/*     */ {
/*     */   public Profile loadProfile(byte[] paramArrayOfByte)
/*     */   {
/*  40 */     Object localObject = new Object();
/*     */     
/*  42 */     long l = loadProfileNative(paramArrayOfByte, localObject);
/*     */     
/*  44 */     if (l != 0L) {
/*  45 */       return new LCMSProfile(l, localObject);
/*     */     }
/*  47 */     return null;
/*     */   }
/*     */   
/*     */   private native long loadProfileNative(byte[] paramArrayOfByte, Object paramObject);
/*     */   
/*     */   private LCMSProfile getLcmsProfile(Profile paramProfile) {
/*  53 */     if ((paramProfile instanceof LCMSProfile)) {
/*  54 */       return (LCMSProfile)paramProfile;
/*     */     }
/*  56 */     throw new CMMException("Invalid profile: " + paramProfile);
/*     */   }
/*     */   
/*     */   public void freeProfile(Profile paramProfile) {}
/*     */   
/*     */   /* Error */
/*     */   public int getProfileSize(Profile paramProfile)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_1
/*     */     //   1: dup
/*     */     //   2: astore_2
/*     */     //   3: monitorenter
/*     */     //   4: aload_0
/*     */     //   5: aload_0
/*     */     //   6: aload_1
/*     */     //   7: invokespecial 144	sun/java2d/cmm/lcms/LCMS:getLcmsProfile	(Lsun/java2d/cmm/Profile;)Lsun/java2d/cmm/lcms/LCMSProfile;
/*     */     //   10: invokevirtual 147	sun/java2d/cmm/lcms/LCMSProfile:getLcmsPtr	()J
/*     */     //   13: invokespecial 139	sun/java2d/cmm/lcms/LCMS:getProfileSizeNative	(J)I
/*     */     //   16: aload_2
/*     */     //   17: monitorexit
/*     */     //   18: ireturn
/*     */     //   19: astore_3
/*     */     //   20: aload_2
/*     */     //   21: monitorexit
/*     */     //   22: aload_3
/*     */     //   23: athrow
/*     */     // Line number table:
/*     */     //   Java source line #67	-> byte code offset #0
/*     */     //   Java source line #68	-> byte code offset #4
/*     */     //   Java source line #69	-> byte code offset #19
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	24	0	this	LCMS
/*     */     //   0	24	1	paramProfile	Profile
/*     */     //   2	19	2	Ljava/lang/Object;	Object
/*     */     //   19	4	3	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   4	18	19	finally
/*     */     //   19	22	19	finally
/*     */   }
/*     */   
/*     */   private native int getProfileSizeNative(long paramLong);
/*     */   
/*     */   public void getProfileData(Profile paramProfile, byte[] paramArrayOfByte)
/*     */   {
/*  76 */     synchronized (paramProfile) {
/*  77 */       getProfileDataNative(getLcmsProfile(paramProfile).getLcmsPtr(), paramArrayOfByte);
/*     */     }
/*     */   }
/*     */   
/*     */   private native void getProfileDataNative(long paramLong, byte[] paramArrayOfByte);
/*     */   
/*     */   public int getTagSize(Profile paramProfile, int paramInt)
/*     */   {
/*  85 */     LCMSProfile localLCMSProfile = getLcmsProfile(paramProfile);
/*     */     
/*  87 */     synchronized (localLCMSProfile) {
/*  88 */       LCMSProfile.TagData localTagData = localLCMSProfile.getTag(paramInt);
/*  89 */       return localTagData == null ? 0 : localTagData.getSize();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   static native byte[] getTagNative(long paramLong, int paramInt);
/*     */   
/*     */   public void getTagData(Profile paramProfile, int paramInt, byte[] paramArrayOfByte)
/*     */   {
/*  98 */     LCMSProfile localLCMSProfile = getLcmsProfile(paramProfile);
/*     */     
/* 100 */     synchronized (localLCMSProfile) {
/* 101 */       LCMSProfile.TagData localTagData = localLCMSProfile.getTag(paramInt);
/* 102 */       if (localTagData != null) {
/* 103 */         localTagData.copyDataTo(paramArrayOfByte);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public synchronized void setTagData(Profile paramProfile, int paramInt, byte[] paramArrayOfByte)
/*     */   {
/* 110 */     LCMSProfile localLCMSProfile = getLcmsProfile(paramProfile);
/*     */     
/* 112 */     synchronized (localLCMSProfile) {
/* 113 */       localLCMSProfile.clearTagCache();
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 121 */       setTagDataNative(localLCMSProfile.getLcmsPtr(), paramInt, paramArrayOfByte);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private native void setTagDataNative(long paramLong, int paramInt, byte[] paramArrayOfByte);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static synchronized native LCMSProfile getProfileID(ICC_Profile paramICC_Profile);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static long createTransform(LCMSProfile[] paramArrayOfLCMSProfile, int paramInt1, int paramInt2, boolean paramBoolean1, int paramInt3, boolean paramBoolean2, Object paramObject)
/*     */   {
/* 148 */     long[] arrayOfLong = new long[paramArrayOfLCMSProfile.length];
/*     */     
/* 150 */     for (int i = 0; i < paramArrayOfLCMSProfile.length; i++) {
/* 151 */       if (paramArrayOfLCMSProfile[i] == null) { throw new CMMException("Unknown profile ID");
/*     */       }
/* 153 */       arrayOfLong[i] = paramArrayOfLCMSProfile[i].getLcmsPtr();
/*     */     }
/*     */     
/* 156 */     return createNativeTransform(arrayOfLong, paramInt1, paramInt2, paramBoolean1, paramInt3, paramBoolean2, paramObject);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static native long createNativeTransform(long[] paramArrayOfLong, int paramInt1, int paramInt2, boolean paramBoolean1, int paramInt3, boolean paramBoolean2, Object paramObject);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ColorTransform createTransform(ICC_Profile paramICC_Profile, int paramInt1, int paramInt2)
/*     */   {
/* 173 */     return new LCMSTransform(paramICC_Profile, paramInt1, paramInt1);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized ColorTransform createTransform(ColorTransform[] paramArrayOfColorTransform)
/*     */   {
/* 183 */     return new LCMSTransform(paramArrayOfColorTransform);
/*     */   }
/*     */   
/*     */ 
/*     */   public static native void colorConvert(LCMSTransform paramLCMSTransform, LCMSImageLayout paramLCMSImageLayout1, LCMSImageLayout paramLCMSImageLayout2);
/*     */   
/*     */ 
/*     */   public static native void freeTransform(long paramLong);
/*     */   
/*     */ 
/*     */   public static native void initLCMS(Class paramClass1, Class paramClass2, Class paramClass3);
/*     */   
/*     */ 
/* 196 */   private static LCMS theLcms = null;
/*     */   
/*     */   static synchronized PCMM getModule() {
/* 199 */     if (theLcms != null) {
/* 200 */       return theLcms;
/*     */     }
/*     */     
/* 203 */     AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */ 
/*     */       public Object run()
/*     */       {
/*     */ 
/* 209 */         System.loadLibrary("awt");
/* 210 */         System.loadLibrary("lcms");
/* 211 */         return null;
/*     */       }
/*     */       
/* 214 */     });
/* 215 */     initLCMS(LCMSTransform.class, LCMSImageLayout.class, ICC_Profile.class);
/*     */     
/* 217 */     theLcms = new LCMS();
/*     */     
/* 219 */     return theLcms;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\java2d\cmm\lcms\LCMS.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */