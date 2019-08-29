/*     */ package sun.awt.image;
/*     */ 
/*     */ import java.awt.GraphicsConfiguration;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.ColorModel;
/*     */ import java.awt.image.DataBuffer;
/*     */ import java.awt.image.DirectColorModel;
/*     */ import java.awt.image.IndexColorModel;
/*     */ import java.awt.image.Raster;
/*     */ import java.awt.image.SampleModel;
/*     */ import java.awt.image.WritableRaster;
/*     */ import sun.java2d.SunGraphics2D;
/*     */ import sun.java2d.SurfaceData;
/*     */ import sun.java2d.loops.CompositeType;
/*     */ import sun.java2d.loops.RenderLoops;
/*     */ import sun.java2d.loops.SurfaceType;
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
/*     */ public class BufImgSurfaceData
/*     */   extends SurfaceData
/*     */ {
/*     */   BufferedImage bufImg;
/*     */   private BufferedImageGraphicsConfig graphicsConfig;
/*     */   RenderLoops solidloops;
/*     */   private static final int DCM_RGBX_RED_MASK = -16777216;
/*     */   private static final int DCM_RGBX_GREEN_MASK = 16711680;
/*     */   private static final int DCM_RGBX_BLUE_MASK = 65280;
/*     */   private static final int DCM_555X_RED_MASK = 63488;
/*     */   private static final int DCM_555X_GREEN_MASK = 1984;
/*     */   private static final int DCM_555X_BLUE_MASK = 62;
/*     */   private static final int DCM_4444_RED_MASK = 3840;
/*     */   private static final int DCM_4444_GREEN_MASK = 240;
/*     */   private static final int DCM_4444_BLUE_MASK = 15;
/*     */   private static final int DCM_4444_ALPHA_MASK = 61440;
/*     */   private static final int DCM_ARGBBM_ALPHA_MASK = 16777216;
/*     */   private static final int DCM_ARGBBM_RED_MASK = 16711680;
/*     */   private static final int DCM_ARGBBM_GREEN_MASK = 65280;
/*     */   private static final int DCM_ARGBBM_BLUE_MASK = 255;
/*     */   private static final int CACHE_SIZE = 5;
/*     */   
/*     */   static
/*     */   {
/*  72 */     initIDs(IndexColorModel.class, ICMColorData.class);
/*     */   }
/*     */   
/*     */   public static SurfaceData createData(BufferedImage paramBufferedImage) {
/*  76 */     if (paramBufferedImage == null) {
/*  77 */       throw new NullPointerException("BufferedImage cannot be null");
/*     */     }
/*     */     
/*  80 */     ColorModel localColorModel = paramBufferedImage.getColorModel();
/*  81 */     int i = paramBufferedImage.getType();
/*     */     Object localObject1;
/*  83 */     Object localObject2; switch (i) {
/*     */     case 4: 
/*  85 */       localObject1 = createDataIC(paramBufferedImage, SurfaceType.IntBgr);
/*  86 */       break;
/*     */     case 1: 
/*  88 */       localObject1 = createDataIC(paramBufferedImage, SurfaceType.IntRgb);
/*  89 */       break;
/*     */     case 2: 
/*  91 */       localObject1 = createDataIC(paramBufferedImage, SurfaceType.IntArgb);
/*  92 */       break;
/*     */     case 3: 
/*  94 */       localObject1 = createDataIC(paramBufferedImage, SurfaceType.IntArgbPre);
/*  95 */       break;
/*     */     case 5: 
/*  97 */       localObject1 = createDataBC(paramBufferedImage, SurfaceType.ThreeByteBgr, 2);
/*  98 */       break;
/*     */     case 6: 
/* 100 */       localObject1 = createDataBC(paramBufferedImage, SurfaceType.FourByteAbgr, 3);
/* 101 */       break;
/*     */     case 7: 
/* 103 */       localObject1 = createDataBC(paramBufferedImage, SurfaceType.FourByteAbgrPre, 3);
/* 104 */       break;
/*     */     case 8: 
/* 106 */       localObject1 = createDataSC(paramBufferedImage, SurfaceType.Ushort565Rgb, null);
/* 107 */       break;
/*     */     case 9: 
/* 109 */       localObject1 = createDataSC(paramBufferedImage, SurfaceType.Ushort555Rgb, null);
/* 110 */       break;
/*     */     
/*     */ 
/*     */     case 13: 
/* 114 */       switch (localColorModel.getTransparency()) {
/*     */       case 1: 
/* 116 */         if (isOpaqueGray((IndexColorModel)localColorModel)) {
/* 117 */           localObject2 = SurfaceType.Index8Gray;
/*     */         } else {
/* 119 */           localObject2 = SurfaceType.ByteIndexedOpaque;
/*     */         }
/* 121 */         break;
/*     */       case 2: 
/* 123 */         localObject2 = SurfaceType.ByteIndexedBm;
/* 124 */         break;
/*     */       case 3: 
/* 126 */         localObject2 = SurfaceType.ByteIndexed;
/* 127 */         break;
/*     */       default: 
/* 129 */         throw new InternalError("Unrecognized transparency");
/*     */       }
/* 131 */       localObject1 = createDataBC(paramBufferedImage, (SurfaceType)localObject2, 0);
/*     */       
/* 133 */       break;
/*     */     case 10: 
/* 135 */       localObject1 = createDataBC(paramBufferedImage, SurfaceType.ByteGray, 0);
/* 136 */       break;
/*     */     case 11: 
/* 138 */       localObject1 = createDataSC(paramBufferedImage, SurfaceType.UshortGray, null);
/* 139 */       break;
/*     */     
/*     */ 
/*     */     case 12: 
/* 143 */       SampleModel localSampleModel = paramBufferedImage.getRaster().getSampleModel();
/* 144 */       switch (localSampleModel.getSampleSize(0)) {
/*     */       case 1: 
/* 146 */         localObject2 = SurfaceType.ByteBinary1Bit;
/* 147 */         break;
/*     */       case 2: 
/* 149 */         localObject2 = SurfaceType.ByteBinary2Bit;
/* 150 */         break;
/*     */       case 4: 
/* 152 */         localObject2 = SurfaceType.ByteBinary4Bit;
/* 153 */         break;
/*     */       case 3: default: 
/* 155 */         throw new InternalError("Unrecognized pixel size");
/*     */       }
/* 157 */       localObject1 = createDataBP(paramBufferedImage, (SurfaceType)localObject2);
/*     */       
/* 159 */       break;
/*     */     
/*     */     case 0: 
/*     */     default: 
/* 163 */       localObject2 = paramBufferedImage.getRaster();
/* 164 */       int j = ((Raster)localObject2).getNumBands();
/* 165 */       SurfaceType localSurfaceType; Object localObject3; int m; int n; int i1; if (((localObject2 instanceof IntegerComponentRaster)) && 
/* 166 */         (((Raster)localObject2).getNumDataElements() == 1) && 
/* 167 */         (((IntegerComponentRaster)localObject2).getPixelStride() == 1))
/*     */       {
/* 169 */         localSurfaceType = SurfaceType.AnyInt;
/* 170 */         if ((localColorModel instanceof DirectColorModel)) {
/* 171 */           localObject3 = (DirectColorModel)localColorModel;
/* 172 */           int k = ((DirectColorModel)localObject3).getAlphaMask();
/* 173 */           m = ((DirectColorModel)localObject3).getRedMask();
/* 174 */           n = ((DirectColorModel)localObject3).getGreenMask();
/* 175 */           i1 = ((DirectColorModel)localObject3).getBlueMask();
/* 176 */           if ((j == 3) && (k == 0) && (m == -16777216) && (n == 16711680) && (i1 == 65280))
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 182 */             localSurfaceType = SurfaceType.IntRgbx;
/* 183 */           } else if ((j == 4) && (k == 16777216) && (m == 16711680) && (n == 65280) && (i1 == 255))
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 189 */             localSurfaceType = SurfaceType.IntArgbBm;
/*     */           } else {
/* 191 */             localSurfaceType = SurfaceType.AnyDcm;
/*     */           }
/*     */         }
/* 194 */         localObject1 = createDataIC(paramBufferedImage, localSurfaceType);
/*     */       }
/* 196 */       else if (((localObject2 instanceof ShortComponentRaster)) && 
/* 197 */         (((Raster)localObject2).getNumDataElements() == 1) && 
/* 198 */         (((ShortComponentRaster)localObject2).getPixelStride() == 1))
/*     */       {
/* 200 */         localSurfaceType = SurfaceType.AnyShort;
/* 201 */         localObject3 = null;
/* 202 */         if ((localColorModel instanceof DirectColorModel)) {
/* 203 */           DirectColorModel localDirectColorModel = (DirectColorModel)localColorModel;
/* 204 */           m = localDirectColorModel.getAlphaMask();
/* 205 */           n = localDirectColorModel.getRedMask();
/* 206 */           i1 = localDirectColorModel.getGreenMask();
/* 207 */           int i2 = localDirectColorModel.getBlueMask();
/* 208 */           if ((j == 3) && (m == 0) && (n == 63488) && (i1 == 1984) && (i2 == 62))
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 214 */             localSurfaceType = SurfaceType.Ushort555Rgbx;
/*     */           }
/* 216 */           else if ((j == 4) && (m == 61440) && (n == 3840) && (i1 == 240) && (i2 == 15))
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 222 */             localSurfaceType = SurfaceType.Ushort4444Argb;
/*     */           }
/* 224 */         } else if ((localColorModel instanceof IndexColorModel)) {
/* 225 */           localObject3 = (IndexColorModel)localColorModel;
/* 226 */           if (((IndexColorModel)localObject3).getPixelSize() == 12) {
/* 227 */             if (isOpaqueGray((IndexColorModel)localObject3)) {
/* 228 */               localSurfaceType = SurfaceType.Index12Gray;
/*     */             } else {
/* 230 */               localSurfaceType = SurfaceType.UshortIndexed;
/*     */             }
/*     */           } else {
/* 233 */             localObject3 = null;
/*     */           }
/*     */         }
/* 236 */         localObject1 = createDataSC(paramBufferedImage, localSurfaceType, (IndexColorModel)localObject3);
/*     */       }
/*     */       else {
/* 239 */         localObject1 = new BufImgSurfaceData(((Raster)localObject2).getDataBuffer(), paramBufferedImage, SurfaceType.Custom);
/*     */       }
/*     */       break;
/*     */     }
/*     */     
/* 244 */     ((BufImgSurfaceData)localObject1).initSolidLoops();
/* 245 */     return (SurfaceData)localObject1;
/*     */   }
/*     */   
/*     */   public static SurfaceData createData(Raster paramRaster, ColorModel paramColorModel) {
/* 249 */     throw new InternalError("SurfaceData not implemented for Raster/CM");
/*     */   }
/*     */   
/*     */ 
/*     */   public static SurfaceData createDataIC(BufferedImage paramBufferedImage, SurfaceType paramSurfaceType)
/*     */   {
/* 255 */     IntegerComponentRaster localIntegerComponentRaster = (IntegerComponentRaster)paramBufferedImage.getRaster();
/*     */     
/* 257 */     BufImgSurfaceData localBufImgSurfaceData = new BufImgSurfaceData(localIntegerComponentRaster.getDataBuffer(), paramBufferedImage, paramSurfaceType);
/* 258 */     localBufImgSurfaceData.initRaster(localIntegerComponentRaster.getDataStorage(), localIntegerComponentRaster
/* 259 */       .getDataOffset(0) * 4, 0, localIntegerComponentRaster
/* 260 */       .getWidth(), localIntegerComponentRaster
/* 261 */       .getHeight(), localIntegerComponentRaster
/* 262 */       .getPixelStride() * 4, localIntegerComponentRaster
/* 263 */       .getScanlineStride() * 4, null);
/*     */     
/* 265 */     return localBufImgSurfaceData;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static SurfaceData createDataSC(BufferedImage paramBufferedImage, SurfaceType paramSurfaceType, IndexColorModel paramIndexColorModel)
/*     */   {
/* 272 */     ShortComponentRaster localShortComponentRaster = (ShortComponentRaster)paramBufferedImage.getRaster();
/*     */     
/* 274 */     BufImgSurfaceData localBufImgSurfaceData = new BufImgSurfaceData(localShortComponentRaster.getDataBuffer(), paramBufferedImage, paramSurfaceType);
/* 275 */     localBufImgSurfaceData.initRaster(localShortComponentRaster.getDataStorage(), localShortComponentRaster
/* 276 */       .getDataOffset(0) * 2, 0, localShortComponentRaster
/* 277 */       .getWidth(), localShortComponentRaster
/* 278 */       .getHeight(), localShortComponentRaster
/* 279 */       .getPixelStride() * 2, localShortComponentRaster
/* 280 */       .getScanlineStride() * 2, paramIndexColorModel);
/*     */     
/* 282 */     return localBufImgSurfaceData;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static SurfaceData createDataBC(BufferedImage paramBufferedImage, SurfaceType paramSurfaceType, int paramInt)
/*     */   {
/* 289 */     ByteComponentRaster localByteComponentRaster = (ByteComponentRaster)paramBufferedImage.getRaster();
/*     */     
/* 291 */     BufImgSurfaceData localBufImgSurfaceData = new BufImgSurfaceData(localByteComponentRaster.getDataBuffer(), paramBufferedImage, paramSurfaceType);
/* 292 */     ColorModel localColorModel = paramBufferedImage.getColorModel();
/* 293 */     IndexColorModel localIndexColorModel = (localColorModel instanceof IndexColorModel) ? (IndexColorModel)localColorModel : null;
/*     */     
/*     */ 
/* 296 */     localBufImgSurfaceData.initRaster(localByteComponentRaster.getDataStorage(), localByteComponentRaster
/* 297 */       .getDataOffset(paramInt), 0, localByteComponentRaster
/* 298 */       .getWidth(), localByteComponentRaster
/* 299 */       .getHeight(), localByteComponentRaster
/* 300 */       .getPixelStride(), localByteComponentRaster
/* 301 */       .getScanlineStride(), localIndexColorModel);
/*     */     
/* 303 */     return localBufImgSurfaceData;
/*     */   }
/*     */   
/*     */ 
/*     */   public static SurfaceData createDataBP(BufferedImage paramBufferedImage, SurfaceType paramSurfaceType)
/*     */   {
/* 309 */     BytePackedRaster localBytePackedRaster = (BytePackedRaster)paramBufferedImage.getRaster();
/*     */     
/* 311 */     BufImgSurfaceData localBufImgSurfaceData = new BufImgSurfaceData(localBytePackedRaster.getDataBuffer(), paramBufferedImage, paramSurfaceType);
/* 312 */     ColorModel localColorModel = paramBufferedImage.getColorModel();
/* 313 */     IndexColorModel localIndexColorModel = (localColorModel instanceof IndexColorModel) ? (IndexColorModel)localColorModel : null;
/*     */     
/*     */ 
/* 316 */     localBufImgSurfaceData.initRaster(localBytePackedRaster.getDataStorage(), localBytePackedRaster
/* 317 */       .getDataBitOffset() / 8, localBytePackedRaster
/* 318 */       .getDataBitOffset() & 0x7, localBytePackedRaster
/* 319 */       .getWidth(), localBytePackedRaster
/* 320 */       .getHeight(), 0, localBytePackedRaster
/*     */       
/* 322 */       .getScanlineStride(), localIndexColorModel);
/*     */     
/* 324 */     return localBufImgSurfaceData;
/*     */   }
/*     */   
/*     */   public RenderLoops getRenderLoops(SunGraphics2D paramSunGraphics2D) {
/* 328 */     if ((paramSunGraphics2D.paintState <= 1) && (paramSunGraphics2D.compositeState <= 0))
/*     */     {
/*     */ 
/* 331 */       return this.solidloops;
/*     */     }
/* 333 */     return super.getRenderLoops(paramSunGraphics2D);
/*     */   }
/*     */   
/*     */   public Raster getRaster(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/* 337 */     return this.bufImg.getRaster();
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
/*     */   public BufImgSurfaceData(DataBuffer paramDataBuffer, BufferedImage paramBufferedImage, SurfaceType paramSurfaceType)
/*     */   {
/* 355 */     super(SunWritableRaster.stealTrackable(paramDataBuffer), paramSurfaceType, paramBufferedImage
/* 356 */       .getColorModel());
/* 357 */     this.bufImg = paramBufferedImage;
/*     */   }
/*     */   
/*     */   protected BufImgSurfaceData(SurfaceType paramSurfaceType, ColorModel paramColorModel) {
/* 361 */     super(paramSurfaceType, paramColorModel);
/*     */   }
/*     */   
/*     */   public void initSolidLoops() {
/* 365 */     this.solidloops = getSolidLoops(getSurfaceType());
/*     */   }
/*     */   
/*     */ 
/* 369 */   private static RenderLoops[] loopcache = new RenderLoops[5];
/* 370 */   private static SurfaceType[] typecache = new SurfaceType[5];
/*     */   
/* 372 */   public static synchronized RenderLoops getSolidLoops(SurfaceType paramSurfaceType) { for (int i = 4; i >= 0; i--) {
/* 373 */       SurfaceType localSurfaceType = typecache[i];
/* 374 */       if (localSurfaceType == paramSurfaceType)
/* 375 */         return loopcache[i];
/* 376 */       if (localSurfaceType == null) {
/*     */         break;
/*     */       }
/*     */     }
/* 380 */     RenderLoops localRenderLoops = makeRenderLoops(SurfaceType.OpaqueColor, CompositeType.SrcNoEa, paramSurfaceType);
/*     */     
/*     */ 
/* 383 */     System.arraycopy(loopcache, 1, loopcache, 0, 4);
/* 384 */     System.arraycopy(typecache, 1, typecache, 0, 4);
/* 385 */     loopcache[4] = localRenderLoops;
/* 386 */     typecache[4] = paramSurfaceType;
/* 387 */     return localRenderLoops;
/*     */   }
/*     */   
/*     */ 
/*     */   public SurfaceData getReplacement()
/*     */   {
/* 393 */     return restoreContents(this.bufImg);
/*     */   }
/*     */   
/*     */   public synchronized GraphicsConfiguration getDeviceConfiguration() {
/* 397 */     if (this.graphicsConfig == null) {
/* 398 */       this.graphicsConfig = BufferedImageGraphicsConfig.getConfig(this.bufImg);
/*     */     }
/* 400 */     return this.graphicsConfig;
/*     */   }
/*     */   
/*     */   public Rectangle getBounds() {
/* 404 */     return new Rectangle(this.bufImg.getWidth(), this.bufImg.getHeight());
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
/* 418 */   public Object getDestination() { return this.bufImg; }
/*     */   
/*     */   private static native void initIDs(Class paramClass1, Class paramClass2);
/*     */   
/* 422 */   public static final class ICMColorData { private long pData = 0L;
/*     */     
/*     */     private ICMColorData(long paramLong) {
/* 425 */       this.pData = paramLong;
/*     */     }
/*     */     
/*     */     public void finalize() {
/* 429 */       if (this.pData != 0L) {
/* 430 */         BufImgSurfaceData.freeNativeICMData(this.pData);
/* 431 */         this.pData = 0L;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   protected native void initRaster(Object paramObject, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, IndexColorModel paramIndexColorModel);
/*     */   
/*     */   protected void checkCustomComposite() {}
/*     */   
/*     */   private static native void freeNativeICMData(long paramLong);
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\image\BufImgSurfaceData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */