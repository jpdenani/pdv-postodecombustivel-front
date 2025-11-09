package br.com.frontend.dto;

import br.com.frontend.enums.TipoEstoque;
import java.math.BigDecimal;
import java.util.Date;

public record EstoqueResponse(
        Long id,
        Long produtoId,
        String nomeProduto,
        BigDecimal quantidade,
        String localTanque,
        String localEndereco,
        String loteFabricacao,
        Date dataValidade,
        TipoEstoque tipoEstoque,
        BigDecimal capacidadeMaxima,
        BigDecimal percentualEstoque
) {}