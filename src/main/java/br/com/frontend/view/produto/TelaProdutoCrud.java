package br.com.frontend.view.produto;

import br.com.frontend.dto.ProdutoRequest;
import br.com.frontend.dto.ProdutoResponse;
import br.com.frontend.enums.TipoProduto;
import br.com.frontend.service.ProdutoService;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

@Component
public class TelaProdutoCrud extends JPanel {

    private final ProdutoService produtoService;
    private final DefaultTableModel tableModel;
    private final JTable table;

    private final JTextField txtId = new JTextField();
    private final JTextField txtNome = new JTextField();
    private final JTextField txtReferencia = new JTextField();
    private final JTextField txtCategoria = new JTextField();
    private final JTextField txtFornecedor = new JTextField();
    private final JTextField txtMarca = new JTextField();
    private final JComboBox<TipoProduto> comboTipoProduto = new JComboBox<>(TipoProduto.values());

    public TelaProdutoCrud(ProdutoService produtoService) {
        this.produtoService = produtoService;
        setLayout(new BorderLayout());

        int padding = 15;
        setBorder(new EmptyBorder(padding, padding, padding, padding));

        String[] columnNames = {"ID","Nome","Referência","Categoria","Fornecedor","Marca","Tipo"};
        tableModel = new DefaultTableModel(columnNames,0){
            @Override public boolean isCellEditable(int row,int col){ return false; }
        };
        table = new JTable(tableModel);

        JPanel formPanel = new JPanel(new GridLayout(7,2,5,5));
        txtId.setEditable(false);

        formPanel.add(new JLabel("ID:")); formPanel.add(txtId);
        formPanel.add(new JLabel("Nome:")); formPanel.add(txtNome);
        formPanel.add(new JLabel("Referência:")); formPanel.add(txtReferencia);
        formPanel.add(new JLabel("Categoria:")); formPanel.add(txtCategoria);
        formPanel.add(new JLabel("Fornecedor:")); formPanel.add(txtFornecedor);
        formPanel.add(new JLabel("Marca:")); formPanel.add(txtMarca);
        formPanel.add(new JLabel("Tipo Produto:")); formPanel.add(comboTipoProduto);

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
