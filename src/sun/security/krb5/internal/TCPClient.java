/*     */ package sun.security.krb5.internal;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Socket;
/*     */ import sun.misc.IOUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class TCPClient
/*     */   extends NetClient
/*     */ {
/*     */   private Socket tcpSocket;
/*     */   private BufferedOutputStream out;
/*     */   private BufferedInputStream in;
/*     */   
/*     */   TCPClient(String paramString, int paramInt1, int paramInt2)
/*     */     throws IOException
/*     */   {
/*  62 */     this.tcpSocket = new Socket();
/*  63 */     this.tcpSocket.connect(new InetSocketAddress(paramString, paramInt1), paramInt2);
/*  64 */     this.out = new BufferedOutputStream(this.tcpSocket.getOutputStream());
/*  65 */     this.in = new BufferedInputStream(this.tcpSocket.getInputStream());
/*  66 */     this.tcpSocket.setSoTimeout(paramInt2);
/*     */   }
/*     */   
/*     */   public void send(byte[] paramArrayOfByte) throws IOException
/*     */   {
/*  71 */     byte[] arrayOfByte = new byte[4];
/*  72 */     intToNetworkByteOrder(paramArrayOfByte.length, arrayOfByte, 0, 4);
/*  73 */     this.out.write(arrayOfByte);
/*     */     
/*  75 */     this.out.write(paramArrayOfByte);
/*  76 */     this.out.flush();
/*     */   }
/*     */   
/*     */   public byte[] receive() throws IOException
/*     */   {
/*  81 */     byte[] arrayOfByte = new byte[4];
/*  82 */     int i = readFully(arrayOfByte, 4);
/*     */     
/*  84 */     if (i != 4) {
/*  85 */       if (Krb5.DEBUG) {
/*  86 */         System.out.println(">>>DEBUG: TCPClient could not read length field");
/*     */       }
/*     */       
/*  89 */       return null;
/*     */     }
/*     */     
/*  92 */     int j = networkByteOrderToInt(arrayOfByte, 0, 4);
/*  93 */     if (Krb5.DEBUG) {
/*  94 */       System.out.println(">>>DEBUG: TCPClient reading " + j + " bytes");
/*     */     }
/*     */     
/*  97 */     if (j <= 0) {
/*  98 */       if (Krb5.DEBUG) {
/*  99 */         System.out.println(">>>DEBUG: TCPClient zero or negative length field: " + j);
/*     */       }
/*     */       
/* 102 */       return null;
/*     */     }
/*     */     try
/*     */     {
/* 106 */       return IOUtils.readFully(this.in, j, true);
/*     */     } catch (IOException localIOException) {
/* 108 */       if (Krb5.DEBUG) {
/* 109 */         System.out.println(">>>DEBUG: TCPClient could not read complete packet (" + j + "/" + i + ")");
/*     */       }
/*     */     }
/*     */     
/* 113 */     return null;
/*     */   }
/*     */   
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/* 119 */     this.tcpSocket.close();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private int readFully(byte[] paramArrayOfByte, int paramInt)
/*     */     throws IOException
/*     */   {
/* 127 */     int j = 0;
/*     */     
/* 129 */     while (paramInt > 0) {
/* 130 */       int i = this.in.read(paramArrayOfByte, j, paramInt);
/*     */       
/* 132 */       if (i == -1) {
/* 133 */         return j == 0 ? -1 : j;
/*     */       }
/* 135 */       j += i;
/* 136 */       paramInt -= i;
/*     */     }
/* 138 */     return j;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static int networkByteOrderToInt(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */   {
/* 146 */     if (paramInt2 > 4) {
/* 147 */       throw new IllegalArgumentException("Cannot handle more than 4 bytes");
/*     */     }
/*     */     
/*     */ 
/* 151 */     int i = 0;
/*     */     
/* 153 */     for (int j = 0; j < paramInt2; j++) {
/* 154 */       i <<= 8;
/* 155 */       i |= paramArrayOfByte[(paramInt1 + j)] & 0xFF;
/*     */     }
/* 157 */     return i;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static void intToNetworkByteOrder(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3)
/*     */   {
/* 166 */     if (paramInt3 > 4) {
/* 167 */       throw new IllegalArgumentException("Cannot handle more than 4 bytes");
/*     */     }
/*     */     
/*     */ 
/* 171 */     for (int i = paramInt3 - 1; i >= 0; i--) {
/* 172 */       paramArrayOfByte[(paramInt2 + i)] = ((byte)(paramInt1 & 0xFF));
/* 173 */       paramInt1 >>>= 8;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\krb5\internal\TCPClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */