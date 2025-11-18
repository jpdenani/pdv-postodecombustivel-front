package br.com.frontend.view.venda;

import br.com.frontend.dto.*;
import br.com.frontend.listener.EstoqueUpdateListener;
import br.com.frontend.view.main.TelaPrincipal;
import org.springframework.web.client.RestTemplate;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class TelaVenda extends JFrame {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String API_PRODUTOS = "http://localhost:8080/api/v1/produtos";
    private final String API_PRECOS = "http://localhost:8080/api/v1/precos";
    private final String API_VENDAS = "http://localhost:8080/api/v1/vendas";

    private final BombaResponse bomba;
    private final AcessoResponse usuario;
    private final TelaPrincipal telaPrincipal;

    private EstoqueUpdateListener estoqueUpdateListener;

    private JComboBox<ProdutoResponse> cbProduto;
    private JTextField txtLitros;
    private JLabel lblValorUnitario;
    private JLabel lblValorTotal;
    private JComboBox<String> cbFormaPagamento;
    private JButton btnConfirmar;
    private JButton btnCancelar;

    private PrecoResponse precoAtual;

    public TelaVenda(BombaResponse bomba, AcessoResponse usuario, TelaPrincipal telaPrincipal) {
        this.bomba = bomba;
        this.usuario = usuario;
        this.telaPrincipal = telaPrincipal;
        initComponents();
        carregarProdutos();
    }


    public void setEstoqueUpdateListener(EstoqueUpdateListener listener) {
        this.estoqueUpdateListener = listener;
    }

    private void initComponents() {
        setTitle("Venda - Bomba " + bomba.numero());
        setSize(600, 550);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);


        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(52, 152, 219));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblBomba = new JLabel("BOMBA " + bomba.numero());
        lblBomba.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblBomba.setForeground(Color.WHITE);

        JLabel lblUsuario = new JLabel("Vendedor: " + usuario.usuario());
        lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblUsuario.setForeground(Color.WHITE);

        topPanel.add(lblBomba, BorderLayout.WEST);
        topPanel.add(lblUsuario, BorderLayout.EAST);


        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Produto
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel lblProduto = new JLabel("Produto:");
        lblProduto.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(lblProduto, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        cbProduto = new JComboBox<>();
        cbProduto.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbProduto.setPreferredSize(new Dimension(300, 35));

        cbProduto.setRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(JList<?> list, Object value,
                                                                   int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof ProdutoResponse) {
                    setText(((ProdutoResponse) value).nome());
                }
                return this;
            }
        });

        cbProduto.addActionListener(e -> atualizarPreco());
        formPanel.add(cbProduto, gbc);

        // Litros
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        JLabel lblLitros = new JLabel("Litros:");
        lblLitros.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(lblLitros, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtLitros = new JTextField();
        txtLitros.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtLitros.setPreferredSize(new Dimension(300, 35));
        txtLitros.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                calcularTotal();
            }
        });
        formPanel.add(txtLitros, gbc);

        // Valor Unitário
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel lblValorUnitTxt = new JLabel("Valor Unitário:");
        lblValorUnitTxt.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(lblValorUnitTxt, gbc);

        gbc.gridx = 1;
        lblValorUnitario = new JLabel("R$ 0,00");
        lblValorUnitario.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblValorUnitario.setForeground(new Color(52, 73, 94));
        formPanel.add(lblValorUnitario, gbc);

        // Valor Total
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel lblValorTotalTxt = new JLabel("VALOR TOTAL:");
        lblValorTotalTxt.setFont(new Font("Segoe UI", Font.BOLD, 18));
        formPanel.add(lblValorTotalTxt, gbc);

        gbc.gridx = 1;
        lblValorTotal = new JLabel("R$ 0,00");
        lblValorTotal.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblValorTotal.setForeground(new Color(46, 204, 113));
        formPanel.add(lblValorTotal, gbc);

        // Forma de Pagamento
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel lblForma = new JLabel("Forma de Pagamento:");
        lblForma.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(lblForma, gbc);

        gbc.gridx = 1;
        cbFormaPagamento = new JComboBox<>(new String[]{
                "Dinheiro", "Cartão de Crédito", "Cartão de Débito", "Pix"
        });
        cbFormaPagamento.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbFormaPagamento.setPreferredSize(new Dimension(300, 35));
        formPanel.add(cbFormaPagamento, gbc);

        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        btnCancelar = new JButton("CANCELAR");
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCancelar.setPreferredSize(new Dimension(140, 45));
        btnCancelar.setBackground(new Color(231, 76, 60));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setBorderPainted(false);
        btnCancelar.addActionListener(e -> cancelar());

        btnConfirmar = new JButton("CONFIRMAR VENDA");
        btnConfirmar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnConfirmar.setPreferredSize(new Dimension(180, 45));
        btnConfirmar.setBackground(new Color(46, 204, 113));
        btnConfirmar.setForeground(Color.WHITE);
        btnConfirmar.setFocusPainted(false);
        btnConfirmar.setBorderPainted(false);
        btnConfirmar.addActionListener(e -> confirmarVenda());

        buttonPanel.add(btnCancelar);
        buttonPanel.add(btnConfirmar);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void carregarProdutos() {
        SwingWorker<List<ProdutoResponse>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<ProdutoResponse> doInBackground() throws Exception {
                ProdutoResponse[] produtos = restTemplate.getForObject(API_PRODUTOS, ProdutoResponse[].class);
                return produtos != null ? List.of(produtos) : List.of();
            }

            @Override
            protected void done() {
                try {
                    List<ProdutoResponse> produtos = get();
                    cbProduto.removeAllItems();
                    for (ProdutoResponse p : produtos) cbProduto.addItem(p);
                    if (!produtos.isEmpty()) atualizarPreco();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(TelaVenda.this, "Erro ao carregar produtos");
                }
            }
        };
        worker.execute();
    }

    private void atualizarPreco() {
        ProdutoResponse produtoSelecionado = (ProdutoResponse) cbProduto.getSelectedItem();
        if (produtoSelecionado == null) return;

        SwingWorker<PrecoResponse, Void> worker = new SwingWorker<>() {
            @Override
            protected PrecoResponse doInBackground() throws Exception {
                try {
                    return restTemplate.getForObject(API_PRECOS + "/" + produtoSelecionado.id(), PrecoResponse.class);
                } catch (Exception e) {

                    PrecoResponse[] precos = restTemplate.getForObject(API_PRECOS, PrecoResponse[].class);
                    return (precos != null && precos.length > 0) ? precos[precos.length - 1] : null;
                }
            }

            @Override
            protected void done() {
                try {
                    precoAtual = get();
                    if (precoAtual != null) {
                        lblValorUnitario.setText(String.format("R$ %.2f", precoAtual.valor()));
                        calcularTotal();
                    } else {
                        lblValorUnitario.setText("Sem preço cadastrado");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void calcularTotal() {
        try {
            if (precoAtual == null) return;
            String litrosStr = txtLitros.getText().trim().replace(",", ".");
            if (litrosStr.isEmpty()) {
                lblValorTotal.setText("R$ 0,00");
                return;
            }
            BigDecimal litros = new BigDecimal(litrosStr);
            BigDecimal total = precoAtual.valor().multiply(litros);
            lblValorTotal.setText(String.format("R$ %.2f", total));
        } catch (Exception ex) {
            lblValorTotal.setText("R$ 0,00");
        }
    }

    private void confirmarVenda() {
        try {
            ProdutoResponse produto = (ProdutoResponse) cbProduto.getSelectedItem();
            String litrosStr = txtLitros.getText().trim().replace(",", ".");
            if (produto == null || litrosStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos!");
                return;
            }

            BigDecimal litros = new BigDecimal(litrosStr);
            if (litros.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(this, "Quantidade inválida!");
                return;
            }

            VendaRequest req = new VendaRequest(bomba.id(), produto.id(), litros, usuario.usuario());
            btnConfirmar.setEnabled(false);
            btnConfirmar.setText("Processando...");

            SwingWorker<VendaResponse, Void> worker = new SwingWorker<>() {
                @Override
                protected VendaResponse doInBackground() throws Exception {
                    return restTemplate.postForObject(API_VENDAS, req, VendaResponse.class);
                }

                @Override
                protected void done() {
                    try {
                        VendaResponse venda = get();

                        if (estoqueUpdateListener != null) {
                            estoqueUpdateListener.onEstoqueAtualizado();
                        }

                        JOptionPane.showMessageDialog(
                                TelaVenda.this,
                                String.format("Venda realizada com sucesso!\nTotal: R$ %.2f", venda.valorTotal()),
                                "Sucesso",
                                JOptionPane.INFORMATION_MESSAGE);
                        if (estoqueUpdateListener != null) estoqueUpdateListener.onEstoqueAtualizado();

                        String API_CUSTOS = "http://localhost:8080/api/v1/custos/" + produto.id();
                        Double impostoPercentual = 0.0;
                        try {
                            var custo = restTemplate.getForObject(API_CUSTOS, br.com.frontend.dto.CustoResponse.class);
                            if (custo != null && custo.imposto() != null) {
                                impostoPercentual = custo.imposto();
                            }
                        } catch (Exception e) {
                            System.err.println("Erro ao buscar imposto do custo: " + e.getMessage());
                        }
                        String formaPagamento = (String) cbFormaPagamento.getSelectedItem();

                        TelaComprovante comprovante = new TelaComprovante(
                                produto.nome(),
                                precoAtual.valor().doubleValue(),
                                litros.doubleValue(),
                                precoAtual.valor().multiply(litros).doubleValue(),
                                impostoPercentual.doubleValue(),
                                formaPagamento
                        );

                        comprovante.setVisible(true);


                        voltar();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        String errorMsg = ex.getMessage();
                        if (errorMsg != null && errorMsg.contains("Estoque insuficiente")) {
                            JOptionPane.showMessageDialog(
                                    TelaVenda.this,
                                    errorMsg,
                                    "Estoque Insuficiente",
                                    JOptionPane.WARNING_MESSAGE
                            );
                        } else {
                            JOptionPane.showMessageDialog(
                                    TelaVenda.this,
                                    "Erro ao realizar venda: " + errorMsg,
                                    "Erro",
                                    JOptionPane.ERROR_MESSAGE
                            );
                        }

                        btnConfirmar.setEnabled(true);
                        btnConfirmar.setText("CONFIRMAR VENDA");
                    }
                }
            };
            worker.execute();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Dados inválidos!");
        }
    }


    private void cancelar() {
        int confirm = JOptionPane.showConfirmDialog(this, "Deseja cancelar esta venda?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) voltar();
    }

    private void voltar() {
        telaPrincipal.voltarParaTelaPrincipal();
        dispose();
    }
}
