package com.pictalk.message.service;

import com.pictalk.global.component.OpenAIClient;
import com.pictalk.message.dto.MessageRequestDto;
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
                    "키워드는 반드시 포함되어야 하며, 이모지나 불필요한 특수 문자는 포함되지 않아야 합니다.\n" +
                    "또한, 상황에 따라 적절한 어투, 용어를 사용해야 합니다.\n" +
                    "## 입력 데이터\n" +
                    "- 상황: %s\n" +
                    "- 키워드들: %s\n\n";


    public MessageResponseDto.CreateAIMessageResponse generateMessage(MessageRequestDto.CreateAIMessageRequest message) {
        String prompt = String.format(PROMPT_TEMPLATE, String.join(", ", message.getKeyword()), message.getSituation());
        String response = openAIClient.getResponseFromOpenAI(prompt);
        return MessageResponseDto.CreateAIMessageResponse.builder()
                .message(response)
                .build();
    }
}
