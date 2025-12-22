package yuuine.ragvector.domain.embedding.model;

import lombok.Data;

@Data
public class VectorizeChunkRequest {
    private String chunkId;
    private String fileMd5;
    private String source;
    private Integer chunkIndex;
    private String chunkText;
    private Integer charCount;
}

