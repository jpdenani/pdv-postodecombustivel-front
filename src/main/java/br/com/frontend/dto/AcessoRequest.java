package br.com.frontend.dto;

import br.com.frontend.enums.TipoAcesso;

public record AcessoRequest(
        String usuario,
        String senha,
        TipoAcesso tipoAcesso
) {}
