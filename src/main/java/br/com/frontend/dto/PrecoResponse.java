package br.com.frontend.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record PrecoResponse(
        Long id,
        Double valor,
        LocalDate dataAlteracao,
        LocalTime horaAlteracao
) {}
