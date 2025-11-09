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
    private final JFormattedTextField txtCpfCnpj;
    private final JFormattedTextField txtCtps;
    private final JFormattedTextField txtDataNascimento;
    private final JComboBox<TipoPessoa> comboTipoPessoa = new JComboBox<>(TipoPessoa.values());

    public TelaPessoaCrud(PessoaService pessoaService) {
        this.pessoaService = pessoaService;

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(Color.WHITE);


        String[] columnNames = {"ID", "Nome Completo", "CPF/CNPJ", "CTPS", "Data Nasc.", "Tipo"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);


        try {
            MaskFormatter cpfMask = new MaskFormatter("###.###.###-##");
            cpfMask.setPlaceholderCharacter('_');
            txtCpfCnpj = new JFormattedTextField(cpfMask);

            MaskFormatter ctpsMask = new MaskFormatter("#########-#");
            ctpsMask.setPlaceholderCharacter('_');
            txtCtps = new JFormattedTextField(ctpsMask);

            MaskFormatter dateMask = new MaskFormatter("##/##/####");
            dateMask.setPlaceholderCharacter('_');
            txtDataNascimento = new JFormattedTextField(dateMask);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }


        JPanel formPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Dados da Pessoa"));
        txtId.setEditable(false);

        formPanel.add(new JLabel("Nome Completo:"));
        formPanel.add(txtNome);
        formPanel.add(new JLabel("CPF:"));
        formPanel.add(txtCpfCnpj);
        formPanel.add(new JLabel("CTPS:"));
        formPanel.add(txtCtps);
        formPanel.add(new JLabel("Data Nascimento:"));
        formPanel.add(txtDataNascimento);
        formPanel.add(new JLabel("Tipo de Pessoa:"));
        formPanel.add(comboTipoPessoa);


        JButton btnSalvar = new JButton("Salvar");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnLimpar = new JButton("Limpar");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnSalvar);
        buttonPanel.add(btnExcluir);
        buttonPanel.add(btnLimpar);


        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);


        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);


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
                        String cpfFormatado = formatarCPF(p.cpfCnpj());
                        String ctpsFormatado = formatarCTPS(p.numeroCtps());
                        String dataFormatada = p.dataNascimento() != null ? p.dataNascimento().format(formatter) : "";

                        tableModel.addRow(new Object[]{
                                p.id(),
                                p.nomeCompleto(),
                                cpfFormatado,
                                ctpsFormatado,
                                dataFormatada,
                                p.tipoPessoa() != null ? p.tipoPessoa().name() : ""
                        });
                    }

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(TelaPessoaCrud.this,
                            "Erro ao buscar pessoas: " + e.getMessage(),
                            "Erro",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private String formatarCPF(String cpf) {
        if (cpf == null || cpf.length() != 11) return cpf;
        return cpf.replaceFirst("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }

    private String formatarCTPS(Long ctps) {
        if (ctps == null) return "";
        String str = String.format("%010d", ctps);
        return str.replaceFirst("(\\d{9})(\\d)", "$1-$2");
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
                    "Formato de data inválido.",
                    "Erro de Validação",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String cpfLimpo = txtCpfCnpj.getText().replaceAll("\\D", "");
        String ctpsLimpo = txtCtps.getText().replaceAll("\\D", "");

        PessoaRequest request = new PessoaRequest(
                txtNome.getText(),
                cpfLimpo,
                Long.parseLong(ctpsLimpo),
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
            txtId.setText(String.valueOf(table.getValueAt(selectedRow, 0)));
            txtNome.setText(String.valueOf(table.getValueAt(selectedRow, 1)));
            txtCpfCnpj.setText(String.valueOf(table.getValueAt(selectedRow, 2)));
            txtCtps.setText(String.valueOf(table.getValueAt(selectedRow, 3)));
            txtDataNascimento.setText(String.valueOf(table.getValueAt(selectedRow, 4)));
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
