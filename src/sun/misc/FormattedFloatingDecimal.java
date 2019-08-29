/*     */ package sun.misc;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FormattedFloatingDecimal
/*     */ {
/*     */   private int decExponentRounded;
/*     */   private char[] mantissa;
/*     */   private char[] exponent;
/*     */   
/*     */   public static enum Form
/*     */   {
/*  32 */     SCIENTIFIC,  COMPATIBLE,  DECIMAL_FLOAT,  GENERAL;
/*     */     
/*     */     private Form() {}
/*     */   }
/*     */   
/*  37 */   public static FormattedFloatingDecimal valueOf(double paramDouble, int paramInt, Form paramForm) { FloatingDecimal.BinaryToASCIIConverter localBinaryToASCIIConverter = FloatingDecimal.getBinaryToASCIIConverter(paramDouble, paramForm == Form.COMPATIBLE);
/*  38 */     return new FormattedFloatingDecimal(paramInt, paramForm, localBinaryToASCIIConverter);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  45 */   private static final ThreadLocal<Object> threadLocalCharBuffer = new ThreadLocal()
/*     */   {
/*     */     protected Object initialValue()
/*     */     {
/*  49 */       return new char[20];
/*     */     }
/*     */   };
/*     */   
/*     */   private static char[] getBuffer() {
/*  54 */     return (char[])threadLocalCharBuffer.get();
/*     */   }
/*     */   
/*     */   private FormattedFloatingDecimal(int paramInt, Form paramForm, FloatingDecimal.BinaryToASCIIConverter paramBinaryToASCIIConverter) {
/*  58 */     if (paramBinaryToASCIIConverter.isExceptional()) {
/*  59 */       this.mantissa = paramBinaryToASCIIConverter.toJavaFormatString().toCharArray();
/*  60 */       this.exponent = null;
/*  61 */       return;
/*     */     }
/*  63 */     char[] arrayOfChar = getBuffer();
/*  64 */     int i = paramBinaryToASCIIConverter.getDigits(arrayOfChar);
/*  65 */     int j = paramBinaryToASCIIConverter.getDecimalExponent();
/*     */     
/*  67 */     boolean bool = paramBinaryToASCIIConverter.isNegative();
/*  68 */     int k; switch (paramForm) {
/*     */     case COMPATIBLE: 
/*  70 */       k = j;
/*  71 */       this.decExponentRounded = k;
/*  72 */       fillCompatible(paramInt, arrayOfChar, i, k, bool);
/*  73 */       break;
/*     */     case DECIMAL_FLOAT: 
/*  75 */       k = applyPrecision(j, arrayOfChar, i, j + paramInt);
/*  76 */       fillDecimal(paramInt, arrayOfChar, i, k, bool);
/*  77 */       this.decExponentRounded = k;
/*  78 */       break;
/*     */     case SCIENTIFIC: 
/*  80 */       k = applyPrecision(j, arrayOfChar, i, paramInt + 1);
/*  81 */       fillScientific(paramInt, arrayOfChar, i, k, bool);
/*  82 */       this.decExponentRounded = k;
/*  83 */       break;
/*     */     case GENERAL: 
/*  85 */       k = applyPrecision(j, arrayOfChar, i, paramInt);
/*     */       
/*     */ 
/*  88 */       if ((k - 1 < -4) || (k - 1 >= paramInt))
/*     */       {
/*  90 */         paramInt--;
/*  91 */         fillScientific(paramInt, arrayOfChar, i, k, bool);
/*     */       }
/*     */       else {
/*  94 */         paramInt -= k;
/*  95 */         fillDecimal(paramInt, arrayOfChar, i, k, bool);
/*     */       }
/*  97 */       this.decExponentRounded = k;
/*  98 */       break;
/*     */     default: 
/* 100 */       if (!$assertionsDisabled) throw new AssertionError();
/*     */       break;
/*     */     }
/*     */   }
/*     */   
/*     */   public int getExponentRounded() {
/* 106 */     return this.decExponentRounded - 1;
/*     */   }
/*     */   
/*     */   public char[] getMantissa() {
/* 110 */     return this.mantissa;
/*     */   }
/*     */   
/*     */   public char[] getExponent() {
/* 114 */     return this.exponent;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static int applyPrecision(int paramInt1, char[] paramArrayOfChar, int paramInt2, int paramInt3)
/*     */   {
/* 121 */     if ((paramInt3 >= paramInt2) || (paramInt3 < 0))
/*     */     {
/* 123 */       return paramInt1;
/*     */     }
/* 125 */     if (paramInt3 == 0)
/*     */     {
/*     */ 
/* 128 */       if (paramArrayOfChar[0] >= '5') {
/* 129 */         paramArrayOfChar[0] = '1';
/* 130 */         Arrays.fill(paramArrayOfChar, 1, paramInt2, '0');
/* 131 */         return paramInt1 + 1;
/*     */       }
/* 133 */       Arrays.fill(paramArrayOfChar, 0, paramInt2, '0');
/* 134 */       return paramInt1;
/*     */     }
/*     */     
/* 137 */     int i = paramArrayOfChar[paramInt3];
/* 138 */     if (i >= 53) {
/* 139 */       int j = paramInt3;
/* 140 */       i = paramArrayOfChar[(--j)];
/* 141 */       if (i == 57) {
/* 142 */         while ((i == 57) && (j > 0)) {
/* 143 */           i = paramArrayOfChar[(--j)];
/*     */         }
/* 145 */         if (i == 57)
/*     */         {
/* 147 */           paramArrayOfChar[0] = '1';
/* 148 */           Arrays.fill(paramArrayOfChar, 1, paramInt2, '0');
/* 149 */           return paramInt1 + 1;
/*     */         }
/*     */       }
/* 152 */       paramArrayOfChar[j] = ((char)(i + 1));
/* 153 */       Arrays.fill(paramArrayOfChar, j + 1, paramInt2, '0');
/*     */     } else {
/* 155 */       Arrays.fill(paramArrayOfChar, paramInt3, paramInt2, '0');
/*     */     }
/* 157 */     return paramInt1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void fillCompatible(int paramInt1, char[] paramArrayOfChar, int paramInt2, int paramInt3, boolean paramBoolean)
/*     */   {
/* 164 */     int i = paramBoolean ? 1 : 0;
/* 165 */     int j; if ((paramInt3 > 0) && (paramInt3 < 8))
/*     */     {
/* 167 */       if (paramInt2 < paramInt3) {
/* 168 */         j = paramInt3 - paramInt2;
/* 169 */         this.mantissa = create(paramBoolean, paramInt2 + j + 2);
/* 170 */         System.arraycopy(paramArrayOfChar, 0, this.mantissa, i, paramInt2);
/* 171 */         Arrays.fill(this.mantissa, i + paramInt2, i + paramInt2 + j, '0');
/* 172 */         this.mantissa[(i + paramInt2 + j)] = '.';
/* 173 */         this.mantissa[(i + paramInt2 + j + 1)] = '0';
/* 174 */       } else if (paramInt3 < paramInt2) {
/* 175 */         j = Math.min(paramInt2 - paramInt3, paramInt1);
/* 176 */         this.mantissa = create(paramBoolean, paramInt3 + 1 + j);
/* 177 */         System.arraycopy(paramArrayOfChar, 0, this.mantissa, i, paramInt3);
/* 178 */         this.mantissa[(i + paramInt3)] = '.';
/* 179 */         System.arraycopy(paramArrayOfChar, paramInt3, this.mantissa, i + paramInt3 + 1, j);
/*     */       } else {
/* 181 */         this.mantissa = create(paramBoolean, paramInt2 + 2);
/* 182 */         System.arraycopy(paramArrayOfChar, 0, this.mantissa, i, paramInt2);
/* 183 */         this.mantissa[(i + paramInt2)] = '.';
/* 184 */         this.mantissa[(i + paramInt2 + 1)] = '0';
/*     */       } } else { int k;
/* 186 */       if ((paramInt3 <= 0) && (paramInt3 > -3)) {
/* 187 */         j = Math.max(0, Math.min(-paramInt3, paramInt1));
/* 188 */         k = Math.max(0, Math.min(paramInt2, paramInt1 + paramInt3));
/*     */         
/* 190 */         if (j > 0) {
/* 191 */           this.mantissa = create(paramBoolean, j + 2 + k);
/* 192 */           this.mantissa[i] = '0';
/* 193 */           this.mantissa[(i + 1)] = '.';
/* 194 */           Arrays.fill(this.mantissa, i + 2, i + 2 + j, '0');
/* 195 */           if (k > 0)
/*     */           {
/* 197 */             System.arraycopy(paramArrayOfChar, 0, this.mantissa, i + 2 + j, k);
/*     */           }
/* 199 */         } else if (k > 0) {
/* 200 */           this.mantissa = create(paramBoolean, j + 2 + k);
/* 201 */           this.mantissa[i] = '0';
/* 202 */           this.mantissa[(i + 1)] = '.';
/*     */           
/* 204 */           System.arraycopy(paramArrayOfChar, 0, this.mantissa, i + 2, k);
/*     */         } else {
/* 206 */           this.mantissa = create(paramBoolean, 1);
/* 207 */           this.mantissa[i] = '0';
/*     */         }
/*     */       } else {
/* 210 */         if (paramInt2 > 1) {
/* 211 */           this.mantissa = create(paramBoolean, paramInt2 + 1);
/* 212 */           this.mantissa[i] = paramArrayOfChar[0];
/* 213 */           this.mantissa[(i + 1)] = '.';
/* 214 */           System.arraycopy(paramArrayOfChar, 1, this.mantissa, i + 2, paramInt2 - 1);
/*     */         } else {
/* 216 */           this.mantissa = create(paramBoolean, 3);
/* 217 */           this.mantissa[i] = paramArrayOfChar[0];
/* 218 */           this.mantissa[(i + 1)] = '.';
/* 219 */           this.mantissa[(i + 2)] = '0';
/*     */         }
/*     */         
/* 222 */         boolean bool = paramInt3 <= 0;
/* 223 */         if (bool) {
/* 224 */           j = -paramInt3 + 1;
/* 225 */           k = 1;
/*     */         } else {
/* 227 */           j = paramInt3 - 1;
/* 228 */           k = 0;
/*     */         }
/*     */         
/* 231 */         if (j <= 9) {
/* 232 */           this.exponent = create(bool, 1);
/* 233 */           this.exponent[k] = ((char)(j + 48));
/* 234 */         } else if (j <= 99) {
/* 235 */           this.exponent = create(bool, 2);
/* 236 */           this.exponent[k] = ((char)(j / 10 + 48));
/* 237 */           this.exponent[(k + 1)] = ((char)(j % 10 + 48));
/*     */         } else {
/* 239 */           this.exponent = create(bool, 3);
/* 240 */           this.exponent[k] = ((char)(j / 100 + 48));
/* 241 */           j %= 100;
/* 242 */           this.exponent[(k + 1)] = ((char)(j / 10 + 48));
/* 243 */           this.exponent[(k + 2)] = ((char)(j % 10 + 48));
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/* 249 */   private static char[] create(boolean paramBoolean, int paramInt) { if (paramBoolean) {
/* 250 */       char[] arrayOfChar = new char[paramInt + 1];
/* 251 */       arrayOfChar[0] = '-';
/* 252 */       return arrayOfChar;
/*     */     }
/* 254 */     return new char[paramInt];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void fillDecimal(int paramInt1, char[] paramArrayOfChar, int paramInt2, int paramInt3, boolean paramBoolean)
/*     */   {
/* 263 */     int i = paramBoolean ? 1 : 0;
/* 264 */     int j; if (paramInt3 > 0)
/*     */     {
/* 266 */       if (paramInt2 < paramInt3) {
/* 267 */         this.mantissa = create(paramBoolean, paramInt3);
/* 268 */         System.arraycopy(paramArrayOfChar, 0, this.mantissa, i, paramInt2);
/* 269 */         Arrays.fill(this.mantissa, i + paramInt2, i + paramInt3, '0');
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 274 */         j = Math.min(paramInt2 - paramInt3, paramInt1);
/* 275 */         this.mantissa = create(paramBoolean, paramInt3 + (j > 0 ? j + 1 : 0));
/* 276 */         System.arraycopy(paramArrayOfChar, 0, this.mantissa, i, paramInt3);
/*     */         
/*     */ 
/*     */ 
/* 280 */         if (j > 0) {
/* 281 */           this.mantissa[(i + paramInt3)] = '.';
/* 282 */           System.arraycopy(paramArrayOfChar, paramInt3, this.mantissa, i + paramInt3 + 1, j);
/*     */         }
/*     */       }
/* 285 */     } else if (paramInt3 <= 0) {
/* 286 */       j = Math.max(0, Math.min(-paramInt3, paramInt1));
/* 287 */       int k = Math.max(0, Math.min(paramInt2, paramInt1 + paramInt3));
/*     */       
/* 289 */       if (j > 0) {
/* 290 */         this.mantissa = create(paramBoolean, j + 2 + k);
/* 291 */         this.mantissa[i] = '0';
/* 292 */         this.mantissa[(i + 1)] = '.';
/* 293 */         Arrays.fill(this.mantissa, i + 2, i + 2 + j, '0');
/* 294 */         if (k > 0)
/*     */         {
/* 296 */           System.arraycopy(paramArrayOfChar, 0, this.mantissa, i + 2 + j, k);
/*     */         }
/* 298 */       } else if (k > 0) {
/* 299 */         this.mantissa = create(paramBoolean, j + 2 + k);
/* 300 */         this.mantissa[i] = '0';
/* 301 */         this.mantissa[(i + 1)] = '.';
/*     */         
/* 303 */         System.arraycopy(paramArrayOfChar, 0, this.mantissa, i + 2, k);
/*     */       } else {
/* 305 */         this.mantissa = create(paramBoolean, 1);
/* 306 */         this.mantissa[i] = '0';
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void fillScientific(int paramInt1, char[] paramArrayOfChar, int paramInt2, int paramInt3, boolean paramBoolean)
/*     */   {
/* 315 */     int i = paramBoolean ? 1 : 0;
/* 316 */     int j = Math.max(0, Math.min(paramInt2 - 1, paramInt1));
/* 317 */     if (j > 0) {
/* 318 */       this.mantissa = create(paramBoolean, j + 2);
/* 319 */       this.mantissa[i] = paramArrayOfChar[0];
/* 320 */       this.mantissa[(i + 1)] = '.';
/* 321 */       System.arraycopy(paramArrayOfChar, 1, this.mantissa, i + 2, j);
/*     */     } else {
/* 323 */       this.mantissa = create(paramBoolean, 1);
/* 324 */       this.mantissa[i] = paramArrayOfChar[0];
/*     */     }
/*     */     int k;
/*     */     int m;
/* 328 */     if (paramInt3 <= 0) {
/* 329 */       k = 45;
/* 330 */       m = -paramInt3 + 1;
/*     */     } else {
/* 332 */       k = 43;
/* 333 */       m = paramInt3 - 1;
/*     */     }
/*     */     
/* 336 */     if (m <= 9) {
/* 337 */       this.exponent = new char[] { k, '0', (char)(m + 48) };
/*     */     }
/* 339 */     else if (m <= 99) {
/* 340 */       this.exponent = new char[] { k, (char)(m / 10 + 48), (char)(m % 10 + 48) };
/*     */     }
/*     */     else {
/* 343 */       int n = (char)(m / 100 + 48);
/* 344 */       m %= 100;
/* 345 */       this.exponent = new char[] { k, n, (char)(m / 10 + 48), (char)(m % 10 + 48) };
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\misc\FormattedFloatingDecimal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */