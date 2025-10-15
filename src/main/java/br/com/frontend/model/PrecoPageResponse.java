package br.com.frontend.model;

import br.com.frontend.dto.PrecoResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PrecoPageResponse {
    private List<PrecoResponse> content;

    public List<PrecoResponse> getContent() {
        return content;
    }

    public void setContent(List<PrecoResponse> content) {
        this.content = content;
    }
}
