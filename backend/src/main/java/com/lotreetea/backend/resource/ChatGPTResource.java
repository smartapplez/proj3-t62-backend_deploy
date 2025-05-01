package com.lotreetea.backend.resource;

import com.lotreetea.backend.dto.PromptRequest;
import com.lotreetea.backend.service.ChatGPTService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class ChatGPTResource {

    private final ChatGPTService chatGPTService;


    public ChatGPTResource(ChatGPTService chatGPTService) {
        this.chatGPTService = chatGPTService;
    }

    @PostMapping
    public String chat(@RequestBody PromptRequest promptRequest) {
        return chatGPTService.getChatResponse(promptRequest);
        //return "Echo: " + promptRequest.prompt();
    }
}