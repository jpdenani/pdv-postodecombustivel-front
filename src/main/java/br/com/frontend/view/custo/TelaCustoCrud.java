package br.com.frontend.view.custo;

import br.com.frontend.dto.CustoRequest;
import br.com.frontend.dto.CustoResponse;
import br.com.frontend.service.CustoService;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;

@Component
public class TelaCustoCrud extends JPanel {

    private final CustoService custoService;
    private final DefaultTableModel tableModel;
    private final JTable table;

    private final JTextField txtId = new JTextField();
    private final JTextField txtImposto = new JTextField();
    private final JTextField txtCustoVariavel = new JTextField();
    private final JTextField txtCustoFixo = new JTextField();
    private final JTextField txtMargemLucro = new JTextField();
    private final JFormattedTextField txtDataProcessamento;

    private final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

    public TelaCustoCrud(CustoService custoService) {
        this.custoService = custoService;
        setLayout(new BorderLayout());

        String[] columnNames = {"ID", "Imposto", "Custo Variável", "Custo Fixo", "Margem Lucro", "Data Processamento"};
        tableModel = new DefaultTableModel(columnNames,0) {
            @Override public boolean isCellEditable(int row,int col){ return false; }
        };
        table = new JTable(tableModel);

        JPanel formPanel = new JPanel(new GridLayout(6,2,5,5));
        txtId.setEditable(false);

        try {
            MaskFormatter dateMask = new MaskFormatter("##/##/####");
            txtDataProcessamento = new JFormattedTextField(dateMask);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        formPanel.add(new JLabel("ID:")); formPanel.add(txtId);
        formPanel.add(new JLabel("Imposto:")); formPanel.add(txtImposto);
        formPanel.add(new JLabel("Custo Variável:")); formPanel.add(txtCustoVariavel);
        formPanel.add(new JLabel("Custo Fixo:")); formPanel.add(txtCustoFixo);
        formPanel.add(new JLabel("Margem Lucro:")); formPanel.add(txtMargemLucro);
        formPanel.add(new JLabel("Data Processamento:")); formPanel.add(txtDataProcessamento);

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

        // Ações salvar/excluir/limpar seguem padrão TelaPessoaCrud
    }
}
