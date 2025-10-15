package br.com.frontend.service;

import br.com.frontend.dto.ProdutoRequest;
import br.com.frontend.dto.ProdutoResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class ProdutoService {

    private final RestTemplate restTemplate;
    private final String API_BASE_URL = "http://localhost:8080/api/v1/produtos";

    public ProdutoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<ProdutoResponse> listarProdutos() {
        ProdutoResponse[] response = restTemplate.getForObject(API_BASE_URL + "?size=100", ProdutoResponse[].class);
        return Arrays.asList(response != null ? response : new ProdutoResponse[0]);
    }

    public ProdutoResponse salvarProduto(ProdutoRequest request, Long id) {
        if (id == null) {
            return restTemplate.postForObject(API_BASE_URL, request, ProdutoResponse.class);
        } else {
            restTemplate.put(API_BASE_URL + "/" + id, request);
            return new ProdutoResponse(id, request.nome(), request.referencia(), request.categoria(), request.fornecedor(), request.marca(), request.tipoProduto());
        }
    }

    public void excluirProduto(Long id) {
        restTemplate.delete(API_BASE_URL + "/" + id);
    }
}
