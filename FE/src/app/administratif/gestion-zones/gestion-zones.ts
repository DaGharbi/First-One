import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService, Groupe } from '../../services/admin.service';

@Component({
  selector: 'app-gestion-zones',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './gestion-zones.html',
  styleUrl: './gestion-zones.css',
})
export class GestionZones implements OnInit {
  groupes: Groupe[] = [];
  searchTerm = '';
  isLoading = false;
  errorMessage = '';
  successMessage = '';
  isSubmitting = false;
  isFormVisible = false;
  formMode: 'create' | 'edit' = 'create';
  groupeForm: Groupe = this.createEmptyGroupe();
  pendingDeleteGroupe: Groupe | null = null;

  constructor(
    private adminService: AdminService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadGroupes();
  }

  get filteredGroupes(): Groupe[] {
    const search = this.searchTerm.trim().toLowerCase();
    return this.groupes.filter((groupe) =>
      !search
      || groupe.codeGroupe?.toLowerCase().includes(search)
      || groupe.libGroupe?.toLowerCase().includes(search)
      || groupe.nameChefGroupe?.toLowerCase().includes(search)
    );
  }

  openCreateForm(): void {
    this.formMode = 'create';
    this.groupeForm = this.createEmptyGroupe();
    this.errorMessage = '';
    this.successMessage = '';
    this.isFormVisible = true;
  }

  openEditForm(groupe: Groupe): void {
    this.formMode = 'edit';
    this.groupeForm = {
      ...groupe,
      matChefGroupe: groupe.matChefGroupe || '',
      nameChefGroupe: groupe.nameChefGroupe || ''
    };
    this.errorMessage = '';
    this.successMessage = '';
    this.isFormVisible = true;
  }

  cancelForm(): void {
    this.isFormVisible = false;
    this.isSubmitting = false;
    this.groupeForm = this.createEmptyGroupe();
  }

  submitForm(): void {
    const payload: Groupe = {
      codeGroupe: this.groupeForm.codeGroupe.trim().toUpperCase(),
      libGroupe: this.groupeForm.libGroupe.trim(),
      matChefGroupe: this.groupeForm.matChefGroupe?.trim() || null,
      nameChefGroupe: this.groupeForm.nameChefGroupe?.trim() || null
    };

    if (!payload.codeGroupe || !payload.libGroupe) {
      this.errorMessage = 'Veuillez remplir les champs obligatoires du groupe.';
      this.successMessage = '';
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = '';

    const request = this.formMode === 'create'
      ? this.adminService.createGroupe(payload)
      : this.adminService.updateGroupe(payload.codeGroupe, payload);

    request.subscribe({
      next: (savedGroupe) => {
        const mode = this.formMode;
        this.closeFormAfterSuccess();
        this.groupes = mode === 'create'
          ? [...this.groupes, savedGroupe].sort((a, b) => a.codeGroupe.localeCompare(b.codeGroupe))
          : this.groupes.map((item) => item.codeGroupe === savedGroupe.codeGroupe ? savedGroupe : item);
        this.successMessage = mode === 'create'
          ? 'Groupe ajoute avec succes.'
          : 'Groupe modifie avec succes.';
        this.cdr.detectChanges();
        this.loadGroupes();
      },
      error: (error) => {
        this.isSubmitting = false;
        this.successMessage = '';
        this.errorMessage = this.extractErrorMessage(error, 'Enregistrement impossible');
        this.cdr.detectChanges();
      }
    });
  }

  requestDelete(groupe: Groupe): void {
    this.pendingDeleteGroupe = groupe;
    this.errorMessage = '';
    this.successMessage = '';
  }

  cancelDelete(): void {
    this.pendingDeleteGroupe = null;
  }

  supprimerZone(codeGroupe: string) {
    this.pendingDeleteGroupe = null;

    this.adminService.deleteGroupe(codeGroupe).subscribe({
      next: () => {
        this.groupes = this.groupes.filter((groupe) => groupe.codeGroupe !== codeGroupe);
        this.errorMessage = '';
        this.successMessage = 'Groupe supprime avec succes.';
        this.cdr.detectChanges();
        this.loadGroupes();
      },
      error: (error) => {
        this.successMessage = '';
        this.errorMessage = this.extractErrorMessage(error, 'Suppression impossible');
        this.cdr.detectChanges();
      }
    });
  }

  private loadGroupes() {
    this.isLoading = true;
    this.errorMessage = '';

    this.adminService.getGroupes().subscribe({
      next: (groupes) => {
        this.groupes = groupes;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.successMessage = '';
        this.errorMessage = 'Chargement des groupes impossible';
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  private createEmptyGroupe(): Groupe {
    return {
      codeGroupe: '',
      libGroupe: '',
      matChefGroupe: '',
      nameChefGroupe: ''
    };
  }

  private closeFormAfterSuccess(): void {
    this.isFormVisible = false;
    this.isSubmitting = false;
    this.groupeForm = this.createEmptyGroupe();
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
