package br.com.frontend.view.preco;

import br.com.frontend.dto.PrecoRequest;
import br.com.frontend.dto.PrecoResponse;
import br.com.frontend.service.PrecoService;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Component
public class TelaPrecoCrud extends JPanel {

    private final PrecoService precoService;
    private final DefaultTableModel tableModel;
    private final JTable table;

    private final JTextField txtId = new JTextField();
    private final JTextField txtValor = new JTextField();
    private final JFormattedTextField txtDataAlteracao;
    private final JFormattedTextField txtHoraAlteracao;

    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
    private final SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");

    public TelaPrecoCrud(PrecoService precoService) {
        this.precoService = precoService;
        setLayout(new BorderLayout());

        String[] columnNames = {"ID","Valor","Data Alteração","Hora Alteração"};
        tableModel = new DefaultTableModel(columnNames,0) {
            @Override public boolean isCellEditable(int row,int col){ return false; }
        };
        table = new JTable(tableModel);

        JPanel formPanel = new JPanel(new GridLayout(4,2,5,5));
        txtId.setEditable(false);

        try {
            txtDataAlteracao = new JFormattedTextField(new MaskFormatter("##/##/####"));
            txtHoraAlteracao = new JFormattedTextField(new MaskFormatter("##:##:##"));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        formPanel.add(new JLabel("ID:")); formPanel.add(txtId);
        formPanel.add(new JLabel("Valor:")); formPanel.add(txtValor);
        formPanel.add(new JLabel("Data Alteração:")); formPanel.add(txtDataAlteracao);
        formPanel.add(new JLabel("Hora Alteração:")); formPanel.add(txtHoraAlteracao);

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
