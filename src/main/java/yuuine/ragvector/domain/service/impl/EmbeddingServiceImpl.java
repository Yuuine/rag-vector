package yuuine.ragvector.domain.service.impl;

import com.alibaba.dashscope.embeddings.TextEmbeddingResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import yuuine.ragvector.domain.service.EmbeddingService;
import yuuine.ragvector.dto.request.VectorAddRequest;
import yuuine.ragvector.dto.response.VectorAddResult;
import yuuine.ragvector.dto.response.VectorChunk;
import yuuine.ragvector.util.DashScopeEmbeddingUtil;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmbeddingServiceImpl implements EmbeddingService {

    private final DashScopeEmbeddingUtil dashScopeEmbeddingUtil;

    // 每次 embedding 请求的 chunk 数量
    private static final int BATCH_SIZE = 10;

    @Override
    public VectorAddResult embedBatch(List<VectorAddRequest> chunks) {

        VectorAddResult result = new VectorAddResult();
        List<VectorChunk> vectorChunks = new ArrayList<>();

        int successCount = 0;
        int failedCount = 0;

        if (chunks == null || chunks.isEmpty()) {
            result.setSuccessChunk(0);
            result.setFailedChunk(0);
            result.setVectorChunks(vectorChunks);
            return result;
        }
        // 分批处理
        for (int i = 0; i < chunks.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, chunks.size());
            List<VectorAddRequest> batch = chunks.subList(i, end);

            // 提取文本
            List<String> texts = batch.stream()
                    .map(VectorAddRequest::getChunkText)
                    .toList();

            try {

                //TODO: 将向量化结果封装后存入 ES
                TextEmbeddingResult embeddingResult = dashScopeEmbeddingUtil.generateEmbeddingResult(texts);

                // 调用成功，按顺序为本批次每个 chunk 创建成功记录
                for (VectorAddRequest req : batch) {
                    VectorChunk vc = new VectorChunk();
                    vc.setChunkId(req.getChunkId());
                    vc.setSuccess(true);
                    vc.setErrorMessage(null);

                    vectorChunks.add(vc);
                    successCount++;
                }

            } catch (Exception e) {
                // 本批次整体失败，为每个 chunk 标记失败
                for (VectorAddRequest req : batch) {
                    VectorChunk vc = new VectorChunk();
                    vc.setChunkId(req.getChunkId());
                    vc.setSuccess(false);
                    vc.setErrorMessage("Embedding 调用失败: " + e.getMessage());

                    vectorChunks.add(vc);
                    failedCount++;
                }
            }
        }

        result.setSuccessChunk(successCount);
        result.setFailedChunk(failedCount);
        result.setVectorChunks(vectorChunks);

        log.info("embedding success, successCount={}, failedCount={}", successCount, failedCount);

        return result;
    }
}
