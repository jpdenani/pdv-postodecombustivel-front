package br.com.frontend.view.main;

import br.com.frontend.dto.AcessoResponse;
import br.com.frontend.dto.BombaResponse;
import br.com.frontend.enums.TipoAcesso;
import br.com.frontend.view.venda.TelaVenda;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.swing.*;
import java.awt.*;
import java.util.List;

@Component
public class TelaPrincipal extends JFrame {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String API_BOMBAS = "http://localhost:8080/api/v1/bombas";

    private AcessoResponse usuarioLogado;
    private JPanel painelBombas;
    private JLabel lblUsuario;
    private JMenuBar menuBar;

    @PostConstruct
    public void init() {
        setTitle("Sistema PDV - Posto de Combustível");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Layout principal
        setLayout(new BorderLayout());

        // Barra superior
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // Painel central com as bombas
        painelBombas = new JPanel();
        painelBombas.setLayout(new GridLayout(0, 3, 20, 20));
        painelBombas.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        painelBombas.setBackground(new Color(236, 240, 241));

        JScrollPane scrollPane = new JScrollPane(painelBombas);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
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

    private void createMenuBar() {
        menuBar = new JMenuBar();
        menuBar.setBackground(new Color(44, 62, 80));

        // Menu Gestão (apenas admin)
        if (usuarioLogado.tipoAcesso() == TipoAcesso.ADMINISTRADOR) {
            JMenu menuGestao = new JMenu("Gestão");
            menuGestao.setForeground(Color.WHITE);
            menuGestao.setFont(new Font("Segoe UI", Font.BOLD, 13));

            JMenuItem itemEstoque = new JMenuItem("Estoque");
            JMenuItem itemCusto = new JMenuItem("Custo");

            menuGestao.add(itemEstoque);
            menuGestao.add(itemCusto);
            menuBar.add(menuGestao);

            // Menu Usuários (apenas admin)
            JMenu menuUsuarios = new JMenu("Usuários");
            menuUsuarios.setForeground(Color.WHITE);
            menuUsuarios.setFont(new Font("Segoe UI", Font.BOLD, 13));

            JMenuItem itemPessoas = new JMenuItem("Pessoas");
            JMenuItem itemContatos = new JMenuItem("Contatos");
            JMenuItem itemAcessos = new JMenuItem("Acessos");

            menuUsuarios.add(itemPessoas);
            menuUsuarios.add(itemContatos);
            menuUsuarios.add(itemAcessos);
            menuBar.add(menuUsuarios);

            // Menu Produtos e Preços (apenas admin)
            JMenu menuProdutos = new JMenu("Produtos e Preços");
            menuProdutos.setForeground(Color.WHITE);
            menuProdutos.setFont(new Font("Segoe UI", Font.BOLD, 13));

            JMenuItem itemProdutos = new JMenuItem("Cadastro de Produtos");
            JMenuItem itemPrecos = new JMenuItem("Cadastro de Preços");

            menuProdutos.add(itemProdutos);
            menuProdutos.add(itemPrecos);
            menuBar.add(menuProdutos);
        }

        // Menu Sair (todos)
        JMenu menuSair = new JMenu("Sair");
        menuSair.setForeground(Color.WHITE);
        menuSair.setFont(new Font("Segoe UI", Font.BOLD, 13));
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
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(menuSair);

        setJMenuBar(menuBar);
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
                    painelBombas.removeAll();

                    if (bombas.isEmpty()) {
                        JLabel lblSemBombas = new JLabel("Nenhuma bomba cadastrada");
                        lblSemBombas.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                        lblSemBombas.setForeground(Color.GRAY);
                        lblSemBombas.setHorizontalAlignment(SwingConstants.CENTER);
                        painelBombas.add(lblSemBombas);
                    } else {
                        for (BombaResponse bomba : bombas) {
                            painelBombas.add(criarPainelBomba(bomba));
                        }
                    }

                    painelBombas.revalidate();
                    painelBombas.repaint();
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

    private JPanel criarPainelBomba(BombaResponse bomba) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Cor de fundo baseada no status
        Color corFundo;
        switch (bomba.tipoBomba()) {
            case DISPONIVEL:
                corFundo = new Color(46, 204, 113, 30);
                panel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(46, 204, 113), 3),
                        BorderFactory.createEmptyBorder(20, 20, 20, 20)
                ));
                break;
            case OCUPADA:
                corFundo = new Color(241, 196, 15, 30);
                panel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(241, 196, 15), 3),
                        BorderFactory.createEmptyBorder(20, 20, 20, 20)
                ));
                break;
            case MANUTENCAO:
                corFundo = new Color(231, 76, 60, 30);
                panel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(231, 76, 60), 3),
                        BorderFactory.createEmptyBorder(20, 20, 20, 20)
                ));
                break;
            default:
                corFundo = Color.WHITE;
        }
        panel.setBackground(corFundo);

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

        panel.add(lblNumero, BorderLayout.NORTH);
        panel.add(lblStatus, BorderLayout.CENTER);
        panel.add(btnSelecionar, BorderLayout.SOUTH);

        return panel;
    }

    private void abrirTelaVenda(BombaResponse bomba) {
        TelaVenda telaVenda = new TelaVenda(bomba, usuarioLogado, this);
        telaVenda.setVisible(true);
        setVisible(false);
    }

    public void voltarParaTelaPrincipal() {
        carregarBombas();
        setVisible(true);
    }
}