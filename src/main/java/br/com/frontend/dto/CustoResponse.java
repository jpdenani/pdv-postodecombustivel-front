package br.com.frontend.dto;

import java.util.Date;

public record CustoResponse(
        Long id,
        Double imposto,
        Double custoVariavel,
        Double custoFixo,
        Double margemLucro,
        Date dataProcessamento // ✅ MUDOU de LocalDate para Date
) {}