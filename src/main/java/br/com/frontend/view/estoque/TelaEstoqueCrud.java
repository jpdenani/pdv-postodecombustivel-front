package br.com.frontend.view.estoque;

import br.com.frontend.dto.EstoqueRequest;
import br.com.frontend.dto.EstoqueResponse;
import br.com.frontend.enums.TipoEstoque;
import br.com.frontend.service.EstoqueService;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

        String[] columnNames = {"ID","Quantidade","Tanque","Endereço","Lote","Data Validade","Tipo"};
        tableModel = new DefaultTableModel(columnNames,0) {
            @Override public boolean isCellEditable(int row,int col){ return false; }
        };
        table = new JTable(tableModel);

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

        JButton btnSalvar = new JButton("Salvar");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnLimpar = new JButton("Limpar");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnSalvar); buttonPanel.add(btnExcluir); buttonPanel.add(btnLimpar);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel,BorderLayout.CENTER);
        topPanel.add(buttonPanel,BorderLayout.SOUTH);

        add(topPanel,BorderLayout.NORTH);
        add(new JScrollPane(table),BorderLayout.CENTER);
    }
}
