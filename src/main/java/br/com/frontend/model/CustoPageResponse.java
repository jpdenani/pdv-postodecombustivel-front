package br.com.frontend.model;

import br.com.frontend.dto.CustoResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CustoPageResponse {
    private List<CustoResponse> content;

    public List<CustoResponse> getContent() {
        return content;
    }

    public void setContent(List<CustoResponse> content) {
        this.content = content;
    }
}
