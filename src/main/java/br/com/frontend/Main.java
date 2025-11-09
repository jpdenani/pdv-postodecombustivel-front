package br.com.frontend;

import br.com.frontend.view.login.TelaLogin;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import javax.swing.*;

@SpringBootApplication(scanBasePackages = "br.com.frontend")
public class Main {

    public static void main(String[] args) {
        //look and feel do sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        var context = new SpringApplicationBuilder(Main.class)
                .headless(false)
                .run(args);

        SwingUtilities.invokeLater(() -> {
            TelaLogin telaLogin = context.getBean(TelaLogin.class);
            telaLogin.setVisible(true);
        });
    }
}