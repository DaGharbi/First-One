import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs';
import { AttijariSessionPanel } from '../../shared/attijari-session-panel/attijari-session-panel';
import { Devise, DeviseService } from '../../services/devise.service';
import { EuropeanDateInput } from '../../shared/european-date-input/european-date-input';
import { DisplayDatePipe } from '../../shared/display-date.pipe';
import { tomorrowDisplayDate } from '../../shared/date-format';
import { AuthService } from '../../services/auth.service';
import { CdeVersDelta, CdeVersDeltaRequest, CdeVersDeltaService } from '../../services/cde-vers-delta.service';

@Component({
  selector: 'app-ttf-agent-commandes-versements',
  standalone: true,
  imports: [AttijariSessionPanel, FormsModule, EuropeanDateInput, DisplayDatePipe],
  templateUrl: './ttf-agent-commandes-versements.html',
  styleUrls: ['../styles/attijari-page.css', './ttf-agent-forms.css'],
})
export class TtfAgentCommandesVersements implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly cdeVersDeltaService = inject(CdeVersDeltaService);
  private readonly cdr = inject(ChangeDetectorRef);
  private readonly currentUser = this.authService.getCurrentUser();

  montant = '';
  dateOperation = tomorrowDisplayDate();
  operation: 'Commande' | 'Versement' | 'Reservation' = 'Commande';
  naturePass = 'Besoin d agence';
  descriptionClient = '';
  isBillets = false;
  devise = 'TND';
  devises: Devise[] = [{ codeDevise: 'TND', libDevise: 'Tunisian Dinar' }];
  readonly codeAgence = this.currentUser?.codeAgence?.trim() ?? '';
  readonly agenceLabel = this.currentUser?.libAgence || this.codeAgence || 'Agence non definie';

  isSaving = false;
  isFormVisible = false;
  isLoadingTable = false;
  feedbackMessage = '';
  errorMessage = '';
  tableErrorMessage = '';
  operations: CdeVersDelta[] = [];

  constructor(private deviseService: DeviseService) {}

  ngOnInit(): void {
    this.loadOperations();

    this.deviseService.listDevises().subscribe({
      next: (devises) => {
        this.devises = this.withDefaultDevise(devises);

        if (!this.devises.some((devise) => devise.codeDevise?.trim().toUpperCase() === this.devise.trim().toUpperCase())) {
          this.devise = this.devises[0]?.codeDevise ?? '';
        }
        this.cdr.detectChanges();
      },
      error: () => {
        this.devises = this.withDefaultDevise([]);
        this.cdr.detectChanges();
      }
    });
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

    if (!this.devise.trim()) {
      this.errorMessage = 'Devise obligatoire.';
      return;
    }

    this.isSaving = true;
    this.feedbackMessage = '';
    this.errorMessage = '';

    this.cdeVersDeltaService.create(this.buildPayload())
      .pipe(finalize(() => {
        this.isSaving = false;
        this.cdr.detectChanges();
      }))
      .subscribe({
      next: () => {
        this.feedbackMessage = 'Operation enregistree.';
        this.resetForm();
        this.isFormVisible = false;
        this.loadOperations();
        this.cdr.detectChanges();
      },
      error: (error) => {
        this.errorMessage = typeof error?.error === 'string'
          ? error.error
          : 'Impossible d enregistrer l operation.';
        this.cdr.detectChanges();
      }
    });
  }

  newForm(): void {
    this.feedbackMessage = '';
    this.errorMessage = '';
    this.resetForm();
  }

  get agencyOperations(): CdeVersDelta[] {
    return this.operations.filter((operation) => operation.codeAgence?.trim() === this.codeAgence);
  }

  operationLabel(cdeVers: number): string {
    if (cdeVers === 2) {
      return 'Reservation';
    }

    return cdeVers === 1 ? 'Versement' : 'Commande';
  }

  private buildPayload(): CdeVersDeltaRequest {
    return {
      codeAgence: this.codeAgence,
      codeDevise: this.devise,
      datePass: this.dateOperation,
      naturePass: this.naturePass,
      descriptionClient: this.naturePass === 'Gros montant' ? this.descriptionClient : '',
      isBillets: this.isBillets,
      montant: Number(this.montant || 0),
      cdeVers: this.operation === 'Reservation' ? 2 : this.operation === 'Versement' ? 1 : 0,
    };
  }

  private withDefaultDevise(devises: Devise[]): Devise[] {
    return devises.some((devise) => devise.codeDevise?.trim().toUpperCase() === 'TND')
      ? devises
      : [{ codeDevise: 'TND', libDevise: 'Tunisian Dinar' }, ...devises];
  }

  private resetForm(): void {
    this.montant = '';
    this.dateOperation = tomorrowDisplayDate();
    this.operation = 'Commande';
    this.naturePass = 'Besoin d agence';
    this.descriptionClient = '';
    this.isBillets = false;
    this.devise = this.devises.some((devise) => devise.codeDevise?.trim().toUpperCase() === 'TND')
      ? 'TND'
      : this.devises[0]?.codeDevise ?? 'TND';
  }

  private loadOperations(): void {
    if (!this.codeAgence) {
      this.tableErrorMessage = 'Code agence introuvable dans la session.';
      return;
    }

    this.isLoadingTable = true;
    this.tableErrorMessage = '';

    this.cdeVersDeltaService.listForAgency(this.codeAgence)
      .pipe(finalize(() => {
        this.isLoadingTable = false;
        this.cdr.detectChanges();
      }))
      .subscribe({
        next: (operations) => {
          this.operations = operations;
          this.cdr.detectChanges();
        },
        error: (error) => {
          this.tableErrorMessage = typeof error?.error === 'string'
            ? error.error
            : 'Impossible de charger les operations.';
          this.cdr.detectChanges();
        }
      });
  }
}
