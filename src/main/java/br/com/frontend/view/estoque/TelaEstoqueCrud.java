package br.com.frontend.view.estoque;

import br.com.frontend.dto.EstoqueRequest;
import br.com.frontend.dto.EstoqueResponse;
import br.com.frontend.enums.TipoEstoque;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@Component
public class TelaEstoqueCrud extends JPanel {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String API_URL = "http://localhost:8080/api/v1/estoques";
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtQuantidade;
    private JTextField txtTanque;
    private JTextField txtEndereco;
    private JTextField txtLote;
    private JFormattedTextField txtDataValidade;
    private JLabel lblCapacidadeMaxima;  // ✅ Exibe capacidade máxima
    private JLabel lblPercentual;        // ✅ Exibe percentual
    private JLabel lblTipoEstoque;       // ✅ Exibe tipo calculado
    private JButton btnSalvar;
    private JButton btnExcluir;
    private JButton btnNovo;

    @PostConstruct
    public void init() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ====== Tabela ======
        String[] colunas = {"ID", "Quantidade", "Capacidade", "%", "Tipo", "Tanque", "Endereço", "Lote", "Validade"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);

        // Esconde a coluna ID
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Lista de Estoques"));
        add(scrollPane, BorderLayout.CENTER);

        // ====== Formulário ======
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Dados do Estoque"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtQuantidade = new JTextField(20);
        txtTanque = new JTextField(20);
        txtEndereco = new JTextField(20);
        txtLote = new JTextField(20);
        lblCapacidadeMaxima = new JLabel("150.000 litros");
        lblPercentual = new JLabel("0%");
        lblTipoEstoque = new JLabel("---");

        try {
            MaskFormatter dateMask = new MaskFormatter("##/##/####");
            dateMask.setPlaceholderCharacter('_');
            txtDataValidade = new JFormattedTextField(dateMask);
        } catch (ParseException e) {
            txtDataValidade = new JFormattedTextField();
        }

        int row = 0;

        // Quantidade
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Quantidade (litros):"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtQuantidade, gbc);
        row++;

        // ✅ Capacidade Máxima (somente leitura)
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Capacidade Máxima:"), gbc);
        gbc.gridx = 1;
        lblCapacidadeMaxima.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(lblCapacidadeMaxima, gbc);
        row++;

        // ✅ Percentual (calculado)
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Percentual:"), gbc);
        gbc.gridx = 1;
        lblPercentual.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(lblPercentual, gbc);
        row++;

        // ✅ Tipo Estoque (calculado)
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Tipo Estoque:"), gbc);
        gbc.gridx = 1;
        lblTipoEstoque.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(lblTipoEstoque, gbc);
        row++;

        // Tanque
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Local Tanque:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtTanque, gbc);
        row++;

        // Endereço
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Local Endereço:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtEndereco, gbc);
        row++;

        // Lote
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Lote Fabricação:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtLote, gbc);
        row++;

        // Data Validade
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Data Validade:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtDataValidade, gbc);
        row++;

        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnNovo = new JButton("Novo");
        btnSalvar = new JButton("Salvar");
        btnExcluir = new JButton("Excluir");

        btnNovo.setBackground(new Color(76, 175, 80));
        btnNovo.setForeground(Color.WHITE);
        btnSalvar.setBackground(new Color(33, 150, 243));
        btnSalvar.setForeground(Color.WHITE);
        btnExcluir.setBackground(new Color(244, 67, 54));
        btnExcluir.setForeground(Color.WHITE);

        buttonPanel.add(btnNovo);
        buttonPanel.add(btnSalvar);
        buttonPanel.add(btnExcluir);

        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.SOUTH);

        // ====== Ações ======
        btnNovo.addActionListener(e -> limparFormulario());
        btnSalvar.addActionListener(e -> salvarOuAtualizar());
        btnExcluir.addActionListener(e -> excluirSelecionado());
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                preencherFormulario();
            }
        });

        // ✅ Atualiza tipo ao digitar quantidade
        txtQuantidade.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                atualizarInfosCalculadas();
            }
        });

        carregarEstoques();
    }

    // ✅ Atualiza percentual e tipo baseado na quantidade
    private void atualizarInfosCalculadas() {
        try {
            String qtdStr = txtQuantidade.getText().trim();
            if (!qtdStr.isEmpty()) {
                BigDecimal quantidade = new BigDecimal(qtdStr);
                BigDecimal capacidade = new BigDecimal("150000");
                BigDecimal percentual = quantidade.multiply(new BigDecimal("100"))
                        .divide(capacidade, 2, java.math.RoundingMode.HALF_UP);

                lblPercentual.setText(percentual + "%");

                // Calcula tipo
                TipoEstoque tipo;
                if (percentual.compareTo(new BigDecimal("20")) < 0) {
                    tipo = TipoEstoque.CRITICO;
                    lblTipoEstoque.setForeground(Color.RED);
                } else if (percentual.compareTo(new BigDecimal("45")) < 0) {
                    tipo = TipoEstoque.BAIXO;
                    lblTipoEstoque.setForeground(new Color(255, 140, 0));
                } else if (percentual.compareTo(new BigDecimal("75")) < 0) {
                    tipo = TipoEstoque.MEDIO;
                    lblTipoEstoque.setForeground(Color.BLUE);
                } else {
                    tipo = TipoEstoque.ALTO;
                    lblTipoEstoque.setForeground(new Color(0, 128, 0));
                }
                lblTipoEstoque.setText(tipo.getDescricao());
            } else {
                lblPercentual.setText("0%");
                lblTipoEstoque.setText("---");
                lblTipoEstoque.setForeground(Color.BLACK);
            }
        } catch (Exception e) {
            lblPercentual.setText("---");
            lblTipoEstoque.setText("---");
        }
    }

    private void carregarEstoques() {
        SwingWorker<List<EstoqueResponse>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<EstoqueResponse> doInBackground() throws Exception {
                try {
                    EstoqueResponse[] resp = restTemplate.getForObject(API_URL, EstoqueResponse[].class);
                    return resp != null ? List.of(resp) : List.of();
                } catch (Exception ex) {
                    System.err.println("Erro ao carregar estoques: " + ex.getMessage());
                    return List.of();
                }
            }

            @Override
            protected void done() {
                try {
                    List<EstoqueResponse> estoques = get();
                    tableModel.setRowCount(0);
                    for (EstoqueResponse est : estoques) {
                        tableModel.addRow(new Object[]{
                                est.id(),
                                est.quantidade().toString() + " L",
                                est.capacidadeMaxima().toString() + " L",
                                est.percentualEstoque() + "%",
                                est.tipoEstoque().getDescricao(),
                                est.localTanque(),
                                est.localEndereco(),
                                est.loteFabricacao(),
                                dateFormat.format(est.dataValidade())
                        });
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            TelaEstoqueCrud.this,
                            "Erro ao carregar estoques: " + ex.getMessage(),
                            "Erro",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private void preencherFormulario() {
        int linhaSelecionada = table.getSelectedRow();
        if (linhaSelecionada >= 0) {
            String qtdStr = String.valueOf(tableModel.getValueAt(linhaSelecionada, 1)).replace(" L", "");
            txtQuantidade.setText(qtdStr);
            txtTanque.setText(String.valueOf(tableModel.getValueAt(linhaSelecionada, 5)));
            txtEndereco.setText(String.valueOf(tableModel.getValueAt(linhaSelecionada, 6)));
            txtLote.setText(String.valueOf(tableModel.getValueAt(linhaSelecionada, 7)));
            txtDataValidade.setText(String.valueOf(tableModel.getValueAt(linhaSelecionada, 8)));

            atualizarInfosCalculadas();
        }
    }

    private void salvarOuAtualizar() {
        try {
            String quantidadeStr = txtQuantidade.getText().trim();
            String tanque = txtTanque.getText().trim();
            String endereco = txtEndereco.getText().trim();
            String lote = txtLote.getText().trim();
            String dataStr = txtDataValidade.getText().trim();

            if (quantidadeStr.isEmpty() || tanque.isEmpty() || endereco.isEmpty() ||
                    lote.isEmpty() || dataStr.isEmpty() || dataStr.contains("_")) {
                JOptionPane.showMessageDialog(
                        this,
                        "Por favor, preencha todos os campos!",
                        "Validação",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            BigDecimal quantidade = new BigDecimal(quantidadeStr);

            // ✅ Tenta converter a data
            java.util.Date dataValidade;
            try {
                dataValidade = dateFormat.parse(dataStr);
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(
                        this,
                        "Data inválida! Use o formato DD/MM/AAAA.",
                        "Erro de Data",
                        JOptionPane.ERROR_MESSAGE
                );
                return; // sai do método
            }

            // ✅ Cria o objeto com data válida
            EstoqueRequest req = new EstoqueRequest(
                    quantidade, tanque, endereco, lote, dataValidade, TipoEstoque.MEDIO
            );

            int linhaSelecionada = table.getSelectedRow();

            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    try {
                        if (linhaSelecionada >= 0) {
                            Object idObj = tableModel.getValueAt(linhaSelecionada, 0);
                            long id = idObj instanceof Number ? ((Number) idObj).longValue() : Long.parseLong(String.valueOf(idObj));
                            restTemplate.put(API_URL + "/" + id, req);
                        } else {
                            restTemplate.postForEntity(API_URL, req, EstoqueResponse.class);
                        }
                    } catch (Exception ex) {
                        System.err.println("Erro ao salvar: " + ex.getMessage());
                        throw ex;
                    }
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        JOptionPane.showMessageDialog(
                                TelaEstoqueCrud.this,
                                "Estoque salvo com sucesso!",
                                "Sucesso",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                        carregarEstoques();
                        limparFormulario();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(
                                TelaEstoqueCrud.this,
                                "Erro ao salvar: " + ex.getMessage(),
                                "Erro",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            };
            worker.execute();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Quantidade inválida! Use ponto para decimais (ex: 100000.00)",
                    "Erro de Formato",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void excluirSelecionado() {
        int linhaSelecionada = table.getSelectedRow();
        if (linhaSelecionada < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecione um estoque para excluir!",
                    "Validação",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Deseja realmente excluir este estoque?",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        Object idObj = tableModel.getValueAt(linhaSelecionada, 0);
        long id = idObj instanceof Number ? ((Number) idObj).longValue() : Long.parseLong(String.valueOf(idObj));

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                restTemplate.delete(API_URL + "/" + id);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(
                            TelaEstoqueCrud.this,
                            "Estoque excluído com sucesso!",
                            "Sucesso",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    carregarEstoques();
                    limparFormulario();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            TelaEstoqueCrud.this,
                            "Erro ao excluir: " + ex.getMessage(),
                            "Erro",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private void limparFormulario() {
        table.clearSelection();
        txtQuantidade.setText("");
        txtTanque.setText("");
        txtEndereco.setText("");
        txtLote.setText("");
        txtDataValidade.setText("");
        lblPercentual.setText("0%");
        lblTipoEstoque.setText("---");
        lblTipoEstoque.setForeground(Color.BLACK);
        txtQuantidade.requestFocus();
    }
}