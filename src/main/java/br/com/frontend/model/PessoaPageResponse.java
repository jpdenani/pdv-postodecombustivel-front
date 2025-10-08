package br.com.frontend.model;

import br.com.frontend.model.dto.PessoaResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PessoaPageResponse {
    private List<PessoaResponse> content;

    public List<PessoaResponse> getContent() {
        return content;
    }

    public void setContent(List<PessoaResponse> content) {
        this.content = content;
    }
}
