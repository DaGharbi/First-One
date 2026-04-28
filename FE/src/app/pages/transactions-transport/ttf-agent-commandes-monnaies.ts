import { DecimalPipe } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs';
import { AttijariSessionPanel } from '../../shared/attijari-session-panel/attijari-session-panel';
import { EuropeanDateInput } from '../../shared/european-date-input/european-date-input';
import { DisplayDatePipe } from '../../shared/display-date.pipe';
import { tomorrowDisplayDate } from '../../shared/date-format';
import { AuthService } from '../../services/auth.service';
import { CdeMonnaies, CdeMonnaiesRequest, CdeMonnaiesService } from '../../services/cde-monnaies.service';

@Component({
  selector: 'app-ttf-agent-commandes-monnaies',
  standalone: true,
  imports: [AttijariSessionPanel, FormsModule, EuropeanDateInput, DecimalPipe, DisplayDatePipe],
  templateUrl: './ttf-agent-commandes-monnaies.html',
  styleUrls: ['../styles/attijari-page.css', './ttf-agent-forms.css'],
})
export class TtfAgentCommandesMonnaies implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly cdeMonnaiesService = inject(CdeMonnaiesService);
  private readonly cdr = inject(ChangeDetectorRef);
  private readonly currentUser = this.authService.getCurrentUser();

  dateOperation = tomorrowDisplayDate();
  readonly codeAgence = this.currentUser?.codeAgence?.trim() ?? '';
  readonly agenceLabel = this.currentUser?.libAgence || this.codeAgence || 'Agence non definie';
  readonly codeDevise = 'TND';

  p100M = 0;
  p10M = 0;
  p1Dn = 0;
  p20M = 0;
  p500M = 0;
  p50M = 0;
  p5M = 0;

  isSaving = false;
  isFormVisible = false;
  isLoadingTable = false;
  feedbackMessage = '';
  errorMessage = '';
  tableErrorMessage = '';
  commandesMonnaies: CdeMonnaies[] = [];

  ngOnInit(): void {
    this.loadCommandes();
  }

  openNewForm(): void {
    this.newForm();
    this.isFormVisible = true;
  }

  closeForm(): void {
    this.isFormVisible = false;
  }

  save(): void {
    if (!this.codeAgence) {
      this.errorMessage = 'Code agence introuvable dans la session.';
      return;
    }

    if (!this.dateOperation.trim()) {
      this.errorMessage = 'Date obligatoire.';
      return;
    }

    this.isSaving = true;
    this.feedbackMessage = '';
    this.errorMessage = '';

    this.cdeMonnaiesService.create(this.buildPayload())
      .pipe(finalize(() => {
        this.isSaving = false;
        this.cdr.detectChanges();
      }))
      .subscribe({
      next: () => {
        this.feedbackMessage = 'Commande monnaies enregistree.';
        this.resetForm();
        this.isFormVisible = false;
        this.loadCommandes();
        this.cdr.detectChanges();
      },
      error: (error) => {
        this.errorMessage = typeof error?.error === 'string'
          ? error.error
          : 'Impossible d enregistrer la commande monnaies.';
        this.cdr.detectChanges();
      }
    });
  }

  newForm(): void {
    this.feedbackMessage = '';
    this.errorMessage = '';
    this.resetForm();
  }

  get agencyCommandesMonnaies(): CdeMonnaies[] {
    return this.commandesMonnaies.filter((commande) => commande.codeAgence?.trim() === this.codeAgence);
  }

  totalPieces(item: CdeMonnaies): number {
    return item.p100M + item.p10M + item.p1Dn + item.p20M + item.p500M + item.p50M + item.p5M;
  }

  private buildPayload(): CdeMonnaiesRequest {
    return {
      codeAgence: this.codeAgence,
      dateCdeMonnaies: this.dateOperation,
      p100M: this.p100M || 0,
      p10M: this.p10M || 0,
      p1Dn: this.p1Dn || 0,
      p20M: this.p20M || 0,
      p500M: this.p500M || 0,
      p50M: this.p50M || 0,
      p5M: this.p5M || 0,
    };
  }

  private resetAmounts(): void {
    this.p100M = 0;
    this.p10M = 0;
    this.p1Dn = 0;
    this.p20M = 0;
    this.p500M = 0;
    this.p50M = 0;
    this.p5M = 0;
  }

  private resetForm(): void {
    this.dateOperation = tomorrowDisplayDate();
    this.resetAmounts();
  }

  private loadCommandes(): void {
    if (!this.codeAgence) {
      this.tableErrorMessage = 'Code agence introuvable dans la session.';
      return;
    }

    this.isLoadingTable = true;
    this.tableErrorMessage = '';

    this.cdeMonnaiesService.listForAgency(this.codeAgence)
      .pipe(finalize(() => {
        this.isLoadingTable = false;
        this.cdr.detectChanges();
      }))
      .subscribe({
        next: (commandes) => {
          this.commandesMonnaies = commandes;
          this.cdr.detectChanges();
        },
        error: (error) => {
          this.tableErrorMessage = typeof error?.error === 'string'
            ? error.error
            : 'Impossible de charger les commandes monnaies.';
          this.cdr.detectChanges();
        }
      });
  }
}
