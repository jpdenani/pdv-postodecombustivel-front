package br.com.frontend.view.acesso;

import br.com.frontend.dto.AcessoRequest;
import br.com.frontend.dto.AcessoResponse;
import br.com.frontend.enums.TipoAcesso;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

@Component
public class TelaAcessoCrud extends JPanel {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String API_URL = "http://localhost:8080/api/v1/acessos";

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField tfUsuario;
    private JTextField tfSenha;
    private JComboBox<TipoAcesso> cbTipoAcesso;
    private JButton btnSalvar;
    private JButton btnExcluir;
    private JButton btnNovo;

    @PostConstruct
    public void init() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ====== Tabela ======
        String[] colunas = {"ID", "Usuário", "Senha", "Tipo de Acesso"};
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
        scrollPane.setBorder(BorderFactory.createTitledBorder("Lista de Acessos"));
        add(scrollPane, BorderLayout.CENTER);

        // ====== Formulário ======
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Dados do Acesso"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        tfUsuario = new JTextField(20);
        tfSenha = new JPasswordField(20);
        cbTipoAcesso = new JComboBox<>(TipoAcesso.values());

        // Linha 0: Usuário
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Usuário:"), gbc);
        gbc.gridx = 1;
        formPanel.add(tfUsuario, gbc);

        // Linha 1: Senha
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Senha:"), gbc);
        gbc.gridx = 1;
        formPanel.add(tfSenha, gbc);

        // Linha 2: Tipo de Acesso
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Tipo de Acesso:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cbTipoAcesso, gbc);

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
        carregarAcessos();
    }

    // ====== Carrega todos os acessos ======
    private void carregarAcessos() {
        SwingWorker<List<AcessoResponse>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<AcessoResponse> doInBackground() throws Exception {
                try {
                    ResponseEntity<AcessoResponse[]> resp = restTemplate.getForEntity(API_URL, AcessoResponse[].class);
                    return resp.getBody() != null ? List.of(resp.getBody()) : List.of();
                } catch (Exception ex) {
                    System.err.println("Erro ao carregar acessos: " + ex.getMessage());
                    ex.printStackTrace();
                    return List.of();
                }
            }

            @Override
            protected void done() {
                try {
                    List<AcessoResponse> acessos = get();
                    tableModel.setRowCount(0);
                    for (AcessoResponse a : acessos) {
                        tableModel.addRow(new Object[]{
                                a.id(),
                                a.usuario(),
                                a.senha(),
                                a.tipoAcesso()
                        });
                    }
                } catch (Exception ex) {
                    System.err.println("Erro ao processar acessos: " + ex.getMessage());
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(
                            TelaAcessoCrud.this,
                            "Erro ao carregar acessos: " + ex.getMessage(),
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
            tfUsuario.setText(String.valueOf(tableModel.getValueAt(linhaSelecionada, 1)));
            tfSenha.setText(String.valueOf(tableModel.getValueAt(linhaSelecionada, 2)));

            Object tipoAcessoObj = tableModel.getValueAt(linhaSelecionada, 3);
            if (tipoAcessoObj instanceof TipoAcesso) {
                cbTipoAcesso.setSelectedItem(tipoAcessoObj);
            } else {
                try {
                    cbTipoAcesso.setSelectedItem(TipoAcesso.valueOf(String.valueOf(tipoAcessoObj)));
                } catch (Exception e) {
                    System.err.println("Erro ao converter TipoAcesso: " + e.getMessage());
                }
            }
        }
    }

    // ====== Salvar ou atualizar ======
    private void salvarOuAtualizar() {
        String usuario = tfUsuario.getText().trim();
        String senha = tfSenha.getText().trim();

        if (usuario.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Por favor, preencha todos os campos!",
                    "Validação",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        TipoAcesso tipo = (TipoAcesso) cbTipoAcesso.getSelectedItem();
        AcessoRequest req = new AcessoRequest(usuario, senha, tipo);

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
                        restTemplate.postForEntity(API_URL, req, AcessoResponse.class);
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
                            TelaAcessoCrud.this,
                            "Acesso salvo com sucesso!",
                            "Sucesso",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    carregarAcessos();
                    limparFormulario();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            TelaAcessoCrud.this,
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
                    "Selecione um acesso para excluir!",
                    "Validação",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Deseja realmente excluir este acesso?",
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
                            TelaAcessoCrud.this,
                            "Acesso excluído com sucesso!",
                            "Sucesso",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    carregarAcessos();
                    limparFormulario();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            TelaAcessoCrud.this,
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
        tfUsuario.setText("");
        tfSenha.setText("");
        cbTipoAcesso.setSelectedIndex(0);
        tfUsuario.requestFocus();
    }
}