package br.com.frontend;

import br.com.frontend.view.TelaPessoaCrud; // Importa nossa tela
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import javax.swing.SwingUtilities;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {

        var context = new SpringApplicationBuilder(Main.class)
                .headless(false)
                .run(args);

        SwingUtilities.invokeLater(() -> {
            var tela = context.getBean(TelaPessoaCrud.class);
            tela.setVisible(true);
        });
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}