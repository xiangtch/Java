/*     */ package sun.nio.ch;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.ServerSocket;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.ClosedByInterruptException;
/*     */ import java.nio.channels.Pipe;
/*     */ import java.nio.channels.Pipe.SinkChannel;
/*     */ import java.nio.channels.Pipe.SourceChannel;
/*     */ import java.nio.channels.ServerSocketChannel;
/*     */ import java.nio.channels.SocketChannel;
/*     */ import java.nio.channels.spi.SelectorProvider;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import java.security.SecureRandom;
/*     */ import java.util.Random;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class PipeImpl
/*     */   extends Pipe
/*     */ {
/*     */   private static final int NUM_SECRET_BYTES = 16;
/*  55 */   private static final Random RANDOM_NUMBER_GENERATOR = new SecureRandom();
/*     */   
/*     */   private SourceChannel source;
/*     */   
/*     */   private SinkChannel sink;
/*     */   
/*     */ 
/*     */   private class Initializer
/*     */     implements PrivilegedExceptionAction<Void>
/*     */   {
/*     */     private final SelectorProvider sp;
/*     */     
/*  67 */     private IOException ioe = null;
/*     */     
/*     */     private Initializer(SelectorProvider paramSelectorProvider) {
/*  70 */       this.sp = paramSelectorProvider;
/*     */     }
/*     */     
/*     */     public Void run() throws IOException
/*     */     {
/*  75 */       LoopbackConnector localLoopbackConnector = new LoopbackConnector(null);
/*  76 */       localLoopbackConnector.run();
/*  77 */       if ((this.ioe instanceof ClosedByInterruptException)) {
/*  78 */         this.ioe = null;
/*  79 */         Thread local1 = new Thread(localLoopbackConnector)
/*     */         {
/*     */           public void interrupt() {}
/*  82 */         };
/*  83 */         local1.start();
/*     */         for (;;) {
/*     */           try {
/*  86 */             local1.join();
/*     */           }
/*     */           catch (InterruptedException localInterruptedException) {}
/*     */         }
/*  90 */         Thread.currentThread().interrupt();
/*     */       }
/*     */       
/*  93 */       if (this.ioe != null) {
/*  94 */         throw new IOException("Unable to establish loopback connection", this.ioe);
/*     */       }
/*  96 */       return null;
/*     */     }
/*     */     
/*     */     private class LoopbackConnector implements Runnable {
/*     */       private LoopbackConnector() {}
/*     */       
/*     */       public void run() {
/* 103 */         ServerSocketChannel localServerSocketChannel = null;
/* 104 */         SocketChannel localSocketChannel1 = null;
/* 105 */         SocketChannel localSocketChannel2 = null;
/*     */         
/*     */         try
/*     */         {
/* 109 */           ByteBuffer localByteBuffer1 = ByteBuffer.allocate(16);
/* 110 */           ByteBuffer localByteBuffer2 = ByteBuffer.allocate(16);
/*     */           
/*     */ 
/* 113 */           InetAddress localInetAddress = InetAddress.getByName("127.0.0.1");
/* 114 */           assert (localInetAddress.isLoopbackAddress());
/* 115 */           InetSocketAddress localInetSocketAddress = null;
/*     */           
/*     */           for (;;)
/*     */           {
/* 119 */             if ((localServerSocketChannel == null) || (!localServerSocketChannel.isOpen())) {
/* 120 */               localServerSocketChannel = ServerSocketChannel.open();
/* 121 */               localServerSocketChannel.socket().bind(new InetSocketAddress(localInetAddress, 0));
/* 122 */               localInetSocketAddress = new InetSocketAddress(localInetAddress, localServerSocketChannel.socket().getLocalPort());
/*     */             }
/*     */             
/*     */ 
/*     */ 
/* 127 */             localSocketChannel1 = SocketChannel.open(localInetSocketAddress);
/* 128 */             PipeImpl.RANDOM_NUMBER_GENERATOR.nextBytes(localByteBuffer1.array());
/*     */             do {
/* 130 */               localSocketChannel1.write(localByteBuffer1);
/* 131 */             } while (localByteBuffer1.hasRemaining());
/* 132 */             localByteBuffer1.rewind();
/*     */             
/*     */ 
/* 135 */             localSocketChannel2 = localServerSocketChannel.accept();
/*     */             do {
/* 137 */               localSocketChannel2.read(localByteBuffer2);
/* 138 */             } while (localByteBuffer2.hasRemaining());
/* 139 */             localByteBuffer2.rewind();
/*     */             
/* 141 */             if (localByteBuffer2.equals(localByteBuffer1)) {
/*     */               break;
/*     */             }
/* 144 */             localSocketChannel2.close();
/* 145 */             localSocketChannel1.close();
/*     */           }
/*     */           
/*     */ 
/* 149 */           PipeImpl.this.source = new SourceChannelImpl(Initializer.this.sp, localSocketChannel1);
/* 150 */           PipeImpl.this.sink = new SinkChannelImpl(Initializer.this.sp, localSocketChannel2); return;
/*     */         } catch (IOException localIOException2) {
/*     */           try {
/* 153 */             if (localSocketChannel1 != null)
/* 154 */               localSocketChannel1.close();
/* 155 */             if (localSocketChannel2 != null)
/* 156 */               localSocketChannel2.close();
/*     */           } catch (IOException localIOException4) {}
/* 158 */           Initializer.this.ioe = localIOException2;
/*     */         } finally {
/*     */           try {
/* 161 */             if (localServerSocketChannel != null)
/* 162 */               localServerSocketChannel.close();
/*     */           } catch (IOException localIOException5) {}
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   PipeImpl(SelectorProvider paramSelectorProvider) throws IOException {
/*     */     try {
/* 171 */       AccessController.doPrivileged(new Initializer(paramSelectorProvider, null));
/*     */     } catch (PrivilegedActionException localPrivilegedActionException) {
/* 173 */       throw ((IOException)localPrivilegedActionException.getCause());
/*     */     }
/*     */   }
/*     */   
/*     */   public SourceChannel source() {
/* 178 */     return this.source;
/*     */   }
/*     */   
/*     */   public SinkChannel sink() {
/* 182 */     return this.sink;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\nio\ch\PipeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */