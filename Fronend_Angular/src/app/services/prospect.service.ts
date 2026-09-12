import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';


export interface ContactInfo {
  firstName: string;
  lastName: string;
  phone: string;
}

@Injectable({
  providedIn: 'root'
})


export class ProspectService {

  private readonly apiUrl = 'http://localhost:8080/api/prospect';

  constructor(private http: HttpClient) { }

  submitContactInfo(contactInfo: ContactInfo): Observable<unknown> {
    return this.http.post<unknown>(`${this.apiUrl}`, contactInfo);
  }

}
