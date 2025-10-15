package br.com.frontend.dto;

import br.com.frontend.enums.TipoEstoque;
import java.time.LocalDate;

public record EstoqueRequest(
        Integer quantidade,
        String localTanque,
        String localEndereco,
        String loteFabricacao,
        LocalDate dataValidade,
        TipoEstoque tipoEstoque
) {}
