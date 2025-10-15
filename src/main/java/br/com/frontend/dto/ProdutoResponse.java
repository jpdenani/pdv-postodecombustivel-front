package br.com.frontend.dto;

import br.com.frontend.enums.TipoProduto;

public record ProdutoResponse(
        Long id,
        String nome,
        String referencia,
        String categoria,
        String fornecedor,
        String marca,
        TipoProduto tipoProduto
) {}
