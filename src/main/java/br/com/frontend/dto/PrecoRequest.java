package br.com.frontend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record PrecoRequest(
        BigDecimal valor,
        LocalDate dataAlteracao,
        LocalTime horaAlteracao
) {}