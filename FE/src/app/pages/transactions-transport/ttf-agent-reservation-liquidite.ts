import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AttijariSessionPanel } from '../../shared/attijari-session-panel/attijari-session-panel';
import { Devise, DeviseService } from '../../services/devise.service';
import { EuropeanDateInput } from '../../shared/european-date-input/european-date-input';
import { tomorrowDisplayDate } from '../../shared/date-format';

@Component({
  selector: 'app-ttf-agent-reservation-liquidite',
  standalone: true,
  imports: [AttijariSessionPanel, FormsModule, EuropeanDateInput],
  templateUrl: './ttf-agent-reservation-liquidite.html',
  styleUrls: ['../styles/attijari-page.css', './ttf-agent-forms.css'],
})
export class TtfAgentReservationLiquidite implements OnInit {
  montant = '';
  dateOperation = tomorrowDisplayDate();
  typeReservation = 'Reservation';
  categorieDemande = 'Besoin d agence';
  devise = 'TND';
  commentaire = '';
  devises: Devise[] = [];

  clientNom = '';
  clientCompte = '';
  clientDate = tomorrowDisplayDate();

  constructor(private deviseService: DeviseService) {}

  ngOnInit(): void {
    this.deviseService.listDevises().subscribe({
      next: (devises) => {
        this.devises = this.withDefaultDevise(devises);

        if (!this.devises.some((devise) => devise.codeDevise === this.devise)) {
          this.devise = this.devises[0]?.codeDevise ?? '';
        }
      },
      error: () => {
        this.devises = this.withDefaultDevise([]);
      }
    });
  }

  private withDefaultDevise(devises: Devise[]): Devise[] {
    return devises.some((devise) => devise.codeDevise?.trim().toUpperCase() === 'TND')
      ? devises
      : [{ codeDevise: 'TND', libDevise: 'Tunisian Dinar' }, ...devises];
  }
}
