/*     */ package sun.java2d.cmm;
/*     */ 
/*     */ import java.awt.color.CMMException;
/*     */ import java.awt.color.ColorSpace;
/*     */ import java.awt.color.ICC_Profile;
/*     */ import java.io.PrintStream;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.ServiceLoader;
/*     */ import sun.security.action.GetPropertyAction;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CMSManager
/*     */ {
/*     */   public static ColorSpace GRAYspace;
/*     */   public static ColorSpace LINEAR_RGBspace;
/*  48 */   private static PCMM cmmImpl = null;
/*     */   
/*     */   public static synchronized PCMM getModule() {
/*  51 */     if (cmmImpl != null) {
/*  52 */       return cmmImpl;
/*     */     }
/*     */     
/*  55 */     CMMServiceProvider localCMMServiceProvider = (CMMServiceProvider)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public CMMServiceProvider run() {
/*  58 */         String str = System.getProperty("sun.java2d.cmm", "sun.java2d.cmm.lcms.LcmsServiceProvider");
/*     */         
/*     */ 
/*     */ 
/*  62 */         ServiceLoader localServiceLoader = ServiceLoader.loadInstalled(CMMServiceProvider.class);
/*     */         
/*  64 */         Object localObject = null;
/*     */         
/*  66 */         for (CMMServiceProvider localCMMServiceProvider : localServiceLoader) {
/*  67 */           localObject = localCMMServiceProvider;
/*  68 */           if (localCMMServiceProvider.getClass().getName().equals(str)) {
/*     */             break;
/*     */           }
/*     */         }
/*  72 */         return (CMMServiceProvider)localObject;
/*     */       }
/*     */       
/*  75 */     });
/*  76 */     cmmImpl = localCMMServiceProvider.getColorManagementModule();
/*     */     
/*  78 */     if (cmmImpl == null) {
/*  79 */       throw new CMMException("Cannot initialize Color Management System.No CM module found");
/*     */     }
/*     */     
/*     */ 
/*  83 */     GetPropertyAction localGetPropertyAction = new GetPropertyAction("sun.java2d.cmm.trace");
/*  84 */     String str = (String)AccessController.doPrivileged(localGetPropertyAction);
/*  85 */     if (str != null) {
/*  86 */       cmmImpl = new CMMTracer(cmmImpl);
/*     */     }
/*     */     
/*  89 */     return cmmImpl;
/*     */   }
/*     */   
/*     */   static synchronized boolean canCreateModule() {
/*  93 */     return cmmImpl == null;
/*     */   }
/*     */   
/*     */   public static class CMMTracer implements PCMM
/*     */   {
/*     */     PCMM tcmm;
/*     */     String cName;
/*     */     
/*     */     public CMMTracer(PCMM paramPCMM)
/*     */     {
/* 103 */       this.tcmm = paramPCMM;
/* 104 */       this.cName = paramPCMM.getClass().getName();
/*     */     }
/*     */     
/*     */     public Profile loadProfile(byte[] paramArrayOfByte) {
/* 108 */       System.err.print(this.cName + ".loadProfile");
/* 109 */       Profile localProfile = this.tcmm.loadProfile(paramArrayOfByte);
/* 110 */       System.err.printf("(ID=%s)\n", new Object[] { localProfile.toString() });
/* 111 */       return localProfile;
/*     */     }
/*     */     
/*     */     public void freeProfile(Profile paramProfile) {
/* 115 */       System.err.printf(this.cName + ".freeProfile(ID=%s)\n", new Object[] { paramProfile.toString() });
/* 116 */       this.tcmm.freeProfile(paramProfile);
/*     */     }
/*     */     
/*     */     public int getProfileSize(Profile paramProfile) {
/* 120 */       System.err.print(this.cName + ".getProfileSize(ID=" + paramProfile + ")");
/* 121 */       int i = this.tcmm.getProfileSize(paramProfile);
/* 122 */       System.err.println("=" + i);
/* 123 */       return i;
/*     */     }
/*     */     
/*     */     public void getProfileData(Profile paramProfile, byte[] paramArrayOfByte) {
/* 127 */       System.err.print(this.cName + ".getProfileData(ID=" + paramProfile + ") ");
/* 128 */       System.err.println("requested " + paramArrayOfByte.length + " byte(s)");
/* 129 */       this.tcmm.getProfileData(paramProfile, paramArrayOfByte);
/*     */     }
/*     */     
/*     */     public int getTagSize(Profile paramProfile, int paramInt) {
/* 133 */       System.err.printf(this.cName + ".getTagSize(ID=%x, TagSig=%s)", new Object[] { paramProfile, 
/* 134 */         signatureToString(paramInt) });
/* 135 */       int i = this.tcmm.getTagSize(paramProfile, paramInt);
/* 136 */       System.err.println("=" + i);
/* 137 */       return i;
/*     */     }
/*     */     
/*     */     public void getTagData(Profile paramProfile, int paramInt, byte[] paramArrayOfByte)
/*     */     {
/* 142 */       System.err.printf(this.cName + ".getTagData(ID=%x, TagSig=%s)", new Object[] { paramProfile, 
/* 143 */         signatureToString(paramInt) });
/* 144 */       System.err.println(" requested " + paramArrayOfByte.length + " byte(s)");
/* 145 */       this.tcmm.getTagData(paramProfile, paramInt, paramArrayOfByte);
/*     */     }
/*     */     
/*     */     public void setTagData(Profile paramProfile, int paramInt, byte[] paramArrayOfByte)
/*     */     {
/* 150 */       System.err.print(this.cName + ".setTagData(ID=" + paramProfile + ", TagSig=" + paramInt + ")");
/*     */       
/* 152 */       System.err.println(" sending " + paramArrayOfByte.length + " byte(s)");
/* 153 */       this.tcmm.setTagData(paramProfile, paramInt, paramArrayOfByte);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public ColorTransform createTransform(ICC_Profile paramICC_Profile, int paramInt1, int paramInt2)
/*     */     {
/* 160 */       System.err.println(this.cName + ".createTransform(ICC_Profile,int,int)");
/* 161 */       return this.tcmm.createTransform(paramICC_Profile, paramInt1, paramInt2);
/*     */     }
/*     */     
/*     */     public ColorTransform createTransform(ColorTransform[] paramArrayOfColorTransform) {
/* 165 */       System.err.println(this.cName + ".createTransform(ColorTransform[])");
/* 166 */       return this.tcmm.createTransform(paramArrayOfColorTransform);
/*     */     }
/*     */     
/*     */     private static String signatureToString(int paramInt) {
/* 170 */       return String.format("%c%c%c%c", new Object[] {
/* 171 */         Character.valueOf((char)(0xFF & paramInt >> 24)), 
/* 172 */         Character.valueOf((char)(0xFF & paramInt >> 16)), 
/* 173 */         Character.valueOf((char)(0xFF & paramInt >> 8)), 
/* 174 */         Character.valueOf((char)(0xFF & paramInt)) });
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\java2d\cmm\CMSManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */