import { DecimalPipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AttijariSessionPanel } from '../../shared/attijari-session-panel/attijari-session-panel';
import { Devise, DeviseService } from '../../services/devise.service';
import { DisplayDatePipe } from '../../shared/display-date.pipe';

@Component({
  selector: 'app-ttf-commande-versement-devises',
  standalone: true,
  imports: [AttijariSessionPanel, FormsModule, DecimalPipe, DisplayDatePipe],
  templateUrl: './ttf-commande-versement-devises.html',
  styleUrls: ['../styles/attijari-page.css', './ttf-commande-versement-devises.css'],
})
export class TtfCommandeVersementDevises implements OnInit {
  amount = '';
  requestType = 'gab';
  currency = 'TND';
  operation = 'commande';
  confirmed = false;
  devises: Devise[] = [];

  readonly requestTypes = ['gab', 'grand montant', 'besoin'];
  readonly operations = ['commande', 'versement'];
  readonly operationsDevises = [
    { agence: 'Agence El Manar', codeAgence: '1039', montant: 3500, devise: 'GBP', date: '20-04-2026', type: 'Commande', statut: 'Validee' },
    { agence: 'Agence Centre Urbain Nord', codeAgence: '1057', montant: 4100, devise: 'CHF', date: '21-04-2026', type: 'Versement', statut: 'En attente' },
    { agence: 'Agence Ben Arous', codeAgence: '1132', montant: 2900, devise: 'CAD', date: '21-04-2026', type: 'Commande', statut: 'Validee' },
    { agence: 'Agence Kef', codeAgence: '1195', montant: 2700, devise: 'SAR', date: '22-04-2026', type: 'Versement', statut: 'Rejetee' },
  ];

  constructor(private deviseService: DeviseService) {}

  ngOnInit(): void {
    this.deviseService.listDevises().subscribe({
      next: (devises) => {
        this.devises = devises;

        if (!this.devises.some((devise) => devise.codeDevise === this.currency)) {
          this.currency = this.devises[0]?.codeDevise ?? '';
        }
      },
      error: () => {
        this.devises = [];
      }
    });
  }
}
