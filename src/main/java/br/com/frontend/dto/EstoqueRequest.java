package br.com.frontend.dto;

import br.com.frontend.enums.TipoEstoque;

import java.math.BigDecimal;
import java.util.Date;

// ✅ Data como String para evitar problemas de serialização
public record EstoqueRequest(
        BigDecimal quantidade,
        String localTanque,
        String localEndereco,
        String loteFabricacao,
        Date dataValidade,      // ✅ String no formato "dd/MM/yyyy"
        TipoEstoque tipoEstoque
) {
}