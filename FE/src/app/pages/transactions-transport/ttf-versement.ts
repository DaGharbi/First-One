import { DecimalPipe } from '@angular/common';
import { Component } from '@angular/core';
import { AttijariSessionPanel } from '../../shared/attijari-session-panel/attijari-session-panel';
import { DisplayDatePipe } from '../../shared/display-date.pipe';

@Component({
  selector: 'app-ttf-versement',
  standalone: true,
  imports: [AttijariSessionPanel, DecimalPipe, DisplayDatePipe],
  templateUrl: './ttf-versement.html',
  styleUrls: ['../styles/attijari-page.css'],
})
export class TtfVersement {
  readonly versements = [
    { agence: 'Agence Ariana', codeAgence: '1005', montant: 7600, date: '20-04-2026', type: 'Versement', statut: 'Validee' },
    { agence: 'Agence Bizerte', codeAgence: '1019', montant: 13200, date: '21-04-2026', type: 'Versement', statut: 'En attente' },
    { agence: 'Agence Sfax Ville', codeAgence: '1087', montant: 11400, date: '21-04-2026', type: 'Versement', statut: 'Validee' },
    { agence: 'Agence Kairouan', codeAgence: '1124', montant: 8900, date: '22-04-2026', type: 'Versement', statut: 'En attente' },
  ];
}
