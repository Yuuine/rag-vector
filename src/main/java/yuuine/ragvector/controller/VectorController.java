package yuuine.ragvector.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import yuuine.ragvector.domain.embedding.model.ResponseResult;
import yuuine.ragvector.domain.embedding.service.EmbeddingService;
import yuuine.ragvector.domain.es.model.RagChunkDocument;
import yuuine.ragvector.domain.es.Repository.RagChunkDocumentRepository;
import yuuine.ragvector.domain.es.service.ESAddService;
import yuuine.ragvector.dto.request.VectorAddRequest;
import yuuine.ragvector.dto.response.VectorAddResult;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/vectors")
public class VectorController {

    private final EmbeddingService embeddingService;
    private final ESAddService esAddService;

    @PostMapping("/add")
    public VectorAddResult add(
            @RequestBody List<VectorAddRequest> chunks) {

        // 将 chunks 进行向量化处理，得到一个 List<RagChunkDocument> 对象和处理结果对象，包含所有的 chunk 的向量化结果
        ResponseResult responseResult = embeddingService.embedBatch(chunks);
        List<RagChunkDocument> ragChunkDocuments = responseResult.getRagChunkDocuments();
        VectorAddResult vectorAddResult = responseResult.getVectorAddResult();

        // 将 List<RagChunkDocument> 对象保存到 ES 中
        esAddService.saveAll(ragChunkDocuments);

        return vectorAddResult;
    }
}
