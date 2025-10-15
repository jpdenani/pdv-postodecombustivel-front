package br.com.frontend.service;

import br.com.frontend.dto.PrecoRequest;
import br.com.frontend.dto.PrecoResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class PrecoService {

    private final RestTemplate restTemplate;
    private final String API_BASE_URL = "http://localhost:8080/api/v1/precos";

    public PrecoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<PrecoResponse> listarPrecos() {
        PrecoResponse[] response = restTemplate.getForObject(API_BASE_URL + "?size=100", PrecoResponse[].class);
        return Arrays.asList(response != null ? response : new PrecoResponse[0]);
    }

    public PrecoResponse salvarPreco(PrecoRequest request, Long id) {
        if (id == null) {
            return restTemplate.postForObject(API_BASE_URL, request, PrecoResponse.class);
        } else {
            restTemplate.put(API_BASE_URL + "/" + id, request);
            return new PrecoResponse(id, request.valor(), request.dataAlteracao(), request.horaAlteracao());
        }
    }

    public void excluirPreco(Long id) {
        restTemplate.delete(API_BASE_URL + "/" + id);
    }
}
