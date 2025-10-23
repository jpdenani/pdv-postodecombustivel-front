package br.com.frontend.view.pessoa;

import br.com.frontend.dto.PessoaRequest;
import br.com.frontend.dto.PessoaResponse;
import br.com.frontend.enums.TipoPessoa;
import br.com.frontend.service.PessoaService;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class TelaPessoaCrud extends JPanel {

    private final PessoaService pessoaService;
    private final DefaultTableModel tableModel;
    private final JTable table;

    private final JTextField txtId = new JTextField();
    private final JTextField txtNome = new JTextField();
    private final JTextField txtCpfCnpj = new JTextField();
    private final JTextField txtCtps = new JTextField();
    private final JFormattedTextField txtDataNascimento;
    private final JComboBox<TipoPessoa> comboTipoPessoa = new JComboBox<>(TipoPessoa.values());

    public TelaPessoaCrud(PessoaService pessoaService) {
        this.pessoaService = pessoaService;

        setLayout(new BorderLayout());

        int padding = 15;
        setBorder(new EmptyBorder(padding, padding, padding, padding));

        setBackground(Color.WHITE);

        // --- Modelo da Tabela ---
        String[] columnNames = {"ID", "Nome Completo", "CPF/CNPJ", "CTPS", "Data Nasc.", "Tipo"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // --- Formulário ---
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Dados da Pessoa"));
        txtId.setEditable(false);

        try {
            MaskFormatter dateMask = new MaskFormatter("##/##/####");
            txtDataNascimento = new JFormattedTextField(dateMask);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        formPanel.add(new JLabel("ID:"));
        formPanel.add(txtId);
        formPanel.add(new JLabel("Nome Completo:"));
        formPanel.add(txtNome);
        formPanel.add(new JLabel("CPF/CNPJ:"));
        formPanel.add(txtCpfCnpj);
        formPanel.add(new JLabel("Nº CTPS:"));
        formPanel.add(txtCtps);
        formPanel.add(new JLabel("Data Nascimento (dd/mm/aaaa):"));
        formPanel.add(txtDataNascimento);
        formPanel.add(new JLabel("Tipo de Pessoa:"));
        formPanel.add(comboTipoPessoa);

        // --- Botões ---
        JButton btnSalvar = new JButton("Salvar");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnLimpar = new JButton("Limpar");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnSalvar);
        buttonPanel.add(btnExcluir);
        buttonPanel.add(btnLimpar);

        // --- Painel Superior ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        // --- Montagem da Tela ---
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- Ações ---
        btnSalvar.addActionListener(e -> salvar());
        btnExcluir.addActionListener(e -> excluir());
        btnLimpar.addActionListener(e -> limparFormulario());
        table.getSelectionModel().addListSelectionListener(e -> preencherFormularioComLinhaSelecionada());

        atualizarTabela();
    }

    private void atualizarTabela() {
        new SwingWorker<List<PessoaResponse>, Void>() {
            @Override
            protected List<PessoaResponse> doInBackground() throws Exception {
                return pessoaService.listarPessoas();
            }

            @Override
            protected void done() {
                try {
                    List<PessoaResponse> pessoas = get();
                    tableModel.setRowCount(0);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                    for (PessoaResponse p : pessoas) {
                        tableModel.addRow(new Object[]{
                                p.id() != null ? p.id().toString() : "",
                                p.nomeCompleto(),
                                p.cpfCnpj(),
                                p.numeroCtps(),
                                p.dataNascimento() != null ? p.dataNascimento().format(formatter) : "",
                                p.tipoPessoa() != null ? p.tipoPessoa().name() : ""
                        });
                    }

                    table.repaint();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(TelaPessoaCrud.this,
                            "Erro ao buscar pessoas: " + e.getMessage(),
                            "Erro",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void salvar() {
        if (txtNome.getText().isBlank() || txtCpfCnpj.getText().isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "Nome e CPF/CNPJ são obrigatórios.",
                    "Erro de Validação",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate dataNascimento;
        try {
            dataNascimento = LocalDate.parse(txtDataNascimento.getText(), formatter);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Formato de data inválido. Use dd/mm/aaaa.",
                    "Erro de Validação",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        PessoaRequest request = new PessoaRequest(
                txtNome.getText(),
                txtCpfCnpj.getText(),
                Long.parseLong(txtCtps.getText()),
                dataNascimento,
                (TipoPessoa) comboTipoPessoa.getSelectedItem()
        );

        Long id = txtId.getText().isEmpty() ? null : Long.valueOf(txtId.getText());

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                pessoaService.salvarPessoa(request, id);
                return null;
            }

            @Override
            protected void done() {
                JOptionPane.showMessageDialog(TelaPessoaCrud.this, "Pessoa salva com sucesso!");
                limparFormulario();
                atualizarTabela();
            }
        }.execute();
    }

    private void excluir() {
        if (table.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma pessoa para excluir.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza?",
                "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Long id = Long.parseLong(table.getValueAt(table.getSelectedRow(), 0).toString());
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    pessoaService.excluirPessoa(id);
                    return null;
                }

                @Override
                protected void done() {
                    JOptionPane.showMessageDialog(TelaPessoaCrud.this, "Pessoa excluída com sucesso!");
                    limparFormulario();
                    atualizarTabela();
                }
            }.execute();
        }
    }

    private void preencherFormularioComLinhaSelecionada() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            txtId.setText(table.getValueAt(selectedRow, 0).toString());
            txtNome.setText(table.getValueAt(selectedRow, 1).toString());
            txtCpfCnpj.setText(table.getValueAt(selectedRow, 2).toString());
            txtCtps.setText(table.getValueAt(selectedRow, 3).toString());
            txtDataNascimento.setText(table.getValueAt(selectedRow, 4).toString());
            comboTipoPessoa.setSelectedItem(TipoPessoa.valueOf(table.getValueAt(selectedRow, 5).toString()));
        }
    }

    private void limparFormulario() {
        txtId.setText("");
        txtNome.setText("");
        txtCpfCnpj.setText("");
        txtCtps.setText("");
        txtDataNascimento.setText("");
        comboTipoPessoa.setSelectedIndex(0);
        table.clearSelection();
    }
}
