package com.pictalk.message.service;


import com.pictalk.global.component.OpenAIClient;
import com.pictalk.message.dto.MessageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AIMessageService {
    private final OpenAIClient openAIClient;

    private static final String PROMPT_TEMPLATE =
            "당신은 사용자로부터 상황을 입력받아 이를 토대로 문자 메시지를 만들어야 합니다.\n" +
                    "이 메시지는 한번에 여러 명의 사용자에게 보내는 용도이며, 반드시 상황에 맞는 내용으로 구성되어야 합니다.\n" +
                    "또한, 상황에 따라 적절한 어투, 용어를 사용해야 합니다.\n\n" +
                    "## 입력 데이터\n" +
                    "- 상황: %s\n";


    public MessageResponseDto.CreateAIMessageResponse generateMessage(String situation) {
        String prompt = String.format(PROMPT_TEMPLATE, situation);
        String response = openAIClient.getResponseFromOpenAI(prompt);
        return MessageResponseDto.CreateAIMessageResponse.builder()
                .message(response)
                .build();
    }
}
