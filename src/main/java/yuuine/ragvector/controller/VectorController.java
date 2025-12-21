package yuuine.ragvector.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import yuuine.ragvector.domain.service.EmbeddingService;
import yuuine.ragvector.dto.request.VectorAddRequest;
import yuuine.ragvector.dto.response.VectorAddResult;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/vectors")
public class VectorController {

    private final EmbeddingService embeddingService;

    @PostMapping("/add")
    public VectorAddResult add(
            @RequestBody List<VectorAddRequest> chunks
    ) {

        //将 chunks 进行向量化处理
        return embeddingService.embedBatch(chunks);

    }

}
