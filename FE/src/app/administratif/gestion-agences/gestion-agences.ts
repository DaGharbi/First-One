import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService, Agence, Groupe } from '../../services/admin.service';

@Component({
  selector: 'app-gestion-agences',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './gestion-agences.html',
  styleUrl: './gestion-agences.css',
})
export class GestionAgences implements OnInit {
  agences: Agence[] = [];
  groupes: Groupe[] = [];
  searchTerm = '';
  groupeFilter = '';
  isLoading = false;
  errorMessage = '';
  successMessage = '';
  isSubmitting = false;
  isFormVisible = false;
  formMode: 'create' | 'edit' = 'create';
  agenceForm: Agence = this.createEmptyAgence();
  pendingDeleteAgence: Agence | null = null;

  constructor(
    private adminService: AdminService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  get filteredAgences(): Agence[] {
    const search = this.searchTerm.trim().toLowerCase();
    return this.agences.filter((agence) => {
      const matchesSearch = !search
        || agence.libAgence?.toLowerCase().includes(search)
        || agence.codeAgence?.toLowerCase().includes(search)
        || agence.codeCent?.toLowerCase().includes(search);
      const matchesGroupe = !this.groupeFilter || agence.codeGroupe === this.groupeFilter;
      return matchesSearch && matchesGroupe;
    });
  }

  openCreateForm(): void {
    this.formMode = 'create';
    this.agenceForm = this.createEmptyAgence();
    this.errorMessage = '';
    this.successMessage = '';
    this.isFormVisible = true;
  }

  openEditForm(agence: Agence): void {
    this.formMode = 'edit';
    this.agenceForm = {
      ...agence,
      codeIbs: agence.codeIbs || '',
      matChefAgence: agence.matChefAgence || ''
    };
    this.errorMessage = '';
    this.successMessage = '';
    this.isFormVisible = true;
  }

  cancelForm(): void {
    this.isFormVisible = false;
    this.isSubmitting = false;
    this.agenceForm = this.createEmptyAgence();
  }

  submitForm(): void {
    const payload: Agence = {
      codeAgence: this.agenceForm.codeAgence.trim().toUpperCase(),
      codeCent: this.agenceForm.codeCent.trim().toUpperCase(),
      codeGroupe: this.agenceForm.codeGroupe.trim().toUpperCase(),
      libAgence: this.agenceForm.libAgence.trim(),
      codeIbs: this.agenceForm.codeIbs?.trim() || null,
      codeCaissCent: this.agenceForm.codeCaissCent.trim().toUpperCase(),
      nbreGab: this.agenceForm.nbreGab ?? 0,
      matChefAgence: this.agenceForm.matChefAgence?.trim() || null
    };

    if (!payload.codeAgence || !payload.codeCent || !payload.codeGroupe || !payload.libAgence || !payload.codeCaissCent) {
      this.errorMessage = 'Veuillez remplir les champs obligatoires de l agence.';
      this.successMessage = '';
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = '';

    const request = this.formMode === 'create'
      ? this.adminService.createAgence(payload)
      : this.adminService.updateAgence(payload.codeAgence, payload);

    request.subscribe({
      next: (savedAgence) => {
        const mode = this.formMode;
        this.closeFormAfterSuccess();
        this.agences = mode === 'create'
          ? [...this.agences, savedAgence].sort((a, b) => a.codeAgence.localeCompare(b.codeAgence))
          : this.agences.map((item) => item.codeAgence === savedAgence.codeAgence ? savedAgence : item);
        this.successMessage = mode === 'create'
          ? 'Agence ajoutee avec succes.'
          : 'Agence modifiee avec succes.';
        this.cdr.detectChanges();
        this.loadData();
      },
      error: (error) => {
        this.isSubmitting = false;
        this.successMessage = '';
        this.errorMessage = this.extractErrorMessage(error, 'Enregistrement impossible');
        this.cdr.detectChanges();
      }
    });
  }

  requestDelete(agence: Agence): void {
    this.pendingDeleteAgence = agence;
    this.errorMessage = '';
    this.successMessage = '';
  }

  cancelDelete(): void {
    this.pendingDeleteAgence = null;
  }

  supprimerAgence(codeAgence: string) {
    this.pendingDeleteAgence = null;

    this.adminService.deleteAgence(codeAgence).subscribe({
      next: () => {
        this.agences = this.agences.filter((agence) => agence.codeAgence !== codeAgence);
        this.errorMessage = '';
        this.successMessage = 'Agence supprimee avec succes.';
        this.cdr.detectChanges();
        this.loadData();
      },
      error: (error) => {
        this.successMessage = '';
        this.errorMessage = this.extractErrorMessage(error, 'Suppression impossible');
        this.cdr.detectChanges();
      }
    });
  }

  private loadData() {
    this.isLoading = true;
    this.errorMessage = '';

    this.adminService.getAgences().subscribe({
      next: (agences) => {
        this.agences = agences;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.successMessage = '';
        this.errorMessage = 'Chargement des agences impossible';
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });

    this.adminService.getGroupes().subscribe({
      next: (groupes) => {
        this.groupes = groupes;
        this.cdr.detectChanges();
      },
      error: () => {
        this.groupes = [];
        this.cdr.detectChanges();
      }
    });
  }

  private createEmptyAgence(): Agence {
    return {
      codeAgence: '',
      codeCent: '',
      codeGroupe: '',
      libAgence: '',
      codeIbs: '',
      codeCaissCent: '',
      nbreGab: 0,
      matChefAgence: ''
    };
  }

  private closeFormAfterSuccess(): void {
    this.isFormVisible = false;
    this.isSubmitting = false;
    this.agenceForm = this.createEmptyAgence();
  }

  private extractErrorMessage(error: unknown, fallback: string): string {
    const candidate = error as {
      error?: { message?: string; detail?: string } | string;
      message?: string;
    };

    if (typeof candidate?.error === 'string' && candidate.error.trim()) {
      return candidate.error;
    }

    if (typeof candidate?.error === 'object' && candidate.error) {
      const backendError = candidate.error.message || candidate.error.detail;
      if (backendError?.trim()) {
        return backendError;
      }
    }

    if (candidate?.message?.trim()) {
      return candidate.message;
    }

    return fallback;
  }
}
