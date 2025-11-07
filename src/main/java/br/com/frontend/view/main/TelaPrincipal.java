package br.com.frontend.view.main;

import br.com.frontend.dto.AcessoResponse;
import br.com.frontend.dto.BombaResponse;
import br.com.frontend.enums.TipoAcesso;
import br.com.frontend.view.acesso.TelaAcessoCrud;
import br.com.frontend.view.bomba.TelaBombaCrud;
import br.com.frontend.view.contato.TelaContatoCrud;
import br.com.frontend.view.custo.TelaCustoCrud;
import br.com.frontend.view.estoque.TelaEstoqueCrud;
import br.com.frontend.view.pessoa.TelaPessoaCrud;
import br.com.frontend.view.preco.TelaPrecoCrud;
import br.com.frontend.view.produto.TelaProdutoCrud;
import br.com.frontend.view.venda.TelaVenda;
import jakarta.annotation.PostConstruct;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TelaPrincipal extends JFrame {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String API_BOMBAS = "http://localhost:8080/api/v1/bombas";
    private final ApplicationContext context;

    private AcessoResponse usuarioLogado;
    private JTabbedPane tabbedPane;
    private JPanel painelBombas;
    private JLabel lblUsuario;
    private JMenuBar menuBar;
    private Map<String, java.awt.Component> abasAbertas = new HashMap<>();

    // ✅ Referência para a tela de estoque (para atualizar após vendas)
    private TelaEstoqueCrud telaEstoqueCrud;

    public TelaPrincipal(ApplicationContext context) {
        this.context = context;
    }

    @PostConstruct
    public void init() {
        setTitle("Sistema PDV - Posto de Combustível");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Barra superior
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // TabbedPane para múltiplas abas
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabbedPane.setTabPlacement(JTabbedPane.TOP);

        // Aba inicial: Painel de Bombas
        painelBombas = new JPanel();
        painelBombas.setLayout(new BorderLayout(10, 10));
        painelBombas.setBackground(new Color(236, 240, 241));

        JPanel painelBombasWrapper = criarPainelBombas();
        painelBombas.add(painelBombasWrapper, BorderLayout.CENTER);

        JScrollPane scrollBombas = new JScrollPane(painelBombas);
        scrollBombas.setBorder(null);
        scrollBombas.getVerticalScrollBar().setUnitIncrement(16);

        tabbedPane.addTab("🏪 Bombas", scrollBombas);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(52, 73, 94));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Título
        JLabel lblTitulo = new JLabel("🏪 PDV - Posto de Combustível");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);

        // Usuário logado
        lblUsuario = new JLabel();
        lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblUsuario.setForeground(Color.WHITE);

        topPanel.add(lblTitulo, BorderLayout.WEST);
        topPanel.add(lblUsuario, BorderLayout.EAST);

        return topPanel;
    }

    private JPanel criarPainelBombas() {
        JPanel wrapper = new JPanel(new BorderLayout(10, 10));
        wrapper.setBackground(new Color(236, 240, 241));
        wrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Título e botão de cadastro
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(236, 240, 241));

        JLabel lblTituloBombas = new JLabel("Selecione uma Bomba");
        lblTituloBombas.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTituloBombas.setForeground(new Color(52, 73, 94));

        JButton btnCadastrarBomba = new JButton("➕ Cadastrar Bomba");
        btnCadastrarBomba.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCadastrarBomba.setBackground(new Color(46, 204, 113));
        btnCadastrarBomba.setForeground(Color.WHITE);
        btnCadastrarBomba.setFocusPainted(false);
        btnCadastrarBomba.setBorderPainted(false);
        btnCadastrarBomba.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCadastrarBomba.setPreferredSize(new Dimension(180, 40));
        btnCadastrarBomba.addActionListener(e -> abrirAbaCadastroBombas());

        headerPanel.add(lblTituloBombas, BorderLayout.WEST);
        headerPanel.add(btnCadastrarBomba, BorderLayout.EAST);

        // Grid de bombas
        JPanel gridBombas = new JPanel(new GridLayout(0, 3, 20, 20));
        gridBombas.setBackground(new Color(236, 240, 241));

        wrapper.add(headerPanel, BorderLayout.NORTH);
        wrapper.add(gridBombas, BorderLayout.CENTER);

        return wrapper;
    }

    private void createMenuBar() {
        menuBar = new JMenuBar();
        menuBar.setBackground(new Color(44, 62, 80));
        menuBar.setBorderPainted(false);

        // Menu Gestão (apenas admin)
        if (usuarioLogado.tipoAcesso() == TipoAcesso.ADMINISTRADOR) {
            JMenu menuGestao = criarMenu("Gestão");
            JMenuItem itemEstoque = criarMenuItem("📦 Estoque", e -> abrirAbaEstoque());
            JMenuItem itemCusto = criarMenuItem("💰 Custo", e -> abrirAba("Custo", context.getBean(TelaCustoCrud.class)));
            menuGestao.add(itemEstoque);
            menuGestao.add(itemCusto);
            menuBar.add(menuGestao);

            // Menu Usuários
            JMenu menuUsuarios = criarMenu("Usuários");
            JMenuItem itemPessoas = criarMenuItem("👤 Pessoas", e -> abrirAba("Pessoas", context.getBean(TelaPessoaCrud.class)));
            JMenuItem itemContatos = criarMenuItem("📞 Contatos", e -> abrirAba("Contatos", context.getBean(TelaContatoCrud.class)));
            JMenuItem itemAcessos = criarMenuItem("🔐 Acessos", e -> abrirAba("Acessos", context.getBean(TelaAcessoCrud.class)));
            menuUsuarios.add(itemPessoas);
            menuUsuarios.add(itemContatos);
            menuUsuarios.add(itemAcessos);
            menuBar.add(menuUsuarios);

            // Menu Produtos e Preços
            JMenu menuProdutos = criarMenu("Produtos");
            JMenuItem itemProdutos = criarMenuItem("🛢️ Produtos", e -> abrirAba("Produtos", context.getBean(TelaProdutoCrud.class)));
            JMenuItem itemPrecos = criarMenuItem("💵 Preços", e -> abrirAba("Preços", context.getBean(TelaPrecoCrud.class)));
            menuProdutos.add(itemProdutos);
            menuProdutos.add(itemPrecos);
            menuBar.add(menuProdutos);
        }

        // Menu Sair (todos)
        menuBar.add(Box.createHorizontalGlue());
        JMenu menuSair = criarMenu("❌ Sair");
        menuSair.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuSelected(javax.swing.event.MenuEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                        TelaPrincipal.this,
                        "Deseja realmente sair?",
                        "Confirmar",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
            public void menuDeselected(javax.swing.event.MenuEvent e) {}
            public void menuCanceled(javax.swing.event.MenuEvent e) {}
        });
        menuBar.add(menuSair);

        setJMenuBar(menuBar);
    }

    private JMenu criarMenu(String texto) {
        JMenu menu = new JMenu(texto);
        menu.setForeground(Color.WHITE);
        menu.setFont(new Font("Segoe UI", Font.BOLD, 14));
        menu.setOpaque(true);
        menu.setBackground(new Color(44, 62, 80));
        return menu;
    }

    private JMenuItem criarMenuItem(String texto, java.awt.event.ActionListener action) {
        JMenuItem item = new JMenuItem(texto);
        item.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        item.setForeground(new Color(44, 62, 80));
        item.setBackground(Color.WHITE);
        item.addActionListener(action);
        return item;
    }

    // ✅ Método específico para abrir a aba de estoque e guardar referência
    private void abrirAbaEstoque() {
        // Verifica se já está aberta
        if (abasAbertas.containsKey("Estoque")) {
            java.awt.Component component = abasAbertas.get("Estoque");
            tabbedPane.setSelectedComponent(component);
            return;
        }

        // Obtém ou cria a instância da tela de estoque
        telaEstoqueCrud = context.getBean(TelaEstoqueCrud.class);
        abrirAba("Estoque", telaEstoqueCrud);
    }

    private void abrirAba(String titulo, Object telaBean) {
        // Verifica se a aba já está aberta
        if (abasAbertas.containsKey(titulo)) {
            java.awt.Component component = abasAbertas.get(titulo);
            tabbedPane.setSelectedComponent(component);
            return;
        }

        // Converte o bean para JPanel
        JPanel tela = (JPanel) telaBean;

        // Inicializa o painel se tiver método init()
        try {
            tela.getClass().getMethod("init").invoke(tela);
        } catch (Exception e) {
            // Se não tiver init(), ignora
        }

        // Cria wrapper com botão de fechar
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(tela, BorderLayout.CENTER);

        // Adiciona a aba
        tabbedPane.addTab(titulo, wrapper);
        abasAbertas.put(titulo, wrapper);

        // Cria componente customizado para o tab com botão de fechar
        int index = tabbedPane.indexOfComponent(wrapper);
        JPanel tabComponent = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        tabComponent.setOpaque(false);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JButton btnFechar = new JButton("✕");
        btnFechar.setFont(new Font("Arial", Font.PLAIN, 12));
        btnFechar.setBorderPainted(false);
        btnFechar.setContentAreaFilled(false);
        btnFechar.setFocusPainted(false);
        btnFechar.setPreferredSize(new Dimension(20, 20));
        btnFechar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnFechar.addActionListener(e -> fecharAba(titulo, wrapper));

        tabComponent.add(lblTitulo);
        tabComponent.add(btnFechar);
        tabbedPane.setTabComponentAt(index, tabComponent);

        // Seleciona a nova aba
        tabbedPane.setSelectedComponent(wrapper);
    }

    private void abrirAbaCadastroBombas() {
        try {
            TelaBombaCrud telaBomba = context.getBean(TelaBombaCrud.class);
            abrirAba("Cadastro de Bombas", telaBomba);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao abrir cadastro de bombas: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void fecharAba(String titulo, java.awt.Component component) {
        int index = tabbedPane.indexOfComponent(component);
        if (index > 0) { // Não fecha a aba de Bombas (index 0)
            tabbedPane.remove(index);
            abasAbertas.remove(titulo);

            // ✅ Se fechar a aba de estoque, limpa a referência
            if (titulo.equals("Estoque")) {
                telaEstoqueCrud = null;
            }
        }
    }

    public void setUsuarioLogado(AcessoResponse usuario) {
        this.usuarioLogado = usuario;
        lblUsuario.setText("👤 " + usuario.usuario() + " (" + usuario.tipoAcesso() + ")");
        createMenuBar();
        carregarBombas();
    }

    private void carregarBombas() {
        SwingWorker<List<BombaResponse>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<BombaResponse> doInBackground() throws Exception {
                try {
                    BombaResponse[] bombas = restTemplate.getForObject(API_BOMBAS, BombaResponse[].class);
                    return bombas != null ? List.of(bombas) : List.of();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return List.of();
                }
            }

            @Override
            protected void done() {
                try {
                    List<BombaResponse> bombas = get();

                    // Pega o grid de bombas
                    JPanel wrapper = (JPanel) painelBombas.getComponent(0);
                    JPanel gridBombas = (JPanel) wrapper.getComponent(1);
                    gridBombas.removeAll();

                    if (bombas.isEmpty()) {
                        JLabel lblSemBombas = new JLabel("Nenhuma bomba cadastrada. Clique em 'Cadastrar Bomba' para adicionar.");
                        lblSemBombas.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                        lblSemBombas.setForeground(Color.GRAY);
                        lblSemBombas.setHorizontalAlignment(SwingConstants.CENTER);
                        gridBombas.add(lblSemBombas);
                    } else {
                        for (BombaResponse bomba : bombas) {
                            gridBombas.add(criarCardBomba(bomba));
                        }
                    }

                    gridBombas.revalidate();
                    gridBombas.repaint();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(
                            TelaPrincipal.this,
                            "Erro ao carregar bombas: " + ex.getMessage(),
                            "Erro",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private JPanel criarCardBomba(BombaResponse bomba) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Cor baseada no status
        Color corFundo;
        Color corBorda;
        switch (bomba.tipoBomba()) {
            case DISPONIVEL:
                corFundo = new Color(46, 204, 113, 30);
                corBorda = new Color(46, 204, 113);
                break;
            case OCUPADA:
                corFundo = new Color(241, 196, 15, 30);
                corBorda = new Color(241, 196, 15);
                break;
            case MANUTENCAO:
                corFundo = new Color(231, 76, 60, 30);
                corBorda = new Color(231, 76, 60);
                break;
            default:
                corFundo = Color.WHITE;
                corBorda = new Color(189, 195, 199);
        }

        card.setBackground(corFundo);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(corBorda, 3),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Número da bomba
        JLabel lblNumero = new JLabel("BOMBA " + bomba.numero(), SwingConstants.CENTER);
        lblNumero.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblNumero.setForeground(new Color(52, 73, 94));

        // Status
        JLabel lblStatus = new JLabel(bomba.tipoBomba().toString(), SwingConstants.CENTER);
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblStatus.setForeground(new Color(127, 140, 141));

        // Botão
        JButton btnSelecionar = new JButton("SELECIONAR");
        btnSelecionar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSelecionar.setPreferredSize(new Dimension(0, 45));
        btnSelecionar.setFocusPainted(false);
        btnSelecionar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (bomba.tipoBomba().toString().equals("DISPONIVEL")) {
            btnSelecionar.setBackground(new Color(52, 152, 219));
            btnSelecionar.setForeground(Color.WHITE);
            btnSelecionar.setBorderPainted(false);
            btnSelecionar.addActionListener(e -> abrirTelaVenda(bomba));
        } else {
            btnSelecionar.setEnabled(false);
            btnSelecionar.setBackground(new Color(189, 195, 199));
        }

        card.add(lblNumero, BorderLayout.NORTH);
        card.add(lblStatus, BorderLayout.CENTER);
        card.add(btnSelecionar, BorderLayout.SOUTH);

        return card;
    }

    private void abrirTelaVenda(BombaResponse bomba) {
        TelaVenda telaVenda = new TelaVenda(bomba, usuarioLogado, this);

        // ✅ CONEXÃO DO LISTENER: Se a tela de estoque estiver aberta, atualiza após venda
        if (telaEstoqueCrud != null) {
            telaVenda.setEstoqueUpdateListener(() -> {
                System.out.println("🔄 Atualizando estoque após venda...");
                telaEstoqueCrud.recarregarEstoques();
            });
        }

        telaVenda.setVisible(true);
        setVisible(false);
    }

    public void voltarParaTelaPrincipal() {
        carregarBombas();
        setVisible(true);
    }
}