package br.com.frontend.view.produto;

import br.com.frontend.dto.ProdutoRequest;
import br.com.frontend.dto.ProdutoResponse;
import br.com.frontend.enums.TipoProduto;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

@Component
public class TelaProdutoCrud extends JPanel {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String API_URL = "http://localhost:8080/api/v1/produtos";

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtNome;
    private JTextField txtReferencia;
    private JTextField txtCategoria;
    private JTextField txtFornecedor;
    private JTextField txtMarca;
    private JComboBox<TipoProduto> cbTipoProduto;
    private JButton btnSalvar;
    private JButton btnExcluir;
    private JButton btnNovo;

    @PostConstruct
    public void init() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] colunas = {"ID", "Nome", "Referência", "Categoria", "Fornecedor", "Marca", "Tipo"};
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

        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Lista de Produtos"));
        add(scrollPane, BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Dados do Produto"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtNome = new JTextField(20);
        txtReferencia = new JTextField(20);
        txtCategoria = new JTextField(20);
        txtFornecedor = new JTextField(20);
        txtMarca = new JTextField(20);
        cbTipoProduto = new JComboBox<>(TipoProduto.values());

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtNome, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Referência:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtReferencia, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Categoria:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtCategoria, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Fornecedor:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtFornecedor, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Marca:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtMarca, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Tipo de Produto:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cbTipoProduto, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnNovo = new JButton("Novo");
        btnSalvar = new JButton("Salvar");
        btnExcluir = new JButton("Excluir");

        buttonPanel.add(btnNovo);
        buttonPanel.add(btnSalvar);
        buttonPanel.add(btnExcluir);

        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.SOUTH);


        btnNovo.addActionListener(e -> limparFormulario());
        btnSalvar.addActionListener(e -> salvarOuAtualizar());
        btnExcluir.addActionListener(e -> excluirSelecionado());
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                preencherFormulario();
            }
        });

        carregarProdutos();
    }

    private void carregarProdutos() {
        SwingWorker<List<ProdutoResponse>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<ProdutoResponse> doInBackground() throws Exception {
                try {
                    ProdutoResponse[] resp = restTemplate.getForObject(API_URL, ProdutoResponse[].class);
                    return resp != null ? List.of(resp) : List.of();
                } catch (Exception ex) {
                    System.err.println("Erro ao carregar produtos: " + ex.getMessage());
                    ex.printStackTrace();
                    return List.of();
                }
            }

            @Override
            protected void done() {
                try {
                    List<ProdutoResponse> produtos = get();
                    tableModel.setRowCount(0);
                    for (ProdutoResponse p : produtos) {

                        tableModel.addRow(new Object[]{
                                p.id(),
                                p.nome(),
                                p.referencia(),
                                p.marca(),
                                p.fornecedor(),
                                p.categoria(),
                                p.tipoProduto()
                        });
                    }
                } catch (Exception ex) {
                    System.err.println("Erro ao processar produtos: " + ex.getMessage());
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(
                            TelaProdutoCrud.this,
                            "Erro ao carregar produtos: " + ex.getMessage(),
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
            // ✅ CORREÇÃO: Leitura correta das colunas
            txtNome.setText(String.valueOf(tableModel.getValueAt(linhaSelecionada, 1)));
            txtReferencia.setText(String.valueOf(tableModel.getValueAt(linhaSelecionada, 2)));
            txtMarca.setText(String.valueOf(tableModel.getValueAt(linhaSelecionada, 3)));        // MARCA - coluna 3
            txtFornecedor.setText(String.valueOf(tableModel.getValueAt(linhaSelecionada, 4)));
            txtCategoria.setText(String.valueOf(tableModel.getValueAt(linhaSelecionada, 5)));    // CATEGORIA - coluna 5

            Object tipoObj = tableModel.getValueAt(linhaSelecionada, 6);
            if (tipoObj instanceof TipoProduto) {
                cbTipoProduto.setSelectedItem(tipoObj);
            } else {
                try {
                    cbTipoProduto.setSelectedItem(TipoProduto.valueOf(String.valueOf(tipoObj)));
                } catch (Exception e) {
                    System.err.println("Erro ao converter TipoProduto: " + e.getMessage());
                }
            }
        }
    }

    private void salvarOuAtualizar() {
        String nome = txtNome.getText().trim();
        String referencia = txtReferencia.getText().trim();
        String categoria = txtCategoria.getText().trim();
        String fornecedor = txtFornecedor.getText().trim();
        String marca = txtMarca.getText().trim();

        if (nome.isEmpty() || referencia.isEmpty() || categoria.isEmpty() ||
                fornecedor.isEmpty() || marca.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Por favor, preencha todos os campos!",
                    "Validação",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        TipoProduto tipo = (TipoProduto) cbTipoProduto.getSelectedItem();
        ProdutoRequest req = new ProdutoRequest(nome, referencia, categoria, fornecedor, marca, tipo);
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
                        restTemplate.postForEntity(API_URL, req, ProdutoResponse.class);
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
                            TelaProdutoCrud.this,
                            "Produto salvo com sucesso!",
                            "Sucesso",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    carregarProdutos();
                    limparFormulario();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            TelaProdutoCrud.this,
                            "Erro ao salvar: " + ex.getMessage(),
                            "Erro",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private void excluirSelecionado() {
        int linhaSelecionada = table.getSelectedRow();
        if (linhaSelecionada < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecione um produto para excluir!",
                    "Validação",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Deseja realmente excluir este produto?",
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
                            TelaProdutoCrud.this,
                            "Produto excluído com sucesso!",
                            "Sucesso",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    carregarProdutos();
                    limparFormulario();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            TelaProdutoCrud.this,
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
        txtNome.setText("");
        txtReferencia.setText("");
        txtCategoria.setText("");
        txtFornecedor.setText("");
        txtMarca.setText("");
        cbTipoProduto.setSelectedIndex(0);
        txtNome.requestFocus();
    }
}