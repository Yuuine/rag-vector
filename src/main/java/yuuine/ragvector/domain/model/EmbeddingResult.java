package yuuine.ragvector.domain.model;

import lombok.Data;

@Data
public class EmbeddingResult {

    private float[] vector;
    private String model;
    private int dimension;

}
