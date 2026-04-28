import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService, AdminUser, Agence } from '../../services/admin.service';

@Component({
  selector: 'app-gestion-utilisateurs',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './gestion-utilisateurs.html',
  styleUrl: './gestion-utilisateurs.css',
})
export class GestionUtilisateurs implements OnInit {
  users: AdminUser[] = [];
  agences: Agence[] = [];
  searchTerm = '';
  roleFilter = '';
  isLoading = false;
  errorMessage = '';
  successMessage = '';
  isSubmitting = false;
  isFormVisible = false;
  formMode: 'create' | 'edit' = 'create';
  userForm: AdminUser = this.createEmptyUser();
  pendingDeleteUser: AdminUser | null = null;

  constructor(
    private adminService: AdminService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  get filteredUsers(): AdminUser[] {
    const search = this.searchTerm.trim().toLowerCase();
    return this.users.filter((user) => {
      const matchesSearch = !search
        || user.usrName?.toLowerCase().includes(search)
        || user.usrMat?.toLowerCase().includes(search);
      const matchesRole = !this.roleFilter || user.codeProfil === this.roleFilter;
      return matchesSearch && matchesRole;
    });
  }

  openCreateForm(): void {
    this.formMode = 'create';
    this.userForm = this.createEmptyUser();
    this.errorMessage = '';
    this.successMessage = '';
    this.isFormVisible = true;
  }

  openEditForm(user: AdminUser): void {
    this.formMode = 'edit';
    this.userForm = {
      ...user,
      suspendu: (user.suspendu || 'N').toUpperCase()
    };
    this.errorMessage = '';
    this.successMessage = '';
    this.isFormVisible = true;
  }

  cancelForm(): void {
    this.isFormVisible = false;
    this.isSubmitting = false;
    this.userForm = this.createEmptyUser();
  }

  submitForm(): void {
    const payload: AdminUser = {
      usrMat: this.userForm.usrMat.trim().toUpperCase(),
      codeAgence: this.userForm.codeAgence.trim().toUpperCase(),
      codeProfil: this.userForm.codeProfil.trim().toUpperCase(),
      usrName: this.userForm.usrName.trim(),
      suspendu: (this.userForm.suspendu || 'N').trim().toUpperCase()
    };

    if (!payload.usrMat || !payload.codeAgence || !payload.codeProfil || !payload.usrName) {
      this.errorMessage = 'Veuillez remplir les champs obligatoires de l utilisateur.';
      this.successMessage = '';
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = '';

    const request = this.formMode === 'create'
      ? this.adminService.createUser(payload)
      : this.adminService.updateUser(payload.usrMat, payload);

    request.subscribe({
      next: (savedUser) => {
        const mode = this.formMode;
        this.closeFormAfterSuccess();
        this.users = mode === 'create'
          ? [...this.users, savedUser].sort((a, b) => a.usrMat.localeCompare(b.usrMat))
          : this.users.map((item) => item.usrMat === savedUser.usrMat ? savedUser : item);
        this.successMessage = mode === 'create'
          ? 'Utilisateur ajoute avec succes.'
          : 'Utilisateur modifie avec succes.';
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

  requestDelete(user: AdminUser): void {
    this.pendingDeleteUser = user;
    this.errorMessage = '';
    this.successMessage = '';
  }

  cancelDelete(): void {
    this.pendingDeleteUser = null;
  }

  supprimerUtilisateur(usrMat: string) {
    this.pendingDeleteUser = null;

    this.adminService.deleteUser(usrMat).subscribe({
      next: () => {
        this.users = this.users.filter((user) => user.usrMat !== usrMat);
        this.errorMessage = '';
        this.successMessage = 'Utilisateur supprime avec succes.';
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

  getRoleLabel(codeProfil: string): string {
    switch ((codeProfil || '').toUpperCase()) {
      case 'ADM':
        return 'Admin';
      case 'RCC':
        return 'Responsable Caisse Centrale';
      case 'RAG':
        return 'Responsable Agence';
      case 'SEC':
        return 'Security';
      default:
        return codeProfil || 'Non defini';
    }
  }

  isActive(user: AdminUser): boolean {
    return (user.suspendu || 'N').toUpperCase() !== 'Y';
  }

  private loadData() {
    this.isLoading = true;
    this.errorMessage = '';

    this.adminService.getUsers().subscribe({
      next: (users) => {
        this.users = users;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.successMessage = '';
        this.errorMessage = 'Chargement des utilisateurs impossible';
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });

    this.adminService.getAgences().subscribe({
      next: (agences) => {
        this.agences = agences;
        this.cdr.detectChanges();
      },
      error: () => {
        this.agences = [];
        this.cdr.detectChanges();
      }
    });
  }

  private createEmptyUser(): AdminUser {
    return {
      usrMat: '',
      codeAgence: '',
      codeProfil: 'SEC',
      usrName: '',
      suspendu: 'N'
    };
  }

  private closeFormAfterSuccess(): void {
    this.isFormVisible = false;
    this.isSubmitting = false;
    this.userForm = this.createEmptyUser();
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
