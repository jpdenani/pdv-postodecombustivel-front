package br.com.frontend.view.login;

import br.com.frontend.dto.AcessoResponse;
import br.com.frontend.view.main.TelaPrincipal;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.swing.*;
import java.awt.*;

@Component
public class TelaLogin extends JFrame {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String API_URL = "http://localhost:8080/api/v1/acessos";
    private final TelaPrincipal telaPrincipal;

    private JTextField txtUsuario;
    private JPasswordField txtSenha;
    private JButton btnEntrar;
    private JLabel lblMensagem;

    public TelaLogin(TelaPrincipal telaPrincipal) {
        this.telaPrincipal = telaPrincipal;
        initComponents();
    }

    private void initComponents() {
        setTitle("Sistema PDV - Login");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);


        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(41, 128, 185),
                        0, getHeight(), new Color(109, 213, 250));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);


        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));

        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.insets = new Insets(8, 8, 8, 8);
        formGbc.fill = GridBagConstraints.HORIZONTAL;


        JLabel lblTitulo = new JLabel("Sistema PDV");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitulo.setForeground(new Color(52, 73, 94));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        formGbc.gridx = 0;
        formGbc.gridy = 0;
        formGbc.gridwidth = 2;
        formPanel.add(lblTitulo, formGbc);

        JLabel lblSubtitulo = new JLabel("Posto de Combustível");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitulo.setForeground(new Color(127, 140, 141));
        lblSubtitulo.setHorizontalAlignment(SwingConstants.CENTER);
        formGbc.gridy = 1;
        formGbc.insets = new Insets(0, 8, 20, 8);
        formPanel.add(lblSubtitulo, formGbc);


        formGbc.gridwidth = 1;
        formGbc.insets = new Insets(8, 8, 8, 8);
        formGbc.gridy = 2;
        formGbc.gridx = 0;
        JLabel lblUsuario = new JLabel("Usuário:");
        lblUsuario.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUsuario.setForeground(new Color(52, 73, 94));
        formPanel.add(lblUsuario, formGbc);

        formGbc.gridx = 1;
        txtUsuario = new JTextField(18);
        txtUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtUsuario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        formPanel.add(txtUsuario, formGbc);


        formGbc.gridy = 3;
        formGbc.gridx = 0;
        JLabel lblSenha = new JLabel("Senha:");
        lblSenha.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblSenha.setForeground(new Color(52, 73, 94));
        formPanel.add(lblSenha, formGbc);

        formGbc.gridx = 1;
        txtSenha = new JPasswordField(18);
        txtSenha.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSenha.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        formPanel.add(txtSenha, formGbc);


        formGbc.gridy = 4;
        formGbc.gridx = 0;
        formGbc.gridwidth = 2;
        lblMensagem = new JLabel(" ");
        lblMensagem.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblMensagem.setForeground(new Color(231, 76, 60));
        lblMensagem.setHorizontalAlignment(SwingConstants.CENTER);
        formPanel.add(lblMensagem, formGbc);


        formGbc.gridy = 5;
        formGbc.insets = new Insets(15, 8, 8, 8);
        btnEntrar = new JButton("ENTRAR");
        btnEntrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnEntrar.setBackground(new Color(46, 204, 113));
        btnEntrar.setForeground(Color.WHITE);
        btnEntrar.setFocusPainted(false);
        btnEntrar.setBorderPainted(false);
        btnEntrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEntrar.setPreferredSize(new Dimension(0, 40));
        formPanel.add(btnEntrar, formGbc);


        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(formPanel, gbc);

        add(mainPanel);


        btnEntrar.addActionListener(e -> realizarLogin());
        txtSenha.addActionListener(e -> realizarLogin());


        txtUsuario.requestFocus();
    }

    private void realizarLogin() {
        String usuario = txtUsuario.getText().trim();
        String senha = new String(txtSenha.getPassword()).trim();

        if (usuario.isEmpty() || senha.isEmpty()) {
            lblMensagem.setText("Preencha todos os campos!");
            return;
        }

        lblMensagem.setText("Verificando...");
        lblMensagem.setForeground(new Color(52, 152, 219));
        btnEntrar.setEnabled(false);

        SwingWorker<AcessoResponse, Void> worker = new SwingWorker<>() {
            @Override
            protected AcessoResponse doInBackground() throws Exception {
                try {

                    AcessoResponse[] acessos = restTemplate.getForObject(API_URL, AcessoResponse[].class);

                    if (acessos != null) {
                        for (AcessoResponse acesso : acessos) {
                            if (acesso.usuario().equals(usuario) && acesso.senha().equals(senha)) {
                                return acesso;
                            }
                        }
                    }
                    return null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    throw ex;
                }
            }

            @Override
            protected void done() {
                try {
                    AcessoResponse acesso = get();

                    if (acesso != null) {

                        telaPrincipal.setUsuarioLogado(acesso);
                        telaPrincipal.setVisible(true);
                        dispose();
                    } else {

                        lblMensagem.setText("Usuário ou senha incorretos!");
                        lblMensagem.setForeground(new Color(231, 76, 60));
                        txtSenha.setText("");
                        txtUsuario.requestFocus();
                    }
                } catch (Exception ex) {
                    lblMensagem.setText("Erro ao conectar com o servidor!");
                    lblMensagem.setForeground(new Color(231, 76, 60));
                    ex.printStackTrace();
                } finally {
                    btnEntrar.setEnabled(true);
                }
            }
        };
        worker.execute();
    }
}