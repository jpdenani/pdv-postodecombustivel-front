package br.com.frontend.dto;

import br.com.frontend.enums.TipoEstoque;

import java.math.BigDecimal;
import java.util.Date;

public record EstoqueResponse(
        Long id,
        BigDecimal quantidade,
        String localTanque,
        String localEndereco,
        String loteFabricacao,
        Date dataValidade,
        TipoEstoque tipoEstoque,
        BigDecimal capacidadeMaxima,    // ✅ Sempre 150.000
        BigDecimal percentualEstoque    // ✅ % atual
) {
}