package com.lifeverse.resume.config;

import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAiConfig {

    @Value("${openai.api.key}")
    private String openAiKey;

    @Bean
    public OpenAiService openAiService() {
        System.out.println("🔥 Using OpenAI Key: " + openAiKey); // DEBUG LINE
        return new OpenAiService(openAiKey); // Do NOT add base URL manually
    }
}

