/*     */ package sun.nio.ch;
/*     */ 
/*     */ import java.io.FileDescriptor;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.AsynchronousCloseException;
/*     */ import java.nio.channels.Pipe.SourceChannel;
/*     */ import java.nio.channels.SocketChannel;
/*     */ import java.nio.channels.spi.SelectorProvider;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class SourceChannelImpl
/*     */   extends Pipe.SourceChannel
/*     */   implements SelChImpl
/*     */ {
/*     */   SocketChannel sc;
/*     */   
/*     */   public FileDescriptor getFD()
/*     */   {
/*  49 */     return ((SocketChannelImpl)this.sc).getFD();
/*     */   }
/*     */   
/*     */   public int getFDVal() {
/*  53 */     return ((SocketChannelImpl)this.sc).getFDVal();
/*     */   }
/*     */   
/*     */   SourceChannelImpl(SelectorProvider paramSelectorProvider, SocketChannel paramSocketChannel) {
/*  57 */     super(paramSelectorProvider);
/*  58 */     this.sc = paramSocketChannel;
/*     */   }
/*     */   
/*     */   protected void implCloseSelectableChannel() throws IOException {
/*  62 */     if (!isRegistered())
/*  63 */       kill();
/*     */   }
/*     */   
/*     */   public void kill() throws IOException {
/*  67 */     this.sc.close();
/*     */   }
/*     */   
/*     */   protected void implConfigureBlocking(boolean paramBoolean) throws IOException {
/*  71 */     this.sc.configureBlocking(paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean translateReadyOps(int paramInt1, int paramInt2, SelectionKeyImpl paramSelectionKeyImpl)
/*     */   {
/*  76 */     int i = paramSelectionKeyImpl.nioInterestOps();
/*  77 */     int j = paramSelectionKeyImpl.nioReadyOps();
/*  78 */     int k = paramInt2;
/*     */     
/*  80 */     if ((paramInt1 & Net.POLLNVAL) != 0) {
/*  81 */       throw new Error("POLLNVAL detected");
/*     */     }
/*  83 */     if ((paramInt1 & (Net.POLLERR | Net.POLLHUP)) != 0) {
/*  84 */       k = i;
/*  85 */       paramSelectionKeyImpl.nioReadyOps(k);
/*  86 */       return (k & (j ^ 0xFFFFFFFF)) != 0;
/*     */     }
/*     */     
/*  89 */     if (((paramInt1 & Net.POLLIN) != 0) && ((i & 0x1) != 0))
/*     */     {
/*  91 */       k |= 0x1;
/*     */     }
/*  93 */     paramSelectionKeyImpl.nioReadyOps(k);
/*  94 */     return (k & (j ^ 0xFFFFFFFF)) != 0;
/*     */   }
/*     */   
/*     */   public boolean translateAndUpdateReadyOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl) {
/*  98 */     return translateReadyOps(paramInt, paramSelectionKeyImpl.nioReadyOps(), paramSelectionKeyImpl);
/*     */   }
/*     */   
/*     */   public boolean translateAndSetReadyOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl) {
/* 102 */     return translateReadyOps(paramInt, 0, paramSelectionKeyImpl);
/*     */   }
/*     */   
/*     */   public void translateAndSetInterestOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl) {
/* 106 */     if ((paramInt & 0x1) != 0)
/* 107 */       paramInt = Net.POLLIN;
/* 108 */     paramSelectionKeyImpl.selector.putEventOps(paramSelectionKeyImpl, paramInt);
/*     */   }
/*     */   
/*     */   public int read(ByteBuffer paramByteBuffer) throws IOException {
/*     */     try {
/* 113 */       return this.sc.read(paramByteBuffer);
/*     */     } catch (AsynchronousCloseException localAsynchronousCloseException) {
/* 115 */       close();
/* 116 */       throw localAsynchronousCloseException;
/*     */     }
/*     */   }
/*     */   
/*     */   public long read(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2)
/*     */     throws IOException
/*     */   {
/* 123 */     if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 > paramArrayOfByteBuffer.length - paramInt2))
/* 124 */       throw new IndexOutOfBoundsException();
/*     */     try {
/* 126 */       return read(Util.subsequence(paramArrayOfByteBuffer, paramInt1, paramInt2));
/*     */     } catch (AsynchronousCloseException localAsynchronousCloseException) {
/* 128 */       close();
/* 129 */       throw localAsynchronousCloseException;
/*     */     }
/*     */   }
/*     */   
/*     */   public long read(ByteBuffer[] paramArrayOfByteBuffer) throws IOException {
/*     */     try {
/* 135 */       return this.sc.read(paramArrayOfByteBuffer);
/*     */     } catch (AsynchronousCloseException localAsynchronousCloseException) {
/* 137 */       close();
/* 138 */       throw localAsynchronousCloseException;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\nio\ch\SourceChannelImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */