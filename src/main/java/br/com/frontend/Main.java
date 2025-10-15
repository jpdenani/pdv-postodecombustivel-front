package br.com.frontend;

import br.com.frontend.view.main.TelaPrincipal;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import javax.swing.*;

@SpringBootApplication(scanBasePackages = "br.com.frontend")
public class Main {

    public static void main(String[] args) {
        var context = new SpringApplicationBuilder(Main.class)
                .headless(false)
                .run(args);

        SwingUtilities.invokeLater(() -> {
            TelaPrincipal principal = context.getBean(TelaPrincipal.class);
            principal.setVisible(true);
        });
    }
}
