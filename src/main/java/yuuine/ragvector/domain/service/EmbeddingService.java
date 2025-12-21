package yuuine.ragvector.domain.service;

import yuuine.ragvector.dto.request.VectorAddRequest;
import yuuine.ragvector.dto.response.VectorAddResult;

import java.util.List;

public interface EmbeddingService {

    VectorAddResult embedBatch(List<VectorAddRequest>  chunks);

}
