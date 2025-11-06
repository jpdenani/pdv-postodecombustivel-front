package br.com.frontend.enums;

// ✅ ENUM ATUALIZADO com os novos percentuais
public enum TipoEstoque {
    CRITICO("Crítico"),      // 0-20%
    BAIXO("Baixo"),          // 20-45%
    MEDIO("Médio"),          // 45-75%
    ALTO("Alto");            // 75-100%

    private final String descricao;

    TipoEstoque(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}