import { CommonModule } from '@angular/common';
import { Component, HostListener, NgZone } from '@angular/core';
import {
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import Swal from 'sweetalert2';
import { ChatbotApiService } from './chatbot-api.service';
import { ContactInfo, ProspectService } from '../services/prospect.service';

interface ChatMessage {
  author: 'client' | 'assistant';
  text?: string;
  audioUrl?: string;
  time: string;
}

export type SupportedLang = 'en' | 'fr' | 'ar' | 'de';

type AudienceType = 'particulier' | 'professionnel';

interface ChatCopy {
  welcomeUser: (name: string) => string;
  prompts: Record<AudienceType, string[]>;
}

const CHAT_COPY: Record<SupportedLang, ChatCopy> = {

  // ============================================================
  // ENGLISH — DEFAULT
  // ============================================================

  en: {
    welcomeUser: (name) =>
      `Welcome ${name}, how can I help you today?`,

    prompts: {
      particulier: [
        'What documents are needed in case of a claim?',
        'What should I do after a claim?',
        'Which personal insurance options are available?',
        'Who are your partners?'
      ],

      professionnel: [
        'Which insurance is best for my business?',
        'How can I protect my employees?',
        'What documents are needed for a professional claim?',
        'Who are your professional partners?'
      ]
    }
  },

  // ============================================================
  // FRENCH
  // ============================================================

  fr: {
    welcomeUser: (name) =>
      `Bienvenue ${name}, comment puis-je vous aider aujourd'hui ?`,

    prompts: {
      particulier: [
        'En cas de sinistre, quelles sont les pièces à fournir ?',
        'Que faire après un sinistre ?',
        'Quelles assurances pour les particuliers ?',
        'Qui sont vos partenaires ?'
      ],

      professionnel: [
        'Quelles assurances pour mon entreprise ?',
        'Comment protéger mes salariés ?',
        'Quelles pièces fournir pour un sinistre professionnel ?',
        'Qui sont vos partenaires professionnels ?'
      ]
    }
  },

  // ============================================================
  // ARABIC
  // ============================================================

  ar: {
    welcomeUser: (name) =>
      `مرحباً ${name}، كيف يمكنني مساعدتك اليوم؟`,

    prompts: {
      particulier: [
        'ما هي الوثائق المطلوبة في حالة وقوع حادث؟',
        'ماذا أفعل بعد وقوع حادث؟',
        'ما هي تأمينات الأفراد المتاحة؟',
        'من هم شركاؤكم؟'
      ],

      professionnel: [
        'ما التأمين المناسب لشركتي؟',
        'كيف أحمي موظفي شركتي؟',
        'ما الوثائق المطلوبة لحادث مهني؟',
        'من هم شركاؤكم المهنيون؟'
      ]
    }
  },

  // ============================================================
  // GERMAN
  // ============================================================

  de: {
    welcomeUser: (name) =>
      `Willkommen ${name}, wie kann ich Ihnen heute helfen?`,

    prompts: {
      particulier: [
        'Welche Dokumente werden im Schadensfall benötigt?',
        'Was sollte ich nach einem Schaden tun?',
        'Welche Versicherungen gibt es für Privatpersonen?',
        'Wer sind Ihre Partner?'
      ],

      professionnel: [
        'Welche Versicherung ist für mein Unternehmen geeignet?',
        'Wie kann ich meine Mitarbeiter schützen?',
        'Welche Dokumente werden bei einem beruflichen Schaden benötigt?',
        'Wer sind Ihre Geschäftspartner?'
      ]
    }
  }
};

@Component({
  selector: 'app-chatbot-page',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule
  ],
  templateUrl: './chatbot-page.component.html',
  styleUrl: './chatbot-page.component.css'
})
export class ChatbotPageComponent {

  constructor(
    private chatbotApiService: ChatbotApiService,
    private prospectService: ProspectService,
    private ngZone: NgZone
  ) { }

  // ============================================================
  // CHAT STATE
  // ============================================================

  isChatVisible = false;
  showPopup = false;
  isChatUnlocked = false;

  // ============================================================
  // LANGUAGE / AUDIENCE
  // ============================================================

  selectedLang: SupportedLang = 'en';
  selectedAudience: AudienceType = 'particulier';

  // ============================================================
  // CONTACT FORM
  // ============================================================

