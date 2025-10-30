package br.com.frontend.dto;

import br.com.frontend.enums.TipoBomba;

public record BombaResponse(
        Long id,
        Integer numero,
        TipoBomba tipoBomba
) {}
