package br.com.frontend.view.bomba;

import br.com.frontend.dto.BombaRequest;
import br.com.frontend.dto.BombaResponse;
import br.com.frontend.enums.TipoBomba;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

@Component
public class TelaBombaCrud extends JPanel {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String API_URL = "http://localhost:8080/api/v1/bombas";

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtNumero;
    private JComboBox<TipoBomba> cbTipoBomba;
    private JButton btnSalvar;
    private JButton btnExcluir;
    private JButton btnNovo;

    @PostConstruct
    public void init() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        String[] colunas = {"ID", "Número", "Status"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex == 1) return Integer.class;
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
        scrollPane.setBorder(BorderFactory.createTitledBorder("Lista de Bombas"));
        add(scrollPane, BorderLayout.CENTER);


        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Dados da Bomba"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtNumero = new JTextField(20);
        cbTipoBomba = new JComboBox<>(TipoBomba.values());


        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Número da Bomba:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtNumero, gbc);


        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cbTipoBomba, gbc);


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

        gbc.gridx = 0; gbc.gridy = 2;
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


        carregarBombas();
    }


    private void carregarBombas() {
        SwingWorker<List<BombaResponse>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<BombaResponse> doInBackground() throws Exception {
                try {
                    BombaResponse[] resp = restTemplate.getForObject(API_URL, BombaResponse[].class);
                    return resp != null ? List.of(resp) : List.of();
                } catch (Exception ex) {
                    System.err.println("Erro ao carregar bombas: " + ex.getMessage());
                    ex.printStackTrace();
                    return List.of();
                }
            }

            @Override
            protected void done() {
                try {
                    List<BombaResponse> bombas = get();
                    tableModel.setRowCount(0);
                    for (BombaResponse b : bombas) {
                        tableModel.addRow(new Object[]{
                                b.id(),
                                b.numero(),
                                b.tipoBomba()
                        });
                    }
                } catch (Exception ex) {
                    System.err.println("Erro ao processar bombas: " + ex.getMessage());
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(
                            TelaBombaCrud.this,
                            "Erro ao carregar bombas: " + ex.getMessage(),
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
            txtNumero.setText(String.valueOf(tableModel.getValueAt(linhaSelecionada, 1)));

            Object tipoObj = tableModel.getValueAt(linhaSelecionada, 2);
            if (tipoObj instanceof TipoBomba) {
                cbTipoBomba.setSelectedItem(tipoObj);
            } else {
                try {
                    cbTipoBomba.setSelectedItem(TipoBomba.valueOf(String.valueOf(tipoObj)));
                } catch (Exception e) {
                    System.err.println("Erro ao converter TipoBomba: " + e.getMessage());
                }
            }
        }
    }


    private void salvarOuAtualizar() {
        String numeroStr = txtNumero.getText().trim();

        if (numeroStr.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Por favor, preencha o número da bomba!",
                    "Validação",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            Integer numero = Integer.parseInt(numeroStr);
            TipoBomba tipo = (TipoBomba) cbTipoBomba.getSelectedItem();
            BombaRequest req = new BombaRequest(numero, tipo);

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
                            restTemplate.postForEntity(API_URL, req, BombaResponse.class);
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
                                TelaBombaCrud.this,
                                "Bomba salva com sucesso!",
                                "Sucesso",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                        carregarBombas();
                        limparFormulario();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(
                                TelaBombaCrud.this,
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
                    "Número da bomba inválido! Digite apenas números.",
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
                    "Selecione uma bomba para excluir!",
                    "Validação",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Deseja realmente excluir esta bomba?",
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
                            TelaBombaCrud.this,
                            "Bomba excluída com sucesso!",
                            "Sucesso",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    carregarBombas();
                    limparFormulario();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            TelaBombaCrud.this,
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
        txtNumero.setText("");
        cbTipoBomba.setSelectedIndex(0);
        txtNumero.requestFocus();
    }
}