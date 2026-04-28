import { DecimalPipe } from '@angular/common';
import { Component } from '@angular/core';
import { AttijariSessionPanel } from '../../shared/attijari-session-panel/attijari-session-panel';
import { DisplayDatePipe } from '../../shared/display-date.pipe';

@Component({
  selector: 'app-ttf-commande-versement-devises-euro-dollar',
  standalone: true,
  imports: [AttijariSessionPanel, DecimalPipe, DisplayDatePipe],
  templateUrl: './ttf-commande-versement-devises-euro-dollar.html',
  styleUrls: ['../styles/attijari-page.css'],
})
export class TtfCommandeVersementDevisesEuroDollar {
  readonly operationsEuroDollar = [
    { agence: 'Agence Marsa', codeAgence: '1048', montant: 6800, date: '20-04-2026', devise: 'EUR', type: 'Commande', statut: 'Validee' },
    { agence: 'Agence La Soukra', codeAgence: '1077', montant: 5600, date: '21-04-2026', devise: 'USD', type: 'Versement', statut: 'En attente' },
    { agence: 'Agence Sfax Nord', codeAgence: '1115', montant: 8200, date: '21-04-2026', devise: 'EUR', type: 'Commande', statut: 'Validee' },
    { agence: 'Agence Djerba Midoun', codeAgence: '1241', montant: 4700, date: '22-04-2026', devise: 'USD', type: 'Versement', statut: 'Rejetee' },
  ];
}
