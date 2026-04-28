import { Component } from '@angular/core';
import { AttijariSessionPanel } from '../../shared/attijari-session-panel/attijari-session-panel';

@Component({
  selector: 'app-consultation',
  standalone: true,
  imports: [AttijariSessionPanel],
  templateUrl: './consultation.html',
  styleUrls: ['../styles/attijari-page.css'],
})
export class ConsultationPage {}
