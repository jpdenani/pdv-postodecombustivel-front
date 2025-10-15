package br.com.frontend.dto;

public record ContatoResponse(
        Long id,
        String email,
        String telefone,
        String endereco
) {}
