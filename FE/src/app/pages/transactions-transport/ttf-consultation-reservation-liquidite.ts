import { DecimalPipe } from '@angular/common';
import { Component } from '@angular/core';
import { AttijariSessionPanel } from '../../shared/attijari-session-panel/attijari-session-panel';
import { DisplayDatePipe } from '../../shared/display-date.pipe';

@Component({
  selector: 'app-ttf-consultation-reservation-liquidite',
  standalone: true,
  imports: [AttijariSessionPanel, DecimalPipe, DisplayDatePipe],
  templateUrl: './ttf-consultation-reservation-liquidite.html',
  styleUrls: ['../styles/attijari-page.css'],
})
export class TtfConsultationReservationLiquidite {
  readonly reservations = [
    { agence: 'Agence Tunis Centre', codeAgence: '1000', montant: 22500, devise: 'TND', date: '20-04-2026', type: 'Reservation liquidite', statut: 'Validee' },
    { agence: 'Agence Hammamet', codeAgence: '1098', montant: 14000, devise: 'TND', date: '21-04-2026', type: 'Reservation liquidite', statut: 'En attente' },
    { agence: 'Agence Tozeur', codeAgence: '1231', montant: 8600, devise: 'TND', date: '21-04-2026', type: 'Reservation liquidite', statut: 'Validee' },
    { agence: 'Agence Mahdia', codeAgence: '1180', montant: 9700, devise: 'TND', date: '22-04-2026', type: 'Reservation liquidite', statut: 'Rejetee' },
    { agence: 'Agence Charguia', codeAgence: '1030', montant: 5200, devise: 'EUR', date: '20-04-2026', type: 'Reservation liquidite', statut: 'Validee' },
    { agence: 'Agence El Menzah', codeAgence: '1036', montant: 7400, devise: 'USD', date: '21-04-2026', type: 'Reservation liquidite', statut: 'En attente' },
    { agence: 'Agence Sidi Bouzid', codeAgence: '1204', montant: 4900, devise: 'EUR', date: '21-04-2026', type: 'Reservation liquidite', statut: 'Validee' },
    { agence: 'Agence Gafsa', codeAgence: '1222', montant: 6100, devise: 'USD', date: '22-04-2026', type: 'Reservation liquidite', statut: 'Rejetee' },
  ];
}
