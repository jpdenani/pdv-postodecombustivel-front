package br.com.frontend.dto;

import br.com.frontend.enums.TipoEstoque;
import java.time.LocalDate;

public record EstoqueResponse(
        Long id,
        Integer quantidade,
        String localTanque,
        String localEndereco,
        String loteFabricacao,
        LocalDate dataValidade,
        TipoEstoque tipoEstoque
) {}
