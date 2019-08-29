/*     */ package sun.awt.image.codec;
/*     */ 
/*     */ import com.sun.image.codec.jpeg.ImageFormatException;
/*     */ import com.sun.image.codec.jpeg.JPEGDecodeParam;
/*     */ import com.sun.image.codec.jpeg.JPEGImageDecoder;
/*     */ import java.awt.Point;
/*     */ import java.awt.color.ColorSpace;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.ColorModel;
/*     */ import java.awt.image.ComponentColorModel;
/*     */ import java.awt.image.DataBufferByte;
/*     */ import java.awt.image.DataBufferInt;
/*     */ import java.awt.image.DirectColorModel;
/*     */ import java.awt.image.Raster;
/*     */ import java.awt.image.WritableRaster;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
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
/*     */ public class JPEGImageDecoderImpl
/*     */   implements JPEGImageDecoder
/*     */ {
/*  96 */   private static final Class InputStreamClass = InputStream.class;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 102 */   private JPEGDecodeParam param = null;
/* 103 */   private InputStream input = null;
/* 104 */   private WritableRaster aRas = null;
/* 105 */   private BufferedImage aBufImg = null;
/* 106 */   private ColorModel cm = null;
/* 107 */   private boolean unpack = false;
/* 108 */   private boolean flip = false;
/*     */   
/*     */   static
/*     */   {
/* 112 */     AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Void run() {
/* 115 */         System.loadLibrary("jpeg");
/* 116 */         return null;
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JPEGImageDecoderImpl(InputStream paramInputStream)
/*     */   {
/* 129 */     if (paramInputStream == null) {
/* 130 */       throw new IllegalArgumentException("InputStream is null.");
/*     */     }
/* 132 */     this.input = paramInputStream;
/* 133 */     initDecoder(InputStreamClass);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JPEGImageDecoderImpl(InputStream paramInputStream, JPEGDecodeParam paramJPEGDecodeParam)
/*     */   {
/* 145 */     this(paramInputStream);
/* 146 */     setJPEGDecodeParam(paramJPEGDecodeParam);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public JPEGDecodeParam getJPEGDecodeParam()
/*     */   {
/* 154 */     if (this.param != null) {
/* 155 */       return (JPEGDecodeParam)this.param.clone();
/*     */     }
/* 157 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setJPEGDecodeParam(JPEGDecodeParam paramJPEGDecodeParam)
/*     */   {
/* 166 */     this.param = ((JPEGDecodeParam)paramJPEGDecodeParam.clone());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized InputStream getInputStream()
/*     */   {
/* 174 */     return this.input;
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
/*     */   public synchronized Raster decodeAsRaster()
/*     */     throws ImageFormatException
/*     */   {
/*     */     try
/*     */     {
/* 190 */       this.param = readJPEGStream(this.input, this.param, false);
/*     */     } catch (IOException localIOException) {
/* 192 */       System.out.println("Can't open input Stream" + localIOException);
/* 193 */       localIOException.printStackTrace();
/*     */     }
/*     */     
/* 196 */     return this.aRas;
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
/*     */   public synchronized BufferedImage decodeAsBufferedImage()
/*     */     throws ImageFormatException
/*     */   {
/*     */     try
/*     */     {
/* 213 */       this.param = readJPEGStream(this.input, this.param, true);
/*     */     } catch (IOException localIOException) {
/* 215 */       System.out.println("Can't open input Stream" + localIOException);
/* 216 */       localIOException.printStackTrace();
/*     */     }
/* 218 */     return this.aBufImg;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void readTables()
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 250 */       this.param = readJPEGStream(this.input, null, false);
/*     */     }
/*     */     catch (ImageFormatException localImageFormatException) {
/* 253 */       localImageFormatException.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private int getDecodedColorModel(int paramInt, boolean paramBoolean)
/*     */     throws ImageFormatException
/*     */   {
/* 263 */     int[] arrayOfInt1 = { 8 };
/* 264 */     int[] arrayOfInt2 = { 8, 8, 8 };
/* 265 */     int[] arrayOfInt3 = { 8, 8, 8, 8 };
/*     */     
/* 267 */     this.cm = null;
/* 268 */     this.unpack = false;
/* 269 */     this.flip = false;
/*     */     
/* 271 */     if (!paramBoolean) { return paramInt;
/*     */     }
/* 273 */     switch (paramInt)
/*     */     {
/*     */     case 1: 
/* 276 */       this.cm = new ComponentColorModel(ColorSpace.getInstance(1003), arrayOfInt1, false, false, 1, 0);
/*     */       
/*     */ 
/* 279 */       return paramInt;
/*     */     
/*     */     case 5: 
/* 282 */       this.cm = new ComponentColorModel(ColorSpace.getInstance(1002), arrayOfInt2, false, false, 1, 0);
/*     */       
/*     */ 
/* 285 */       return paramInt;
/*     */     
/*     */     case 10: 
/* 288 */       this.cm = new ComponentColorModel(ColorSpace.getInstance(1002), arrayOfInt3, true, false, 3, 0);
/*     */       
/*     */ 
/* 291 */       return paramInt;
/*     */     
/*     */     case 2: 
/*     */     case 3: 
/* 295 */       this.unpack = true;
/* 296 */       this.cm = new DirectColorModel(24, 16711680, 65280, 255);
/* 297 */       return 2;
/*     */     
/*     */     case 8: 
/*     */     case 9: 
/* 301 */       this.flip = true;
/*     */     case 6: 
/*     */     case 7: 
/* 304 */       this.unpack = true;
/*     */       
/* 306 */       this.cm = new DirectColorModel(ColorSpace.getInstance(1000), 32, 16711680, 65280, 255, -16777216, false, 3);
/*     */       
/*     */ 
/* 309 */       return 6;
/*     */     }
/*     */     
/*     */     
/*     */ 
/*     */ 
/* 315 */     throw new ImageFormatException("Can't construct a BufferedImage for given COLOR_ID");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private Object allocateDataBuffer(int paramInt1, int paramInt2, int paramInt3)
/*     */   {
/*     */     Object localObject;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 334 */     if (this.unpack) { int[] arrayOfInt;
/* 335 */       if (paramInt3 == 3) {
/* 336 */         arrayOfInt = new int[] { 16711680, 65280, 255 };
/* 337 */         this.aRas = Raster.createPackedRaster(3, paramInt1, paramInt2, arrayOfInt, new Point(0, 0));
/*     */       }
/* 339 */       else if (paramInt3 == 4) {
/* 340 */         arrayOfInt = new int[] { 16711680, 65280, 255, -16777216 };
/* 341 */         this.aRas = Raster.createPackedRaster(3, paramInt1, paramInt2, arrayOfInt, new Point(0, 0));
/*     */       }
/*     */       else {
/* 344 */         throw new ImageFormatException("Can't unpack with anything other than 3 or 4 components");
/*     */       }
/*     */       
/* 347 */       localObject = ((DataBufferInt)this.aRas.getDataBuffer()).getData();
/*     */     } else {
/* 349 */       this.aRas = Raster.createInterleavedRaster(0, paramInt1, paramInt2, paramInt3, new Point(0, 0));
/*     */       
/*     */ 
/* 352 */       localObject = ((DataBufferByte)this.aRas.getDataBuffer()).getData();
/*     */     }
/*     */     
/* 355 */     if (this.cm != null) {
/* 356 */       this.aBufImg = new BufferedImage(this.cm, this.aRas, true, null);
/*     */     }
/* 358 */     return localObject;
/*     */   }
/*     */   
/*     */   private native void initDecoder(Class paramClass);
/*     */   
/*     */   private synchronized native JPEGDecodeParam readJPEGStream(InputStream paramInputStream, JPEGDecodeParam paramJPEGDecodeParam, boolean paramBoolean)
/*     */     throws IOException, ImageFormatException;
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\image\codec\JPEGImageDecoderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */