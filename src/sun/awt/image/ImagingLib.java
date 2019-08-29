/*     */ package sun.awt.image;
/*     */ 
/*     */ import java.awt.geom.AffineTransform;
/*     */ import java.awt.image.AffineTransformOp;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.BufferedImageOp;
/*     */ import java.awt.image.ByteLookupTable;
/*     */ import java.awt.image.ConvolveOp;
/*     */ import java.awt.image.Kernel;
/*     */ import java.awt.image.LookupOp;
/*     */ import java.awt.image.LookupTable;
/*     */ import java.awt.image.Raster;
/*     */ import java.awt.image.RasterOp;
/*     */ import java.awt.image.WritableRaster;
/*     */ import java.io.PrintStream;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ImagingLib
/*     */ {
/*  56 */   static boolean useLib = true;
/*  57 */   static boolean verbose = false;
/*     */   
/*     */   private static final int NUM_NATIVE_OPS = 3;
/*     */   
/*     */   private static final int LOOKUP_OP = 0;
/*     */   private static final int AFFINE_OP = 1;
/*     */   private static final int CONVOLVE_OP = 2;
/*  64 */   private static Class[] nativeOpClass = new Class[3];
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static
/*     */   {
/*  92 */     PrivilegedAction local1 = new PrivilegedAction()
/*     */     {
/*     */       public Boolean run() {
/*  95 */         String str = System.getProperty("os.arch");
/*     */         
/*  97 */         if ((str == null) || (!str.startsWith("sparc"))) {
/*     */           try {
/*  99 */             System.loadLibrary("mlib_image");
/*     */           } catch (UnsatisfiedLinkError localUnsatisfiedLinkError) {
/* 101 */             return Boolean.FALSE;
/*     */           }
/*     */         }
/*     */         
/* 105 */         boolean bool = ImagingLib.access$000();
/* 106 */         return Boolean.valueOf(bool);
/*     */       }
/*     */       
/* 109 */     };
/* 110 */     useLib = ((Boolean)AccessController.doPrivileged(local1)).booleanValue();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     try
/*     */     {
/* 118 */       nativeOpClass[0] = Class.forName("java.awt.image.LookupOp");
/*     */     } catch (ClassNotFoundException localClassNotFoundException1) {
/* 120 */       System.err.println("Could not find class: " + localClassNotFoundException1);
/*     */     }
/*     */     try
/*     */     {
/* 124 */       nativeOpClass[1] = Class.forName("java.awt.image.AffineTransformOp");
/*     */     } catch (ClassNotFoundException localClassNotFoundException2) {
/* 126 */       System.err.println("Could not find class: " + localClassNotFoundException2);
/*     */     }
/*     */     try
/*     */     {
/* 130 */       nativeOpClass[2] = Class.forName("java.awt.image.ConvolveOp");
/*     */     } catch (ClassNotFoundException localClassNotFoundException3) {
/* 132 */       System.err.println("Could not find class: " + localClassNotFoundException3);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static int getNativeOpIndex(Class paramClass)
/*     */   {
/* 142 */     int i = -1;
/* 143 */     for (int j = 0; j < 3; j++) {
/* 144 */       if (paramClass == nativeOpClass[j]) {
/* 145 */         i = j;
/* 146 */         break;
/*     */       }
/*     */     }
/* 149 */     return i;
/*     */   }
/*     */   
/*     */ 
/*     */   public static WritableRaster filter(RasterOp paramRasterOp, Raster paramRaster, WritableRaster paramWritableRaster)
/*     */   {
/* 155 */     if (!useLib) {
/* 156 */       return null;
/*     */     }
/*     */     
/*     */ 
/* 160 */     if (paramWritableRaster == null) {
/* 161 */       paramWritableRaster = paramRasterOp.createCompatibleDestRaster(paramRaster);
/*     */     }
/*     */     
/*     */ 
/* 165 */     WritableRaster localWritableRaster = null;
/* 166 */     Object localObject; switch (getNativeOpIndex(paramRasterOp.getClass()))
/*     */     {
/*     */ 
/*     */     case 0: 
/* 170 */       LookupTable localLookupTable = ((LookupOp)paramRasterOp).getTable();
/* 171 */       if (localLookupTable.getOffset() != 0)
/*     */       {
/* 173 */         return null;
/*     */       }
/* 175 */       if ((localLookupTable instanceof ByteLookupTable)) {
/* 176 */         localObject = (ByteLookupTable)localLookupTable;
/* 177 */         if (lookupByteRaster(paramRaster, paramWritableRaster, ((ByteLookupTable)localObject).getTable()) > 0)
/* 178 */           localWritableRaster = paramWritableRaster;
/*     */       }
/* 180 */       break;
/*     */     
/*     */ 
/*     */     case 1: 
/* 184 */       localObject = (AffineTransformOp)paramRasterOp;
/* 185 */       double[] arrayOfDouble = new double[6];
/* 186 */       ((AffineTransformOp)localObject).getTransform().getMatrix(arrayOfDouble);
/* 187 */       if (transformRaster(paramRaster, paramWritableRaster, arrayOfDouble, ((AffineTransformOp)localObject)
/* 188 */         .getInterpolationType()) > 0) {
/* 189 */         localWritableRaster = paramWritableRaster;
/*     */       }
/*     */       
/*     */       break;
/*     */     case 2: 
/* 194 */       ConvolveOp localConvolveOp = (ConvolveOp)paramRasterOp;
/* 195 */       if (convolveRaster(paramRaster, paramWritableRaster, localConvolveOp
/* 196 */         .getKernel(), localConvolveOp.getEdgeCondition()) > 0) {
/* 197 */         localWritableRaster = paramWritableRaster;
/*     */       }
/*     */       
/*     */ 
/*     */       break;
/*     */     }
/*     */     
/*     */     
/* 205 */     if (localWritableRaster != null) {
/* 206 */       SunWritableRaster.markDirty(localWritableRaster);
/*     */     }
/*     */     
/* 209 */     return localWritableRaster;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static BufferedImage filter(BufferedImageOp paramBufferedImageOp, BufferedImage paramBufferedImage1, BufferedImage paramBufferedImage2)
/*     */   {
/* 216 */     if (verbose) {
/* 217 */       System.out.println("in filter and op is " + paramBufferedImageOp + "bufimage is " + paramBufferedImage1 + " and " + paramBufferedImage2);
/*     */     }
/*     */     
/*     */ 
/* 221 */     if (!useLib) {
/* 222 */       return null;
/*     */     }
/*     */     
/*     */ 
/* 226 */     if (paramBufferedImage2 == null) {
/* 227 */       paramBufferedImage2 = paramBufferedImageOp.createCompatibleDestImage(paramBufferedImage1, null);
/*     */     }
/*     */     
/* 230 */     BufferedImage localBufferedImage = null;
/* 231 */     Object localObject; switch (getNativeOpIndex(paramBufferedImageOp.getClass()))
/*     */     {
/*     */ 
/*     */     case 0: 
/* 235 */       LookupTable localLookupTable = ((LookupOp)paramBufferedImageOp).getTable();
/* 236 */       if (localLookupTable.getOffset() != 0)
/*     */       {
/* 238 */         return null;
/*     */       }
/* 240 */       if ((localLookupTable instanceof ByteLookupTable)) {
/* 241 */         localObject = (ByteLookupTable)localLookupTable;
/* 242 */         if (lookupByteBI(paramBufferedImage1, paramBufferedImage2, ((ByteLookupTable)localObject).getTable()) > 0)
/* 243 */           localBufferedImage = paramBufferedImage2;
/*     */       }
/* 245 */       break;
/*     */     
/*     */ 
/*     */     case 1: 
/* 249 */       localObject = (AffineTransformOp)paramBufferedImageOp;
/* 250 */       double[] arrayOfDouble = new double[6];
/* 251 */       AffineTransform localAffineTransform = ((AffineTransformOp)localObject).getTransform();
/* 252 */       ((AffineTransformOp)localObject).getTransform().getMatrix(arrayOfDouble);
/*     */       
/* 254 */       if (transformBI(paramBufferedImage1, paramBufferedImage2, arrayOfDouble, ((AffineTransformOp)localObject)
/* 255 */         .getInterpolationType()) > 0) {
/* 256 */         localBufferedImage = paramBufferedImage2;
/*     */       }
/*     */       
/*     */       break;
/*     */     case 2: 
/* 261 */       ConvolveOp localConvolveOp = (ConvolveOp)paramBufferedImageOp;
/* 262 */       if (convolveBI(paramBufferedImage1, paramBufferedImage2, localConvolveOp.getKernel(), localConvolveOp
/* 263 */         .getEdgeCondition()) > 0) {
/* 264 */         localBufferedImage = paramBufferedImage2;
/*     */       }
/*     */       
/*     */ 
/*     */       break;
/*     */     }
/*     */     
/*     */     
/* 272 */     if (localBufferedImage != null) {
/* 273 */       SunWritableRaster.markDirty(localBufferedImage);
/*     */     }
/*     */     
/* 276 */     return localBufferedImage;
/*     */   }
/*     */   
/*     */   private static native boolean init();
/*     */   
/*     */   public static native int transformBI(BufferedImage paramBufferedImage1, BufferedImage paramBufferedImage2, double[] paramArrayOfDouble, int paramInt);
/*     */   
/*     */   public static native int transformRaster(Raster paramRaster1, Raster paramRaster2, double[] paramArrayOfDouble, int paramInt);
/*     */   
/*     */   public static native int convolveBI(BufferedImage paramBufferedImage1, BufferedImage paramBufferedImage2, Kernel paramKernel, int paramInt);
/*     */   
/*     */   public static native int convolveRaster(Raster paramRaster1, Raster paramRaster2, Kernel paramKernel, int paramInt);
/*     */   
/*     */   public static native int lookupByteBI(BufferedImage paramBufferedImage1, BufferedImage paramBufferedImage2, byte[][] paramArrayOfByte);
/*     */   
/*     */   public static native int lookupByteRaster(Raster paramRaster1, Raster paramRaster2, byte[][] paramArrayOfByte);
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\image\ImagingLib.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */