package app.demo.backEnd.service;

import java.util.Map;

public interface EmailService {

    void BrancheDetectEmail(
            String type,
            String userMessage,
            String aiReply,
            Map<String, Object> user);

    void sendNewChatBotUseEmail(
            String firstName,
            String lastName,
            String phone);
}
