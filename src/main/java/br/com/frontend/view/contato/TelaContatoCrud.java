package br.com.frontend.view.contato;

import br.com.frontend.dto.ContatoRequest;
import br.com.frontend.dto.ContatoResponse;
import br.com.frontend.service.ContatoService;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

@Component
public class TelaContatoCrud extends JPanel {

    private final ContatoService contatoService;
    private final DefaultTableModel tableModel;
    private final JTable table;

    private final JTextField txtId = new JTextField();
    private final JTextField txtEmail = new JTextField();
    private final JTextField txtTelefone = new JTextField();
    private final JTextField txtEndereco = new JTextField();

    public TelaContatoCrud(ContatoService contatoService) {
        this.contatoService = contatoService;

        setLayout(new BorderLayout());

        String[] columnNames = {"ID","Email","Telefone","Endereço"};
        tableModel = new DefaultTableModel(columnNames,0){
            @Override public boolean isCellEditable(int row,int col){ return false; }
        };
        table = new JTable(tableModel);

        JPanel formPanel = new JPanel(new GridLayout(4,2,5,5));
        txtId.setEditable(false);

        formPanel.add(new JLabel("ID:")); formPanel.add(txtId);
        formPanel.add(new JLabel("Email:")); formPanel.add(txtEmail);
        formPanel.add(new JLabel("Telefone:")); formPanel.add(txtTelefone);
        formPanel.add(new JLabel("Endereço:")); formPanel.add(txtEndereco);

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

        // Ações seguem mesma lógica de TelaPessoaCrud
    }
}
