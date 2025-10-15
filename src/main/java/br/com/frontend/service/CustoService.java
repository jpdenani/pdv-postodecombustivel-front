package br.com.frontend.service;

import br.com.frontend.dto.CustoRequest;
import br.com.frontend.dto.CustoResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

@Service
public class CustoService {

    private final RestTemplate restTemplate;
    private final String API_BASE_URL = "http://localhost:8080/api/v1/custos";

    public CustoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<CustoResponse> listarCustos() {
        CustoResponse[] response = restTemplate.getForObject(API_BASE_URL + "?size=100", CustoResponse[].class);
        return Arrays.asList(response != null ? response : new CustoResponse[0]);
    }

    public CustoResponse salvarCusto(CustoRequest request, Long id) {
        if (id == null) {
            return restTemplate.postForObject(API_BASE_URL, request, CustoResponse.class);
        } else {
            restTemplate.put(API_BASE_URL + "/" + id, request);

            // Converte java.util.Date para java.time.LocalDate
            LocalDate dataProcessamentoLocal = request.dataProcessamento() == null ? null :
                    request.dataProcessamento().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();

            return new CustoResponse(
                    id,
                    request.imposto(),
                    request.custoVariavel(),
                    request.custoFixo(),
                    request.margemLucro(),
                    dataProcessamentoLocal
            );
        }
    }

    public void excluirCusto(Long id) {
        restTemplate.delete(API_BASE_URL + "/" + id);
    }
}
