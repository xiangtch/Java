/*     */ package sun.reflect.annotation;
/*     */ 
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.annotation.AnnotationFormatError;
/*     */ import java.lang.reflect.AnnotatedElement;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class TypeAnnotation
/*     */ {
/*     */   private final TypeAnnotationTargetInfo targetInfo;
/*     */   private final LocationInfo loc;
/*     */   private final Annotation annotation;
/*     */   private final AnnotatedElement baseDeclaration;
/*     */   
/*     */   public TypeAnnotation(TypeAnnotationTargetInfo paramTypeAnnotationTargetInfo, LocationInfo paramLocationInfo, Annotation paramAnnotation, AnnotatedElement paramAnnotatedElement)
/*     */   {
/*  53 */     this.targetInfo = paramTypeAnnotationTargetInfo;
/*  54 */     this.loc = paramLocationInfo;
/*  55 */     this.annotation = paramAnnotation;
/*  56 */     this.baseDeclaration = paramAnnotatedElement;
/*     */   }
/*     */   
/*     */   public TypeAnnotationTargetInfo getTargetInfo() {
/*  60 */     return this.targetInfo;
/*     */   }
/*     */   
/*  63 */   public Annotation getAnnotation() { return this.annotation; }
/*     */   
/*     */   public AnnotatedElement getBaseDeclaration() {
/*  66 */     return this.baseDeclaration;
/*     */   }
/*     */   
/*  69 */   public LocationInfo getLocationInfo() { return this.loc; }
/*     */   
/*     */ 
/*     */   public static List<TypeAnnotation> filter(TypeAnnotation[] paramArrayOfTypeAnnotation, TypeAnnotationTarget paramTypeAnnotationTarget)
/*     */   {
/*  74 */     ArrayList localArrayList = new ArrayList(paramArrayOfTypeAnnotation.length);
/*  75 */     for (TypeAnnotation localTypeAnnotation : paramArrayOfTypeAnnotation)
/*  76 */       if (localTypeAnnotation.getTargetInfo().getTarget() == paramTypeAnnotationTarget)
/*  77 */         localArrayList.add(localTypeAnnotation);
/*  78 */     localArrayList.trimToSize();
/*  79 */     return localArrayList;
/*     */   }
/*     */   
/*     */   public static enum TypeAnnotationTarget {
/*  83 */     CLASS_TYPE_PARAMETER, 
/*  84 */     METHOD_TYPE_PARAMETER, 
/*  85 */     CLASS_EXTENDS, 
/*  86 */     CLASS_IMPLEMENTS, 
/*  87 */     CLASS_TYPE_PARAMETER_BOUND, 
/*  88 */     METHOD_TYPE_PARAMETER_BOUND, 
/*  89 */     FIELD, 
/*  90 */     METHOD_RETURN, 
/*  91 */     METHOD_RECEIVER, 
/*  92 */     METHOD_FORMAL_PARAMETER, 
/*  93 */     THROWS;
/*     */     
/*     */     private TypeAnnotationTarget() {}
/*     */   }
/*     */   
/*     */   public static final class TypeAnnotationTargetInfo { private final TypeAnnotationTarget target;
/*     */     private final int count;
/*     */     private final int secondaryIndex;
/*     */     private static final int UNUSED_INDEX = -2;
/*     */     
/* 103 */     public TypeAnnotationTargetInfo(TypeAnnotationTarget paramTypeAnnotationTarget) { this(paramTypeAnnotationTarget, -2, -2); }
/*     */     
/*     */ 
/*     */     public TypeAnnotationTargetInfo(TypeAnnotationTarget paramTypeAnnotationTarget, int paramInt)
/*     */     {
/* 108 */       this(paramTypeAnnotationTarget, paramInt, -2);
/*     */     }
/*     */     
/*     */ 
/*     */     public TypeAnnotationTargetInfo(TypeAnnotationTarget paramTypeAnnotationTarget, int paramInt1, int paramInt2)
/*     */     {
/* 114 */       this.target = paramTypeAnnotationTarget;
/* 115 */       this.count = paramInt1;
/* 116 */       this.secondaryIndex = paramInt2;
/*     */     }
/*     */     
/*     */     public TypeAnnotationTarget getTarget() {
/* 120 */       return this.target;
/*     */     }
/*     */     
/* 123 */     public int getCount() { return this.count; }
/*     */     
/*     */     public int getSecondaryIndex() {
/* 126 */       return this.secondaryIndex;
/*     */     }
/*     */     
/*     */     public String toString()
/*     */     {
/* 131 */       return "" + this.target + ": " + this.count + ", " + this.secondaryIndex;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static final class LocationInfo {
/*     */     private final int depth;
/*     */     private final Location[] locations;
/*     */     
/* 140 */     private LocationInfo() { this(0, new Location[0]); }
/*     */     
/*     */     private LocationInfo(int paramInt, Location[] paramArrayOfLocation) {
/* 143 */       this.depth = paramInt;
/* 144 */       this.locations = paramArrayOfLocation;
/*     */     }
/*     */     
/* 147 */     public static final LocationInfo BASE_LOCATION = new LocationInfo();
/*     */     
/*     */     public static LocationInfo parseLocationInfo(ByteBuffer paramByteBuffer) {
/* 150 */       int i = paramByteBuffer.get() & 0xFF;
/* 151 */       if (i == 0)
/* 152 */         return BASE_LOCATION;
/* 153 */       Location[] arrayOfLocation = new Location[i];
/* 154 */       for (int j = 0; j < i; j++) {
/* 155 */         byte b = paramByteBuffer.get();
/* 156 */         short s = (short)(paramByteBuffer.get() & 0xFF);
/* 157 */         if (b != 0) if ((((b == 1 ? 1 : 0) | (b == 2 ? 1 : 0)) == 0) && (b != 3))
/* 158 */             throw new AnnotationFormatError("Bad Location encoding in Type Annotation");
/* 159 */         if ((b != 3) && (s != 0))
/* 160 */           throw new AnnotationFormatError("Bad Location encoding in Type Annotation");
/* 161 */         arrayOfLocation[j] = new Location(b, s);
/*     */       }
/* 163 */       return new LocationInfo(i, arrayOfLocation);
/*     */     }
/*     */     
/*     */     public LocationInfo pushArray() {
/* 167 */       return pushLocation((byte)0, (short)0);
/*     */     }
/*     */     
/*     */     public LocationInfo pushInner() {
/* 171 */       return pushLocation((byte)1, (short)0);
/*     */     }
/*     */     
/*     */     public LocationInfo pushWildcard() {
/* 175 */       return pushLocation((byte)2, (short)0);
/*     */     }
/*     */     
/*     */     public LocationInfo pushTypeArg(short paramShort) {
/* 179 */       return pushLocation((byte)3, paramShort);
/*     */     }
/*     */     
/*     */     public LocationInfo pushLocation(byte paramByte, short paramShort) {
/* 183 */       int i = this.depth + 1;
/* 184 */       Location[] arrayOfLocation = new Location[i];
/* 185 */       System.arraycopy(this.locations, 0, arrayOfLocation, 0, this.depth);
/* 186 */       arrayOfLocation[(i - 1)] = new Location(paramByte, (short)(paramShort & 0xFF));
/* 187 */       return new LocationInfo(i, arrayOfLocation);
/*     */     }
/*     */     
/*     */     public TypeAnnotation[] filter(TypeAnnotation[] paramArrayOfTypeAnnotation) {
/* 191 */       ArrayList localArrayList = new ArrayList(paramArrayOfTypeAnnotation.length);
/* 192 */       for (TypeAnnotation localTypeAnnotation : paramArrayOfTypeAnnotation) {
/* 193 */         if (isSameLocationInfo(localTypeAnnotation.getLocationInfo()))
/* 194 */           localArrayList.add(localTypeAnnotation);
/*     */       }
/* 196 */       return (TypeAnnotation[])localArrayList.toArray(new TypeAnnotation[0]);
/*     */     }
/*     */     
/*     */     boolean isSameLocationInfo(LocationInfo paramLocationInfo) {
/* 200 */       if (this.depth != paramLocationInfo.depth)
/* 201 */         return false;
/* 202 */       for (int i = 0; i < this.depth; i++)
/* 203 */         if (!this.locations[i].isSameLocation(paramLocationInfo.locations[i]))
/* 204 */           return false;
/* 205 */       return true;
/*     */     }
/*     */     
/*     */     public static final class Location {
/*     */       public final byte tag;
/*     */       public final short index;
/*     */       
/*     */       boolean isSameLocation(Location paramLocation) {
/* 213 */         return (this.tag == paramLocation.tag) && (this.index == paramLocation.index);
/*     */       }
/*     */       
/*     */       public Location(byte paramByte, short paramShort) {
/* 217 */         this.tag = paramByte;
/* 218 */         this.index = paramShort;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 225 */     return 
/*     */     
/* 227 */       this.annotation.toString() + " with Targetnfo: " + this.targetInfo.toString() + " on base declaration: " + this.baseDeclaration.toString();
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\reflect\annotation\TypeAnnotation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */