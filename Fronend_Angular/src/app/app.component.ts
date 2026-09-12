import { Component } from '@angular/core';
import { ChatbotPageComponent } from './chatbot-page/chatbot-page.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [ChatbotPageComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
}
