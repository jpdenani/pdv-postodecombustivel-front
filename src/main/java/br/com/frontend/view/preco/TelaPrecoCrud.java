package br.com.frontend.view.preco;

import br.com.frontend.dto.PrecoRequest;
import br.com.frontend.dto.PrecoResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class TelaPrecoCrud extends JPanel {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String API_URL = "http://localhost:8080/api/v1/precos";

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtValor;
    private JFormattedTextField txtDataAlteracao;
    private JFormattedTextField txtHoraAlteracao;
    private JButton btnSalvar;
    private JButton btnExcluir;
    private JButton btnNovo;

    @PostConstruct
    public void init() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ====== Tabela ======
        String[] colunas = {"ID", "Valor", "Data Alteração", "Hora Alteração"};
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
        scrollPane.setBorder(BorderFactory.createTitledBorder("Lista de Preços"));
        add(scrollPane, BorderLayout.CENTER);

        // ====== Formulário ======
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Dados do Preço"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtValor = new JTextField(20);

        try {
            MaskFormatter dateMask = new MaskFormatter("##/##/####");
            dateMask.setPlaceholderCharacter('_');
            txtDataAlteracao = new JFormattedTextField(dateMask);

            MaskFormatter timeMask = new MaskFormatter("##:##:##");
            timeMask.setPlaceholderCharacter('_');
            txtHoraAlteracao = new JFormattedTextField(timeMask);
        } catch (ParseException e) {
            txtDataAlteracao = new JFormattedTextField();
            txtHoraAlteracao = new JFormattedTextField();
        }

        // Linha 0: Valor
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Valor (R$):"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtValor, gbc);

        // Linha 1: Data
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Data Alteração:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtDataAlteracao, gbc);

        // Linha 2: Hora
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Hora Alteração:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtHoraAlteracao, gbc);

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

        carregarPrecos();
    }

    private void carregarPrecos() {
        SwingWorker<List<PrecoResponse>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<PrecoResponse> doInBackground() throws Exception {
                try {
                    PrecoResponse[] resp = restTemplate.getForObject(API_URL, PrecoResponse[].class);
                    return resp != null ? List.of(resp) : List.of();
                } catch (Exception ex) {
                    System.err.println("Erro ao carregar preços: " + ex.getMessage());
                    ex.printStackTrace();
                    return List.of();
                }
            }

            @Override
            protected void done() {
                try {
                    List<PrecoResponse> precos = get();
                    tableModel.setRowCount(0);
                    for (PrecoResponse p : precos) {
                        tableModel.addRow(new Object[]{
                                p.id(),
                                "R$ " + p.valor().toString(),
                                p.dataAlteracao().format(dateFormatter),
                                p.horaAlteracao().format(timeFormatter)
                        });
                    }
                } catch (Exception ex) {
                    System.err.println("Erro ao processar preços: " + ex.getMessage());
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(
                            TelaPrecoCrud.this,
                            "Erro ao carregar preços: " + ex.getMessage(),
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
            String valor = String.valueOf(tableModel.getValueAt(linhaSelecionada, 1)).replace("R$ ", "");
            txtValor.setText(valor);
            txtDataAlteracao.setText(String.valueOf(tableModel.getValueAt(linhaSelecionada, 2)));
            txtHoraAlteracao.setText(String.valueOf(tableModel.getValueAt(linhaSelecionada, 3)));
        }
    }

    private void salvarOuAtualizar() {
        try {
            String valorStr = txtValor.getText().trim();
            String dataStr = txtDataAlteracao.getText().trim();
            String horaStr = txtHoraAlteracao.getText().trim();

            if (valorStr.isEmpty() || dataStr.isEmpty() || horaStr.isEmpty() ||
                    dataStr.contains("_") || horaStr.contains("_")) {
                JOptionPane.showMessageDialog(
                        this,
                        "Por favor, preencha todos os campos!",
                        "Validação",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            BigDecimal valor = new BigDecimal(valorStr);
            LocalDate data = LocalDate.parse(dataStr, dateFormatter);
            LocalTime hora = LocalTime.parse(horaStr, timeFormatter);

            PrecoRequest req = new PrecoRequest(valor, data, hora);
            int linhaSelecionada = table.getSelectedRow();

            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    try {
                        System.out.println("=== Enviando para backend ===");
                        System.out.println("Valor: " + req.valor());
                        System.out.println("Data: " + req.dataAlteracao());
                        System.out.println("Hora: " + req.horaAlteracao());

                        if (linhaSelecionada >= 0) {
                            Object idObj = tableModel.getValueAt(linhaSelecionada, 0);
                            long id = idObj instanceof Number ? ((Number) idObj).longValue() : Long.parseLong(String.valueOf(idObj));
                            restTemplate.put(API_URL + "/" + id, req);
                        } else {
                            restTemplate.postForEntity(API_URL, req, PrecoResponse.class);
                        }
                    } catch (Exception ex) {
                        System.err.println("ERRO DETALHADO:");
                        System.err.println("Tipo: " + ex.getClass().getName());
                        System.err.println("Mensagem: " + ex.getMessage());
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
                                TelaPrecoCrud.this,
                                "Preço salvo com sucesso!",
                                "Sucesso",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                        carregarPrecos();
                        limparFormulario();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(
                                TelaPrecoCrud.this,
                                "Erro ao salvar: " + ex.getMessage(),
                                "Erro",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            };
            worker.execute();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Erro nos dados: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void excluirSelecionado() {
        int linhaSelecionada = table.getSelectedRow();
        if (linhaSelecionada < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecione um preço para excluir!",
                    "Validação",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Deseja realmente excluir este preço?",
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
                            TelaPrecoCrud.this,
                            "Preço excluído com sucesso!",
                            "Sucesso",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    carregarPrecos();
                    limparFormulario();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            TelaPrecoCrud.this,
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
        txtValor.setText("");
        txtDataAlteracao.setText("");
        txtHoraAlteracao.setText("");
        txtValor.requestFocus();
    }
}