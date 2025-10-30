package br.com.frontend.dto;

import br.com.frontend.enums.TipoBomba;

public record BombaRequest(
        Integer numero,
        TipoBomba tipoBomba
) {}