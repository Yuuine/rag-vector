# RAG Vector Service

RAG Vector Service 提供面向 RAG 场景的向量存储与语义检索，将文本块自动嵌入高维向量并支持高效 Elasticsearch 搜索。

## 核心功能

- **文本向量化与语义检索**：调用大模型 Embedding API 将文本转换为高维向量，并基于余弦相似度返回最相关的 Top-K 文本块
- **向量存储与高效 KNN 检索**：将向量写入 Elasticsearch，实现快速语义搜索
- **混合检索（BM25 + KNN + RRF）**: 结合 IK 分词器的 BM25 词面匹配与 KNN 语义匹配，通过 RRF 算法融合排序，提高检索准确性和召回率

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
      "chunkId": {
        "type": "keyword"
      },
      "fileMd5": {
        "type": "keyword"
      },
      "source": {
        "type": "text"
      },
      "chunkIndex": {
        "type": "integer"
      },
      "content": {
        "type": "text",
        "analyzer": "ik_max_word",
        "search_analyzer": "ik_smart"
      },
      "charCount": {
        "type": "integer"
      },
      "embedding": {
        "type": "dense_vector",
        "dims": 1024,
        "index": true,
        "similarity": "cosine"
      },
      "embeddingDim": {
        "type": "integer"
      },
      "model": {
        "type": "keyword"
      },
      "createdAt": {
        "type": "date"
      }
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
- ik 分词器 [IK Analyzer](https://github.com/medcl/elasticsearch-analysis-ik)