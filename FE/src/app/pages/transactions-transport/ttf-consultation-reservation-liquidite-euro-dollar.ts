import { DecimalPipe } from '@angular/common';
import { Component } from '@angular/core';
import { AttijariSessionPanel } from '../../shared/attijari-session-panel/attijari-session-panel';
import { DisplayDatePipe } from '../../shared/display-date.pipe';

@Component({
  selector: 'app-ttf-consultation-reservation-liquidite-euro-dollar',
  standalone: true,
  imports: [AttijariSessionPanel, DecimalPipe, DisplayDatePipe],
  templateUrl: './ttf-consultation-reservation-liquidite-euro-dollar.html',
  styleUrls: ['../styles/attijari-page.css'],
})
export class TtfConsultationReservationLiquiditeEuroDollar {
  readonly reservationsEuroDollar = [
    { agence: 'Agence Charguia', codeAgence: '1030', montant: 5200, date: '20-04-2026', devise: 'EUR', type: 'Reservation', statut: 'Validee' },
    { agence: 'Agence El Menzah', codeAgence: '1036', montant: 7400, date: '21-04-2026', devise: 'USD', type: 'Reservation', statut: 'En attente' },
    { agence: 'Agence Sidi Bouzid', codeAgence: '1204', montant: 4900, date: '21-04-2026', devise: 'EUR', type: 'Reservation', statut: 'Validee' },
    { agence: 'Agence Gafsa', codeAgence: '1222', montant: 6100, date: '22-04-2026', devise: 'USD', type: 'Reservation', statut: 'Rejetee' },
  ];
}
