package yuuine.ragvector.domain.embedding.service;

import yuuine.ragvector.domain.embedding.model.ResponseResult;
import yuuine.ragvector.dto.request.VectorAddRequest;

import java.util.List;

public interface EmbeddingService {

    ResponseResult embedBatch(List<VectorAddRequest>  chunks);

}
