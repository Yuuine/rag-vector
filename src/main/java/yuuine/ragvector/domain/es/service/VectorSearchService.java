package yuuine.ragvector.domain.es.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import yuuine.ragvector.domain.embedding.service.EmbeddingService;
import yuuine.ragvector.domain.es.Repository.RagChunkDocumentRepository;
import yuuine.ragvector.domain.es.model.RagChunkDocument;
import yuuine.ragvector.dto.request.VectorSearchRequest;
import yuuine.ragvector.dto.response.VectorSearchResult;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VectorSearchService {

    private final EmbeddingService embeddingService;
    private final RagChunkDocumentRepository repository;

    public List<VectorSearchResult> search(VectorSearchRequest vectorSearchRequest) {

        String query = vectorSearchRequest.getQuery();
        Integer topK = vectorSearchRequest.getTopK();

        log.info("开始执行向量搜索: query={}, topK={}", query, topK);

        // 1. query → embedding
        float[] queryVector = embeddingService.embedQuery(query);
        log.debug("查询文本向量化完成，向量维度: {}", queryVector.length);

        // 2. 向量检索
        List<Float> queryVectorList = new ArrayList<>(queryVector.length);
        for (float v : queryVector) {
            queryVectorList.add(v);
        }

        List<RagChunkDocument> docs =
                repository.knnSearch(queryVectorList, topK, topK * 10);

        log.info("向量检索完成，返回结果数量: {}", docs.size());

        // 3. 结果转换（领域对象 → DTO）
        List<VectorSearchResult> results = docs.stream().map(doc -> {
            VectorSearchResult r = new VectorSearchResult();
            r.setChunkId(doc.getChunkId());
            r.setSource(doc.getSource());
            r.setChunkIndex(doc.getChunkIndex());
            r.setContent(doc.getContent());
            // Spring Data ES 默认不返回 score
            r.setScore(null);
            return r;
        }).collect(Collectors.toList());

        log.info("搜索结果转换完成，最终返回结果数量: {}", results.size());
        return results;
    }
}
