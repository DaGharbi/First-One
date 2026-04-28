import { DecimalPipe } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AttijariSessionPanel } from '../../shared/attijari-session-panel/attijari-session-panel';
import { DisplayDatePipe } from '../../shared/display-date.pipe';
import { AuthService } from '../../services/auth.service';
import { CdeVersDelta, CdeVersDeltaService } from '../../services/cde-vers-delta.service';

@Component({
  selector: 'app-ttf-commande',
  standalone: true,
  imports: [AttijariSessionPanel, DecimalPipe, DisplayDatePipe, FormsModule],
  templateUrl: './ttf-commande.html',
  styleUrls: ['../styles/attijari-page.css', './ttf-commande.css'],
})
export class TtfCommande implements OnInit {
  operations: CdeVersDelta[] = [];
  deviseFilter: 'ALL' | 'TND' | 'DEVISE' = 'ALL';
  operationFilter: 'ALL' | 'COMMANDE' | 'VERSEMENT' | 'RESERVATION' = 'ALL';
  isLoading = false;
  errorMessage = '';

  constructor(
    private authService: AuthService,
    private cdeVersDeltaService: CdeVersDeltaService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadOperations();
  }

  get filteredOperations(): CdeVersDelta[] {
    return this.operations.filter((operation) => {
      const matchesDevise = this.deviseFilter === 'ALL'
        || (this.deviseFilter === 'TND' && operation.codeDevise === 'TND')
        || (this.deviseFilter === 'DEVISE' && operation.codeDevise !== 'TND');
      const matchesOperation = this.operationFilter === 'ALL'
        || (this.operationFilter === 'COMMANDE' && operation.cdeVers === 0)
        || (this.operationFilter === 'VERSEMENT' && operation.cdeVers === 1)
        || (this.operationFilter === 'RESERVATION' && operation.cdeVers === 2);

      return matchesDevise && matchesOperation;
    });
  }

  get activeFiltersCount(): number {
    return Number(this.deviseFilter !== 'ALL') + Number(this.operationFilter !== 'ALL');
  }

  resetFilters(): void {
    this.deviseFilter = 'ALL';
    this.operationFilter = 'ALL';
  }

  operationLabel(cdeVers: number): string {
    if (cdeVers === 2) {
      return 'Reservation';
    }

    return cdeVers === 1 ? 'Versement' : 'Commande';
  }

  private loadOperations(): void {
    const codeAgence = this.authService.getCurrentUser()?.codeAgence?.trim();
    if (!codeAgence) {
      this.errorMessage = 'Code agence introuvable dans la session.';
      this.cdr.detectChanges();
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
    this.cdr.detectChanges();

    this.cdeVersDeltaService.listForSameCaisseCentrale(codeAgence).subscribe({
      next: (operations) => {
        this.operations = operations;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        this.errorMessage = typeof error?.error === 'string'
          ? error.error
          : 'Impossible de charger les operations.';
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }
}
