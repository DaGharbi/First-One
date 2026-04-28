import { Component } from '@angular/core';
import { AttijariSessionPanel } from '../../shared/attijari-session-panel/attijari-session-panel';

@Component({
  selector: 'app-calcul-encaisse-consultation-limites',
  standalone: true,
  imports: [AttijariSessionPanel],
  templateUrl: './calcul-encaisse-consultation-limites.html',
  styleUrls: ['../styles/attijari-page.css'],
})
export class CalculEncaisseConsultationLimites {}
