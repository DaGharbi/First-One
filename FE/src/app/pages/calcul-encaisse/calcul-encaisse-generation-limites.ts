import { DecimalPipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { finalize, forkJoin } from 'rxjs';
import { AuthService } from '../../services/auth.service';
import { AdminService, Agence } from '../../services/admin.service';
import {
  EncaisseMlService,
  EncaissePredictionResult,
} from '../../services/encaisse-ml.service';
import { displayDateToIso, tomorrowDisplayDate } from '../../shared/date-format';
import { DisplayDatePipe } from '../../shared/display-date.pipe';
import { EuropeanDateInput } from '../../shared/european-date-input/european-date-input';
import { AttijariSessionPanel } from '../../shared/attijari-session-panel/attijari-session-panel';

@Component({
  selector: 'app-calcul-encaisse-generation-limites',
  standalone: true,
  imports: [AttijariSessionPanel, FormsModule, DecimalPipe, DisplayDatePipe, EuropeanDateInput],
  templateUrl: './calcul-encaisse-generation-limites-view.html',
  styleUrls: ['../styles/attijari-page.css', './calcul-encaisse-generation-limites.css'],
})
export class CalculEncaisseGenerationLimites implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly adminService = inject(AdminService);
  private readonly encaisseMlService = inject(EncaisseMlService);
  private readonly currentUser = this.authService.getCurrentUser();

  readonly predictions = signal<EncaissePredictionResult[]>([]);
  readonly isLoading = signal(false);
  readonly errorMessage = signal('');

  private readonly sessionCodeAgence = this.currentUser?.codeAgence?.trim() ?? '';
  readonly isAgent = this.authService.isAgent();
  selectedAgence = this.sessionCodeAgence;
  selectedAgences: string[] = [];
  agenceSelectionMode: 'ALL' | 'CUSTOM' = 'ALL';
  caisseAgences: Agence[] = [];
  readonly selectedAgenceLabel = this.buildAgenceLabel();
  readonly tomorrow = tomorrowDisplayDate();
  startDate = this.tomorrow;
  endDate = this.tomorrow;

  constructor() {
    if (!this.selectedAgence) {
      this.errorMessage.set("Code agence introuvable dans la session utilisateur.");
    }
  }

  ngOnInit(): void {
    if (this.isAgent) {
      this.selectedAgences = [this.sessionCodeAgence].filter(Boolean);
      return;
    }

    this.loadAgencesForSameCaisseCentrale();
  }

  requestEstimation(): void {
    this.selectedAgence = this.isAgent ? this.sessionCodeAgence : this.selectedAgence.trim();
    const selectedCodes = this.getSelectedAgenceCodes();

    if (!selectedCodes.length || !this.startDate || !this.endDate || this.isLoading()) {
      return;
    }

    const startDateIso = displayDateToIso(this.startDate);
    const endDateIso = displayDateToIso(this.endDate);

    if (endDateIso < startDateIso) {
      this.errorMessage.set("La date de fin doit etre superieure ou egale a la date de debut.");
      this.predictions.set([]);
      return;
    }

    this.errorMessage.set('');
    this.predictions.set([]);
    this.isLoading.set(true);

    const requests = selectedCodes.map((codeAgence) =>
      this.encaisseMlService.estimate({
        codeAgence: this.normalizeAgenceCodeForMl(codeAgence),
        startDate: startDateIso,
        endDate: endDateIso,
      })
    );

    forkJoin(requests)
      .pipe(finalize(() => this.isLoading.set(false)))
      .subscribe({
        next: (results) => this.predictions.set(results),
        error: (error) => {
          const detail = error?.error?.detail;
          this.errorMessage.set(
            typeof detail === 'string'
              ? detail
              : 'Le service ML est indisponible pour le moment.',
          );
        },
      });
  }

  onAgenceSelectionModeChange(): void {
    if (this.agenceSelectionMode === 'ALL') {
      this.selectedAgences = this.caisseAgences.map((agence) => agence.codeAgence);
      return;
    }

    if (!this.selectedAgences.length && this.sessionCodeAgence) {
      this.selectedAgences = [this.sessionCodeAgence];
    }
  }

  onAgenceInputChange(): void {
    if (this.isAgent) {
      return;
    }

    if (this.errorMessage() === "Code agence introuvable dans la session utilisateur." && this.selectedAgence.trim()) {
      this.errorMessage.set('');
    }
  }

  predictionCards(result: EncaissePredictionResult): EncaissePredictionResult['predictions'] {
    return result.predictions ?? [];
  }

  getAgenceLabel(codeAgence: string): string {
    const normalizedCodeAgence = codeAgence.trim();
    const agence = this.caisseAgences.find((item) => item.codeAgence?.trim() === normalizedCodeAgence);

    if (agence?.libAgence) {
      return `${normalizedCodeAgence} - ${agence.libAgence}`;
    }

    return normalizedCodeAgence;
  }

  getSelectedAgenceCodes(): string[] {
    if (this.isAgent) {
      return [this.sessionCodeAgence].filter(Boolean);
    }

    if (this.agenceSelectionMode === 'ALL') {
      return this.caisseAgences.map((agence) => agence.codeAgence?.trim()).filter(Boolean);
    }

    return this.selectedAgences.map((codeAgence) => codeAgence.trim()).filter(Boolean);
  }

  private loadAgencesForSameCaisseCentrale(): void {
    if (!this.sessionCodeAgence) {
      return;
    }

    this.adminService.getAgences().subscribe({
      next: (agences) => {
        const connectedAgence = agences.find((agence) => agence.codeAgence?.trim() === this.sessionCodeAgence);
        const connectedCodeCaissCent = connectedAgence?.codeCaissCent?.trim();

        this.caisseAgences = connectedCodeCaissCent
          ? agences.filter((agence) => agence.codeCaissCent?.trim() === connectedCodeCaissCent)
          : [];
        this.selectedAgences = this.caisseAgences.map((agence) => agence.codeAgence);
      },
      error: () => {
        this.errorMessage.set('Impossible de charger les agences de la caisse centrale.');
      }
    });
  }

  private normalizeAgenceCodeForMl(codeAgence: string): string {
    const trimmed = codeAgence.trim();
    const numericPrefix = trimmed.match(/^\d+/)?.[0] ?? trimmed;
    const unpadded = numericPrefix.replace(/^0+(?=\d)/, '');
    return unpadded || '0';
  }

  private buildAgenceLabel(): string {
    const codeAgence = this.currentUser?.codeAgence?.trim() ?? '';
    const libAgence = this.currentUser?.libAgence?.trim() ?? '';

    if (codeAgence && libAgence) {
      return `${codeAgence} - ${libAgence}`;
    }

    return codeAgence || libAgence;
  }
}
