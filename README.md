# RAG Vector Service

RAG Vector Service 是一个面向检索增强生成（RAG）场景的向量存储与语义检索服务，支持将文本块(chunk)自动嵌入为高维向量并持久化到
Elasticsearch，同时提供基于向量相似度的语义搜索能力。

## 核心功能

- **自动向量化**：调用大模型 Embedding API 将文本转换为向量
- **向量存储**：向量写入 Elasticsearch，支持高效 KNN 检索
- **混合检索**: BM25 + knn 混合检索
- **语义检索**：基于余弦相似度返回 Top-K 最相关文本块

### 可配置项

```yaml
app:
  rag:
    retrieval:
      hybrid-enabled: true        # 是否启用混合检索
      recall-top-k: 5             # 检索时返回的 Top-K 结果数量
      candidate-multiplier: 10    # kNN 候选集倍数
      rrf:
        k: 60                     # 平滑常数
        text-weight: 1.0          # 文本检索（BM25）结果的权重
        vector-weight: 1.0        # 向量检索（kNN）结果的权重
        final-top-k: 5            #  最终返回的 Top-K 结果数量
```

### 环境要求

- Java 17+
- SpringBoot 4.0.1
- Elasticsearch 9x（需启用 `dense_vector` 支持）
- [ali Embedding(text-embedding-v4) 服务](https://bailian.console.aliyun.com/?tab=model#/model-market/detail/text-embedding-v4)

> SpringBoot 4.x 支持 Elasticsearch(9.x) 自动装配，极大简化了相关配置

### 初始化索引

Elasticsearch 索引： `rag_chunks`

mapping 配置：

```json
{
  "settings": {
    "number_of_shards": 1,
    "number_of_replicas": 0
  },
  "mappings": {
    "properties": {
      "chunkId": { "type": "keyword" },
      "fileMd5": { "type": "keyword" },
      "source": { "type": "text" },
      "chunkIndex": { "type": "integer" },
      "content": { "type": "text" },
      "charCount": { "type": "integer" },
      "embedding": {
        "type": "dense_vector",
        "dims": 1024,
        "index": true,
        "similarity": "cosine"
      },
      "model": { "type": "keyword" },
      "createdAt": { "type": "date" }
    }
  }
}
```

> 向量维度（`dims`）需与所用 Embedding 模型输出一致。

## 接口文档

完整 API 说明请参阅：[API 文档](docs/api.md)

## 致谢

- 分布式向量搜索引擎 [Elasticsearch](https://www.elastic.co/elasticsearch/)
- 阿里云百炼大模型平台（Embedding 服务） [DashScope](https://dashscope.aliyun.com/)