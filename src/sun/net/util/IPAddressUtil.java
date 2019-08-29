/*     */ package sun.net.util;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class IPAddressUtil
/*     */ {
/*     */   private static final int INADDR4SZ = 4;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final int INADDR16SZ = 16;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final int INT16SZ = 2;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static byte[] textToNumericFormatV4(String paramString)
/*     */   {
/*  43 */     byte[] arrayOfByte = new byte[4];
/*     */     
/*  45 */     long l = 0L;
/*  46 */     int i = 0;
/*  47 */     int j = 1;
/*     */     
/*  49 */     int k = paramString.length();
/*  50 */     if ((k == 0) || (k > 15)) {
/*  51 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  78 */     for (int m = 0; m < k; m++) {
/*  79 */       char c = paramString.charAt(m);
/*  80 */       if (c == '.') {
/*  81 */         if ((j != 0) || (l < 0L) || (l > 255L) || (i == 3)) {
/*  82 */           return null;
/*     */         }
/*  84 */         arrayOfByte[(i++)] = ((byte)(int)(l & 0xFF));
/*  85 */         l = 0L;
/*  86 */         j = 1;
/*     */       } else {
/*  88 */         int n = Character.digit(c, 10);
/*  89 */         if (n < 0) {
/*  90 */           return null;
/*     */         }
/*  92 */         l *= 10L;
/*  93 */         l += n;
/*  94 */         j = 0;
/*     */       }
/*     */     }
/*  97 */     if ((j != 0) || (l < 0L) || (l >= 1L << (4 - i) * 8)) {
/*  98 */       return null;
/*     */     }
/* 100 */     switch (i) {
/*     */     case 0: 
/* 102 */       arrayOfByte[0] = ((byte)(int)(l >> 24 & 0xFF));
/*     */     case 1: 
/* 104 */       arrayOfByte[1] = ((byte)(int)(l >> 16 & 0xFF));
/*     */     case 2: 
/* 106 */       arrayOfByte[2] = ((byte)(int)(l >> 8 & 0xFF));
/*     */     case 3: 
/* 108 */       arrayOfByte[3] = ((byte)(int)(l >> 0 & 0xFF));
/*     */     }
/* 110 */     return arrayOfByte;
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
/*     */   public static byte[] textToNumericFormatV6(String paramString)
/*     */   {
/* 126 */     if (paramString.length() < 2) {
/* 127 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 134 */     char[] arrayOfChar = paramString.toCharArray();
/* 135 */     byte[] arrayOfByte1 = new byte[16];
/*     */     
/* 137 */     int m = arrayOfChar.length;
/* 138 */     int n = paramString.indexOf("%");
/* 139 */     if (n == m - 1) {
/* 140 */       return null;
/*     */     }
/*     */     
/* 143 */     if (n != -1) {
/* 144 */       m = n;
/*     */     }
/*     */     
/* 147 */     int i = -1;
/* 148 */     int i1 = 0;int i2 = 0;
/*     */     
/* 150 */     if ((arrayOfChar[i1] == ':') && 
/* 151 */       (arrayOfChar[(++i1)] != ':'))
/* 152 */       return null;
/* 153 */     int i3 = i1;
/* 154 */     int j = 0;
/* 155 */     int k = 0;
/* 156 */     int i4; while (i1 < m) {
/* 157 */       char c = arrayOfChar[(i1++)];
/* 158 */       i4 = Character.digit(c, 16);
/* 159 */       if (i4 != -1) {
/* 160 */         k <<= 4;
/* 161 */         k |= i4;
/* 162 */         if (k > 65535)
/* 163 */           return null;
/* 164 */         j = 1;
/*     */ 
/*     */       }
/* 167 */       else if (c == ':') {
/* 168 */         i3 = i1;
/* 169 */         if (j == 0) {
/* 170 */           if (i != -1)
/* 171 */             return null;
/* 172 */           i = i2;
/*     */         } else {
/* 174 */           if (i1 == m) {
/* 175 */             return null;
/*     */           }
/* 177 */           if (i2 + 2 > 16)
/* 178 */             return null;
/* 179 */           arrayOfByte1[(i2++)] = ((byte)(k >> 8 & 0xFF));
/* 180 */           arrayOfByte1[(i2++)] = ((byte)(k & 0xFF));
/* 181 */           j = 0;
/* 182 */           k = 0;
/*     */         }
/*     */       }
/* 185 */       else if ((c == '.') && (i2 + 4 <= 16)) {
/* 186 */         String str = paramString.substring(i3, m);
/*     */         
/* 188 */         int i5 = 0;int i6 = 0;
/* 189 */         while ((i6 = str.indexOf('.', i6)) != -1) {
/* 190 */           i5++;
/* 191 */           i6++;
/*     */         }
/* 193 */         if (i5 != 3) {
/* 194 */           return null;
/*     */         }
/* 196 */         byte[] arrayOfByte3 = textToNumericFormatV4(str);
/* 197 */         if (arrayOfByte3 == null) {
/* 198 */           return null;
/*     */         }
/* 200 */         for (int i7 = 0; i7 < 4; i7++) {
/* 201 */           arrayOfByte1[(i2++)] = arrayOfByte3[i7];
/*     */         }
/* 203 */         j = 0;
/*     */       }
/*     */       else {
/* 206 */         return null;
/*     */       } }
/* 208 */     if (j != 0) {
/* 209 */       if (i2 + 2 > 16)
/* 210 */         return null;
/* 211 */       arrayOfByte1[(i2++)] = ((byte)(k >> 8 & 0xFF));
/* 212 */       arrayOfByte1[(i2++)] = ((byte)(k & 0xFF));
/*     */     }
/*     */     
/* 215 */     if (i != -1) {
/* 216 */       i4 = i2 - i;
/*     */       
/* 218 */       if (i2 == 16)
/* 219 */         return null;
/* 220 */       for (i1 = 1; i1 <= i4; i1++) {
/* 221 */         arrayOfByte1[(16 - i1)] = arrayOfByte1[(i + i4 - i1)];
/* 222 */         arrayOfByte1[(i + i4 - i1)] = 0;
/*     */       }
/* 224 */       i2 = 16;
/*     */     }
/* 226 */     if (i2 != 16)
/* 227 */       return null;
/* 228 */     byte[] arrayOfByte2 = convertFromIPv4MappedAddress(arrayOfByte1);
/* 229 */     if (arrayOfByte2 != null) {
/* 230 */       return arrayOfByte2;
/*     */     }
/* 232 */     return arrayOfByte1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean isIPv4LiteralAddress(String paramString)
/*     */   {
/* 241 */     return textToNumericFormatV4(paramString) != null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean isIPv6LiteralAddress(String paramString)
/*     */   {
/* 249 */     return textToNumericFormatV6(paramString) != null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static byte[] convertFromIPv4MappedAddress(byte[] paramArrayOfByte)
/*     */   {
/* 260 */     if (isIPv4MappedAddress(paramArrayOfByte)) {
/* 261 */       byte[] arrayOfByte = new byte[4];
/* 262 */       System.arraycopy(paramArrayOfByte, 12, arrayOfByte, 0, 4);
/* 263 */       return arrayOfByte;
/*     */     }
/* 265 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static boolean isIPv4MappedAddress(byte[] paramArrayOfByte)
/*     */   {
/* 276 */     if (paramArrayOfByte.length < 16) {
/* 277 */       return false;
/*     */     }
/* 279 */     if ((paramArrayOfByte[0] == 0) && (paramArrayOfByte[1] == 0) && (paramArrayOfByte[2] == 0) && (paramArrayOfByte[3] == 0) && (paramArrayOfByte[4] == 0) && (paramArrayOfByte[5] == 0) && (paramArrayOfByte[6] == 0) && (paramArrayOfByte[7] == 0) && (paramArrayOfByte[8] == 0) && (paramArrayOfByte[9] == 0) && (paramArrayOfByte[10] == -1) && (paramArrayOfByte[11] == -1))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 286 */       return true;
/*     */     }
/* 288 */     return false;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\net\util\IPAddressUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */