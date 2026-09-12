package app.demo.backEnd.service.implmentation;

import app.demo.backEnd.service.EmailService;

import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    private static final String NOTIFICATION_EMAIL = "nizartarik994@gmail.com";

    // ============================================================
    // INSURANCE INTENT EMAIL
    // ============================================================

    @Override
    public void BrancheDetectEmail(
            String type,
            String userMessage,
            String aiReply,
            Map<String, Object> user) {

        try {

            String safeFirstName = "Unknown";
            String safeLastName = "";
            String safePhone = "Unknown";

            if (user != null) {

                Object firstNameObj = user.get("firstName");
                Object lastNameObj = user.get("lastName");
                Object phoneObj = user.get("phone");

                if (firstNameObj != null) {
                    safeFirstName = firstNameObj.toString();
                }

                if (lastNameObj != null) {
                    safeLastName = lastNameObj.toString();
                }

                if (phoneObj != null) {
                    safePhone = phoneObj.toString();
                }
            }

            String fullName = (safeFirstName + " " + safeLastName).trim();

            String safeType = type != null ? type : "UNKNOWN";

            String safeUserMessage = userMessage != null
                    ? userMessage
                    : "No message provided.";

            String safeAiReply = aiReply != null
                    ? aiReply
                    : "No AI response available.";

            MimeMessage mimeMessage = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    true,
                    "UTF-8");

            helper.setTo(NOTIFICATION_EMAIL);

            // English is the default email language
            helper.setSubject(
                    "Insurance Assistant - " + safeType);

            // ====================================================
            // WHATSAPP MESSAGES
            // ====================================================

            String whatsappMessageEn = "Hello " + fullName + ",%0A%0A"
                    + "Thank you for contacting our insurance assistant.%0A"
                    + "We have received your request regarding: "
                    + safeType
                    + ".%0A%0A"
                    + "Our team will contact you shortly "
                    + "to assist you and provide a solution "
                    + "adapted to your needs.%0A%0A"
                    + "Best regards,%0A"
                    + "The Insurance Team";

            String whatsappMessageFr = "Bonjour " + fullName + ",%0A%0A"
                    + "Merci d'avoir contacté notre assistant d'assurance.%0A"
                    + "Nous avons bien reçu votre demande concernant : "
                    + safeType
                    + ".%0A%0A"
                    + "Notre équipe vous contactera dans les plus brefs délais "
                    + "afin de vous accompagner et vous proposer une solution "
                    + "adaptée à vos besoins.%0A%0A"
                    + "Cordialement,%0A"
                    + "L'équipe d'assurance";

            String whatsappMessageAr = "مرحبا " + fullName + "،%0A%0A"
                    + "شكراً لتواصلكم مع مساعد التأمين الخاص بنا.%0A"
                    + "لقد توصلنا بطلبكم بخصوص : "
                    + safeType
                    + ".%0A%0A"
                    + "سيقوم فريقنا بالتواصل معكم في أقرب وقت "
                    + "لمساعدتكم واقتراح الحل المناسب لاحتياجاتكم.%0A%0A"
                    + "مع تحيات فريق التأمين";

            String whatsappMessageDe = "Hallo " + fullName + ",%0A%0A"
                    + "vielen Dank für Ihre Kontaktaufnahme mit unserem "
                    + "Versicherungsassistenten.%0A"
                    + "Wir haben Ihre Anfrage bezüglich "
                    + safeType
                    + " erhalten.%0A%0A"
                    + "Unser Team wird sich schnellstmöglich mit Ihnen in Verbindung setzen, "
                    + "um Sie zu unterstützen und eine passende Lösung für Ihre Bedürfnisse "
                    + "anzubieten.%0A%0A"
                    + "Mit freundlichen Grüßen,%0A"
                    + "Das Versicherungsteam";

            // ====================================================
            // EMAIL HTML
            // ====================================================

            String html = "<div style='"
                    + "font-family:Arial,sans-serif;"
                    + "background:#f4f7f9;"
                    + "padding:30px;"
                    + "'>"

                    + "<div style='"
                    + "max-width:750px;"
                    + "margin:auto;"
                    + "background:#ffffff;"
                    + "border-radius:14px;"
                    + "overflow:hidden;"
                    + "box-shadow:0 6px 20px rgba(0,0,0,0.08);"
                    + "'>"

                    // ====================================================
                    // HEADER
                    // ====================================================

                    + "<div style='"
                    + "background:linear-gradient(135deg,#0f766e,#10b981);"
                    + "color:white;"
                    + "padding:24px;"
                    + "text-align:center;"
                    + "'>"

                    + "<h2 style='margin:0;'>"
                    + "Insurance Assistant"
                    + "</h2>"

                    + "<p style='"
                    + "margin:6px 0 0;"
                    + "font-size:14px;"
                    + "opacity:0.9;"
                    + "'>"
                    + "New insurance inquiry"
                    + "</p>"

                    + "</div>"

                    // ====================================================
                    // BODY
                    // ====================================================

                    + "<div style='padding:25px;'>"

                    + "<h3 style='color:#0f766e;'>"
                    + "👤 Customer Information"
                    + "</h3>"

                    + "<p>"
                    + "<strong>Name:</strong> "
                    + fullName
                    + "</p>"

                    + "<p>"
                    + "<strong>Phone:</strong> "
                    + safePhone
                    + "</p>"

                    + "<p>"
                    + "<strong>Detected Interest:</strong> "
                    + safeType
                    + "</p>"

                    + "<hr style='"
                    + "border:none;"
                    + "border-top:1px solid #e5e7eb;"
                    + "margin:20px 0;"
                    + "'>"

                    // ====================================================
                    // USER MESSAGE
                    // ====================================================

                    + "<h3 style='color:#0f766e;'>"
                    + "💬 Customer Message"
                    + "</h3>"

                    + "<div style='"
                    + "background:#f1f5f9;"
                    + "padding:15px;"
                    + "border-radius:10px;"
                    + "line-height:1.6;"
                    + "'>"
                    + safeUserMessage
                    + "</div>"

                    // ====================================================
                    // AI RESPONSE
                    // ====================================================

                    + "<h3 style='color:#0f766e;margin-top:22px;'>"
                    + "🤖 AI Response"
                    + "</h3>"

                    + "<div style='"
                    + "background:#ecfdf5;"
                    + "padding:15px;"
                    + "border-radius:10px;"
                    + "line-height:1.6;"
                    + "'>"
                    + safeAiReply
                    + "</div>"

                    + "<hr style='"
                    + "border:none;"
                    + "border-top:1px solid #e5e7eb;"
                    + "margin:25px 0;"
                    + "'>"

                    // ====================================================
                    // WHATSAPP ACTIONS
                    // ====================================================

                    + "<h3 style='"
                    + "text-align:center;"
                    + "margin-bottom:15px;"
                    + "'>"
                    + "📲 Contact Customer"
                    + "</h3>"

                    + "<div style='"
                    + "text-align:center;"
                    + "'>"

                    // ENGLISH
                    + "<a href='https://wa.me/"
                    + safePhone
                    + "?text="
                    + whatsappMessageEn
                    + "' style='"
                    + "display:inline-block;"
                    + "width:240px;"
                    + "margin:8px;"
                    + "padding:14px 18px;"
                    + "background:#25D366;"
                    + "color:white;"
                    + "text-decoration:none;"
                    + "border-radius:8px;"
                    + "font-weight:bold;"
                    + "'>"
                    + "🇬🇧 Contact in English"
                    + "</a>"

                    // FRENCH
                    + "<a href='https://wa.me/"
                    + safePhone
                    + "?text="
                    + whatsappMessageFr
                    + "' style='"
                    + "display:inline-block;"
                    + "width:240px;"
                    + "margin:8px;"
                    + "padding:14px 18px;"
                    + "background:#25D366;"
                    + "color:white;"
                    + "text-decoration:none;"
                    + "border-radius:8px;"
                    + "font-weight:bold;"
                    + "'>"
                    + "🇫🇷 Contact en français"
                    + "</a>"

                    // ARABIC
                    + "<a href='https://wa.me/"
                    + safePhone
                    + "?text="
                    + whatsappMessageAr
                    + "' style='"
                    + "display:inline-block;"
                    + "width:240px;"
                    + "margin:8px;"
                    + "padding:14px 18px;"
                    + "background:#128C7E;"
                    + "color:#fff;"
                    + "text-decoration:none;"
                    + "border-radius:8px;"
                    + "font-weight:bold;"
                    + "direction:rtl;"
                    + "'>"
                    + "🇲🇦 التواصل بالعربية"
                    + "</a>"

                    // GERMAN
                    + "<a href='https://wa.me/"
                    + safePhone
                    + "?text="
                    + whatsappMessageDe
                    + "' style='"
                    + "display:inline-block;"
                    + "width:240px;"
                    + "margin:8px;"
                    + "padding:14px 18px;"
                    + "background:#25D366;"
                    + "color:#fff;"
                    + "text-decoration:none;"
                    + "border-radius:8px;"
                    + "font-weight:bold;"
                    + "'>"
                    + "🇩🇪 Kontakt auf Deutsch"
                    + "</a>"

                    + "</div>"

                    + "</div>"

                    // ====================================================
                    // FOOTER
                    // ====================================================

                    + "<div style='"
                    + "background:#0f766e;"
                    + "color:white;"
                    + "text-align:center;"
                    + "padding:12px;"
                    + "font-size:12px;"
                    + "'>"
                    + "Insurance AI Assistant"
                    + "</div>"

                    + "</div>"

                    + "</div>";

            helper.setText(html, true);

            mailSender.send(mimeMessage);

            System.out.println(
                    "INSURANCE INTENT EMAIL SENT SUCCESSFULLY");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ============================================================
    // NEW CHATBOT USER EMAIL
    // ============================================================

    @Override
    public void sendNewChatBotUseEmail(
            String firstName,
            String lastName,
            String phone) {

        try {

            String safeFirstName = firstName != null
                    ? firstName
                    : "Unknown";

            String safeLastName = lastName != null
                    ? lastName
                    : "Unknown";

            String safePhone = phone != null
                    ? phone
                    : "Unknown";

            String fullName = (safeFirstName + " " + safeLastName).trim();

            MimeMessage mimeMessage = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    true,
                    "UTF-8");

            helper.setTo(NOTIFICATION_EMAIL);

            // English is the default language
            helper.setSubject(
                    "Insurance AI Chatbot - New User - "
                            + fullName);

            // ====================================================
            // WHATSAPP MESSAGES
            // ====================================================

            String whatsappMessageEn = "Hello " + fullName + ",%0A%0A"
                    + "Thank you for using our insurance assistant.%0A"
                    + "Our team will contact you shortly "
                    + "to assist you with your request.%0A%0A"
                    + "Best regards,%0A"
                    + "The Insurance Team";

            String whatsappMessageFr = "Bonjour " + fullName + ",%0A%0A"
                    + "Merci d'avoir utilisé notre assistant d'assurance.%0A"
                    + "Notre équipe vous contactera dans les plus brefs délais.%0A%0A"
                    + "Cordialement,%0A"
                    + "L'équipe d'assurance";

            String whatsappMessageAr = "مرحبا " + fullName + "،%0A%0A"
                    + "شكراً لاستخدامكم مساعد التأمين الخاص بنا.%0A"
                    + "سيقوم فريقنا بالتواصل معكم في أقرب وقت.%0A%0A"
                    + "مع تحيات فريق التأمين";

            String whatsappMessageDe = "Hallo " + fullName + ",%0A%0A"
                    + "vielen Dank, dass Sie unseren Versicherungsassistenten "
                    + "verwendet haben.%0A"
                    + "Unser Team wird sich schnellstmöglich mit Ihnen in Verbindung setzen.%0A%0A"
                    + "Mit freundlichen Grüßen,%0A"
                    + "Das Versicherungsteam";

            // ====================================================
            // EMAIL HTML
            // ====================================================

            String html = "<div style='"
                    + "font-family:Arial,sans-serif;"
                    + "background:#f4f7f9;"
                    + "padding:30px;"
                    + "'>"

                    + "<div style='"
                    + "max-width:700px;"
                    + "margin:auto;"
                    + "background:#ffffff;"
                    + "border-radius:14px;"
                    + "overflow:hidden;"
                    + "box-shadow:0 6px 20px rgba(0,0,0,0.08);"
                    + "'>"

                    // ====================================================
                    // HEADER
                    // ====================================================

                    + "<div style='"
                    + "background:linear-gradient(135deg,#0f766e,#10b981);"
                    + "color:white;"
                    + "text-align:center;"
                    + "padding:22px;"
                    + "'>"

                    + "<h2 style='margin:0;'>"
                    + "Insurance AI Chatbot"
                    + "</h2>"

                    + "<p style='"
                    + "margin:6px 0 0;"
                    + "font-size:13px;"
                    + "opacity:0.9;"
                    + "'>"
                    + "New customer interaction"
                    + "</p>"

                    + "</div>"

                    // ====================================================
                    // BODY
                    // ====================================================

                    + "<div style='padding:25px;'>"

                    + "<h3 style='"
                    + "color:#0f766e;"
                    + "margin-bottom:12px;"
                    + "'>"
                    + "👤 Customer"
                    + "</h3>"

                    + "<p>"
                    + "<strong>Name:</strong> "
                    + fullName
                    + "</p>"

                    + "<p>"
                    + "<strong>Phone:</strong> "
                    + safePhone
                    + "</p>"

                    + "<hr style='"
                    + "border:none;"
                    + "border-top:1px solid #e5e7eb;"
                    + "margin:20px 0;"
                    + "'>"

                    // ====================================================
                    // STATUS
                    // ====================================================

                    + "<h3 style='"
                    + "color:#0f766e;"
                    + "margin-bottom:12px;"
                    + "'>"
                    + "📊 Status"
                    + "</h3>"

                    + "<div style='"
                    + "background:#ecfdf5;"
                    + "padding:12px;"
                    + "border-radius:8px;"
                    + "'>"
                    + "New interaction through the insurance chatbot."
                    + "</div>"

                    + "<hr style='"
                    + "border:none;"
                    + "border-top:1px solid #e5e7eb;"
                    + "margin:20px 0;"
                    + "'>"

                    // ====================================================
                    // ACTIONS
                    // ====================================================

                    + "<h3 style='"
                    + "color:#0f766e;"
                    + "text-align:center;"
                    + "'>"
                    + "📲 Contact Customer"
                    + "</h3>"

                    + "<div style='"
                    + "text-align:center;"
                    + "margin-top:12px;"
                    + "'>"

                    // ENGLISH
                    + "<a href='https://wa.me/"
                    + safePhone
                    + "?text="
                    + whatsappMessageEn
                    + "' style='"
                    + "display:inline-block;"
                    + "margin:5px;"
                    + "padding:12px 18px;"
                    + "background:#25D366;"
                    + "color:#fff;"
                    + "text-decoration:none;"
                    + "border-radius:8px;"
                    + "font-weight:bold;"
                    + "'>"
                    + "🇬🇧 WhatsApp English"
                    + "</a>"

                    // FRENCH
                    + "<a href='https://wa.me/"
                    + safePhone
                    + "?text="
                    + whatsappMessageFr
                    + "' style='"
                    + "display:inline-block;"
                    + "margin:5px;"
                    + "padding:12px 18px;"
                    + "background:#25D366;"
                    + "color:#fff;"
                    + "text-decoration:none;"
                    + "border-radius:8px;"
                    + "font-weight:bold;"
                    + "'>"
                    + "🇫🇷 WhatsApp Français"
                    + "</a>"

                    // ARABIC
                    + "<a href='https://wa.me/"
                    + safePhone
                    + "?text="
                    + whatsappMessageAr
                    + "' style='"
                    + "display:inline-block;"
                    + "margin:5px;"
                    + "padding:12px 18px;"
                    + "background:#128C7E;"
                    + "color:#fff;"
                    + "text-decoration:none;"
                    + "border-radius:8px;"
                    + "font-weight:bold;"
                    + "'>"
                    + "🇲🇦 العربية"
                    + "</a>"

                    // GERMAN
                    + "<a href='https://wa.me/"
                    + safePhone
                    + "?text="
                    + whatsappMessageDe
                    + "' style='"
                    + "display:inline-block;"
                    + "margin:5px;"
                    + "padding:12px 18px;"
                    + "background:#25D366;"
                    + "color:#fff;"
                    + "text-decoration:none;"
                    + "border-radius:8px;"
                    + "font-weight:bold;"
                    + "'>"
                    + "🇩🇪 WhatsApp Deutsch"
                    + "</a>"

                    + "</div>"

                    + "</div>"

                    // ====================================================
                    // FOOTER
                    // ====================================================

                    + "<div style='"
                    + "background:#0f766e;"
                    + "color:#fff;"
                    + "text-align:center;"
                    + "padding:12px;"
                    + "font-size:12px;"
                    + "'>"
                    + "Insurance AI System"
                    + "</div>"

                    + "</div>"

                    + "</div>";

            helper.setText(html, true);

            mailSender.send(mimeMessage);

            System.out.println(
                    "NEW CHATBOT USER EMAIL SENT SUCCESSFULLY");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}