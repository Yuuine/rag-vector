package yuuine.ragvector.domain.es.service;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import yuuine.ragvector.domain.es.model.RagChunkDocument;

@Repository
public interface RagChunkDocumentRepository extends ElasticsearchRepository<RagChunkDocument, String> {



}
