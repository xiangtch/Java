/*     */ package sun.nio.ch;
/*     */ 
/*     */ import java.io.FileDescriptor;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.AsynchronousCloseException;
/*     */ import java.nio.channels.Pipe.SinkChannel;
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
/*     */ 
/*     */ class SinkChannelImpl
/*     */   extends Pipe.SinkChannel
/*     */   implements SelChImpl
/*     */ {
/*     */   SocketChannel sc;
/*     */   
/*     */   public FileDescriptor getFD()
/*     */   {
/*  50 */     return ((SocketChannelImpl)this.sc).getFD();
/*     */   }
/*     */   
/*     */   public int getFDVal() {
/*  54 */     return ((SocketChannelImpl)this.sc).getFDVal();
/*     */   }
/*     */   
/*     */   SinkChannelImpl(SelectorProvider paramSelectorProvider, SocketChannel paramSocketChannel) {
/*  58 */     super(paramSelectorProvider);
/*  59 */     this.sc = paramSocketChannel;
/*     */   }
/*     */   
/*     */   protected void implCloseSelectableChannel() throws IOException {
/*  63 */     if (!isRegistered())
/*  64 */       kill();
/*     */   }
/*     */   
/*     */   public void kill() throws IOException {
/*  68 */     this.sc.close();
/*     */   }
/*     */   
/*     */   protected void implConfigureBlocking(boolean paramBoolean) throws IOException {
/*  72 */     this.sc.configureBlocking(paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean translateReadyOps(int paramInt1, int paramInt2, SelectionKeyImpl paramSelectionKeyImpl)
/*     */   {
/*  77 */     int i = paramSelectionKeyImpl.nioInterestOps();
/*  78 */     int j = paramSelectionKeyImpl.nioReadyOps();
/*  79 */     int k = paramInt2;
/*     */     
/*  81 */     if ((paramInt1 & Net.POLLNVAL) != 0) {
/*  82 */       throw new Error("POLLNVAL detected");
/*     */     }
/*  84 */     if ((paramInt1 & (Net.POLLERR | Net.POLLHUP)) != 0) {
/*  85 */       k = i;
/*  86 */       paramSelectionKeyImpl.nioReadyOps(k);
/*  87 */       return (k & (j ^ 0xFFFFFFFF)) != 0;
/*     */     }
/*     */     
/*  90 */     if (((paramInt1 & Net.POLLOUT) != 0) && ((i & 0x4) != 0))
/*     */     {
/*  92 */       k |= 0x4;
/*     */     }
/*  94 */     paramSelectionKeyImpl.nioReadyOps(k);
/*  95 */     return (k & (j ^ 0xFFFFFFFF)) != 0;
/*     */   }
/*     */   
/*     */   public boolean translateAndUpdateReadyOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl) {
/*  99 */     return translateReadyOps(paramInt, paramSelectionKeyImpl.nioReadyOps(), paramSelectionKeyImpl);
/*     */   }
/*     */   
/*     */   public boolean translateAndSetReadyOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl) {
/* 103 */     return translateReadyOps(paramInt, 0, paramSelectionKeyImpl);
/*     */   }
/*     */   
/*     */   public void translateAndSetInterestOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl) {
/* 107 */     if ((paramInt & 0x4) != 0)
/* 108 */       paramInt = Net.POLLOUT;
/* 109 */     paramSelectionKeyImpl.selector.putEventOps(paramSelectionKeyImpl, paramInt);
/*     */   }
/*     */   
/*     */   public int write(ByteBuffer paramByteBuffer) throws IOException {
/*     */     try {
/* 114 */       return this.sc.write(paramByteBuffer);
/*     */     } catch (AsynchronousCloseException localAsynchronousCloseException) {
/* 116 */       close();
/* 117 */       throw localAsynchronousCloseException;
/*     */     }
/*     */   }
/*     */   
/*     */   public long write(ByteBuffer[] paramArrayOfByteBuffer) throws IOException {
/*     */     try {
/* 123 */       return this.sc.write(paramArrayOfByteBuffer);
/*     */     } catch (AsynchronousCloseException localAsynchronousCloseException) {
/* 125 */       close();
/* 126 */       throw localAsynchronousCloseException;
/*     */     }
/*     */   }
/*     */   
/*     */   public long write(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2)
/*     */     throws IOException
/*     */   {
/* 133 */     if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 > paramArrayOfByteBuffer.length - paramInt2))
/* 134 */       throw new IndexOutOfBoundsException();
/*     */     try {
/* 136 */       return write(Util.subsequence(paramArrayOfByteBuffer, paramInt1, paramInt2));
/*     */     } catch (AsynchronousCloseException localAsynchronousCloseException) {
/* 138 */       close();
/* 139 */       throw localAsynchronousCloseException;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\nio\ch\SinkChannelImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */