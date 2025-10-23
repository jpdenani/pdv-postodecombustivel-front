package br.com.frontend.view.estoque;

import br.com.frontend.dto.EstoqueRequest;
import br.com.frontend.dto.EstoqueResponse;
import br.com.frontend.enums.TipoEstoque;
import br.com.frontend.service.EstoqueService;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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

    private final EstoqueService estoqueService;
    private final DefaultTableModel tableModel;
    private final JTable table;

    private final JTextField txtId = new JTextField();
    private final JTextField txtQuantidade = new JTextField();
    private final JTextField txtTanque = new JTextField();
    private final JTextField txtEndereco = new JTextField();
    private final JTextField txtLote = new JTextField();
    private final JFormattedTextField txtDataValidade;
    private final JComboBox<TipoEstoque> comboTipoEstoque = new JComboBox<>(TipoEstoque.values());

    private final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

    public TelaEstoqueCrud(EstoqueService estoqueService) {
        this.estoqueService = estoqueService;
        setLayout(new BorderLayout());

        int padding = 15;
        setBorder(new EmptyBorder(padding, padding, padding, padding));

        // --- Tabela ---
        String[] columnNames = {"ID","Quantidade","Tanque","Endereço","Lote","Data Validade","Tipo"};
        tableModel = new DefaultTableModel(columnNames,0) {
            @Override public boolean isCellEditable(int row,int col){ return false; }
        };
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // --- Formulário ---
        JPanel formPanel = new JPanel(new GridLayout(7,2,5,5));
        txtId.setEditable(false);

        try {
            MaskFormatter dateMask = new MaskFormatter("##/##/####");
            txtDataValidade = new JFormattedTextField(dateMask);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        formPanel.add(new JLabel("ID:")); formPanel.add(txtId);
        formPanel.add(new JLabel("Quantidade:")); formPanel.add(txtQuantidade);
        formPanel.add(new JLabel("Tanque:")); formPanel.add(txtTanque);
        formPanel.add(new JLabel("Endereço:")); formPanel.add(txtEndereco);
        formPanel.add(new JLabel("Lote:")); formPanel.add(txtLote);
        formPanel.add(new JLabel("Data Validade:")); formPanel.add(txtDataValidade);
        formPanel.add(new JLabel("Tipo de Estoque:")); formPanel.add(comboTipoEstoque);

        // --- Botões ---
        JButton btnSalvar = new JButton("Salvar");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnLimpar = new JButton("Limpar");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnSalvar); buttonPanel.add(btnExcluir); buttonPanel.add(btnLimpar);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel,BorderLayout.CENTER);
        topPanel.add(buttonPanel,BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // --- Listeners ---
        btnSalvar.addActionListener(e -> salvarEstoque());
        btnExcluir.addActionListener(e -> excluirEstoque());
        btnLimpar.addActionListener(e -> limparFormulario());
        table.getSelectionModel().addListSelectionListener(e -> preencherFormulario());

        // --- Carregar tabela ---
        carregarTabela();
    }

    private void carregarTabela() {
        tableModel.setRowCount(0);
        List<EstoqueResponse> estoques = estoqueService.listarEstoques();
        for (EstoqueResponse e : estoques) {
            tableModel.addRow(new Object[]{
                    e.id(),
                    e.quantidade(),
                    e.localTanque(),
                    e.localEndereco(),
                    e.loteFabricacao(),
                    formatter.format(e.dataValidade()),
                    e.tipoEstoque()
            });
        }
    }

    private void salvarEstoque() {
        try {
            BigDecimal quantidade = new BigDecimal(txtQuantidade.getText().trim());
            String tanque = txtTanque.getText().trim();
            String endereco = txtEndereco.getText().trim();
            String lote = txtLote.getText().trim();
            Date dataValidade = formatter.parse(txtDataValidade.getText().trim());
            TipoEstoque tipo = (TipoEstoque) comboTipoEstoque.getSelectedItem();

            EstoqueRequest request = new EstoqueRequest(quantidade, tanque, endereco, lote, dataValidade, tipo);
            Long id = txtId.getText().isEmpty() ? null : Long.parseLong(txtId.getText());
            estoqueService.salvarEstoque(request, id);

            carregarTabela();
            limparFormulario();
            JOptionPane.showMessageDialog(this, "Estoque salvo com sucesso!");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar estoque: " + ex.getMessage());
        }
    }

    private void excluirEstoque() {
        if (!txtId.getText().isEmpty()) {
            int confirm = JOptionPane.showConfirmDialog(this, "Deseja realmente excluir?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                Long id = Long.parseLong(txtId.getText());
                estoqueService.excluirEstoque(id);
                carregarTabela();
                limparFormulario();
            }
        }
    }

    private void limparFormulario() {
        txtId.setText("");
        txtQuantidade.setText("");
        txtTanque.setText("");
        txtEndereco.setText("");
        txtLote.setText("");
        txtDataValidade.setText("");
        comboTipoEstoque.setSelectedIndex(0);
        table.clearSelection();
    }

    private void preencherFormulario() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            txtId.setText(tableModel.getValueAt(selectedRow, 0).toString());
            txtQuantidade.setText(tableModel.getValueAt(selectedRow, 1).toString());
            txtTanque.setText(tableModel.getValueAt(selectedRow, 2).toString());
            txtEndereco.setText(tableModel.getValueAt(selectedRow, 3).toString());
            txtLote.setText(tableModel.getValueAt(selectedRow, 4).toString());
            txtDataValidade.setText(tableModel.getValueAt(selectedRow, 5).toString());
            comboTipoEstoque.setSelectedItem(tableModel.getValueAt(selectedRow, 6));
        }
    }
}
