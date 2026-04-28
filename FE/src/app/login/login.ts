import { ChangeDetectorRef, Component } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { finalize, TimeoutError } from 'rxjs';
import { AuthService, AuthRequest } from '../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {
  credentials: AuthRequest = {
    username: '',
    password: ''
  };

  isLoading: boolean = false;
  errorMessage: string = '';

  constructor(
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  login() {
    if (!this.credentials.username || !this.credentials.password) {
      this.errorMessage = 'Veuillez saisir votre identifiant et mot de passe';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    this.authService.login(this.credentials)
      .pipe(
        finalize(() => {
          this.isLoading = false;
        })
      )
      .subscribe({
        next: (response) => {
          const role = this.authService.resolveRoleFromCodeProfil(response.user?.codeProfil);
          const targetRoute = role === 'AGENT'
            ? '/home/transactions-transport/agent-commandes-versements'
            : '/home/menu';
          this.router.navigateByUrl(targetRoute);
        },
        error: (error) => {
          const backendMessage = typeof error?.error === 'string'
            ? error.error
            : error?.error?.message;

          if (error instanceof TimeoutError || error?.name === 'TimeoutError') {
            this.errorMessage = 'Le serveur met trop de temps a repondre. Veuillez reessayer.';
          } else if (error?.status === 401 && backendMessage === 'User not existing') {
            this.errorMessage = 'Utilisateur inexistant';
          } else if (error?.status === 403 && (backendMessage === 'Profile not allowed' || backendMessage === 'User profile not found')) {
            this.errorMessage = 'Profil non autorise pour cette application';
          } else if (error?.status === 401) {
            this.errorMessage = 'Mot de passe incorrect';
          } else if (error?.status === 0 || error?.status === 502 || error?.status === 503) {
            this.errorMessage = 'Service indisponible. Veuillez reessayer.';
          } else {
            this.errorMessage = 'Erreur de connexion. Veuillez reessayer.';
          }

          this.cdr.detectChanges();
        }
      });
  }

  goToRegister(event: Event) {
    event.preventDefault();
    this.router.navigate(['/register']);
  }
}
