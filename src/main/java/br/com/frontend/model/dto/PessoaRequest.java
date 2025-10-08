package br.com.frontend.model.dto;

import br.com.frontend.model.enums.TipoPessoa;
import java.time.LocalDate;

public record PessoaRequest(
        String nomeCompleto,
        String cpfCnpj,
        Long numeroCtps,
        LocalDate dataNascimento,
        TipoPessoa tipoPessoa
) {}