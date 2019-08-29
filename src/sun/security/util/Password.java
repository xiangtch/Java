/*     */ package sun.security.util;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.Console;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PushbackInputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.CharBuffer;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.CharsetEncoder;
/*     */ import java.nio.charset.CodingErrorAction;
/*     */ import java.util.Arrays;
/*     */ import sun.misc.JavaIOAccess;
/*     */ import sun.misc.SharedSecrets;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Password
/*     */ {
/*     */   private static volatile CharsetEncoder enc;
/*     */   
/*     */   public static char[] readPassword(InputStream paramInputStream)
/*     */     throws IOException
/*     */   {
/*  40 */     return readPassword(paramInputStream, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static char[] readPassword(InputStream paramInputStream, boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/*  50 */     char[] arrayOfChar1 = null;
/*  51 */     byte[] arrayOfByte = null;
/*     */     
/*     */     try
/*     */     {
/*  55 */       Console localConsole = null;
/*  56 */       Object localObject1; if ((!paramBoolean) && (paramInputStream == System.in) && ((localConsole = System.console()) != null)) {
/*  57 */         arrayOfChar1 = localConsole.readPassword();
/*     */         
/*     */ 
/*  60 */         if ((arrayOfChar1 != null) && (arrayOfChar1.length == 0)) {
/*  61 */           return null;
/*     */         }
/*  63 */         arrayOfByte = convertToBytes(arrayOfChar1);
/*  64 */         paramInputStream = new ByteArrayInputStream(arrayOfByte);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  74 */       char[] arrayOfChar2 = localObject1 = new char[''];
/*     */       
/*  76 */       int i = arrayOfChar2.length;
/*  77 */       int j = 0;
/*     */       
/*     */ 
/*  80 */       int m = 0;
/*  81 */       while (m == 0) { int k;
/*  82 */         switch (k = paramInputStream.read()) {
/*     */         case -1: 
/*     */         case 10: 
/*  85 */           m = 1;
/*  86 */           break;
/*     */         
/*     */         case 13: 
/*  89 */           int n = paramInputStream.read();
/*  90 */           if ((n != 10) && (n != -1)) {
/*  91 */             if (!(paramInputStream instanceof PushbackInputStream)) {
/*  92 */               paramInputStream = new PushbackInputStream(paramInputStream);
/*     */             }
/*  94 */             ((PushbackInputStream)paramInputStream).unread(n);
/*     */           } else {
/*  96 */             m = 1; }
/*  97 */           break;
/*     */         
/*     */ 
/*     */         default: 
/* 101 */           i--; if (i < 0) {
/* 102 */             arrayOfChar2 = new char[j + 128];
/* 103 */             i = arrayOfChar2.length - j - 1;
/* 104 */             System.arraycopy(localObject1, 0, arrayOfChar2, 0, j);
/* 105 */             Arrays.fill((char[])localObject1, ' ');
/* 106 */             localObject1 = arrayOfChar2;
/*     */           }
/* 108 */           arrayOfChar2[(j++)] = ((char)k);
/*     */         }
/*     */         
/*     */       }
/*     */       
/* 113 */       if (j == 0) {
/* 114 */         return null;
/*     */       }
/*     */       
/* 117 */       char[] arrayOfChar3 = new char[j];
/* 118 */       System.arraycopy(arrayOfChar2, 0, arrayOfChar3, 0, j);
/* 119 */       Arrays.fill(arrayOfChar2, ' ');
/*     */       
/* 121 */       return arrayOfChar3;
/*     */     } finally {
/* 123 */       if (arrayOfChar1 != null) {
/* 124 */         Arrays.fill(arrayOfChar1, ' ');
/*     */       }
/* 126 */       if (arrayOfByte != null) {
/* 127 */         Arrays.fill(arrayOfByte, (byte)0);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static byte[] convertToBytes(char[] paramArrayOfChar)
/*     */   {
/* 140 */     if (enc == null) {
/* 141 */       synchronized (Password.class)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 146 */         enc = SharedSecrets.getJavaIOAccess().charset().newEncoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
/*     */       }
/*     */     }
/* 149 */     ??? = new byte[(int)(enc.maxBytesPerChar() * paramArrayOfChar.length)];
/* 150 */     ByteBuffer localByteBuffer = ByteBuffer.wrap((byte[])???);
/* 151 */     synchronized (enc) {
/* 152 */       enc.reset().encode(CharBuffer.wrap(paramArrayOfChar), localByteBuffer, true);
/*     */     }
/* 154 */     if (localByteBuffer.position() < ???.length) {
/* 155 */       ???[localByteBuffer.position()] = 10;
/*     */     }
/* 157 */     return (byte[])???;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\util\Password.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */