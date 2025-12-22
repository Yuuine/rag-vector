package yuuine.ragvector.domain.embedding.service;

import yuuine.ragvector.domain.embedding.model.ResponseResult;
import yuuine.ragvector.dto.request.VectorAddRequest;

import java.util.List;

public interface EmbeddingService {

    // 批量向量化 ( add 服务）
    ResponseResult embedBatch(List<VectorAddRequest>  chunks);

    // 单条 query 向量化 ( search 服务）
    float[] embedQuery(String query);
}
