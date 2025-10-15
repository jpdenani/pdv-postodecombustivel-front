package br.com.frontend.model;

import br.com.frontend.dto.AcessoResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AcessoPageResponse {
    private List<AcessoResponse> content;

    public List<AcessoResponse> getContent() {
        return content;
    }

    public void setContent(List<AcessoResponse> content) {
        this.content = content;
    }
}
