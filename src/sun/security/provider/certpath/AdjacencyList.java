/*     */ package sun.security.provider.certpath;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AdjacencyList
/*     */ {
/*     */   private ArrayList<BuildStep> mStepList;
/*     */   private List<List<Vertex>> mOrigList;
/*     */   
/*     */   public AdjacencyList(List<List<Vertex>> paramList)
/*     */   {
/* 101 */     this.mStepList = new ArrayList();
/* 102 */     this.mOrigList = paramList;
/* 103 */     buildList(paramList, 0, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Iterator<BuildStep> iterator()
/*     */   {
/* 114 */     return Collections.unmodifiableList(this.mStepList).iterator();
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
/*     */   private boolean buildList(List<List<Vertex>> paramList, int paramInt, BuildStep paramBuildStep)
/*     */   {
/* 129 */     List localList = (List)paramList.get(paramInt);
/*     */     
/*     */ 
/* 132 */     int i = 1;
/*     */     
/* 134 */     int j = 1;
/*     */     
/* 136 */     for (Object localObject1 = localList.iterator(); ((Iterator)localObject1).hasNext();) { localObject2 = (Vertex)((Iterator)localObject1).next();
/* 137 */       if (((Vertex)localObject2).getIndex() != -1)
/*     */       {
/*     */ 
/* 140 */         if (((List)paramList.get(((Vertex)localObject2).getIndex())).size() != 0) {
/* 141 */           i = 0;
/*     */         }
/* 143 */       } else if (((Vertex)localObject2).getThrowable() == null) {
/* 144 */         j = 0;
/*     */       }
/*     */       
/*     */ 
/* 148 */       this.mStepList.add(new BuildStep((Vertex)localObject2, 1));
/*     */     }
/*     */     Vertex localVertex;
/* 151 */     if (i != 0)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 156 */       if (j != 0)
/*     */       {
/* 158 */         if (paramBuildStep == null) {
/* 159 */           this.mStepList.add(new BuildStep(null, 4));
/*     */         } else {
/* 161 */           this.mStepList.add(new BuildStep(paramBuildStep.getVertex(), 2));
/*     */         }
/*     */         
/* 164 */         return false;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 170 */       localObject1 = new ArrayList();
/* 171 */       for (localObject2 = localList.iterator(); ((Iterator)localObject2).hasNext();) { localVertex = (Vertex)((Iterator)localObject2).next();
/* 172 */         if (localVertex.getThrowable() == null) {
/* 173 */           ((List)localObject1).add(localVertex);
/*     */         }
/*     */       }
/* 176 */       if (((List)localObject1).size() == 1)
/*     */       {
/* 178 */         this.mStepList.add(new BuildStep((Vertex)((List)localObject1).get(0), 5));
/*     */ 
/*     */ 
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 188 */         this.mStepList.add(new BuildStep((Vertex)((List)localObject1).get(0), 5));
/*     */       }
/*     */       
/*     */ 
/* 192 */       return true;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 200 */     boolean bool = false;
/*     */     
/* 202 */     for (Object localObject2 = localList.iterator(); ((Iterator)localObject2).hasNext();) { localVertex = (Vertex)((Iterator)localObject2).next();
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 208 */       if ((localVertex.getIndex() != -1) && 
/* 209 */         (((List)paramList.get(localVertex.getIndex())).size() != 0))
/*     */       {
/*     */ 
/*     */ 
/* 213 */         BuildStep localBuildStep = new BuildStep(localVertex, 3);
/* 214 */         this.mStepList.add(localBuildStep);
/* 215 */         bool = buildList(paramList, localVertex.getIndex(), localBuildStep);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 220 */     if (bool)
/*     */     {
/* 222 */       return true;
/*     */     }
/*     */     
/*     */ 
/* 226 */     if (paramBuildStep == null) {
/* 227 */       this.mStepList.add(new BuildStep(null, 4));
/*     */     } else {
/* 229 */       this.mStepList.add(new BuildStep(paramBuildStep.getVertex(), 2));
/*     */     }
/*     */     
/* 232 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 244 */     StringBuilder localStringBuilder = new StringBuilder("[\n");
/*     */     
/* 246 */     int i = 0;
/* 247 */     for (List localList : this.mOrigList) {
/* 248 */       localStringBuilder.append("LinkedList[").append(i++).append("]:\n");
/*     */       
/* 250 */       for (Vertex localVertex : localList) {
/* 251 */         localStringBuilder.append(localVertex.toString()).append("\n");
/*     */       }
/*     */     }
/* 254 */     localStringBuilder.append("]\n");
/*     */     
/* 256 */     return localStringBuilder.toString();
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\provider\certpath\AdjacencyList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */