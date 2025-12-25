package yuuine.ragvector.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import yuuine.ragvector.domain.embedding.service.EmbeddingService;
import yuuine.ragvector.domain.es.service.VectorAddService;
import yuuine.ragvector.domain.es.service.VectorSearchService;
import yuuine.ragvector.domain.embedding.model.ResponseResult;
import yuuine.ragvector.domain.es.model.RagChunkDocument;
import yuuine.ragvector.dto.request.VectorAddRequest;
import yuuine.ragvector.dto.request.VectorSearchRequest;
import yuuine.ragvector.dto.response.VectorAddResult;
import yuuine.ragvector.dto.response.VectorSearchResult;

import java.io.IOException;
import java.util.List;

@Slf4j
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

        log.info("接收到向量添加请求，待处理chunks数量: {}", chunks != null ? chunks.size() : 0);

        // 将 chunks 进行向量化处理，得到一个 List<RagChunkDocument> 对象和处理结果对象，包含所有的 chunk 的向量化结果
        ResponseResult responseResult = embeddingService.embedBatch(chunks);
        List<RagChunkDocument> ragChunkDocuments = responseResult.getRagChunkDocuments();

        // 将 List<RagChunkDocument> 对象保存到 ES 中
        vectorAddService.saveAll(ragChunkDocuments);

        log.info("向量添加完成，成功: {}, 失败: {}",
                responseResult.getVectorAddResult().getSuccessChunk(),
                responseResult.getVectorAddResult().getFailedChunk());

        return responseResult.getVectorAddResult();
    }

    @PostMapping("/search")
    public List<VectorSearchResult> search(
            @RequestBody VectorSearchRequest vectorSearchRequest) throws IOException {

        log.info("接收到向量搜索请求: query='{}', topK={}",
                vectorSearchRequest.getQuery() != null ? vectorSearchRequest.getQuery().substring(0, Math.min(vectorSearchRequest.getQuery().length(), 50)) + (vectorSearchRequest.getQuery().length() > 50 ? "..." : "") : null,
                vectorSearchRequest.getTopK());

        List<VectorSearchResult> results = vectorSearchService.search(vectorSearchRequest);

        log.info("向量搜索完成，返回结果数量: {}", results.size());

        return results;
    }
}
