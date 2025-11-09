package br.com.frontend.dto;

import java.util.Date;

public record CustoRequest(
        Long produtoId,
        Double imposto,
        Double custoVariavel,
        Double custoFixo,
        Double margemLucro,
        Date dataProcessamento
) {}