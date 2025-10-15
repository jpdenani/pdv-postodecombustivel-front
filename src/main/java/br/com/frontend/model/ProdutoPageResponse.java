package br.com.frontend.model;

import br.com.frontend.dto.ProdutoResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProdutoPageResponse {
    private List<ProdutoResponse> content;

    public List<ProdutoResponse> getContent() {
        return content;
    }

    public void setContent(List<ProdutoResponse> content) {
        this.content = content;
    }
}
