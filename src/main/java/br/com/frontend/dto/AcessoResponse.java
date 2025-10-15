package br.com.frontend.dto;

import br.com.frontend.enums.TipoAcesso;

public record AcessoResponse(
        Long id,
        String usuario,
        String senha,
        TipoAcesso tipoAcesso
) {}
