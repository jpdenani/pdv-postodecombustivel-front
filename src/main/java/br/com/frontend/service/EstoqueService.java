package br.com.frontend.service;

import br.com.frontend.dto.EstoqueRequest;
import br.com.frontend.dto.EstoqueResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;
import java.util.List;

@Service
public class EstoqueService {

    private final RestTemplate restTemplate;
    private final String API_BASE_URL = "http://localhost:8080/api/v1/estoques";

    public EstoqueService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<EstoqueResponse> listarEstoques() {
        EstoqueResponse[] response = restTemplate.getForObject(API_BASE_URL + "?size=100", EstoqueResponse[].class);
        return Arrays.asList(response != null ? response : new EstoqueResponse[0]);
    }

    public EstoqueResponse salvarEstoque(EstoqueRequest request, Long id) {
        if (id == null) {
            return restTemplate.postForObject(API_BASE_URL, request, EstoqueResponse.class);
        } else {
            restTemplate.put(API_BASE_URL + "/" + id, request);

            // ✅ Cria uma resposta "simples" só pra feedback
            return new EstoqueResponse(
                    id,
                    request.quantidade(),
                    request.localTanque(),
                    request.localEndereco(),
                    request.loteFabricacao(),
                    request.dataValidade(),
                    request.tipoEstoque(),
                    new java.math.BigDecimal("150000"), // capacidade padrão
                    null // percentual será atualizado quando recarregar do backend
            );
        }
    }


    public void excluirEstoque(Long id) {
        restTemplate.delete(API_BASE_URL + "/" + id);
    }
}
