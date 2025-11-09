package br.com.frontend.view.contato;

import br.com.frontend.dto.ContatoRequest;
import br.com.frontend.dto.ContatoResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;
import java.util.List;

@Component
public class TelaContatoCrud extends JPanel {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String API_URL = "http://localhost:8080/api/v1/contatos";

    private JTable table;
    private DefaultTableModel tableModel;

    private JFormattedTextField txtTelefone;
    private JTextField txtEmail;
    private JTextField txtEndereco;
    private JButton btnSalvar;
    private JButton btnExcluir;
    private JButton btnNovo;

    @PostConstruct
    public void init() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        String[] colunas = {"ID", "Telefone", "Email", "Endereço"};
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
        scrollPane.setBorder(BorderFactory.createTitledBorder("Lista de Contatos"));
        add(scrollPane, BorderLayout.CENTER);


        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Dados do Contato"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        try {
            MaskFormatter telMask = new MaskFormatter("(##) #####-####");
            telMask.setPlaceholderCharacter('_');
            txtTelefone = new JFormattedTextField(telMask);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        txtEmail = new JTextField(20);
        txtEndereco = new JTextField(20);


        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Telefone:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtTelefone, gbc);


        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtEmail, gbc);


        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Endereço:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtEndereco, gbc);


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnNovo = new JButton("Novo");
        btnSalvar = new JButton("Salvar");
        btnExcluir = new JButton("Excluir");

        buttonPanel.add(btnNovo);
        buttonPanel.add(btnSalvar);
        buttonPanel.add(btnExcluir);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.SOUTH);


        btnNovo.addActionListener(e -> limparFormulario());
        btnSalvar.addActionListener(e -> salvarOuAtualizar());
        btnExcluir.addActionListener(e -> excluirSelecionado());
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) preencherFormulario();
        });

        carregarContatos();
    }

    private void carregarContatos() {
        SwingWorker<List<ContatoResponse>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<ContatoResponse> doInBackground() {
                ContatoResponse[] resp = restTemplate.getForObject(API_URL, ContatoResponse[].class);
                return resp != null ? List.of(resp) : List.of();
            }

            @Override
            protected void done() {
                try {
                    List<ContatoResponse> contatos = get();
                    tableModel.setRowCount(0);
                    for (ContatoResponse c : contatos) {
                        tableModel.addRow(new Object[]{
                                c.id(),
                                formatarTelefone(c.telefone()),
                                c.email(),
                                c.endereco()
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private String formatarTelefone(String tel) {
        if (tel == null || tel.length() < 10) return tel;
        tel = tel.replaceAll("\\D", "");
        if (tel.length() == 11)
            return tel.replaceFirst("(\\d{2})(\\d{5})(\\d{4})", "($1) $2-$3");
        else
            return tel.replaceFirst("(\\d{2})(\\d{4})(\\d{4})", "($1) $2-$3");
    }

    private void preencherFormulario() {
        int linhaSelecionada = table.getSelectedRow();
        if (linhaSelecionada >= 0) {
            txtTelefone.setText(String.valueOf(tableModel.getValueAt(linhaSelecionada, 1)));
            txtEmail.setText(String.valueOf(tableModel.getValueAt(linhaSelecionada, 2)));
            txtEndereco.setText(String.valueOf(tableModel.getValueAt(linhaSelecionada, 3)));
        }
    }

    private void salvarOuAtualizar() {
        String telefone = txtTelefone.getText().replaceAll("\\D", "");
        String email = txtEmail.getText().trim();
        String endereco = txtEndereco.getText().trim();

        if (telefone.isEmpty() || email.isEmpty() || endereco.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos!", "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ContatoRequest req = new ContatoRequest(telefone, email, endereco);
        int linhaSelecionada = table.getSelectedRow();

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                if (linhaSelecionada >= 0) {
                    long id = Long.parseLong(String.valueOf(tableModel.getValueAt(linhaSelecionada, 0)));
                    restTemplate.put(API_URL + "/" + id, req);
                } else {
                    restTemplate.postForEntity(API_URL, req, ContatoResponse.class);
                }
                return null;
            }

            @Override
            protected void done() {
                JOptionPane.showMessageDialog(TelaContatoCrud.this, "Contato salvo com sucesso!");
                carregarContatos();
                limparFormulario();
            }
        };
        worker.execute();
    }

    private void excluirSelecionado() {
        int linhaSelecionada = table.getSelectedRow();
        if (linhaSelecionada < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um contato!", "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        long id = Long.parseLong(String.valueOf(tableModel.getValueAt(linhaSelecionada, 0)));
        restTemplate.delete(API_URL + "/" + id);
        JOptionPane.showMessageDialog(this, "Contato excluído!");
        carregarContatos();
        limparFormulario();
    }

    private void limparFormulario() {
        table.clearSelection();
        txtTelefone.setValue(null);
        txtEmail.setText("");
        txtEndereco.setText("");
        txtTelefone.requestFocus();
    }
}
