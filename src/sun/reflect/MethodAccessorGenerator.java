/*     */ package sun.reflect;
/*     */ 
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
/*     */ class MethodAccessorGenerator
/*     */   extends AccessorGenerator
/*     */ {
/*     */   private static final short NUM_BASE_CPOOL_ENTRIES = 12;
/*     */   private static final short NUM_METHODS = 2;
/*     */   private static final short NUM_SERIALIZATION_CPOOL_ENTRIES = 2;
/*  47 */   private static volatile int methodSymnum = 0;
/*  48 */   private static volatile int constructorSymnum = 0;
/*  49 */   private static volatile int serializationConstructorSymnum = 0;
/*     */   
/*     */ 
/*     */   private Class<?> declaringClass;
/*     */   
/*     */ 
/*     */   private Class<?>[] parameterTypes;
/*     */   
/*     */ 
/*     */   private Class<?> returnType;
/*     */   
/*     */   private boolean isConstructor;
/*     */   
/*     */   private boolean forSerialization;
/*     */   
/*     */   private short targetMethodRef;
/*     */   
/*     */   private short invokeIdx;
/*     */   
/*     */   private short invokeDescriptorIdx;
/*     */   
/*     */   private short nonPrimitiveParametersBaseIdx;
/*     */   
/*     */ 
/*     */   public MethodAccessor generateMethod(Class<?> paramClass1, String paramString, Class<?>[] paramArrayOfClass1, Class<?> paramClass2, Class<?>[] paramArrayOfClass2, int paramInt)
/*     */   {
/*  75 */     return (MethodAccessor)generate(paramClass1, paramString, paramArrayOfClass1, paramClass2, paramArrayOfClass2, paramInt, false, false, null);
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
/*     */   public ConstructorAccessor generateConstructor(Class<?> paramClass, Class<?>[] paramArrayOfClass1, Class<?>[] paramArrayOfClass2, int paramInt)
/*     */   {
/*  92 */     return (ConstructorAccessor)generate(paramClass, "<init>", paramArrayOfClass1, Void.TYPE, paramArrayOfClass2, paramInt, true, false, null);
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
/*     */   public SerializationConstructorAccessorImpl generateSerializationConstructor(Class<?> paramClass1, Class<?>[] paramArrayOfClass1, Class<?>[] paramArrayOfClass2, int paramInt, Class<?> paramClass2)
/*     */   {
/* 111 */     return 
/* 112 */       (SerializationConstructorAccessorImpl)generate(paramClass1, "<init>", paramArrayOfClass1, Void.TYPE, paramArrayOfClass2, paramInt, true, true, paramClass2);
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
/*     */   private MagicAccessorImpl generate(final Class<?> paramClass1, String paramString, Class<?>[] paramArrayOfClass1, Class<?> paramClass2, Class<?>[] paramArrayOfClass2, int paramInt, boolean paramBoolean1, boolean paramBoolean2, Class<?> paramClass3)
/*     */   {
/* 134 */     ByteVector localByteVector = ByteVectorFactory.create();
/* 135 */     this.asm = new ClassFileAssembler(localByteVector);
/* 136 */     this.declaringClass = paramClass1;
/* 137 */     this.parameterTypes = paramArrayOfClass1;
/* 138 */     this.returnType = paramClass2;
/* 139 */     this.modifiers = paramInt;
/* 140 */     this.isConstructor = paramBoolean1;
/* 141 */     this.forSerialization = paramBoolean2;
/*     */     
/* 143 */     this.asm.emitMagicAndVersion();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 271 */     short s1 = 42;
/* 272 */     boolean bool = usesPrimitiveTypes();
/* 273 */     if (bool) {
/* 274 */       s1 = (short)(s1 + 72);
/*     */     }
/* 276 */     if (paramBoolean2) {
/* 277 */       s1 = (short)(s1 + 2);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 282 */     s1 = (short)(s1 + (short)(2 * numNonPrimitiveParameterTypes()));
/*     */     
/* 284 */     this.asm.emitShort(add(s1, (short)1));
/*     */     
/* 286 */     final String str = generateName(paramBoolean1, paramBoolean2);
/* 287 */     this.asm.emitConstantPoolUTF8(str);
/* 288 */     this.asm.emitConstantPoolClass(this.asm.cpi());
/* 289 */     this.thisClass = this.asm.cpi();
/* 290 */     if (paramBoolean1) {
/* 291 */       if (paramBoolean2)
/*     */       {
/* 293 */         this.asm.emitConstantPoolUTF8("sun/reflect/SerializationConstructorAccessorImpl");
/*     */       } else {
/* 295 */         this.asm.emitConstantPoolUTF8("sun/reflect/ConstructorAccessorImpl");
/*     */       }
/*     */     } else {
/* 298 */       this.asm.emitConstantPoolUTF8("sun/reflect/MethodAccessorImpl");
/*     */     }
/* 300 */     this.asm.emitConstantPoolClass(this.asm.cpi());
/* 301 */     this.superClass = this.asm.cpi();
/* 302 */     this.asm.emitConstantPoolUTF8(getClassName(paramClass1, false));
/* 303 */     this.asm.emitConstantPoolClass(this.asm.cpi());
/* 304 */     this.targetClass = this.asm.cpi();
/* 305 */     short s2 = 0;
/* 306 */     if (paramBoolean2) {
/* 307 */       this.asm.emitConstantPoolUTF8(getClassName(paramClass3, false));
/* 308 */       this.asm.emitConstantPoolClass(this.asm.cpi());
/* 309 */       s2 = this.asm.cpi();
/*     */     }
/* 311 */     this.asm.emitConstantPoolUTF8(paramString);
/* 312 */     this.asm.emitConstantPoolUTF8(buildInternalSignature());
/* 313 */     this.asm.emitConstantPoolNameAndType(sub(this.asm.cpi(), (short)1), this.asm.cpi());
/* 314 */     if (isInterface()) {
/* 315 */       this.asm.emitConstantPoolInterfaceMethodref(this.targetClass, this.asm.cpi());
/*     */     }
/* 317 */     else if (paramBoolean2) {
/* 318 */       this.asm.emitConstantPoolMethodref(s2, this.asm.cpi());
/*     */     } else {
/* 320 */       this.asm.emitConstantPoolMethodref(this.targetClass, this.asm.cpi());
/*     */     }
/*     */     
/* 323 */     this.targetMethodRef = this.asm.cpi();
/* 324 */     if (paramBoolean1) {
/* 325 */       this.asm.emitConstantPoolUTF8("newInstance");
/*     */     } else {
/* 327 */       this.asm.emitConstantPoolUTF8("invoke");
/*     */     }
/* 329 */     this.invokeIdx = this.asm.cpi();
/* 330 */     if (paramBoolean1) {
/* 331 */       this.asm.emitConstantPoolUTF8("([Ljava/lang/Object;)Ljava/lang/Object;");
/*     */     }
/*     */     else {
/* 334 */       this.asm.emitConstantPoolUTF8("(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;");
/*     */     }
/* 336 */     this.invokeDescriptorIdx = this.asm.cpi();
/*     */     
/*     */ 
/* 339 */     this.nonPrimitiveParametersBaseIdx = add(this.asm.cpi(), (short)2);
/* 340 */     for (int i = 0; i < paramArrayOfClass1.length; i++) {
/* 341 */       Class<?> localClass = paramArrayOfClass1[i];
/* 342 */       if (!isPrimitive(localClass)) {
/* 343 */         this.asm.emitConstantPoolUTF8(getClassName(localClass, false));
/* 344 */         this.asm.emitConstantPoolClass(this.asm.cpi());
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 349 */     emitCommonConstantPoolEntries();
/*     */     
/*     */ 
/* 352 */     if (bool) {
/* 353 */       emitBoxingContantPoolEntries();
/*     */     }
/*     */     
/* 356 */     if (this.asm.cpi() != s1) {
/* 357 */       throw new InternalError("Adjust this code (cpi = " + this.asm.cpi() + ", numCPEntries = " + s1 + ")");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 362 */     this.asm.emitShort((short)1);
/*     */     
/*     */ 
/* 365 */     this.asm.emitShort(this.thisClass);
/*     */     
/*     */ 
/* 368 */     this.asm.emitShort(this.superClass);
/*     */     
/*     */ 
/* 371 */     this.asm.emitShort((short)0);
/*     */     
/*     */ 
/* 374 */     this.asm.emitShort((short)0);
/*     */     
/*     */ 
/* 377 */     this.asm.emitShort((short)2);
/*     */     
/* 379 */     emitConstructor();
/* 380 */     emitInvoke();
/*     */     
/*     */ 
/* 383 */     this.asm.emitShort((short)0);
/*     */     
/*     */ 
/* 386 */     localByteVector.trim();
/* 387 */     final byte[] arrayOfByte = localByteVector.getData();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 393 */     (MagicAccessorImpl)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public MagicAccessorImpl run() {
/*     */         try {
/* 397 */           return 
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 403 */             (MagicAccessorImpl)ClassDefiner.defineClass(str, arrayOfByte, 0, arrayOfByte.length, paramClass1.getClassLoader()).newInstance();
/*     */         } catch (InstantiationException|IllegalAccessException localInstantiationException) {
/* 405 */           throw new InternalError(localInstantiationException);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void emitInvoke()
/*     */   {
/* 416 */     if (this.parameterTypes.length > 65535) {
/* 417 */       throw new InternalError("Can't handle more than 65535 parameters");
/*     */     }
/*     */     
/*     */ 
/* 421 */     ClassFileAssembler localClassFileAssembler = new ClassFileAssembler();
/* 422 */     if (this.isConstructor)
/*     */     {
/* 424 */       localClassFileAssembler.setMaxLocals(2);
/*     */     }
/*     */     else {
/* 427 */       localClassFileAssembler.setMaxLocals(3);
/*     */     }
/*     */     
/* 430 */     short s1 = 0;
/*     */     
/* 432 */     if (this.isConstructor)
/*     */     {
/*     */ 
/*     */ 
/* 436 */       localClassFileAssembler.opc_new(this.targetClass);
/* 437 */       localClassFileAssembler.opc_dup();
/*     */     }
/*     */     else {
/* 440 */       if (isPrimitive(this.returnType))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 446 */         localClassFileAssembler.opc_new(indexForPrimitiveType(this.returnType));
/* 447 */         localClassFileAssembler.opc_dup();
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 455 */       if (!isStatic())
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 465 */         localClassFileAssembler.opc_aload_1();
/* 466 */         localLabel1 = new Label();
/* 467 */         localClassFileAssembler.opc_ifnonnull(localLabel1);
/* 468 */         localClassFileAssembler.opc_new(this.nullPointerClass);
/* 469 */         localClassFileAssembler.opc_dup();
/* 470 */         localClassFileAssembler.opc_invokespecial(this.nullPointerCtorIdx, 0, 0);
/* 471 */         localClassFileAssembler.opc_athrow();
/* 472 */         localLabel1.bind();
/* 473 */         s1 = localClassFileAssembler.getLength();
/* 474 */         localClassFileAssembler.opc_aload_1();
/* 475 */         localClassFileAssembler.opc_checkcast(this.targetClass);
/*     */       }
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
/* 495 */     Label localLabel1 = new Label();
/* 496 */     if (this.parameterTypes.length == 0) {
/* 497 */       if (this.isConstructor) {
/* 498 */         localClassFileAssembler.opc_aload_1();
/*     */       } else {
/* 500 */         localClassFileAssembler.opc_aload_2();
/*     */       }
/* 502 */       localClassFileAssembler.opc_ifnull(localLabel1);
/*     */     }
/* 504 */     if (this.isConstructor) {
/* 505 */       localClassFileAssembler.opc_aload_1();
/*     */     } else {
/* 507 */       localClassFileAssembler.opc_aload_2();
/*     */     }
/* 509 */     localClassFileAssembler.opc_arraylength();
/* 510 */     localClassFileAssembler.opc_sipush((short)this.parameterTypes.length);
/* 511 */     localClassFileAssembler.opc_if_icmpeq(localLabel1);
/* 512 */     localClassFileAssembler.opc_new(this.illegalArgumentClass);
/* 513 */     localClassFileAssembler.opc_dup();
/* 514 */     localClassFileAssembler.opc_invokespecial(this.illegalArgumentCtorIdx, 0, 0);
/* 515 */     localClassFileAssembler.opc_athrow();
/* 516 */     localLabel1.bind();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 522 */     short s2 = this.nonPrimitiveParametersBaseIdx;
/* 523 */     Label localLabel2 = null;
/* 524 */     byte b = 1;
/*     */     
/* 526 */     for (int i = 0; i < this.parameterTypes.length; i++) {
/* 527 */       Class localClass = this.parameterTypes[i];
/* 528 */       b = (byte)(b + (byte)typeSizeInStackSlots(localClass));
/* 529 */       if (localLabel2 != null) {
/* 530 */         localLabel2.bind();
/* 531 */         localLabel2 = null;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 536 */       if (this.isConstructor) {
/* 537 */         localClassFileAssembler.opc_aload_1();
/*     */       } else {
/* 539 */         localClassFileAssembler.opc_aload_2();
/*     */       }
/* 541 */       localClassFileAssembler.opc_sipush((short)i);
/* 542 */       localClassFileAssembler.opc_aaload();
/* 543 */       if (isPrimitive(localClass))
/*     */       {
/*     */ 
/*     */ 
/* 547 */         if (this.isConstructor) {
/* 548 */           localClassFileAssembler.opc_astore_2();
/*     */         } else {
/* 550 */           localClassFileAssembler.opc_astore_3();
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 570 */         Label localLabel3 = null;
/* 571 */         localLabel2 = new Label();
/*     */         
/* 573 */         for (j = 0; j < primitiveTypes.length; j++) {
/* 574 */           localObject = primitiveTypes[j];
/* 575 */           if (canWidenTo((Class)localObject, localClass)) {
/* 576 */             if (localLabel3 != null) {
/* 577 */               localLabel3.bind();
/*     */             }
/*     */             
/* 580 */             if (this.isConstructor) {
/* 581 */               localClassFileAssembler.opc_aload_2();
/*     */             } else {
/* 583 */               localClassFileAssembler.opc_aload_3();
/*     */             }
/* 585 */             localClassFileAssembler.opc_instanceof(indexForPrimitiveType((Class)localObject));
/* 586 */             localLabel3 = new Label();
/* 587 */             localClassFileAssembler.opc_ifeq(localLabel3);
/* 588 */             if (this.isConstructor) {
/* 589 */               localClassFileAssembler.opc_aload_2();
/*     */             } else {
/* 591 */               localClassFileAssembler.opc_aload_3();
/*     */             }
/* 593 */             localClassFileAssembler.opc_checkcast(indexForPrimitiveType((Class)localObject));
/* 594 */             localClassFileAssembler.opc_invokevirtual(unboxingMethodForPrimitiveType((Class)localObject), 0, 
/*     */             
/* 596 */               typeSizeInStackSlots((Class)localObject));
/* 597 */             emitWideningBytecodeForPrimitiveConversion(localClassFileAssembler, (Class)localObject, localClass);
/*     */             
/*     */ 
/* 600 */             localClassFileAssembler.opc_goto(localLabel2);
/*     */           }
/*     */         }
/*     */         
/* 604 */         if (localLabel3 == null) {
/* 605 */           throw new InternalError("Must have found at least identity conversion");
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 613 */         localLabel3.bind();
/* 614 */         localClassFileAssembler.opc_new(this.illegalArgumentClass);
/* 615 */         localClassFileAssembler.opc_dup();
/* 616 */         localClassFileAssembler.opc_invokespecial(this.illegalArgumentCtorIdx, 0, 0);
/* 617 */         localClassFileAssembler.opc_athrow();
/*     */       }
/*     */       else {
/* 620 */         localClassFileAssembler.opc_checkcast(s2);
/* 621 */         s2 = add(s2, (short)2);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 626 */     if (localLabel2 != null) {
/* 627 */       localLabel2.bind();
/*     */     }
/*     */     
/* 630 */     i = localClassFileAssembler.getLength();
/*     */     
/*     */ 
/* 633 */     if (this.isConstructor) {
/* 634 */       localClassFileAssembler.opc_invokespecial(this.targetMethodRef, b, 0);
/*     */     }
/* 636 */     else if (isStatic()) {
/* 637 */       localClassFileAssembler.opc_invokestatic(this.targetMethodRef, b, 
/*     */       
/* 639 */         typeSizeInStackSlots(this.returnType));
/*     */     }
/* 641 */     else if (isInterface()) {
/* 642 */       if (isPrivate()) {
/* 643 */         localClassFileAssembler.opc_invokespecial(this.targetMethodRef, b, 0);
/*     */       } else {
/* 645 */         localClassFileAssembler.opc_invokeinterface(this.targetMethodRef, b, b, 
/*     */         
/*     */ 
/* 648 */           typeSizeInStackSlots(this.returnType));
/*     */       }
/*     */     } else {
/* 651 */       localClassFileAssembler.opc_invokevirtual(this.targetMethodRef, b, 
/*     */       
/* 653 */         typeSizeInStackSlots(this.returnType));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 658 */     short s3 = localClassFileAssembler.getLength();
/*     */     
/* 660 */     if (!this.isConstructor)
/*     */     {
/* 662 */       if (isPrimitive(this.returnType)) {
/* 663 */         localClassFileAssembler.opc_invokespecial(ctorIndexForPrimitiveType(this.returnType), 
/* 664 */           typeSizeInStackSlots(this.returnType), 0);
/*     */       }
/* 666 */       else if (this.returnType == Void.TYPE) {
/* 667 */         localClassFileAssembler.opc_aconst_null();
/*     */       }
/*     */     }
/* 670 */     localClassFileAssembler.opc_areturn();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 678 */     short s4 = localClassFileAssembler.getLength();
/*     */     
/*     */ 
/* 681 */     localClassFileAssembler.setStack(1);
/* 682 */     localClassFileAssembler.opc_invokespecial(this.toStringIdx, 0, 1);
/* 683 */     localClassFileAssembler.opc_new(this.illegalArgumentClass);
/* 684 */     localClassFileAssembler.opc_dup_x1();
/* 685 */     localClassFileAssembler.opc_swap();
/* 686 */     localClassFileAssembler.opc_invokespecial(this.illegalArgumentStringCtorIdx, 1, 0);
/* 687 */     localClassFileAssembler.opc_athrow();
/*     */     
/* 689 */     int j = localClassFileAssembler.getLength();
/*     */     
/*     */ 
/* 692 */     localClassFileAssembler.setStack(1);
/* 693 */     localClassFileAssembler.opc_new(this.invocationTargetClass);
/* 694 */     localClassFileAssembler.opc_dup_x1();
/* 695 */     localClassFileAssembler.opc_swap();
/* 696 */     localClassFileAssembler.opc_invokespecial(this.invocationTargetCtorIdx, 1, 0);
/* 697 */     localClassFileAssembler.opc_athrow();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 703 */     Object localObject = new ClassFileAssembler();
/*     */     
/* 705 */     ((ClassFileAssembler)localObject).emitShort(s1);
/* 706 */     ((ClassFileAssembler)localObject).emitShort(i);
/* 707 */     ((ClassFileAssembler)localObject).emitShort(s4);
/* 708 */     ((ClassFileAssembler)localObject).emitShort(this.classCastClass);
/*     */     
/* 710 */     ((ClassFileAssembler)localObject).emitShort(s1);
/* 711 */     ((ClassFileAssembler)localObject).emitShort(i);
/* 712 */     ((ClassFileAssembler)localObject).emitShort(s4);
/* 713 */     ((ClassFileAssembler)localObject).emitShort(this.nullPointerClass);
/*     */     
/* 715 */     ((ClassFileAssembler)localObject).emitShort(i);
/* 716 */     ((ClassFileAssembler)localObject).emitShort(s3);
/* 717 */     ((ClassFileAssembler)localObject).emitShort(j);
/* 718 */     ((ClassFileAssembler)localObject).emitShort(this.throwableClass);
/*     */     
/* 720 */     emitMethod(this.invokeIdx, localClassFileAssembler.getMaxLocals(), localClassFileAssembler, (ClassFileAssembler)localObject, new short[] { this.invocationTargetClass });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean usesPrimitiveTypes()
/*     */   {
/* 728 */     if (this.returnType.isPrimitive()) {
/* 729 */       return true;
/*     */     }
/* 731 */     for (int i = 0; i < this.parameterTypes.length; i++) {
/* 732 */       if (this.parameterTypes[i].isPrimitive()) {
/* 733 */         return true;
/*     */       }
/*     */     }
/* 736 */     return false;
/*     */   }
/*     */   
/*     */   private int numNonPrimitiveParameterTypes() {
/* 740 */     int i = 0;
/* 741 */     for (int j = 0; j < this.parameterTypes.length; j++) {
/* 742 */       if (!this.parameterTypes[j].isPrimitive()) {
/* 743 */         i++;
/*     */       }
/*     */     }
/* 746 */     return i;
/*     */   }
/*     */   
/*     */   private boolean isInterface() {
/* 750 */     return this.declaringClass.isInterface();
/*     */   }
/*     */   
/*     */   private String buildInternalSignature() {
/* 754 */     StringBuffer localStringBuffer = new StringBuffer();
/* 755 */     localStringBuffer.append("(");
/* 756 */     for (int i = 0; i < this.parameterTypes.length; i++) {
/* 757 */       localStringBuffer.append(getClassName(this.parameterTypes[i], true));
/*     */     }
/* 759 */     localStringBuffer.append(")");
/* 760 */     localStringBuffer.append(getClassName(this.returnType, true));
/* 761 */     return localStringBuffer.toString();
/*     */   }
/*     */   
/*     */ 
/*     */   private static synchronized String generateName(boolean paramBoolean1, boolean paramBoolean2)
/*     */   {
/* 767 */     if (paramBoolean1) {
/* 768 */       if (paramBoolean2) {
/* 769 */         i = ++serializationConstructorSymnum;
/* 770 */         return "sun/reflect/GeneratedSerializationConstructorAccessor" + i;
/*     */       }
/* 772 */       i = ++constructorSymnum;
/* 773 */       return "sun/reflect/GeneratedConstructorAccessor" + i;
/*     */     }
/*     */     
/* 776 */     int i = ++methodSymnum;
/* 777 */     return "sun/reflect/GeneratedMethodAccessor" + i;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\reflect\MethodAccessorGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */