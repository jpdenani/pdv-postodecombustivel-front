// EstoqueRequest.java
package br.com.frontend.dto;

import br.com.frontend.enums.TipoEstoque;
import java.util.Date;

public record EstoqueRequest(
        java.math.BigDecimal quantidade,
        String localTanque,
        String localEndereco,
        String loteFabricacao,
        Date dataValidade,
        TipoEstoque tipoEstoque
) {}
