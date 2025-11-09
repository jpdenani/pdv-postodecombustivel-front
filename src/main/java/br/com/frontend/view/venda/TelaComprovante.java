package br.com.frontend.view.venda;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TelaComprovante extends JFrame {

    private static final DecimalFormat df = new DecimalFormat("#,##0.00");

    public TelaComprovante(
            String produtoNome,
            double precoUnitario,
            double quantidadeLitros,
            double valorTotal,
            double imposto,
            String formaPagamento
    ) {
        setTitle("Comprovante de Venda");
        setSize(320, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));


        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String dataHora = sdf.format(new Date());


        double impostoPercentual = imposto;
        if (impostoPercentual > 1) {
            impostoPercentual = impostoPercentual / 100.0;
        }
        double valorImposto = valorTotal * impostoPercentual;

        //topo
        addLabel(panel, "POSTO DE COMBUSTÍVEIS", true, 16);
        addLabel(panel, "--------------------------------------", false, 12);
        addLabel(panel, "DATA: " + dataHora, false, 12);
        addLabel(panel, "--------------------------------------", false, 12);

        //detalhes da venda
        addLabel(panel, "PRODUTO: " + produtoNome, false, 13);
        addLabel(panel, "QTD: " + df.format(quantidadeLitros) + " L", false, 13);
        addLabel(panel, "PREÇO UNIT: R$ " + df.format(precoUnitario), false, 13);
        addLabel(panel, "--------------------------------------", false, 12);

        //totais
        addLabel(panel, "VALOR TOTAL: R$ " + df.format(valorTotal), true, 14);
        addLabel(panel, "IMPOSTOS: R$ " + df.format(valorImposto), false, 13);
        addLabel(panel, "PAGAMENTO: " + formaPagamento, false, 13);

        addLabel(panel, "--------------------------------------", false, 12);
        addLabel(panel, "Obrigado pela preferência!", true, 13);
        addLabel(panel, "--------------------------------------", false, 12);

        //botão de fechar
        JButton btnFechar = new JButton("Fechar");
        btnFechar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnFechar.addActionListener(e -> dispose());
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnFechar);

        add(panel);
    }

    private void addLabel(JPanel panel, String text, boolean bold, int size) {
        JLabel label = new JLabel(text);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setFont(new Font("Monospaced", bold ? Font.BOLD : Font.PLAIN, size));
        panel.add(label);
        panel.add(Box.createVerticalStrut(4));
    }
}
