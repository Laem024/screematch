package com.aluracursos.screenmatch.service;


import com.theokanning.openai.OpenAiHttpException;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;

import java.util.Collections;

public class ConsultaChatGPT {
    public static String obtenerTraduccion(String texto) {
        try {
            // Instancia del servicio con tu clave API
            OpenAiService service = new OpenAiService("mi-api-key");

            // Crea el mensaje para el modelo
            ChatMessage mensaje = new ChatMessage("user", "Traduce el siguiente texto a español: " + texto);

            // Configura la solicitud
            ChatCompletionRequest solicitud = ChatCompletionRequest.builder()
                    .model("gpt-3.5-turbo")
                    .messages(Collections.singletonList(mensaje))
                    .maxTokens(1000) // Máximo número de tokens en la respuesta
                    .temperature(0.7) // Controla la creatividad de la respuesta
                    .build();

            var respuesta = service.createChatCompletion(solicitud);
            return respuesta.getChoices().get(0).getMessage().getContent().trim();
        } catch (OpenAiHttpException e) {
            if (e.getMessage().contains("quota")) {
                System.err.println("Error: Has excedido tu cuota de API. Revisa tu plan o actualiza tu clave API.");
                return texto;
            }
            throw e; // Rethrow si es otro error
        }
    }
}


