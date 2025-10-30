package br.com.frontend.dto;

import java.math.BigDecimal;

public record VendaRequest(
        Long bombaId,
        Long produtoId,
        BigDecimal litros,
        String usuarioVendedor
) {}