  contactForm = new FormGroup({
    firstName: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required]
    }),

    lastName: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required]
    }),

    phone: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required]
    })
  });

  contactInfo: ContactInfo | null = null;

  // ============================================================
  // CHAT
  // ============================================================

  newMessage = '';
  isTyping = false;
  isStartingChat = false;

  prompts: string[] = this.getPrompts();

  /*
   * No initial welcome message here.
   * The welcome message is added only after the user
   * completes the form and the chat is unlocked.
   */
  messages: ChatMessage[] = [];

  // ============================================================
  // VOICE RECOGNITION
  // ============================================================

  recognition: any;
  isListening = false;
  isVoiceStarting = false;

  async startVoiceRecognition(): Promise<void> {

    if (!this.isChatUnlocked) {
      this.showPopup = true;
      return;
    }

    if (this.isListening || this.isVoiceStarting) {
      return;
    }

    const SpeechRecognition =
      (window as any).SpeechRecognition ||
      (window as any).webkitSpeechRecognition;

    if (!SpeechRecognition) {
      Swal.fire({
        icon: 'info',
        title: 'Voice unavailable',
        text: 'Voice recognition is not supported in this browser.',
        confirmButtonText: 'OK'
      });

      return;
    }

    this.isVoiceStarting = true;

    const hasMicrophoneAccess =
      await this.requestMicrophoneAccess();

    if (!hasMicrophoneAccess || !this.isVoiceStarting) {
      this.isVoiceStarting = false;
      this.isListening = false;
      return;
    }

    const recognition = new SpeechRecognition();

    this.recognition = recognition;

    recognition.lang =
      this.selectedLang === 'ar'
        ? 'ar-MA'
        : this.selectedLang === 'fr'
          ? 'fr-FR'
          : this.selectedLang === 'de'
            ? 'de-DE'
            : 'en-US';

    recognition.continuous = false;
    recognition.interimResults = false;
    recognition.maxAlternatives = 1;

    recognition.onstart = () => {

      if (this.recognition !== recognition) {
        return;
      }

      this.ngZone.run(() => {
        this.isVoiceStarting = false;
        this.isListening = true;
      });
    };

    recognition.onresult = (event: any) => {

      if (this.recognition !== recognition) {
        return;
      }

      const transcript =
        event.results[0][0].transcript;

      console.log(
        '🎤 Recognized text:',
        transcript
      );

      this.ngZone.run(() => {
        this.newMessage = transcript;
      });
    };

    recognition.onerror = (event: any) => {

      if (this.recognition !== recognition) {
        return;
      }

      this.ngZone.run(() => {

        console.warn(
          'Speech recognition error:',
          event.error
        );

        this.isVoiceStarting = false;
        this.isListening = false;
        this.recognition = null;

        if (event.error === 'not-allowed') {

          this.showVoicePermissionMessage(
            'Microphone blocked',
            'Allow microphone access in your browser site settings. If you are using a local network URL, open the app on localhost or use HTTPS.'
          );
        }
      });
    };

    recognition.onend = () => {

      if (this.recognition !== recognition) {
        return;
      }

      this.ngZone.run(() => {

        this.isVoiceStarting = false;
        this.isListening = false;
        this.recognition = null;
      });
    };

    try {

      recognition.start();

      this.isVoiceStarting = false;
      this.isListening = true;

    } catch {

      this.isVoiceStarting = false;
      this.isListening = false;
      this.recognition = null;
    }
  }

  // ============================================================
  // MICROPHONE ACCESS
  // ============================================================

  private async requestMicrophoneAccess(): Promise<boolean> {

    if (!window.isSecureContext) {

      this.showVoicePermissionMessage(
        'Secure connection required',
        'Voice input requires HTTPS or localhost. Please open the application using localhost or a secure HTTPS connection.'
      );

      return false;
    }

    if (!navigator.mediaDevices?.getUserMedia) {

      this.showVoicePermissionMessage(
        'Microphone unavailable',
        'This browser cannot access microphone devices. Please try Chrome or Edge.'
      );

      return false;
    }

    try {

      const stream =
        await navigator.mediaDevices.getUserMedia({
          audio: true
        });

      stream
        .getTracks()
        .forEach(track => track.stop());

      return true;

    } catch (error: any) {

      const errorName = error?.name;

      if (
        errorName === 'NotAllowedError' ||
        errorName === 'PermissionDeniedError'
      ) {

        this.showVoicePermissionMessage(
          'Microphone permission denied',
          'Click the lock icon in the address bar, set Microphone to Allow, then refresh the page.'
        );

      } else if (
        errorName === 'NotFoundError' ||
        errorName === 'DevicesNotFoundError'
      ) {

        this.showVoicePermissionMessage(
          'No microphone found',
          'Connect or enable a microphone, then try again.'
        );

      } else {

        this.showVoicePermissionMessage(
          'Microphone unavailable',
          'The browser could not start microphone access. Check your browser and system microphone settings.'
        );
      }

      return false;
    }
  }

  private showVoicePermissionMessage(
    title: string,
    text: string
  ): void {

    Swal.fire({
      icon: 'warning',
      title,
      text,
      confirmButtonText: 'OK'
    });
  }

  stopVoiceRecognition(): void {

    if (this.recognition) {

      try {
        this.recognition.stop();
      } catch {
        this.recognition = null;
      }
    }

    this.isVoiceStarting = false;
    this.isListening = false;
  }

  // ============================================================
  // DOCUMENT CLICK
  // ============================================================

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {

    const target =
      event.target as HTMLElement | null;

    if (target?.closest('.voice-toggle-btn')) {
      return;
    }

    this.isVoiceStarting = false;
  }

  // ============================================================
  // OPEN / CLOSE CHAT
  // ============================================================

  openChatbot(): void {

    this.isChatVisible = true;
    this.showPopup = !this.isChatUnlocked;
  }

  closeChatbot(): void {

    this.stopVoiceRecognition();

    this.isChatVisible = false;
    this.showPopup = false;
  }

  // ============================================================
  // LANGUAGE
  // ============================================================

  setLanguage(lang: SupportedLang): void {

    this.selectedLang = lang;

    this.updatePrompts();
  }

  // ============================================================
  // AUDIENCE
  // ============================================================

  setAudience(audience: AudienceType): void {

    this.selectedAudience = audience;

    this.updatePrompts();
  }

  // ============================================================
  // START CHAT
  // ============================================================

  startChat(): void {

    if (this.contactForm.invalid) {

      this.contactForm.markAllAsTouched();

      Swal.fire({
        icon: 'warning',
        title: 'Missing information',
        text: 'Please fill in all required fields.',
        confirmButtonText: 'OK'
      });

      return;
    }

    const contactInfo =
      this.contactForm.getRawValue();

    this.isStartingChat = true;

    this.prospectService
      .submitContactInfo(contactInfo)
      .subscribe({

        next: () => {

          this.unlockChat(contactInfo);

          this.isStartingChat = false;
        },

        error: () => {

          Swal.fire({
            icon: 'error',
            title: 'Error',
            text: 'Unable to save your information. Please try again.',
            confirmButtonText: 'OK'
          });

          this.isStartingChat = false;
        }
      });
  }

  // ============================================================
  // UNLOCK CHAT
  // ============================================================

  private unlockChat(contactInfo: ContactInfo): void {

    this.contactInfo = contactInfo;

    this.showPopup = false;
    this.isChatUnlocked = true;

    const translation =
      CHAT_COPY[this.selectedLang];

    const displayName =
      this.getContactDisplayName(contactInfo);

    /*
     * ONE welcome message only.
     */
    this.messages = [
      {
        author: 'assistant',
        text: translation.welcomeUser(displayName),
        time: this.currentTime
      }
    ];

    Swal.fire({
      icon: 'success',
      title: 'Welcome',
      text: `Welcome ${displayName}!`,
      timer: 1800,
      showConfirmButton: false
    });
  }

  // ============================================================
  // CONTACT NAME
  // ============================================================

  private getContactDisplayName(
    contactInfo: ContactInfo
  ): string {

    return `${contactInfo.firstName} ${contactInfo.lastName}`.trim();
  }

  // ============================================================
  // PROMPTS
  // ============================================================

  private updatePrompts(): void {

    this.prompts = this.getPrompts();
  }

  private getPrompts(): string[] {

    return CHAT_COPY[
      this.selectedLang
    ].prompts[
      this.selectedAudience
    ];
  }

  // ============================================================
  // VOICE TOGGLE
  // ============================================================

  toggleVoice(event?: MouseEvent): void {

    event?.stopPropagation();

    if (
      this.isListening ||
      this.isVoiceStarting
    ) {

      this.stopVoiceRecognition();

    } else {

      void this.startVoiceRecognition();
    }
  }

  // ============================================================
  // SEND MESSAGE
  // ============================================================

  sendMessage(): void {

    if (!this.isChatUnlocked) {

      this.showPopup = true;
      return;
    }

    const text =
      this.newMessage.trim();

    if (!text || this.isTyping) {
      return;
    }

    const contactInfo =
      this.contactInfo;

    if (!contactInfo) {

      this.showPopup = true;
      return;
    }

    this.messages.push({
      author: 'client',
      text,
      time: this.currentTime
    });

    this.newMessage = '';
    this.isTyping = true;

    this.chatbotApiService
      .sendMessage({
        message: text,
        user: contactInfo,
        lang: this.selectedLang
      })
      .subscribe({

        next: (res) => {

          this.messages.push({
            author: 'assistant',
            text: res.reply ?? res.message,
            time: this.currentTime
          });

          this.isTyping = false;
        },

        error: () => {

          this.messages.push({
            author: 'assistant',
            text: 'Sorry, something went wrong. Please try again later.',
            time: this.currentTime
          });

          this.isTyping = false;
        }
      });
  }

  // ============================================================
  // QUICK PROMPT
  // ============================================================

  usePrompt(prompt: string): void {

    if (this.isTyping) {
      return;
    }

    this.newMessage = prompt;

    this.sendMessage();
  }

  // ============================================================
  // CURRENT TIME
  // ============================================================

  private get currentTime(): string {

    return new Intl.DateTimeFormat(
      'en',
      {
        hour: '2-digit',
        minute: '2-digit',
        hour12: false
      }
    ).format(new Date());
  }
}
