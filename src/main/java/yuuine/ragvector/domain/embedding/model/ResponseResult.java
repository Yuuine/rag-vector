package yuuine.ragvector.domain.embedding.model;

import lombok.Data;
import yuuine.ragvector.domain.es.model.RagChunkDocument;
import yuuine.ragvector.dto.response.VectorAddResult;

import java.util.List;

@Data
public class ResponseResult {

    private List<RagChunkDocument> ragChunkDocuments;
    private VectorAddResult vectorAddResult;

}
