package jsug.springfestsitegenerator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.util.List;
import java.util.Map;

@Component
public class SpringfestSiteGenerator implements CommandLineRunner {

    private final WebClient webClient;
    private final SpringTemplateEngine templateEngine;
    private final ObjectMapper objectMapper;

    public SpringfestSiteGenerator(WebClient.Builder builder, SpringTemplateEngine templateEngine, ObjectMapper objectMapper) {
        this.webClient = builder.baseUrl("https://portside-api.cfapps.io").build();
        this.templateEngine = templateEngine;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) throws Exception {
        List<Map<String, Object>> sessions = this.webClient.get().uri("sessions") //
                .retrieve().bodyToFlux(JsonNode.class) //
                .map(n -> this.objectMapper.<Map<String, Object>>convertValue(n, new TypeReference<Map<String, Object>>() {
                })) //
                .collectList() //
                .block();
        String html = this.render(sessions);
        System.out.println(html);
    }

    private String render(List<Map<String, Object>> sessions) {
        Context context = new Context();
        context.setVariable("sessions", sessions);
        return this.templateEngine.process("index", context);
    }
}
