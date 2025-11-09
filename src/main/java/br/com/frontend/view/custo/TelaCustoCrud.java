package br.com.frontend.view.custo;

import br.com.frontend.dto.CustoRequest;
import br.com.frontend.dto.CustoResponse;
import br.com.frontend.dto.ProdutoResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class TelaCustoCrud extends JPanel {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String API_URL = "http://localhost:8080/api/v1/custos";
    private final String API_PRODUTOS = "http://localhost:8080/api/v1/produtos";
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    private JTable table;
    private DefaultTableModel tableModel;


    private JComboBox<ProdutoResponse> cbProduto;

    private JTextField txtImposto;
    private JTextField txtCustoVariavel;
    private JTextField txtCustoFixo;
    private JTextField txtMargemLucro;
    private JFormattedTextField txtDataProcessamento;
    private JButton btnSalvar;
    private JButton btnExcluir;
    private JButton btnNovo;

    @PostConstruct
    public void init() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        String[] colunas = {"ID", "Produto", "Imposto", "Custo Variável", "Custo Fixo", "Margem Lucro", "Data Processamento"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);


        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Lista de Custos"));
        add(scrollPane, BorderLayout.CENTER);


        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Dados do Custo"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;


        cbProduto = new JComboBox<>();
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

        txtImposto = new JTextField(20);
        txtCustoVariavel = new JTextField(20);
        txtCustoFixo = new JTextField(20);
        txtMargemLucro = new JTextField(20);

        try {
            MaskFormatter dateMask = new MaskFormatter("##/##/####");
            dateMask.setPlaceholderCharacter('_');
            txtDataProcessamento = new JFormattedTextField(dateMask);
        } catch (ParseException e) {
            txtDataProcessamento = new JFormattedTextField();
        }

        int row = 0;


        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Produto:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cbProduto, gbc);
        row++;


        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Imposto (%):"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtImposto, gbc);
        row++;


        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Custo Variável (R$):"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtCustoVariavel, gbc);
        row++;


        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Custo Fixo (R$):"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtCustoFixo, gbc);
        row++;


        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Margem Lucro (%):"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtMargemLucro, gbc);
        row++;


        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Data Processamento:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtDataProcessamento, gbc);
        row++;


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnNovo = new JButton("Novo");
        btnSalvar = new JButton("Salvar");
        btnExcluir = new JButton("Excluir");

        buttonPanel.add(btnNovo);
        buttonPanel.add(btnSalvar);
        buttonPanel.add(btnExcluir);

        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.SOUTH);


        btnNovo.addActionListener(e -> {
            limparFormulario();

            txtDataProcessamento.setValue(dateFormat.format(new java.util.Date()));
        });

        btnSalvar.addActionListener(e -> salvarOuAtualizar());
        btnExcluir.addActionListener(e -> excluirSelecionado());
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                preencherFormulario();
            }
        });


        carregarProdutos();
        carregarCustos();
    }


    private void carregarProdutos() {
        SwingWorker<List<ProdutoResponse>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<ProdutoResponse> doInBackground() throws Exception {
                ProdutoResponse[] resp = restTemplate.getForObject(API_PRODUTOS, ProdutoResponse[].class);
                return resp != null ? Arrays.asList(resp) : List.of();
            }

            @Override
            protected void done() {
                try {
                    List<ProdutoResponse> produtos = get();
                    cbProduto.removeAllItems();
                    for (ProdutoResponse p : produtos) {
                        cbProduto.addItem(p);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            TelaCustoCrud.this,
                            "Erro ao carregar produtos: " + ex.getMessage(),
                            "Erro",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private void carregarCustos() {
        SwingWorker<List<CustoResponse>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<CustoResponse> doInBackground() throws Exception {
                try {
                    CustoResponse[] resp = restTemplate.getForObject(API_URL, CustoResponse[].class);
                    return resp != null ? Arrays.asList(resp) : List.of();
                } catch (Exception ex) {
                    System.err.println("Erro ao carregar custos: " + ex.getMessage());
                    ex.printStackTrace();
                    return List.of();
                }
            }

            @Override
            protected void done() {
                try {
                    List<CustoResponse> custos = get();
                    tableModel.setRowCount(0);
                    for (CustoResponse c : custos) {
                        tableModel.addRow(new Object[]{
                                c.id(),
                                c.produtoNome(),  // ✅ NOVO: Nome do produto
                                String.format("%.2f%%", c.imposto()),
                                String.format("R$ %.2f", c.custoVariavel()),
                                String.format("R$ %.2f", c.custoFixo()),
                                String.format("%.2f%%", c.margemLucro()),
                                c.dataProcessamento() != null ? dateFormat.format(c.dataProcessamento()) : ""
                        });
                    }
                    System.out.println("✅ Custos carregados: " + custos.size());
                } catch (Exception ex) {
                    System.err.println("Erro ao processar custos: " + ex.getMessage());
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(
                            TelaCustoCrud.this,
                            "Erro ao carregar custos: " + ex.getMessage(),
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

            String nomeProduto = String.valueOf(tableModel.getValueAt(linhaSelecionada, 1));
            for (int i = 0; i < cbProduto.getItemCount(); i++) {
                ProdutoResponse p = cbProduto.getItemAt(i);
                if (p.nome().equals(nomeProduto)) {
                    cbProduto.setSelectedIndex(i);
                    break;
                }
            }


            String impostoStr = String.valueOf(tableModel.getValueAt(linhaSelecionada, 2)).replace("%", "").trim();
            String custoVarStr = String.valueOf(tableModel.getValueAt(linhaSelecionada, 3)).replace("R$", "").trim();
            String custoFixoStr = String.valueOf(tableModel.getValueAt(linhaSelecionada, 4)).replace("R$", "").trim();
            String margemStr = String.valueOf(tableModel.getValueAt(linhaSelecionada, 5)).replace("%", "").trim();

            txtImposto.setText(impostoStr);
            txtCustoVariavel.setText(custoVarStr);
            txtCustoFixo.setText(custoFixoStr);
            txtMargemLucro.setText(margemStr);
            txtDataProcessamento.setText(String.valueOf(tableModel.getValueAt(linhaSelecionada, 6)));
        }
    }

    private void salvarOuAtualizar() {
        try {

            ProdutoResponse produto = (ProdutoResponse) cbProduto.getSelectedItem();
            if (produto == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Selecione um produto!",
                        "Validação",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            String impostoStr = txtImposto.getText().trim();
            String custoVarStr = txtCustoVariavel.getText().trim();
            String custoFixoStr = txtCustoFixo.getText().trim();
            String margemStr = txtMargemLucro.getText().trim();
            String dataStr = txtDataProcessamento.getText().trim();

            if (impostoStr.isEmpty() || custoVarStr.isEmpty() || custoFixoStr.isEmpty() ||
                    margemStr.isEmpty() || dataStr.isEmpty() || dataStr.contains("_")) {
                JOptionPane.showMessageDialog(
                        this,
                        "Por favor, preencha todos os campos!",
                        "Validação",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            Double imposto = Double.parseDouble(impostoStr);
            Double custoVariavel = Double.parseDouble(custoVarStr);
            Double custoFixo = Double.parseDouble(custoFixoStr);
            Double margemLucro = Double.parseDouble(margemStr);
            Date dataProcessamento = dateFormat.parse(dataStr);


            CustoRequest req = new CustoRequest(
                    produto.id(),
                    imposto,
                    custoVariavel,
                    custoFixo,
                    margemLucro,
                    dataProcessamento
            );

            int linhaSelecionada = table.getSelectedRow();

            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    try {
                        if (linhaSelecionada >= 0) {
                            // Atualizar
                            Object idObj = tableModel.getValueAt(linhaSelecionada, 0);
                            long id = idObj instanceof Number ? ((Number) idObj).longValue() : Long.parseLong(String.valueOf(idObj));
                            restTemplate.put(API_URL + "/" + id, req);
                        } else {
                            // Novo
                            restTemplate.postForEntity(API_URL, req, CustoResponse.class);
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
                                TelaCustoCrud.this,
                                "Custo salvo com sucesso!",
                                "Sucesso",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                        carregarCustos();
                        limparFormulario();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(
                                TelaCustoCrud.this,
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
                    "Valores numéricos inválidos! Use ponto para decimais (ex: 10.5)",
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
                    "Selecione um custo para excluir!",
                    "Validação",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Deseja realmente excluir este custo?",
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
                            TelaCustoCrud.this,
                            "Custo excluído com sucesso!",
                            "Sucesso",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    carregarCustos();
                    limparFormulario();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            TelaCustoCrud.this,
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
        cbProduto.setSelectedIndex(-1);  // ✅ Limpa combo
        txtImposto.setText("");
        txtCustoVariavel.setText("");
        txtCustoFixo.setText("");
        txtMargemLucro.setText("");
        txtDataProcessamento.setText("");
        cbProduto.requestFocus();
    }
}