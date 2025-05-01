package com.lotreetea.backend.service;

import com.lotreetea.backend.dto.ChatGPTRequest;
import com.lotreetea.backend.dto.ChatGPTResponse;
import com.lotreetea.backend.dto.PromptRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

import com.lotreetea.backend.model.MenuItem;
import com.lotreetea.backend.service.MenuItemService; 
import com.lotreetea.backend.model.MenuItemComponent;
import com.lotreetea.backend.model.InventoryItem;
import com.lotreetea.backend.repo.InventoryItemRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatGPTService {

    private final RestClient restClient;
    private final MenuItemService menuItemService;
    private final InventoryItemRepo inventoryItemRepo;

    @Value("${openapi.api.key}")
    private String apiKey;

    @Value("${openapi.api.model}")
    private String model;

    public String getChatResponse(PromptRequest promptRequest) {
        List<MenuItem> menuItems = menuItemService.getAllMenuItems();

        StringBuilder menuDescription = new StringBuilder("Here is the current LoTreeTea menu:\n");

        for (MenuItem item : menuItems) {
            menuDescription.append("- ")
                    .append(item.getItemName())
                    .append(" ($")
                    .append(item.getPrice())
                    .append(")");

            List<MenuItemComponent> components = item.getComponents();

            if (components != null && !components.isEmpty()) {
                menuDescription.append(": includes ingredients [");

                String ingredientList = components.stream()
                        .map(c -> inventoryItemRepo.findById(c.getInventoryItemId())
                                .map(InventoryItem::getItemName)
                                .orElse("Unknown Ingredient"))
                        .collect(Collectors.joining(", "));

                menuDescription.append(ingredientList).append("]");
            }

            menuDescription.append("\n");
        }

        List<ChatGPTRequest.Message> messages = List.of(
                new ChatGPTRequest.Message(
                        "system",
                        "You are a friendly assistant, named Lohit, for a boba tea shop named LoTreeTea. " +
                                "Help customers with menu items and give recommendations. Many customers love the Lohit Creamy Special!" +
                                "Direct people towards how to order if they ask. To order: select items and quantity. They can sort by category as well. Once they have filled their cart, hit order! " +
                                "Use the menu below to answer questions or make recommendations:\n\n" +
                                menuDescription
                ),
                new ChatGPTRequest.Message("user", promptRequest.prompt())
        );

        ChatGPTRequest chatGPTRequest = new ChatGPTRequest(model, messages);

        try {
            ChatGPTResponse response = restClient.post()
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .body(chatGPTRequest)
                    .retrieve()
                    .body(ChatGPTResponse.class);

            return response.choices().get(0).message().content();
        } catch (Exception ex) {
            ex.printStackTrace();
            return "[OpenAI error: " + ex.getMessage() + "]";
        }
    }
}