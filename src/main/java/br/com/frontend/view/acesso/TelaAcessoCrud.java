package br.com.frontend.view.acesso;

import br.com.frontend.dto.AcessoRequest;
import br.com.frontend.dto.AcessoResponse;
import br.com.frontend.enums.TipoAcesso;
import br.com.frontend.service.AcessoService;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

@Component
public class TelaAcessoCrud extends JPanel {

    private final AcessoService acessoService;
    private final DefaultTableModel tableModel;
    private final JTable table;

    private final JTextField txtId = new JTextField();
    private final JTextField txtUsuario = new JTextField();
    private final JTextField txtSenha = new JTextField();
    private final JComboBox<TipoAcesso> comboTipoAcesso = new JComboBox<>(TipoAcesso.values());

    public TelaAcessoCrud(AcessoService acessoService) {
        this.acessoService = acessoService;

        setLayout(new BorderLayout());

        String[] columnNames = {"ID", "Usuário", "Senha", "Tipo"};
        tableModel = new DefaultTableModel(columnNames,0) {
            @Override public boolean isCellEditable(int row,int col){ return false; }
        };
        table = new JTable(tableModel);

        JPanel formPanel = new JPanel(new GridLayout(4,2,5,5));
        txtId.setEditable(false);

        formPanel.add(new JLabel("ID:")); formPanel.add(txtId);
        formPanel.add(new JLabel("Usuário:")); formPanel.add(txtUsuario);
        formPanel.add(new JLabel("Senha:")); formPanel.add(txtSenha);
        formPanel.add(new JLabel("Tipo de Acesso:")); formPanel.add(comboTipoAcesso);

        JButton btnSalvar = new JButton("Salvar");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnLimpar = new JButton("Limpar");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnSalvar);
        buttonPanel.add(btnExcluir);
        buttonPanel.add(btnLimpar);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel,BorderLayout.CENTER);
        topPanel.add(buttonPanel,BorderLayout.SOUTH);

        add(topPanel,BorderLayout.NORTH);
        add(new JScrollPane(table),BorderLayout.CENTER);

        // Ações (salvar, excluir, limpar, preencher) seguem lógica similar à TelaPessoaCrud
    }
}
