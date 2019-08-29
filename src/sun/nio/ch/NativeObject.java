/*     */ package sun.nio.ch;
/*     */ 
/*     */ import java.nio.ByteOrder;
/*     */ import sun.misc.Unsafe;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class NativeObject
/*     */ {
/*  43 */   protected static final Unsafe unsafe = Unsafe.getUnsafe();
/*     */   
/*     */ 
/*     */ 
/*     */   protected long allocationAddress;
/*     */   
/*     */ 
/*     */ 
/*     */   private final long address;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   NativeObject(long paramLong)
/*     */   {
/*  58 */     this.allocationAddress = paramLong;
/*  59 */     this.address = paramLong;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   NativeObject(long paramLong1, long paramLong2)
/*     */   {
/*  67 */     this.allocationAddress = paramLong1;
/*  68 */     this.address = (paramLong1 + paramLong2);
/*     */   }
/*     */   
/*     */ 
/*     */   protected NativeObject(int paramInt, boolean paramBoolean)
/*     */   {
/*  74 */     if (!paramBoolean) {
/*  75 */       this.allocationAddress = unsafe.allocateMemory(paramInt);
/*  76 */       this.address = this.allocationAddress;
/*     */     } else {
/*  78 */       int i = pageSize();
/*  79 */       long l = unsafe.allocateMemory(paramInt + i);
/*  80 */       this.allocationAddress = l;
/*  81 */       this.address = (l + i - (l & i - 1));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   long address()
/*     */   {
/*  91 */     return this.address;
/*     */   }
/*     */   
/*     */   long allocationAddress() {
/*  95 */     return this.allocationAddress;
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
/*     */   NativeObject subObject(int paramInt)
/*     */   {
/* 109 */     return new NativeObject(paramInt + this.address);
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
/*     */   NativeObject getObject(int paramInt)
/*     */   {
/* 124 */     long l = 0L;
/* 125 */     switch (addressSize()) {
/*     */     case 8: 
/* 127 */       l = unsafe.getLong(paramInt + this.address);
/* 128 */       break;
/*     */     case 4: 
/* 130 */       l = unsafe.getInt(paramInt + this.address) & 0xFFFFFFFF;
/* 131 */       break;
/*     */     default: 
/* 133 */       throw new InternalError("Address size not supported");
/*     */     }
/*     */     
/* 136 */     return new NativeObject(l);
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
/*     */   void putObject(int paramInt, NativeObject paramNativeObject)
/*     */   {
/* 151 */     switch () {
/*     */     case 8: 
/* 153 */       putLong(paramInt, paramNativeObject.address);
/* 154 */       break;
/*     */     case 4: 
/* 156 */       putInt(paramInt, (int)(paramNativeObject.address & 0xFFFFFFFFFFFFFFFF));
/* 157 */       break;
/*     */     default: 
/* 159 */       throw new InternalError("Address size not supported");
/*     */     }
/*     */     
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
/*     */   final byte getByte(int paramInt)
/*     */   {
/* 176 */     return unsafe.getByte(paramInt + this.address);
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
/*     */   final void putByte(int paramInt, byte paramByte)
/*     */   {
/* 190 */     unsafe.putByte(paramInt + this.address, paramByte);
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
/*     */   final short getShort(int paramInt)
/*     */   {
/* 203 */     return unsafe.getShort(paramInt + this.address);
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
/*     */   final void putShort(int paramInt, short paramShort)
/*     */   {
/* 217 */     unsafe.putShort(paramInt + this.address, paramShort);
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
/*     */   final char getChar(int paramInt)
/*     */   {
/* 230 */     return unsafe.getChar(paramInt + this.address);
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
/*     */   final void putChar(int paramInt, char paramChar)
/*     */   {
/* 244 */     unsafe.putChar(paramInt + this.address, paramChar);
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
/*     */   final int getInt(int paramInt)
/*     */   {
/* 257 */     return unsafe.getInt(paramInt + this.address);
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
/*     */   final void putInt(int paramInt1, int paramInt2)
/*     */   {
/* 271 */     unsafe.putInt(paramInt1 + this.address, paramInt2);
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
/*     */   final long getLong(int paramInt)
/*     */   {
/* 284 */     return unsafe.getLong(paramInt + this.address);
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
/*     */   final void putLong(int paramInt, long paramLong)
/*     */   {
/* 298 */     unsafe.putLong(paramInt + this.address, paramLong);
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
/*     */   final float getFloat(int paramInt)
/*     */   {
/* 311 */     return unsafe.getFloat(paramInt + this.address);
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
/*     */   final void putFloat(int paramInt, float paramFloat)
/*     */   {
/* 325 */     unsafe.putFloat(paramInt + this.address, paramFloat);
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
/*     */   final double getDouble(int paramInt)
/*     */   {
/* 338 */     return unsafe.getDouble(paramInt + this.address);
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
/*     */   final void putDouble(int paramInt, double paramDouble)
/*     */   {
/* 352 */     unsafe.putDouble(paramInt + this.address, paramDouble);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static int addressSize()
/*     */   {
/* 361 */     return unsafe.addressSize();
/*     */   }
/*     */   
/*     */ 
/* 365 */   private static ByteOrder byteOrder = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 392 */   private static int pageSize = -1;
/*     */   
/*     */   /* Error */
/*     */   static ByteOrder byteOrder()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: getstatic 144	sun/nio/ch/NativeObject:byteOrder	Ljava/nio/ByteOrder;
/*     */     //   3: ifnull +7 -> 10
/*     */     //   6: getstatic 144	sun/nio/ch/NativeObject:byteOrder	Ljava/nio/ByteOrder;
/*     */     //   9: areturn
/*     */     //   10: getstatic 145	sun/nio/ch/NativeObject:unsafe	Lsun/misc/Unsafe;
/*     */     //   13: ldc2_w 88
/*     */     //   16: invokevirtual 157	sun/misc/Unsafe:allocateMemory	(J)J
/*     */     //   19: lstore_0
/*     */     //   20: getstatic 145	sun/nio/ch/NativeObject:unsafe	Lsun/misc/Unsafe;
/*     */     //   23: lload_0
/*     */     //   24: ldc2_w 90
/*     */     //   27: invokevirtual 166	sun/misc/Unsafe:putLong	(JJ)V
/*     */     //   30: getstatic 145	sun/nio/ch/NativeObject:unsafe	Lsun/misc/Unsafe;
/*     */     //   33: lload_0
/*     */     //   34: invokevirtual 152	sun/misc/Unsafe:getByte	(J)B
/*     */     //   37: istore_2
/*     */     //   38: iload_2
/*     */     //   39: lookupswitch	default:+43->82, 1:+25->64, 8:+34->73
/*     */     //   64: getstatic 138	java/nio/ByteOrder:BIG_ENDIAN	Ljava/nio/ByteOrder;
/*     */     //   67: putstatic 144	sun/nio/ch/NativeObject:byteOrder	Ljava/nio/ByteOrder;
/*     */     //   70: goto +26 -> 96
/*     */     //   73: getstatic 139	java/nio/ByteOrder:LITTLE_ENDIAN	Ljava/nio/ByteOrder;
/*     */     //   76: putstatic 144	sun/nio/ch/NativeObject:byteOrder	Ljava/nio/ByteOrder;
/*     */     //   79: goto +17 -> 96
/*     */     //   82: getstatic 143	sun/nio/ch/NativeObject:$assertionsDisabled	Z
/*     */     //   85: ifne +11 -> 96
/*     */     //   88: new 92	java/lang/AssertionError
/*     */     //   91: dup
/*     */     //   92: invokespecial 146	java/lang/AssertionError:<init>	()V
/*     */     //   95: athrow
/*     */     //   96: getstatic 145	sun/nio/ch/NativeObject:unsafe	Lsun/misc/Unsafe;
/*     */     //   99: lload_0
/*     */     //   100: invokevirtual 160	sun/misc/Unsafe:freeMemory	(J)V
/*     */     //   103: goto +13 -> 116
/*     */     //   106: astore_3
/*     */     //   107: getstatic 145	sun/nio/ch/NativeObject:unsafe	Lsun/misc/Unsafe;
/*     */     //   110: lload_0
/*     */     //   111: invokevirtual 160	sun/misc/Unsafe:freeMemory	(J)V
/*     */     //   114: aload_3
/*     */     //   115: athrow
/*     */     //   116: getstatic 144	sun/nio/ch/NativeObject:byteOrder	Ljava/nio/ByteOrder;
/*     */     //   119: areturn
/*     */     // Line number table:
/*     */     //   Java source line #373	-> byte code offset #0
/*     */     //   Java source line #374	-> byte code offset #6
/*     */     //   Java source line #375	-> byte code offset #10
/*     */     //   Java source line #377	-> byte code offset #20
/*     */     //   Java source line #378	-> byte code offset #30
/*     */     //   Java source line #379	-> byte code offset #38
/*     */     //   Java source line #380	-> byte code offset #64
/*     */     //   Java source line #381	-> byte code offset #73
/*     */     //   Java source line #383	-> byte code offset #82
/*     */     //   Java source line #386	-> byte code offset #96
/*     */     //   Java source line #387	-> byte code offset #103
/*     */     //   Java source line #386	-> byte code offset #106
/*     */     //   Java source line #387	-> byte code offset #114
/*     */     //   Java source line #388	-> byte code offset #116
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   19	92	0	l	long
/*     */     //   37	2	2	i	int
/*     */     //   106	9	3	localObject	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   20	96	106	finally
/*     */   }
/*     */   
/*     */   static int pageSize()
/*     */   {
/* 400 */     if (pageSize == -1)
/* 401 */       pageSize = unsafe.pageSize();
/* 402 */     return pageSize;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\nio\ch\NativeObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */