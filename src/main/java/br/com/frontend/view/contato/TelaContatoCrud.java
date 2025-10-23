package br.com.frontend.view.contato;

import br.com.frontend.dto.ContatoRequest;
import br.com.frontend.dto.ContatoResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

@Component
public class TelaContatoCrud extends JPanel {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String API_URL = "http://localhost:8080/api/v1/contatos";

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtTelefone;
    private JTextField txtEmail;
    private JTextField txtEndereco;
    private JButton btnSalvar;
    private JButton btnExcluir;
    private JButton btnNovo;

    @PostConstruct
    public void init() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ====== Tabela ======
        String[] colunas = {"ID", "Telefone", "Email", "Endereço"};
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
        scrollPane.setBorder(BorderFactory.createTitledBorder("Lista de Contatos"));
        add(scrollPane, BorderLayout.CENTER);

        // ====== Formulário ======
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Dados do Contato"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtTelefone = new JTextField(20);
        txtEmail = new JTextField(20);
        txtEndereco = new JTextField(20);

        // Linha 0: Telefone
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Telefone:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtTelefone, gbc);

        // Linha 1: Email
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtEmail, gbc);

        // Linha 2: Endereço
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Endereço:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtEndereco, gbc);

        // Linha 3: Botões
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

        gbc.gridx = 0; gbc.gridy = 3;
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

        // Carrega dados ao iniciar
        carregarContatos();
    }

    // ====== Carrega todos os contatos ======
    private void carregarContatos() {
        SwingWorker<List<ContatoResponse>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<ContatoResponse> doInBackground() throws Exception {
                try {
                    ContatoResponse[] resp = restTemplate.getForObject(API_URL, ContatoResponse[].class);
                    return resp != null ? List.of(resp) : List.of();
                } catch (Exception ex) {
                    System.err.println("Erro ao carregar contatos: " + ex.getMessage());
                    ex.printStackTrace();
                    return List.of();
                }
            }

            @Override
            protected void done() {
                try {
                    List<ContatoResponse> contatos = get();
                    tableModel.setRowCount(0);
                    for (ContatoResponse c : contatos) {
                        tableModel.addRow(new Object[]{
                                c.id(),
                                c.telefone(),
                                c.email(),
                                c.endereco()
                        });
                    }
                } catch (Exception ex) {
                    System.err.println("Erro ao processar contatos: " + ex.getMessage());
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(
                            TelaContatoCrud.this,
                            "Erro ao carregar contatos: " + ex.getMessage(),
                            "Erro",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    // ====== Preenche formulário ao selecionar ======
    private void preencherFormulario() {
        int linhaSelecionada = table.getSelectedRow();
        if (linhaSelecionada >= 0) {
            txtTelefone.setText(String.valueOf(tableModel.getValueAt(linhaSelecionada, 1)));
            txtEmail.setText(String.valueOf(tableModel.getValueAt(linhaSelecionada, 2)));
            txtEndereco.setText(String.valueOf(tableModel.getValueAt(linhaSelecionada, 3)));
        }
    }

    // ====== Salvar ou atualizar ======
    private void salvarOuAtualizar() {
        String telefone = txtTelefone.getText().trim();
        String email = txtEmail.getText().trim();
        String endereco = txtEndereco.getText().trim();

        if (telefone.isEmpty() || email.isEmpty() || endereco.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Por favor, preencha todos os campos!",
                    "Validação",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        ContatoRequest req = new ContatoRequest(email, telefone, endereco);
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
                        restTemplate.postForEntity(API_URL, req, ContatoResponse.class);
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
                            TelaContatoCrud.this,
                            "Contato salvo com sucesso!",
                            "Sucesso",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    carregarContatos();
                    limparFormulario();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            TelaContatoCrud.this,
                            "Erro ao salvar: " + ex.getMessage(),
                            "Erro",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    // ====== Excluir ======
    private void excluirSelecionado() {
        int linhaSelecionada = table.getSelectedRow();
        if (linhaSelecionada < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecione um contato para excluir!",
                    "Validação",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Deseja realmente excluir este contato?",
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
                            TelaContatoCrud.this,
                            "Contato excluído com sucesso!",
                            "Sucesso",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    carregarContatos();
                    limparFormulario();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            TelaContatoCrud.this,
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
        txtTelefone.setText("");
        txtEmail.setText("");
        txtEndereco.setText("");
        txtTelefone.requestFocus();
    }
}