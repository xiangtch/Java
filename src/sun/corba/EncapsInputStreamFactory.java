/*     */ package sun.corba;
/*     */ 
/*     */ import com.sun.corba.se.impl.encoding.EncapsInputStream;
/*     */ import com.sun.corba.se.impl.encoding.TypeCodeInputStream;
/*     */ import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
/*     */ import com.sun.org.omg.SendingContext.CodeBase;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import org.omg.CORBA.ORB;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class EncapsInputStreamFactory
/*     */ {
/*     */   public static EncapsInputStream newEncapsInputStream(ORB paramORB, final byte[] paramArrayOfByte, final int paramInt, final boolean paramBoolean, final GIOPVersion paramGIOPVersion)
/*     */   {
/*  45 */     
/*  46 */       (EncapsInputStream)AccessController.doPrivileged(new PrivilegedAction()
/*     */       {
/*     */         public EncapsInputStream run() {
/*  49 */           return new EncapsInputStream(this.val$orb, paramArrayOfByte, paramInt, paramBoolean, paramGIOPVersion);
/*     */         }
/*     */       });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static EncapsInputStream newEncapsInputStream(ORB paramORB, final ByteBuffer paramByteBuffer, final int paramInt, final boolean paramBoolean, final GIOPVersion paramGIOPVersion)
/*     */   {
/*  59 */     
/*  60 */       (EncapsInputStream)AccessController.doPrivileged(new PrivilegedAction()
/*     */       {
/*     */         public EncapsInputStream run() {
/*  63 */           return new EncapsInputStream(this.val$orb, paramByteBuffer, paramInt, paramBoolean, paramGIOPVersion);
/*     */         }
/*     */       });
/*     */   }
/*     */   
/*     */ 
/*     */   public static EncapsInputStream newEncapsInputStream(ORB paramORB, final byte[] paramArrayOfByte, final int paramInt)
/*     */   {
/*  71 */     
/*  72 */       (EncapsInputStream)AccessController.doPrivileged(new PrivilegedAction()
/*     */       {
/*     */         public EncapsInputStream run() {
/*  75 */           return new EncapsInputStream(this.val$orb, paramArrayOfByte, paramInt);
/*     */         }
/*     */       });
/*     */   }
/*     */   
/*     */   public static EncapsInputStream newEncapsInputStream(EncapsInputStream paramEncapsInputStream)
/*     */   {
/*  82 */     
/*  83 */       (EncapsInputStream)AccessController.doPrivileged(new PrivilegedAction()
/*     */       {
/*     */         public EncapsInputStream run() {
/*  86 */           return new EncapsInputStream(this.val$eis);
/*     */         }
/*     */       });
/*     */   }
/*     */   
/*     */ 
/*     */   public static EncapsInputStream newEncapsInputStream(ORB paramORB, final byte[] paramArrayOfByte, final int paramInt, final GIOPVersion paramGIOPVersion)
/*     */   {
/*  94 */     
/*  95 */       (EncapsInputStream)AccessController.doPrivileged(new PrivilegedAction()
/*     */       {
/*     */         public EncapsInputStream run() {
/*  98 */           return new EncapsInputStream(this.val$orb, paramArrayOfByte, paramInt, paramGIOPVersion);
/*     */         }
/*     */       });
/*     */   }
/*     */   
/*     */ 
/*     */   public static EncapsInputStream newEncapsInputStream(ORB paramORB, final byte[] paramArrayOfByte, final int paramInt, final GIOPVersion paramGIOPVersion, final CodeBase paramCodeBase)
/*     */   {
/* 106 */     
/* 107 */       (EncapsInputStream)AccessController.doPrivileged(new PrivilegedAction()
/*     */       {
/*     */         public EncapsInputStream run() {
/* 110 */           return new EncapsInputStream(this.val$orb, paramArrayOfByte, paramInt, paramGIOPVersion, paramCodeBase);
/*     */         }
/*     */       });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static TypeCodeInputStream newTypeCodeInputStream(ORB paramORB, final byte[] paramArrayOfByte, final int paramInt, final boolean paramBoolean, final GIOPVersion paramGIOPVersion)
/*     */   {
/* 119 */     
/* 120 */       (TypeCodeInputStream)AccessController.doPrivileged(new PrivilegedAction()
/*     */       {
/*     */         public TypeCodeInputStream run() {
/* 123 */           return new TypeCodeInputStream(this.val$orb, paramArrayOfByte, paramInt, paramBoolean, paramGIOPVersion);
/*     */         }
/*     */       });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static TypeCodeInputStream newTypeCodeInputStream(ORB paramORB, final ByteBuffer paramByteBuffer, final int paramInt, final boolean paramBoolean, final GIOPVersion paramGIOPVersion)
/*     */   {
/* 133 */     
/* 134 */       (TypeCodeInputStream)AccessController.doPrivileged(new PrivilegedAction()
/*     */       {
/*     */         public TypeCodeInputStream run() {
/* 137 */           return new TypeCodeInputStream(this.val$orb, paramByteBuffer, paramInt, paramBoolean, paramGIOPVersion);
/*     */         }
/*     */       });
/*     */   }
/*     */   
/*     */ 
/*     */   public static TypeCodeInputStream newTypeCodeInputStream(ORB paramORB, final byte[] paramArrayOfByte, final int paramInt)
/*     */   {
/* 145 */     
/* 146 */       (TypeCodeInputStream)AccessController.doPrivileged(new PrivilegedAction()
/*     */       {
/*     */         public TypeCodeInputStream run() {
/* 149 */           return new TypeCodeInputStream(this.val$orb, paramArrayOfByte, paramInt);
/*     */         }
/*     */       });
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\corba\EncapsInputStreamFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */