/*     */ package sun.awt.windows;
/*     */ 
/*     */ import java.awt.datatransfer.DataFlavor;
/*     */ import java.awt.datatransfer.Transferable;
/*     */ import java.awt.datatransfer.UnsupportedFlavorException;
/*     */ import java.io.IOException;
/*     */ import java.io.NotSerializableException;
/*     */ import java.util.Map;
/*     */ import java.util.SortedMap;
/*     */ import sun.awt.datatransfer.DataTransferer;
/*     */ import sun.awt.datatransfer.SunClipboard;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class WClipboard
/*     */   extends SunClipboard
/*     */ {
/*     */   private boolean isClipboardViewerRegistered;
/*     */   
/*     */   WClipboard()
/*     */   {
/*  55 */     super("System");
/*     */   }
/*     */   
/*     */   public long getID()
/*     */   {
/*  60 */     return 0L;
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
/*     */   protected void setContentsNative(Transferable paramTransferable)
/*     */   {
/*  73 */     SortedMap localSortedMap = WDataTransferer.getInstance().getFormatsForTransferable(paramTransferable, getDefaultFlavorTable());
/*     */     
/*  75 */     openClipboard(this);
/*     */     try
/*     */     {
/*  78 */       for (Long localLong : localSortedMap.keySet()) {
/*  79 */         DataFlavor localDataFlavor = (DataFlavor)localSortedMap.get(localLong);
/*     */         
/*     */         try
/*     */         {
/*  83 */           byte[] arrayOfByte = WDataTransferer.getInstance().translateTransferable(paramTransferable, localDataFlavor, localLong.longValue());
/*  84 */           publishClipboardData(localLong.longValue(), arrayOfByte);
/*     */ 
/*     */         }
/*     */         catch (IOException localIOException)
/*     */         {
/*  89 */           if ((localDataFlavor.isMimeTypeEqual("application/x-java-jvm-local-objectref")) && ((localIOException instanceof NotSerializableException))) {}
/*     */         }
/*     */         
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/*  96 */       closeClipboard();
/*     */     }
/*     */   }
/*     */   
/*     */   private void lostSelectionOwnershipImpl() {
/* 101 */     lostOwnershipImpl();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void clearNativeContext() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public native void openClipboard(SunClipboard paramSunClipboard)
/*     */     throws IllegalStateException;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public native void closeClipboard();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private native void publishClipboardData(long paramLong, byte[] paramArrayOfByte);
/*     */   
/*     */ 
/*     */ 
/*     */   private static native void init();
/*     */   
/*     */ 
/*     */ 
/*     */   protected native long[] getClipboardFormats();
/*     */   
/*     */ 
/*     */ 
/*     */   protected native byte[] getClipboardData(long paramLong)
/*     */     throws IOException;
/*     */   
/*     */ 
/*     */ 
/*     */   protected void registerClipboardViewerChecked()
/*     */   {
/* 142 */     if (!this.isClipboardViewerRegistered) {
/* 143 */       registerClipboardViewer();
/* 144 */       this.isClipboardViewerRegistered = true;
/*     */     }
/*     */   }
/*     */   
/*     */   private native void registerClipboardViewer();
/*     */   
/*     */   protected void unregisterClipboardViewerChecked() {}
/*     */   
/*     */   /* Error */
/*     */   private void handleContentsChanged()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: invokevirtual 138	sun/awt/windows/WClipboard:areFlavorListenersRegistered	()Z
/*     */     //   4: ifne +4 -> 8
/*     */     //   7: return
/*     */     //   8: aconst_null
/*     */     //   9: astore_1
/*     */     //   10: aload_0
/*     */     //   11: aconst_null
/*     */     //   12: invokevirtual 144	sun/awt/windows/WClipboard:openClipboard	(Lsun/awt/datatransfer/SunClipboard;)V
/*     */     //   15: aload_0
/*     */     //   16: invokevirtual 139	sun/awt/windows/WClipboard:getClipboardFormats	()[J
/*     */     //   19: astore_1
/*     */     //   20: aload_0
/*     */     //   21: invokevirtual 134	sun/awt/windows/WClipboard:closeClipboard	()V
/*     */     //   24: goto +18 -> 42
/*     */     //   27: astore_2
/*     */     //   28: aload_0
/*     */     //   29: invokevirtual 134	sun/awt/windows/WClipboard:closeClipboard	()V
/*     */     //   32: goto +10 -> 42
/*     */     //   35: astore_3
/*     */     //   36: aload_0
/*     */     //   37: invokevirtual 134	sun/awt/windows/WClipboard:closeClipboard	()V
/*     */     //   40: aload_3
/*     */     //   41: athrow
/*     */     //   42: aload_0
/*     */     //   43: aload_1
/*     */     //   44: invokevirtual 142	sun/awt/windows/WClipboard:checkChange	([J)V
/*     */     //   47: return
/*     */     // Line number table:
/*     */     //   Java source line #162	-> byte code offset #0
/*     */     //   Java source line #163	-> byte code offset #7
/*     */     //   Java source line #166	-> byte code offset #8
/*     */     //   Java source line #168	-> byte code offset #10
/*     */     //   Java source line #169	-> byte code offset #15
/*     */     //   Java source line #173	-> byte code offset #20
/*     */     //   Java source line #174	-> byte code offset #24
/*     */     //   Java source line #170	-> byte code offset #27
/*     */     //   Java source line #173	-> byte code offset #28
/*     */     //   Java source line #174	-> byte code offset #32
/*     */     //   Java source line #173	-> byte code offset #35
/*     */     //   Java source line #174	-> byte code offset #40
/*     */     //   Java source line #175	-> byte code offset #42
/*     */     //   Java source line #176	-> byte code offset #47
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	48	0	this	WClipboard
/*     */     //   9	35	1	arrayOfLong	long[]
/*     */     //   27	1	2	localIllegalStateException	IllegalStateException
/*     */     //   35	6	3	localObject	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   10	20	27	java/lang/IllegalStateException
/*     */     //   10	20	35	finally
/*     */   }
/*     */   
/*     */   protected Transferable createLocaleTransferable(long[] paramArrayOfLong)
/*     */     throws IOException
/*     */   {
/* 185 */     int i = 0;
/* 186 */     for (int j = 0; j < paramArrayOfLong.length; j++) {
/* 187 */       if (paramArrayOfLong[j] == 16L) {
/* 188 */         i = 1;
/* 189 */         break;
/*     */       }
/*     */     }
/* 192 */     if (i == 0) {
/* 193 */       return null;
/*     */     }
/*     */     
/* 196 */     byte[] arrayOfByte1 = null;
/*     */     try {
/* 198 */       arrayOfByte1 = getClipboardData(16L);
/*     */     } catch (IOException localIOException) {
/* 200 */       return null;
/*     */     }
/*     */     
/* 203 */     final byte[] arrayOfByte2 = arrayOfByte1;
/*     */     
/* 205 */     new Transferable()
/*     */     {
/*     */       public DataFlavor[] getTransferDataFlavors() {
/* 208 */         return new DataFlavor[] { DataTransferer.javaTextEncodingFlavor };
/*     */       }
/*     */       
/*     */       public boolean isDataFlavorSupported(DataFlavor paramAnonymousDataFlavor) {
/* 212 */         return paramAnonymousDataFlavor.equals(DataTransferer.javaTextEncodingFlavor);
/*     */       }
/*     */       
/*     */       public Object getTransferData(DataFlavor paramAnonymousDataFlavor) throws UnsupportedFlavorException {
/* 216 */         if (isDataFlavorSupported(paramAnonymousDataFlavor)) {
/* 217 */           return arrayOfByte2;
/*     */         }
/* 219 */         throw new UnsupportedFlavorException(paramAnonymousDataFlavor);
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */   static {}
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\windows\WClipboard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */