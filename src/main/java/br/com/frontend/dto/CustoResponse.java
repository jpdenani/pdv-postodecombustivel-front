package br.com.frontend.dto;

import java.time.LocalDate;

public record CustoResponse(
        Long id,
        Double imposto,
        Double custoVariavel,
        Double custoFixo,
        Double margemLucro,
        LocalDate dataProcessamento
) {}
