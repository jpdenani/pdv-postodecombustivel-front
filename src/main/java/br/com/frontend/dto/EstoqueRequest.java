package br.com.frontend.dto;

import br.com.frontend.enums.TipoEstoque;
import java.math.BigDecimal;

public record EstoqueRequest(
        Long produtoId,
        BigDecimal quantidade,
        String localTanque,
        String localEndereco,
        String loteFabricacao,
        String dataValidade,
        TipoEstoque tipoEstoque
) {}