package br.com.frontend.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record PrecoRequest(
        Double valor,
        LocalDate dataAlteracao,
        LocalTime horaAlteracao
) {}
