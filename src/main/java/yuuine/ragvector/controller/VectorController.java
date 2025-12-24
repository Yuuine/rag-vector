package yuuine.ragvector.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import yuuine.ragvector.domain.embedding.model.ResponseResult;
import yuuine.ragvector.domain.embedding.service.EmbeddingService;
import yuuine.ragvector.domain.es.model.RagChunkDocument;
import yuuine.ragvector.domain.es.service.VectorAddService;
import yuuine.ragvector.domain.es.service.VectorSearchService;
import yuuine.ragvector.dto.request.VectorAddRequest;
import yuuine.ragvector.dto.request.VectorSearchRequest;
import yuuine.ragvector.dto.response.VectorAddResult;
import yuuine.ragvector.dto.response.VectorSearchResult;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/vectors")
public class VectorController {

    private final EmbeddingService embeddingService;
    private final VectorAddService vectorAddService;
    private final VectorSearchService vectorSearchService;

    @PostMapping("/add")
    public VectorAddResult add(
            @RequestBody List<VectorAddRequest> chunks) {

        // 将 chunks 进行向量化处理，得到一个 List<RagChunkDocument> 对象和处理结果对象，包含所有的 chunk 的向量化结果
        ResponseResult responseResult = embeddingService.embedBatch(chunks);
        List<RagChunkDocument> ragChunkDocuments = responseResult.getRagChunkDocuments();

        // 将 List<RagChunkDocument> 对象保存到 ES 中
        vectorAddService.saveAll(ragChunkDocuments);

        return responseResult.getVectorAddResult();
    }

    @PostMapping("/search")
    public List<VectorSearchResult> search(
            @RequestBody VectorSearchRequest vectorSearchRequest) {

        /* 将 vectorSearchRequest 传给 VectorSearchService
           返回一个 List<VectorSearchResult> 对象  */
        return vectorSearchService.search(vectorSearchRequest);
    }
}
