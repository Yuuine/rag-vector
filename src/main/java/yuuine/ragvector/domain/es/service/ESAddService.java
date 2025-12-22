package yuuine.ragvector.domain.es.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import yuuine.ragvector.domain.es.Repository.RagChunkDocumentRepository;
import yuuine.ragvector.domain.es.model.RagChunkDocument;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ESAddService {

    private final RagChunkDocumentRepository ragChunkDocumentRepository;

    public void saveAll(List<RagChunkDocument> documents) {

        ragChunkDocumentRepository.saveAll(documents);

        log.info("ESAddService: saveAll: documents={}", documents.size());
    }

}
