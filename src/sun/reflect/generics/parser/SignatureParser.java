/*     */ package sun.reflect.generics.parser;
/*     */ 
/*     */ import java.lang.reflect.GenericSignatureFormatError;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import sun.reflect.generics.tree.ArrayTypeSignature;
/*     */ import sun.reflect.generics.tree.BaseType;
/*     */ import sun.reflect.generics.tree.BooleanSignature;
/*     */ import sun.reflect.generics.tree.BottomSignature;
/*     */ import sun.reflect.generics.tree.ByteSignature;
/*     */ import sun.reflect.generics.tree.CharSignature;
/*     */ import sun.reflect.generics.tree.ClassSignature;
/*     */ import sun.reflect.generics.tree.ClassTypeSignature;
/*     */ import sun.reflect.generics.tree.DoubleSignature;
/*     */ import sun.reflect.generics.tree.FieldTypeSignature;
/*     */ import sun.reflect.generics.tree.FloatSignature;
/*     */ import sun.reflect.generics.tree.FormalTypeParameter;
/*     */ import sun.reflect.generics.tree.IntSignature;
/*     */ import sun.reflect.generics.tree.LongSignature;
/*     */ import sun.reflect.generics.tree.MethodTypeSignature;
/*     */ import sun.reflect.generics.tree.ReturnType;
/*     */ import sun.reflect.generics.tree.ShortSignature;
/*     */ import sun.reflect.generics.tree.SimpleClassTypeSignature;
/*     */ import sun.reflect.generics.tree.TypeArgument;
/*     */ import sun.reflect.generics.tree.TypeSignature;
/*     */ import sun.reflect.generics.tree.TypeVariableSignature;
/*     */ import sun.reflect.generics.tree.VoidDescriptor;
/*     */ import sun.reflect.generics.tree.Wildcard;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SignatureParser
/*     */ {
/*     */   private char[] input;
/*  68 */   private int index = 0;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final char EOI = ':';
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final boolean DEBUG = false;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private char getNext()
/*     */   {
/*  85 */     assert (this.index <= this.input.length);
/*     */     try {
/*  87 */       return this.input[(this.index++)]; } catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {}
/*  88 */     return ':';
/*     */   }
/*     */   
/*     */   private char current()
/*     */   {
/*  93 */     assert (this.index <= this.input.length);
/*     */     try {
/*  95 */       return this.input[this.index]; } catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {}
/*  96 */     return ':';
/*     */   }
/*     */   
/*     */   private void advance()
/*     */   {
/* 101 */     assert (this.index <= this.input.length);
/* 102 */     this.index += 1;
/*     */   }
/*     */   
/*     */   private String remainder()
/*     */   {
/* 107 */     return new String(this.input, this.index, this.input.length - this.index);
/*     */   }
/*     */   
/*     */   private boolean matches(char paramChar, char... paramVarArgs)
/*     */   {
/* 112 */     for (char c : paramVarArgs) {
/* 113 */       if (paramChar == c) return true;
/*     */     }
/* 115 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private Error error(String paramString)
/*     */   {
/* 123 */     return new GenericSignatureFormatError("Signature Parse error: " + paramString + "\n\tRemaining input: " + 
/* 124 */       remainder());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void progress(int paramInt)
/*     */   {
/* 132 */     if (this.index <= paramInt) {
/* 133 */       throw error("Failure to make progress!");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static SignatureParser make()
/*     */   {
/* 141 */     return new SignatureParser();
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
/*     */   public ClassSignature parseClassSig(String paramString)
/*     */   {
/* 155 */     this.input = paramString.toCharArray();
/* 156 */     return parseClassSignature();
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
/*     */   public MethodTypeSignature parseMethodSig(String paramString)
/*     */   {
/* 170 */     this.input = paramString.toCharArray();
/* 171 */     return parseMethodTypeSignature();
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
/*     */   public TypeSignature parseTypeSig(String paramString)
/*     */   {
/* 187 */     this.input = paramString.toCharArray();
/* 188 */     return parseTypeSignature();
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
/*     */   private ClassSignature parseClassSignature()
/*     */   {
/* 211 */     assert (this.index == 0);
/* 212 */     return ClassSignature.make(parseZeroOrMoreFormalTypeParameters(), 
/* 213 */       parseClassTypeSignature(), 
/* 214 */       parseSuperInterfaces());
/*     */   }
/*     */   
/*     */   private FormalTypeParameter[] parseZeroOrMoreFormalTypeParameters() {
/* 218 */     if (current() == '<') {
/* 219 */       return parseFormalTypeParameters();
/*     */     }
/* 221 */     return new FormalTypeParameter[0];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private FormalTypeParameter[] parseFormalTypeParameters()
/*     */   {
/* 230 */     ArrayList localArrayList = new ArrayList(3);
/* 231 */     assert (current() == '<');
/* 232 */     if (current() != '<') throw error("expected '<'");
/* 233 */     advance();
/* 234 */     localArrayList.add(parseFormalTypeParameter());
/* 235 */     while (current() != '>') {
/* 236 */       int i = this.index;
/* 237 */       localArrayList.add(parseFormalTypeParameter());
/* 238 */       progress(i);
/*     */     }
/* 240 */     advance();
/* 241 */     return (FormalTypeParameter[])localArrayList.toArray(new FormalTypeParameter[localArrayList.size()]);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private FormalTypeParameter parseFormalTypeParameter()
/*     */   {
/* 249 */     String str = parseIdentifier();
/* 250 */     FieldTypeSignature[] arrayOfFieldTypeSignature = parseBounds();
/* 251 */     return FormalTypeParameter.make(str, arrayOfFieldTypeSignature);
/*     */   }
/*     */   
/*     */   private String parseIdentifier() {
/* 255 */     StringBuilder localStringBuilder = new StringBuilder();
/* 256 */     while (!Character.isWhitespace(current())) {
/* 257 */       char c = current();
/* 258 */       switch (c) {
/*     */       case '.': 
/*     */       case '/': 
/*     */       case ':': 
/*     */       case ';': 
/*     */       case '<': 
/*     */       case '>': 
/*     */       case '[': 
/* 266 */         return localStringBuilder.toString();
/*     */       }
/* 268 */       localStringBuilder.append(c);
/* 269 */       advance();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 274 */     return localStringBuilder.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private FieldTypeSignature parseFieldTypeSignature()
/*     */   {
/* 283 */     return parseFieldTypeSignature(true);
/*     */   }
/*     */   
/*     */   private FieldTypeSignature parseFieldTypeSignature(boolean paramBoolean) {
/* 287 */     switch (current()) {
/*     */     case 'L': 
/* 289 */       return parseClassTypeSignature();
/*     */     case 'T': 
/* 291 */       return parseTypeVariableSignature();
/*     */     case '[': 
/* 293 */       if (paramBoolean) {
/* 294 */         return parseArrayTypeSignature();
/*     */       }
/* 296 */       throw error("Array signature not allowed here."); }
/* 297 */     throw error("Expected Field Type Signature");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private ClassTypeSignature parseClassTypeSignature()
/*     */   {
/* 306 */     assert (current() == 'L');
/* 307 */     if (current() != 'L') throw error("expected a class type");
/* 308 */     advance();
/* 309 */     ArrayList localArrayList = new ArrayList(5);
/* 310 */     localArrayList.add(parsePackageNameAndSimpleClassTypeSignature());
/*     */     
/* 312 */     parseClassTypeSignatureSuffix(localArrayList);
/* 313 */     if (current() != ';') {
/* 314 */       throw error("expected ';' got '" + current() + "'");
/*     */     }
/* 316 */     advance();
/* 317 */     return ClassTypeSignature.make(localArrayList);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private SimpleClassTypeSignature parsePackageNameAndSimpleClassTypeSignature()
/*     */   {
/* 328 */     String str = parseIdentifier();
/*     */     
/* 330 */     if (current() == '/') {
/* 331 */       StringBuilder localStringBuilder = new StringBuilder(str);
/*     */       
/* 333 */       while (current() == '/') {
/* 334 */         advance();
/* 335 */         localStringBuilder.append(".");
/* 336 */         localStringBuilder.append(parseIdentifier());
/*     */       }
/* 338 */       str = localStringBuilder.toString();
/*     */     }
/*     */     
/* 341 */     switch (current()) {
/*     */     case ';': 
/* 343 */       return SimpleClassTypeSignature.make(str, false, new TypeArgument[0]);
/*     */     
/*     */     case '<': 
/* 346 */       return SimpleClassTypeSignature.make(str, false, parseTypeArguments());
/*     */     }
/* 348 */     throw error("expected '<' or ';' but got " + current());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private SimpleClassTypeSignature parseSimpleClassTypeSignature(boolean paramBoolean)
/*     */   {
/* 357 */     String str = parseIdentifier();
/* 358 */     char c = current();
/*     */     
/* 360 */     switch (c) {
/*     */     case '.': 
/*     */     case ';': 
/* 363 */       return SimpleClassTypeSignature.make(str, paramBoolean, new TypeArgument[0]);
/*     */     case '<': 
/* 365 */       return SimpleClassTypeSignature.make(str, paramBoolean, parseTypeArguments());
/*     */     }
/* 367 */     throw error("expected '<' or ';' or '.', got '" + c + "'.");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void parseClassTypeSignatureSuffix(List<SimpleClassTypeSignature> paramList)
/*     */   {
/* 376 */     while (current() == '.') {
/* 377 */       advance();
/* 378 */       paramList.add(parseSimpleClassTypeSignature(true));
/*     */     }
/*     */   }
/*     */   
/*     */   private TypeArgument[] parseTypeArgumentsOpt() {
/* 383 */     if (current() == '<') return parseTypeArguments();
/* 384 */     return new TypeArgument[0];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private TypeArgument[] parseTypeArguments()
/*     */   {
/* 392 */     ArrayList localArrayList = new ArrayList(3);
/* 393 */     assert (current() == '<');
/* 394 */     if (current() != '<') throw error("expected '<'");
/* 395 */     advance();
/* 396 */     localArrayList.add(parseTypeArgument());
/* 397 */     while (current() != '>')
/*     */     {
/* 399 */       localArrayList.add(parseTypeArgument());
/*     */     }
/* 401 */     advance();
/* 402 */     return (TypeArgument[])localArrayList.toArray(new TypeArgument[localArrayList.size()]);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private TypeArgument parseTypeArgument()
/*     */   {
/* 412 */     FieldTypeSignature[] arrayOfFieldTypeSignature1 = new FieldTypeSignature[1];
/* 413 */     FieldTypeSignature[] arrayOfFieldTypeSignature2 = new FieldTypeSignature[1];
/* 414 */     TypeArgument[] arrayOfTypeArgument = new TypeArgument[0];
/* 415 */     int i = current();
/* 416 */     switch (i) {
/*     */     case 43: 
/* 418 */       advance();
/* 419 */       arrayOfFieldTypeSignature1[0] = parseFieldTypeSignature();
/* 420 */       arrayOfFieldTypeSignature2[0] = BottomSignature.make();
/* 421 */       return Wildcard.make(arrayOfFieldTypeSignature1, arrayOfFieldTypeSignature2);
/*     */     
/*     */     case 42: 
/* 424 */       advance();
/* 425 */       arrayOfFieldTypeSignature1[0] = SimpleClassTypeSignature.make("java.lang.Object", false, arrayOfTypeArgument);
/* 426 */       arrayOfFieldTypeSignature2[0] = BottomSignature.make();
/* 427 */       return Wildcard.make(arrayOfFieldTypeSignature1, arrayOfFieldTypeSignature2);
/*     */     
/*     */     case 45: 
/* 430 */       advance();
/* 431 */       arrayOfFieldTypeSignature2[0] = parseFieldTypeSignature();
/* 432 */       arrayOfFieldTypeSignature1[0] = SimpleClassTypeSignature.make("java.lang.Object", false, arrayOfTypeArgument);
/* 433 */       return Wildcard.make(arrayOfFieldTypeSignature1, arrayOfFieldTypeSignature2);
/*     */     }
/*     */     
/* 436 */     return parseFieldTypeSignature();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private TypeVariableSignature parseTypeVariableSignature()
/*     */   {
/* 445 */     assert (current() == 'T');
/* 446 */     if (current() != 'T') throw error("expected a type variable usage");
/* 447 */     advance();
/* 448 */     TypeVariableSignature localTypeVariableSignature = TypeVariableSignature.make(parseIdentifier());
/* 449 */     if (current() != ';') {
/* 450 */       throw error("; expected in signature of type variable named" + localTypeVariableSignature
/* 451 */         .getIdentifier());
/*     */     }
/* 453 */     advance();
/* 454 */     return localTypeVariableSignature;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private ArrayTypeSignature parseArrayTypeSignature()
/*     */   {
/* 462 */     if (current() != '[') throw error("expected array type signature");
/* 463 */     advance();
/* 464 */     return ArrayTypeSignature.make(parseTypeSignature());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private TypeSignature parseTypeSignature()
/*     */   {
/* 473 */     switch (current()) {
/*     */     case 'B': 
/*     */     case 'C': 
/*     */     case 'D': 
/*     */     case 'F': 
/*     */     case 'I': 
/*     */     case 'J': 
/*     */     case 'S': 
/*     */     case 'Z': 
/* 482 */       return parseBaseType();
/*     */     }
/*     */     
/* 485 */     return parseFieldTypeSignature();
/*     */   }
/*     */   
/*     */   private BaseType parseBaseType()
/*     */   {
/* 490 */     switch (current()) {
/*     */     case 'B': 
/* 492 */       advance();
/* 493 */       return ByteSignature.make();
/*     */     case 'C': 
/* 495 */       advance();
/* 496 */       return CharSignature.make();
/*     */     case 'D': 
/* 498 */       advance();
/* 499 */       return DoubleSignature.make();
/*     */     case 'F': 
/* 501 */       advance();
/* 502 */       return FloatSignature.make();
/*     */     case 'I': 
/* 504 */       advance();
/* 505 */       return IntSignature.make();
/*     */     case 'J': 
/* 507 */       advance();
/* 508 */       return LongSignature.make();
/*     */     case 'S': 
/* 510 */       advance();
/* 511 */       return ShortSignature.make();
/*     */     case 'Z': 
/* 513 */       advance();
/* 514 */       return BooleanSignature.make();
/*     */     }
/* 516 */     if (!$assertionsDisabled) throw new AssertionError();
/* 517 */     throw error("expected primitive type");
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
/*     */   private FieldTypeSignature[] parseBounds()
/*     */   {
/* 530 */     ArrayList localArrayList = new ArrayList(3);
/*     */     
/* 532 */     if (current() == ':') {
/* 533 */       advance();
/* 534 */       switch (current())
/*     */       {
/*     */       case ':': 
/*     */         break;
/*     */       default: 
/* 539 */         localArrayList.add(parseFieldTypeSignature());
/*     */       }
/*     */       
/*     */       
/* 543 */       while (current() == ':') {
/* 544 */         advance();
/* 545 */         localArrayList.add(parseFieldTypeSignature());
/*     */       }
/*     */     }
/* 548 */     error("Bound expected");
/*     */     
/* 550 */     return (FieldTypeSignature[])localArrayList.toArray(new FieldTypeSignature[localArrayList.size()]);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private ClassTypeSignature[] parseSuperInterfaces()
/*     */   {
/* 558 */     ArrayList localArrayList = new ArrayList(5);
/* 559 */     while (current() == 'L') {
/* 560 */       localArrayList.add(parseClassTypeSignature());
/*     */     }
/* 562 */     return (ClassTypeSignature[])localArrayList.toArray(new ClassTypeSignature[localArrayList.size()]);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private MethodTypeSignature parseMethodTypeSignature()
/*     */   {
/* 574 */     assert (this.index == 0);
/* 575 */     return MethodTypeSignature.make(parseZeroOrMoreFormalTypeParameters(), 
/* 576 */       parseFormalParameters(), 
/* 577 */       parseReturnType(), 
/* 578 */       parseZeroOrMoreThrowsSignatures());
/*     */   }
/*     */   
/*     */   private TypeSignature[] parseFormalParameters()
/*     */   {
/* 583 */     if (current() != '(') throw error("expected '('");
/* 584 */     advance();
/* 585 */     TypeSignature[] arrayOfTypeSignature = parseZeroOrMoreTypeSignatures();
/* 586 */     if (current() != ')') throw error("expected ')'");
/* 587 */     advance();
/* 588 */     return arrayOfTypeSignature;
/*     */   }
/*     */   
/*     */   private TypeSignature[] parseZeroOrMoreTypeSignatures()
/*     */   {
/* 593 */     ArrayList localArrayList = new ArrayList();
/* 594 */     int i = 0;
/* 595 */     while (i == 0) {
/* 596 */       switch (current()) {
/*     */       case 'B': 
/*     */       case 'C': 
/*     */       case 'D': 
/*     */       case 'F': 
/*     */       case 'I': 
/*     */       case 'J': 
/*     */       case 'L': 
/*     */       case 'S': 
/*     */       case 'T': 
/*     */       case 'Z': 
/*     */       case '[': 
/* 608 */         localArrayList.add(parseTypeSignature());
/* 609 */         break;
/*     */       case 'E': case 'G': case 'H': case 'K': case 'M': case 'N': case 'O': case 'P': case 'Q': case 'R': case 'U': case 'V': case 'W': case 'X': case 'Y': default: 
/* 611 */         i = 1;
/*     */       }
/*     */     }
/* 614 */     return (TypeSignature[])localArrayList.toArray(new TypeSignature[localArrayList.size()]);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private ReturnType parseReturnType()
/*     */   {
/* 623 */     if (current() == 'V') {
/* 624 */       advance();
/* 625 */       return VoidDescriptor.make();
/*     */     }
/* 627 */     return parseTypeSignature();
/*     */   }
/*     */   
/*     */   private FieldTypeSignature[] parseZeroOrMoreThrowsSignatures()
/*     */   {
/* 632 */     ArrayList localArrayList = new ArrayList(3);
/* 633 */     while (current() == '^') {
/* 634 */       localArrayList.add(parseThrowsSignature());
/*     */     }
/* 636 */     return (FieldTypeSignature[])localArrayList.toArray(new FieldTypeSignature[localArrayList.size()]);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private FieldTypeSignature parseThrowsSignature()
/*     */   {
/* 645 */     assert (current() == '^');
/* 646 */     if (current() != '^') throw error("expected throws signature");
/* 647 */     advance();
/* 648 */     return parseFieldTypeSignature(false);
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\reflect\generics\parser\SignatureParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */