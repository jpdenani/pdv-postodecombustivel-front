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
import java.util.Date;
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
    private JComboBox<TipoEstoque> cbTipoEstoque;
    private JButton btnSalvar;
    private JButton btnExcluir;
    private JButton btnNovo;

    @PostConstruct
    public void init() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ====== Tabela ======
        String[] colunas = {"ID", "Quantidade", "Tanque", "Endereço", "Lote", "Data Validade", "Tipo"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Long.class;
                return String.class;
            }
        };

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);

        // Esconde a coluna ID
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

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
        cbTipoEstoque = new JComboBox<>(TipoEstoque.values());

        try {
            MaskFormatter dateMask = new MaskFormatter("##/##/####");
            dateMask.setPlaceholderCharacter('_');
            txtDataValidade = new JFormattedTextField(dateMask);
        } catch (ParseException e) {
            txtDataValidade = new JFormattedTextField();
        }

        // Linha 0: Quantidade
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Quantidade:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtQuantidade, gbc);

        // Linha 1: Tanque
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Local Tanque:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtTanque, gbc);

        // Linha 2: Endereço
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Local Endereço:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtEndereco, gbc);

        // Linha 3: Lote
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Lote Fabricação:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtLote, gbc);

        // Linha 4: Data Validade
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Data Validade:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtDataValidade, gbc);

        // Linha 5: Tipo Estoque
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Tipo de Estoque:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cbTipoEstoque, gbc);

        // Linha 6: Botões
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

        gbc.gridx = 0; gbc.gridy = 6;
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

        carregarEstoques();
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
                    ex.printStackTrace();
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
                                est.quantidade().toString(),
                                est.localTanque(),
                                est.localEndereco(),
                                est.loteFabricacao(),
                                dateFormat.format(est.dataValidade()),
                                est.tipoEstoque()
                        });
                    }
                } catch (Exception ex) {
                    System.err.println("Erro ao processar estoques: " + ex.getMessage());
                    ex.printStackTrace();
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
            txtQuantidade.setText(String.valueOf(tableModel.getValueAt(linhaSelecionada, 1)));
            txtTanque.setText(String.valueOf(tableModel.getValueAt(linhaSelecionada, 2)));
            txtEndereco.setText(String.valueOf(tableModel.getValueAt(linhaSelecionada, 3)));
            txtLote.setText(String.valueOf(tableModel.getValueAt(linhaSelecionada, 4)));
            txtDataValidade.setText(String.valueOf(tableModel.getValueAt(linhaSelecionada, 5)));

            Object tipoObj = tableModel.getValueAt(linhaSelecionada, 6);
            if (tipoObj instanceof TipoEstoque) {
                cbTipoEstoque.setSelectedItem(tipoObj);
            } else {
                try {
                    cbTipoEstoque.setSelectedItem(TipoEstoque.valueOf(String.valueOf(tipoObj)));
                } catch (Exception e) {
                    System.err.println("Erro ao converter TipoEstoque: " + e.getMessage());
                }
            }
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
            Date dataValidade = dateFormat.parse(dataStr);
            TipoEstoque tipo = (TipoEstoque) cbTipoEstoque.getSelectedItem();

            EstoqueRequest req = new EstoqueRequest(quantidade, tanque, endereco, lote, dataValidade, tipo);
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
                        ex.printStackTrace();
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
                    "Quantidade inválida! Use ponto para decimais (ex: 100.50)",
                    "Erro de Formato",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Data inválida! Use o formato dd/MM/yyyy",
                    "Erro de Data",
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
                try {
                    restTemplate.delete(API_URL + "/" + id);
                } catch (Exception ex) {
                    System.err.println("Erro ao excluir: " + ex.getMessage());
                    ex.printStackTrace();
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
        cbTipoEstoque.setSelectedIndex(0);
        txtQuantidade.requestFocus();
    }
}