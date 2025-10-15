package br.com.frontend.view.main;

import br.com.frontend.view.pessoa.TelaPessoaCrud;
import br.com.frontend.view.acesso.TelaAcessoCrud;
import br.com.frontend.view.contato.TelaContatoCrud;
import br.com.frontend.view.custo.TelaCustoCrud;
import br.com.frontend.view.estoque.TelaEstoqueCrud;
import br.com.frontend.view.preco.TelaPrecoCrud;
import br.com.frontend.view.produto.TelaProdutoCrud;

import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
public class TelaPrincipal extends JFrame {

    private final TelaPessoaCrud telaPessoa;
    private final TelaAcessoCrud telaAcesso;
    private final TelaContatoCrud telaContato;
    private final TelaCustoCrud telaCusto;
    private final TelaEstoqueCrud telaEstoque;
    private final TelaPrecoCrud telaPreco;
    private final TelaProdutoCrud telaProduto;

    public TelaPrincipal(TelaPessoaCrud telaPessoa,
                         TelaAcessoCrud telaAcesso,
                         TelaContatoCrud telaContato,
                         TelaCustoCrud telaCusto,
                         TelaEstoqueCrud telaEstoque,
                         TelaPrecoCrud telaPreco,
                         TelaProdutoCrud telaProduto) {
        this.telaPessoa = telaPessoa;
        this.telaAcesso = telaAcesso;
        this.telaContato = telaContato;
        this.telaCusto = telaCusto;
        this.telaEstoque = telaEstoque;
        this.telaPreco = telaPreco;
        this.telaProduto = telaProduto;

        setTitle("Sistema PDV Posto de Combustível");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // --- Abas ---
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Pessoas", telaPessoa);
        tabbedPane.addTab("Acessos", telaAcesso);
        tabbedPane.addTab("Contatos", telaContato);
        tabbedPane.addTab("Custos", telaCusto);
        tabbedPane.addTab("Estoques", telaEstoque);
        tabbedPane.addTab("Preços", telaPreco);
        tabbedPane.addTab("Produtos", telaProduto);

        add(tabbedPane, BorderLayout.CENTER);
    }
}
