package app.demo.backEnd.controller;

import app.demo.backEnd.model.dto.InsuranceIntent;
import app.demo.backEnd.service.AiService;
import app.demo.backEnd.service.EmailService;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin("*")
public class ChatBotController {

        private final AiService aiService;
        private final EmailService emailService;

        public ChatBotController(AiService aiService, EmailService emailService) {
                this.aiService = aiService;
                this.emailService = emailService;
        }

        @PostMapping
        public InsuranceIntent chat(@RequestBody Map<String, Object> body) {

                String message = (String) body.get("message");
                Map<String, Object> user = (Map<String, Object>) body.get("user");

                InsuranceIntent intent = aiService.ask(message);

                String type = intent.type.toUpperCase();

                if (!"UNKNOWN".equals(type)) {

                        emailService.BrancheDetectEmail(
                                        type,
                                        message,
                                        intent.reply,
                                        user);
                } else {
                        System.out.println("⚠ No email sent (UNKNOWN intent)");
                }

                return intent;
        }
}