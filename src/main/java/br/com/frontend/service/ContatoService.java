package br.com.frontend.service;

import br.com.frontend.dto.ContatoRequest;
import br.com.frontend.dto.ContatoResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class ContatoService {

    private final RestTemplate restTemplate;
    private final String API_BASE_URL = "http://localhost:8080/api/v1/contatos";

    public ContatoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<ContatoResponse> listarContatos() {
        ContatoResponse[] response = restTemplate.getForObject(API_BASE_URL + "?size=100", ContatoResponse[].class);
        return Arrays.asList(response != null ? response : new ContatoResponse[0]);
    }

    public ContatoResponse salvarContato(ContatoRequest request, Long id) {
        if (id == null) {
            return restTemplate.postForObject(API_BASE_URL, request, ContatoResponse.class);
        } else {
            restTemplate.put(API_BASE_URL + "/" + id, request);
            return new ContatoResponse(id, request.email(), request.telefone(), request.endereco());
        }
    }

    public void excluirContato(Long id) {
        restTemplate.delete(API_BASE_URL + "/" + id);
    }
}
