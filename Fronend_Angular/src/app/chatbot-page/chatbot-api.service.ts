import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ContactInfo } from '../services/prospect.service';


export interface ChatRequest {
  message: string;
  user: ContactInfo;
  lang: string;
}

export interface ChatResponse {
  reply?: string;
  message?: string;
}

@Injectable({
  providedIn: 'root',
})
export class ChatbotApiService {
  private readonly apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) { }

  sendMessage(request: ChatRequest): Observable<ChatResponse> {
    return this.http.post<ChatResponse>(`${this.apiUrl}/chat`, request);
  }


}
