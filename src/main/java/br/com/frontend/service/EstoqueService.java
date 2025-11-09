package br.com.frontend.service;

import br.com.frontend.dto.EstoqueRequest;
import br.com.frontend.dto.EstoqueResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class EstoqueService {

    private final RestTemplate restTemplate;
    private final String API_BASE_URL = "http://localhost:8080/api/v1/estoques";
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

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


            Date dataValidade = null;
            try {
                dataValidade = dateFormat.parse(request.dataValidade());
            } catch (Exception e) {
                System.err.println("Erro ao converter data: " + e.getMessage());
            }


            return new EstoqueResponse(
                    id,                                      // 1. id
                    request.produtoId(),                     // 2. produtoId ✅ NOVO
                    "Produto",                               // 3. nomeProduto ✅ NOVO (placeholder)
                    request.quantidade(),                    // 4. quantidade
                    request.localTanque(),                   // 5. localTanque
                    request.localEndereco(),                 // 6. localEndereco
                    request.loteFabricacao(),                // 7. loteFabricacao
                    dataValidade,                            // 8. dataValidade
                    request.tipoEstoque(),                   // 9. tipoEstoque
                    new java.math.BigDecimal("150000"),      // 10. capacidadeMaxima
                    null                                     // 11. percentualEstoque
            );
        }
    }

    public void excluirEstoque(Long id) {
        restTemplate.delete(API_BASE_URL + "/" + id);
    }
}