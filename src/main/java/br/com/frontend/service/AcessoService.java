package br.com.frontend.service;

import br.com.frontend.dto.AcessoRequest;
import br.com.frontend.dto.AcessoResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class AcessoService {

    private final RestTemplate restTemplate;
    private final String API_BASE_URL = "http://localhost:8080/api/v1/acessos";

    public AcessoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Lista todos os acessos, ajustando page/size para o backend
    public List<AcessoResponse> listarAcessos() {
        int page = 0;          // página inicial
        int size = 100;        // ou maior se precisar de mais registros
        String sortBy = "id";  // ordenar por id
        String dir = "ASC";    // ordem crescente

        String url = String.format("%s?page=%d&size=%d&sortBy=%s&dir=%s",
                API_BASE_URL, page, size, sortBy, dir);

        AcessoResponse[] response = restTemplate.getForObject(url, AcessoResponse[].class);
        return Arrays.asList(response != null ? response : new AcessoResponse[0]);
    }


    // Salvar ou atualizar acesso
    public AcessoResponse salvarAcesso(AcessoRequest request, Long id) {
        if (id == null) {
            return restTemplate.postForObject(API_BASE_URL, request, AcessoResponse.class);
        } else {
            restTemplate.put(API_BASE_URL + "/" + id, request);
            return new AcessoResponse(id, request.usuario(), request.senha(), request.tipoAcesso());
        }
    }

    // Excluir acesso
    public void excluirAcesso(Long id) {
        restTemplate.delete(API_BASE_URL + "/" + id);
    }

    // Autenticação simples
    public boolean autenticar(String usuario, String senha) {
        return listarAcessos().stream()
                .anyMatch(a -> a.usuario().equals(usuario) && a.senha().equals(senha));
    }
}
