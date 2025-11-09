package br.com.frontend.dto;

import java.util.Date;

public record CustoResponse(
        Long id,
        Long produtoId,
        String produtoNome,
        Double imposto,
        Double custoVariavel,
        Double custoFixo,
        Double margemLucro,
        Date dataProcessamento
) {}