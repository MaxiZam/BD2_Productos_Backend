package ar.unrn.tp.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // Rutas que permitirán CORS
                .allowedOrigins("http://localhost:5173") // Dirección de tu frontend
                .allowedMethods("GET", "POST", "PUT", "DELETE") // Métodos HTTP permitidos
                .allowCredentials(true);
    }
}
