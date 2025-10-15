package br.com.frontend.dto;

public record ContatoRequest(
        String email,
        String telefone,
        String endereco
) {}
