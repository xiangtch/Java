/*     */ package sun.reflect;
/*     */ 
/*     */ import java.lang.reflect.Modifier;
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
/*     */ class AccessorGenerator
/*     */   implements ClassFileConstants
/*     */ {
/*  34 */   static final Unsafe unsafe = ;
/*     */   
/*     */   protected static final short S0 = 0;
/*     */   
/*     */   protected static final short S1 = 1;
/*     */   
/*     */   protected static final short S2 = 2;
/*     */   
/*     */   protected static final short S3 = 3;
/*     */   
/*     */   protected static final short S4 = 4;
/*     */   
/*     */   protected static final short S5 = 5;
/*     */   
/*     */   protected static final short S6 = 6;
/*     */   
/*     */   protected ClassFileAssembler asm;
/*     */   
/*     */   protected int modifiers;
/*     */   protected short thisClass;
/*     */   protected short superClass;
/*     */   protected short targetClass;
/*     */   protected short throwableClass;
/*     */   protected short classCastClass;
/*     */   protected short nullPointerClass;
/*     */   protected short illegalArgumentClass;
/*     */   protected short invocationTargetClass;
/*     */   protected short initIdx;
/*     */   protected short initNameAndTypeIdx;
/*     */   protected short initStringNameAndTypeIdx;
/*     */   protected short nullPointerCtorIdx;
/*     */   protected short illegalArgumentCtorIdx;
/*     */   protected short illegalArgumentStringCtorIdx;
/*     */   protected short invocationTargetCtorIdx;
/*     */   protected short superCtorIdx;
/*     */   protected short objectClass;
/*     */   protected short toStringIdx;
/*     */   protected short codeIdx;
/*     */   protected short exceptionsIdx;
/*     */   protected short booleanIdx;
/*     */   protected short booleanCtorIdx;
/*     */   protected short booleanUnboxIdx;
/*     */   protected short byteIdx;
/*     */   protected short byteCtorIdx;
/*     */   protected short byteUnboxIdx;
/*     */   protected short characterIdx;
/*     */   protected short characterCtorIdx;
/*     */   protected short characterUnboxIdx;
/*     */   protected short doubleIdx;
/*     */   protected short doubleCtorIdx;
/*     */   protected short doubleUnboxIdx;
/*     */   protected short floatIdx;
/*     */   protected short floatCtorIdx;
/*     */   protected short floatUnboxIdx;
/*     */   protected short integerIdx;
/*     */   protected short integerCtorIdx;
/*     */   protected short integerUnboxIdx;
/*     */   protected short longIdx;
/*     */   protected short longCtorIdx;
/*     */   protected short longUnboxIdx;
/*     */   protected short shortIdx;
/*     */   protected short shortCtorIdx;
/*     */   protected short shortUnboxIdx;
/*  97 */   protected final short NUM_COMMON_CPOOL_ENTRIES = 30;
/*  98 */   protected final short NUM_BOXING_CPOOL_ENTRIES = 72;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void emitCommonConstantPoolEntries()
/*     */   {
/* 132 */     this.asm.emitConstantPoolUTF8("java/lang/Throwable");
/* 133 */     this.asm.emitConstantPoolClass(this.asm.cpi());
/* 134 */     this.throwableClass = this.asm.cpi();
/* 135 */     this.asm.emitConstantPoolUTF8("java/lang/ClassCastException");
/* 136 */     this.asm.emitConstantPoolClass(this.asm.cpi());
/* 137 */     this.classCastClass = this.asm.cpi();
/* 138 */     this.asm.emitConstantPoolUTF8("java/lang/NullPointerException");
/* 139 */     this.asm.emitConstantPoolClass(this.asm.cpi());
/* 140 */     this.nullPointerClass = this.asm.cpi();
/* 141 */     this.asm.emitConstantPoolUTF8("java/lang/IllegalArgumentException");
/* 142 */     this.asm.emitConstantPoolClass(this.asm.cpi());
/* 143 */     this.illegalArgumentClass = this.asm.cpi();
/* 144 */     this.asm.emitConstantPoolUTF8("java/lang/reflect/InvocationTargetException");
/* 145 */     this.asm.emitConstantPoolClass(this.asm.cpi());
/* 146 */     this.invocationTargetClass = this.asm.cpi();
/* 147 */     this.asm.emitConstantPoolUTF8("<init>");
/* 148 */     this.initIdx = this.asm.cpi();
/* 149 */     this.asm.emitConstantPoolUTF8("()V");
/* 150 */     this.asm.emitConstantPoolNameAndType(this.initIdx, this.asm.cpi());
/* 151 */     this.initNameAndTypeIdx = this.asm.cpi();
/* 152 */     this.asm.emitConstantPoolMethodref(this.nullPointerClass, this.initNameAndTypeIdx);
/* 153 */     this.nullPointerCtorIdx = this.asm.cpi();
/* 154 */     this.asm.emitConstantPoolMethodref(this.illegalArgumentClass, this.initNameAndTypeIdx);
/* 155 */     this.illegalArgumentCtorIdx = this.asm.cpi();
/* 156 */     this.asm.emitConstantPoolUTF8("(Ljava/lang/String;)V");
/* 157 */     this.asm.emitConstantPoolNameAndType(this.initIdx, this.asm.cpi());
/* 158 */     this.initStringNameAndTypeIdx = this.asm.cpi();
/* 159 */     this.asm.emitConstantPoolMethodref(this.illegalArgumentClass, this.initStringNameAndTypeIdx);
/* 160 */     this.illegalArgumentStringCtorIdx = this.asm.cpi();
/* 161 */     this.asm.emitConstantPoolUTF8("(Ljava/lang/Throwable;)V");
/* 162 */     this.asm.emitConstantPoolNameAndType(this.initIdx, this.asm.cpi());
/* 163 */     this.asm.emitConstantPoolMethodref(this.invocationTargetClass, this.asm.cpi());
/* 164 */     this.invocationTargetCtorIdx = this.asm.cpi();
/* 165 */     this.asm.emitConstantPoolMethodref(this.superClass, this.initNameAndTypeIdx);
/* 166 */     this.superCtorIdx = this.asm.cpi();
/* 167 */     this.asm.emitConstantPoolUTF8("java/lang/Object");
/* 168 */     this.asm.emitConstantPoolClass(this.asm.cpi());
/* 169 */     this.objectClass = this.asm.cpi();
/* 170 */     this.asm.emitConstantPoolUTF8("toString");
/* 171 */     this.asm.emitConstantPoolUTF8("()Ljava/lang/String;");
/* 172 */     this.asm.emitConstantPoolNameAndType(sub(this.asm.cpi(), (short)1), this.asm.cpi());
/* 173 */     this.asm.emitConstantPoolMethodref(this.objectClass, this.asm.cpi());
/* 174 */     this.toStringIdx = this.asm.cpi();
/* 175 */     this.asm.emitConstantPoolUTF8("Code");
/* 176 */     this.codeIdx = this.asm.cpi();
/* 177 */     this.asm.emitConstantPoolUTF8("Exceptions");
/* 178 */     this.exceptionsIdx = this.asm.cpi();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void emitBoxingContantPoolEntries()
/*     */   {
/* 257 */     this.asm.emitConstantPoolUTF8("java/lang/Boolean");
/* 258 */     this.asm.emitConstantPoolClass(this.asm.cpi());
/* 259 */     this.booleanIdx = this.asm.cpi();
/* 260 */     this.asm.emitConstantPoolUTF8("(Z)V");
/* 261 */     this.asm.emitConstantPoolNameAndType(this.initIdx, this.asm.cpi());
/* 262 */     this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)2), this.asm.cpi());
/* 263 */     this.booleanCtorIdx = this.asm.cpi();
/* 264 */     this.asm.emitConstantPoolUTF8("booleanValue");
/* 265 */     this.asm.emitConstantPoolUTF8("()Z");
/* 266 */     this.asm.emitConstantPoolNameAndType(sub(this.asm.cpi(), (short)1), this.asm.cpi());
/* 267 */     this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)6), this.asm.cpi());
/* 268 */     this.booleanUnboxIdx = this.asm.cpi();
/*     */     
/*     */ 
/* 271 */     this.asm.emitConstantPoolUTF8("java/lang/Byte");
/* 272 */     this.asm.emitConstantPoolClass(this.asm.cpi());
/* 273 */     this.byteIdx = this.asm.cpi();
/* 274 */     this.asm.emitConstantPoolUTF8("(B)V");
/* 275 */     this.asm.emitConstantPoolNameAndType(this.initIdx, this.asm.cpi());
/* 276 */     this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)2), this.asm.cpi());
/* 277 */     this.byteCtorIdx = this.asm.cpi();
/* 278 */     this.asm.emitConstantPoolUTF8("byteValue");
/* 279 */     this.asm.emitConstantPoolUTF8("()B");
/* 280 */     this.asm.emitConstantPoolNameAndType(sub(this.asm.cpi(), (short)1), this.asm.cpi());
/* 281 */     this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)6), this.asm.cpi());
/* 282 */     this.byteUnboxIdx = this.asm.cpi();
/*     */     
/*     */ 
/* 285 */     this.asm.emitConstantPoolUTF8("java/lang/Character");
/* 286 */     this.asm.emitConstantPoolClass(this.asm.cpi());
/* 287 */     this.characterIdx = this.asm.cpi();
/* 288 */     this.asm.emitConstantPoolUTF8("(C)V");
/* 289 */     this.asm.emitConstantPoolNameAndType(this.initIdx, this.asm.cpi());
/* 290 */     this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)2), this.asm.cpi());
/* 291 */     this.characterCtorIdx = this.asm.cpi();
/* 292 */     this.asm.emitConstantPoolUTF8("charValue");
/* 293 */     this.asm.emitConstantPoolUTF8("()C");
/* 294 */     this.asm.emitConstantPoolNameAndType(sub(this.asm.cpi(), (short)1), this.asm.cpi());
/* 295 */     this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)6), this.asm.cpi());
/* 296 */     this.characterUnboxIdx = this.asm.cpi();
/*     */     
/*     */ 
/* 299 */     this.asm.emitConstantPoolUTF8("java/lang/Double");
/* 300 */     this.asm.emitConstantPoolClass(this.asm.cpi());
/* 301 */     this.doubleIdx = this.asm.cpi();
/* 302 */     this.asm.emitConstantPoolUTF8("(D)V");
/* 303 */     this.asm.emitConstantPoolNameAndType(this.initIdx, this.asm.cpi());
/* 304 */     this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)2), this.asm.cpi());
/* 305 */     this.doubleCtorIdx = this.asm.cpi();
/* 306 */     this.asm.emitConstantPoolUTF8("doubleValue");
/* 307 */     this.asm.emitConstantPoolUTF8("()D");
/* 308 */     this.asm.emitConstantPoolNameAndType(sub(this.asm.cpi(), (short)1), this.asm.cpi());
/* 309 */     this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)6), this.asm.cpi());
/* 310 */     this.doubleUnboxIdx = this.asm.cpi();
/*     */     
/*     */ 
/* 313 */     this.asm.emitConstantPoolUTF8("java/lang/Float");
/* 314 */     this.asm.emitConstantPoolClass(this.asm.cpi());
/* 315 */     this.floatIdx = this.asm.cpi();
/* 316 */     this.asm.emitConstantPoolUTF8("(F)V");
/* 317 */     this.asm.emitConstantPoolNameAndType(this.initIdx, this.asm.cpi());
/* 318 */     this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)2), this.asm.cpi());
/* 319 */     this.floatCtorIdx = this.asm.cpi();
/* 320 */     this.asm.emitConstantPoolUTF8("floatValue");
/* 321 */     this.asm.emitConstantPoolUTF8("()F");
/* 322 */     this.asm.emitConstantPoolNameAndType(sub(this.asm.cpi(), (short)1), this.asm.cpi());
/* 323 */     this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)6), this.asm.cpi());
/* 324 */     this.floatUnboxIdx = this.asm.cpi();
/*     */     
/*     */ 
/* 327 */     this.asm.emitConstantPoolUTF8("java/lang/Integer");
/* 328 */     this.asm.emitConstantPoolClass(this.asm.cpi());
/* 329 */     this.integerIdx = this.asm.cpi();
/* 330 */     this.asm.emitConstantPoolUTF8("(I)V");
/* 331 */     this.asm.emitConstantPoolNameAndType(this.initIdx, this.asm.cpi());
/* 332 */     this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)2), this.asm.cpi());
/* 333 */     this.integerCtorIdx = this.asm.cpi();
/* 334 */     this.asm.emitConstantPoolUTF8("intValue");
/* 335 */     this.asm.emitConstantPoolUTF8("()I");
/* 336 */     this.asm.emitConstantPoolNameAndType(sub(this.asm.cpi(), (short)1), this.asm.cpi());
/* 337 */     this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)6), this.asm.cpi());
/* 338 */     this.integerUnboxIdx = this.asm.cpi();
/*     */     
/*     */ 
/* 341 */     this.asm.emitConstantPoolUTF8("java/lang/Long");
/* 342 */     this.asm.emitConstantPoolClass(this.asm.cpi());
/* 343 */     this.longIdx = this.asm.cpi();
/* 344 */     this.asm.emitConstantPoolUTF8("(J)V");
/* 345 */     this.asm.emitConstantPoolNameAndType(this.initIdx, this.asm.cpi());
/* 346 */     this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)2), this.asm.cpi());
/* 347 */     this.longCtorIdx = this.asm.cpi();
/* 348 */     this.asm.emitConstantPoolUTF8("longValue");
/* 349 */     this.asm.emitConstantPoolUTF8("()J");
/* 350 */     this.asm.emitConstantPoolNameAndType(sub(this.asm.cpi(), (short)1), this.asm.cpi());
/* 351 */     this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)6), this.asm.cpi());
/* 352 */     this.longUnboxIdx = this.asm.cpi();
/*     */     
/*     */ 
/* 355 */     this.asm.emitConstantPoolUTF8("java/lang/Short");
/* 356 */     this.asm.emitConstantPoolClass(this.asm.cpi());
/* 357 */     this.shortIdx = this.asm.cpi();
/* 358 */     this.asm.emitConstantPoolUTF8("(S)V");
/* 359 */     this.asm.emitConstantPoolNameAndType(this.initIdx, this.asm.cpi());
/* 360 */     this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)2), this.asm.cpi());
/* 361 */     this.shortCtorIdx = this.asm.cpi();
/* 362 */     this.asm.emitConstantPoolUTF8("shortValue");
/* 363 */     this.asm.emitConstantPoolUTF8("()S");
/* 364 */     this.asm.emitConstantPoolNameAndType(sub(this.asm.cpi(), (short)1), this.asm.cpi());
/* 365 */     this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)6), this.asm.cpi());
/* 366 */     this.shortUnboxIdx = this.asm.cpi();
/*     */   }
/*     */   
/*     */   protected static short add(short paramShort1, short paramShort2)
/*     */   {
/* 371 */     return (short)(paramShort1 + paramShort2);
/*     */   }
/*     */   
/*     */   protected static short sub(short paramShort1, short paramShort2) {
/* 375 */     return (short)(paramShort1 - paramShort2);
/*     */   }
/*     */   
/*     */   protected boolean isStatic() {
/* 379 */     return Modifier.isStatic(this.modifiers);
/*     */   }
/*     */   
/*     */   protected boolean isPrivate() {
/* 383 */     return Modifier.isPrivate(this.modifiers);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected static String getClassName(Class<?> paramClass, boolean paramBoolean)
/*     */   {
/* 391 */     if (paramClass.isPrimitive()) {
/* 392 */       if (paramClass == Boolean.TYPE)
/* 393 */         return "Z";
/* 394 */       if (paramClass == Byte.TYPE)
/* 395 */         return "B";
/* 396 */       if (paramClass == Character.TYPE)
/* 397 */         return "C";
/* 398 */       if (paramClass == Double.TYPE)
/* 399 */         return "D";
/* 400 */       if (paramClass == Float.TYPE)
/* 401 */         return "F";
/* 402 */       if (paramClass == Integer.TYPE)
/* 403 */         return "I";
/* 404 */       if (paramClass == Long.TYPE)
/* 405 */         return "J";
/* 406 */       if (paramClass == Short.TYPE)
/* 407 */         return "S";
/* 408 */       if (paramClass == Void.TYPE) {
/* 409 */         return "V";
/*     */       }
/* 411 */       throw new InternalError("Should have found primitive type"); }
/* 412 */     if (paramClass.isArray()) {
/* 413 */       return "[" + getClassName(paramClass.getComponentType(), true);
/*     */     }
/* 415 */     if (paramBoolean) {
/* 416 */       return internalize("L" + paramClass.getName() + ";");
/*     */     }
/* 418 */     return internalize(paramClass.getName());
/*     */   }
/*     */   
/*     */ 
/*     */   private static String internalize(String paramString)
/*     */   {
/* 424 */     return paramString.replace('.', '/');
/*     */   }
/*     */   
/*     */   protected void emitConstructor()
/*     */   {
/* 429 */     ClassFileAssembler localClassFileAssembler = new ClassFileAssembler();
/*     */     
/* 431 */     localClassFileAssembler.setMaxLocals(1);
/* 432 */     localClassFileAssembler.opc_aload_0();
/* 433 */     localClassFileAssembler.opc_invokespecial(this.superCtorIdx, 0, 0);
/* 434 */     localClassFileAssembler.opc_return();
/*     */     
/*     */ 
/* 437 */     emitMethod(this.initIdx, localClassFileAssembler.getMaxLocals(), localClassFileAssembler, null, null);
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
/*     */   protected void emitMethod(short paramShort, int paramInt, ClassFileAssembler paramClassFileAssembler1, ClassFileAssembler paramClassFileAssembler2, short[] paramArrayOfShort)
/*     */   {
/* 453 */     int i = paramClassFileAssembler1.getLength();
/* 454 */     int j = 0;
/* 455 */     if (paramClassFileAssembler2 != null) {
/* 456 */       j = paramClassFileAssembler2.getLength();
/* 457 */       if (j % 8 != 0) {
/* 458 */         throw new IllegalArgumentException("Illegal exception table");
/*     */       }
/*     */     }
/* 461 */     int k = 12 + i + j;
/* 462 */     j /= 8;
/*     */     
/* 464 */     this.asm.emitShort((short)1);
/* 465 */     this.asm.emitShort(paramShort);
/* 466 */     this.asm.emitShort(add(paramShort, (short)1));
/* 467 */     if (paramArrayOfShort == null)
/*     */     {
/* 469 */       this.asm.emitShort((short)1);
/*     */     }
/*     */     else {
/* 472 */       this.asm.emitShort((short)2);
/*     */     }
/*     */     
/* 475 */     this.asm.emitShort(this.codeIdx);
/* 476 */     this.asm.emitInt(k);
/* 477 */     this.asm.emitShort(paramClassFileAssembler1.getMaxStack());
/* 478 */     this.asm.emitShort((short)Math.max(paramInt, paramClassFileAssembler1.getMaxLocals()));
/* 479 */     this.asm.emitInt(i);
/* 480 */     this.asm.append(paramClassFileAssembler1);
/* 481 */     this.asm.emitShort((short)j);
/* 482 */     if (paramClassFileAssembler2 != null) {
/* 483 */       this.asm.append(paramClassFileAssembler2);
/*     */     }
/* 485 */     this.asm.emitShort((short)0);
/* 486 */     if (paramArrayOfShort != null)
/*     */     {
/* 488 */       this.asm.emitShort(this.exceptionsIdx);
/* 489 */       this.asm.emitInt(2 + 2 * paramArrayOfShort.length);
/* 490 */       this.asm.emitShort((short)paramArrayOfShort.length);
/* 491 */       for (int m = 0; m < paramArrayOfShort.length; m++) {
/* 492 */         this.asm.emitShort(paramArrayOfShort[m]);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   protected short indexForPrimitiveType(Class<?> paramClass) {
/* 498 */     if (paramClass == Boolean.TYPE)
/* 499 */       return this.booleanIdx;
/* 500 */     if (paramClass == Byte.TYPE)
/* 501 */       return this.byteIdx;
/* 502 */     if (paramClass == Character.TYPE)
/* 503 */       return this.characterIdx;
/* 504 */     if (paramClass == Double.TYPE)
/* 505 */       return this.doubleIdx;
/* 506 */     if (paramClass == Float.TYPE)
/* 507 */       return this.floatIdx;
/* 508 */     if (paramClass == Integer.TYPE)
/* 509 */       return this.integerIdx;
/* 510 */     if (paramClass == Long.TYPE)
/* 511 */       return this.longIdx;
/* 512 */     if (paramClass == Short.TYPE) {
/* 513 */       return this.shortIdx;
/*     */     }
/* 515 */     throw new InternalError("Should have found primitive type");
/*     */   }
/*     */   
/*     */   protected short ctorIndexForPrimitiveType(Class<?> paramClass) {
/* 519 */     if (paramClass == Boolean.TYPE)
/* 520 */       return this.booleanCtorIdx;
/* 521 */     if (paramClass == Byte.TYPE)
/* 522 */       return this.byteCtorIdx;
/* 523 */     if (paramClass == Character.TYPE)
/* 524 */       return this.characterCtorIdx;
/* 525 */     if (paramClass == Double.TYPE)
/* 526 */       return this.doubleCtorIdx;
/* 527 */     if (paramClass == Float.TYPE)
/* 528 */       return this.floatCtorIdx;
/* 529 */     if (paramClass == Integer.TYPE)
/* 530 */       return this.integerCtorIdx;
/* 531 */     if (paramClass == Long.TYPE)
/* 532 */       return this.longCtorIdx;
/* 533 */     if (paramClass == Short.TYPE) {
/* 534 */       return this.shortCtorIdx;
/*     */     }
/* 536 */     throw new InternalError("Should have found primitive type");
/*     */   }
/*     */   
/*     */ 
/*     */   protected static boolean canWidenTo(Class<?> paramClass1, Class<?> paramClass2)
/*     */   {
/* 542 */     if (!paramClass1.isPrimitive()) {
/* 543 */       return false;
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
/* 554 */     if (paramClass1 == Boolean.TYPE) {
/* 555 */       if (paramClass2 == Boolean.TYPE) {
/* 556 */         return true;
/*     */       }
/* 558 */     } else if (paramClass1 == Byte.TYPE) {
/* 559 */       if ((paramClass2 == Byte.TYPE) || (paramClass2 == Short.TYPE) || (paramClass2 == Integer.TYPE) || (paramClass2 == Long.TYPE) || (paramClass2 == Float.TYPE) || (paramClass2 == Double.TYPE))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 565 */         return true;
/*     */       }
/* 567 */     } else if (paramClass1 == Short.TYPE) {
/* 568 */       if ((paramClass2 == Short.TYPE) || (paramClass2 == Integer.TYPE) || (paramClass2 == Long.TYPE) || (paramClass2 == Float.TYPE) || (paramClass2 == Double.TYPE))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 573 */         return true;
/*     */       }
/* 575 */     } else if (paramClass1 == Character.TYPE) {
/* 576 */       if ((paramClass2 == Character.TYPE) || (paramClass2 == Integer.TYPE) || (paramClass2 == Long.TYPE) || (paramClass2 == Float.TYPE) || (paramClass2 == Double.TYPE))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 581 */         return true;
/*     */       }
/* 583 */     } else if (paramClass1 == Integer.TYPE) {
/* 584 */       if ((paramClass2 == Integer.TYPE) || (paramClass2 == Long.TYPE) || (paramClass2 == Float.TYPE) || (paramClass2 == Double.TYPE))
/*     */       {
/*     */ 
/*     */ 
/* 588 */         return true;
/*     */       }
/* 590 */     } else if (paramClass1 == Long.TYPE) {
/* 591 */       if ((paramClass2 == Long.TYPE) || (paramClass2 == Float.TYPE) || (paramClass2 == Double.TYPE))
/*     */       {
/*     */ 
/* 594 */         return true;
/*     */       }
/* 596 */     } else if (paramClass1 == Float.TYPE) {
/* 597 */       if ((paramClass2 == Float.TYPE) || (paramClass2 == Double.TYPE))
/*     */       {
/* 599 */         return true;
/*     */       }
/* 601 */     } else if ((paramClass1 == Double.TYPE) && 
/* 602 */       (paramClass2 == Double.TYPE)) {
/* 603 */       return true;
/*     */     }
/*     */     
/*     */ 
/* 607 */     return false;
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
/*     */   protected static void emitWideningBytecodeForPrimitiveConversion(ClassFileAssembler paramClassFileAssembler, Class<?> paramClass1, Class<?> paramClass2)
/*     */   {
/* 631 */     if ((paramClass1 == Byte.TYPE) || (paramClass1 == Short.TYPE) || (paramClass1 == Character.TYPE) || (paramClass1 == Integer.TYPE))
/*     */     {
/*     */ 
/*     */ 
/* 635 */       if (paramClass2 == Long.TYPE) {
/* 636 */         paramClassFileAssembler.opc_i2l();
/* 637 */       } else if (paramClass2 == Float.TYPE) {
/* 638 */         paramClassFileAssembler.opc_i2f();
/* 639 */       } else if (paramClass2 == Double.TYPE) {
/* 640 */         paramClassFileAssembler.opc_i2d();
/*     */       }
/* 642 */     } else if (paramClass1 == Long.TYPE) {
/* 643 */       if (paramClass2 == Float.TYPE) {
/* 644 */         paramClassFileAssembler.opc_l2f();
/* 645 */       } else if (paramClass2 == Double.TYPE) {
/* 646 */         paramClassFileAssembler.opc_l2d();
/*     */       }
/* 648 */     } else if ((paramClass1 == Float.TYPE) && 
/* 649 */       (paramClass2 == Double.TYPE)) {
/* 650 */       paramClassFileAssembler.opc_f2d();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected short unboxingMethodForPrimitiveType(Class<?> paramClass)
/*     */   {
/* 658 */     if (paramClass == Boolean.TYPE)
/* 659 */       return this.booleanUnboxIdx;
/* 660 */     if (paramClass == Byte.TYPE)
/* 661 */       return this.byteUnboxIdx;
/* 662 */     if (paramClass == Character.TYPE)
/* 663 */       return this.characterUnboxIdx;
/* 664 */     if (paramClass == Short.TYPE)
/* 665 */       return this.shortUnboxIdx;
/* 666 */     if (paramClass == Integer.TYPE)
/* 667 */       return this.integerUnboxIdx;
/* 668 */     if (paramClass == Long.TYPE)
/* 669 */       return this.longUnboxIdx;
/* 670 */     if (paramClass == Float.TYPE)
/* 671 */       return this.floatUnboxIdx;
/* 672 */     if (paramClass == Double.TYPE) {
/* 673 */       return this.doubleUnboxIdx;
/*     */     }
/* 675 */     throw new InternalError("Illegal primitive type " + paramClass.getName());
/*     */   }
/*     */   
/* 678 */   protected static final Class<?>[] primitiveTypes = { Boolean.TYPE, Byte.TYPE, Character.TYPE, Short.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private ClassFileAssembler illegalArgumentCodeBuffer;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected static boolean isPrimitive(Class<?> paramClass)
/*     */   {
/* 691 */     return (paramClass.isPrimitive()) && (paramClass != Void.TYPE);
/*     */   }
/*     */   
/*     */   protected int typeSizeInStackSlots(Class<?> paramClass) {
/* 695 */     if (paramClass == Void.TYPE) {
/* 696 */       return 0;
/*     */     }
/* 698 */     if ((paramClass == Long.TYPE) || (paramClass == Double.TYPE)) {
/* 699 */       return 2;
/*     */     }
/* 701 */     return 1;
/*     */   }
/*     */   
/*     */   protected ClassFileAssembler illegalArgumentCodeBuffer()
/*     */   {
/* 706 */     if (this.illegalArgumentCodeBuffer == null) {
/* 707 */       this.illegalArgumentCodeBuffer = new ClassFileAssembler();
/* 708 */       this.illegalArgumentCodeBuffer.opc_new(this.illegalArgumentClass);
/* 709 */       this.illegalArgumentCodeBuffer.opc_dup();
/* 710 */       this.illegalArgumentCodeBuffer.opc_invokespecial(this.illegalArgumentCtorIdx, 0, 0);
/* 711 */       this.illegalArgumentCodeBuffer.opc_athrow();
/*     */     }
/*     */     
/* 714 */     return this.illegalArgumentCodeBuffer;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\reflect\AccessorGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */