import { Component, OnInit, inject } from '@angular/core';
import { Router, NavigationEnd, RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { filter } from 'rxjs/operators';
import { AuthService, AuthUser } from '../services/auth.service';
import { formatDateForDisplay } from '../shared/date-format';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home implements OnInit {
  private readonly router = inject(Router);
  private readonly authService = inject(AuthService);
  journee = formatDateForDisplay(new Date());
  connectedUser: AuthUser | null = null;

  /** Sous-menus repliés par défaut ; s’ouvrent au clic ou si la route active est dedans */
  adminOpen = false;
  prepOpen = false;
  ttfOpen = false;
  calculOpen = false;
  statOpen = false;
  statOisivesOpen = false;
  statVolumeDevisesOpen = false;
  consultOpen = false;

  ngOnInit(): void {
    this.connectedUser = this.authService.getCurrentUser();
    this.syncExpandFromUrl(this.router.url);
    this.router.events
      .pipe(filter((e): e is NavigationEnd => e instanceof NavigationEnd))
      .subscribe(() => this.syncExpandFromUrl(this.router.url));
  }

  get utilisateur(): string {
    return this.connectedUser?.usrName || this.connectedUser?.usrMat || 'Utilisateur';
  }

  get roleLabel(): string {
    const role = this.authService.resolveRoleFromCodeProfil(this.connectedUser?.codeProfil);

    switch (role) {
      case 'ADMIN':
        return 'Administrateur';
      case 'CC':
        return 'Caisse centrale';
      case 'SECURITY':
        return 'Security';
      case 'AGENT':
        return 'Agent';
      default:
        return role || 'Role non defini';
    }
  }

  get isAdmin(): boolean {
    return this.authService.isAdmin();
  }

  get isCc(): boolean {
    return this.authService.isCc();
  }

  get isSecurity(): boolean {
    return this.authService.isSecurity();
  }

  get isAgent(): boolean {
    return this.authService.isAgent();
  }

  get canAccessBusinessSections(): boolean {
    return this.authService.canAccessBusinessSections();
  }

  get canAccessAdministration(): boolean {
    return this.authService.canAccessAdministration();
  }

  get canAccessStatistics(): boolean {
    return this.authService.canAccessStatistics();
  }

  get agence(): string {
    return this.connectedUser?.libAgence || this.connectedUser?.codeAgence || 'Agence non definie';
  }

  get caisseCentrale(): string {
    return this.connectedUser?.libCaisseCent || 'Caisse centrale non definie';
  }

  get groupe(): string {
    return this.connectedUser?.libGroupe || 'Groupe non defini';
  }

  get initials(): string {
    const source = this.connectedUser?.usrName || this.connectedUser?.usrMat || 'U';
    return source
      .split(' ')
      .filter(Boolean)
      .slice(0, 2)
      .map((part) => part.charAt(0).toUpperCase())
      .join('');
  }

  private syncExpandFromUrl(url: string): void {
    this.closePrimarySections();
    this.statOisivesOpen = false;
    this.statVolumeDevisesOpen = false;

    if (url.includes('/administratif')) {
      if (!this.canAccessAdministration) {
        return;
      }
      this.adminOpen = true;
      return;
    }
    if (url.includes('/preparation-environnement')) {
      if (!this.canAccessBusinessSections) {
        return;
      }
      this.prepOpen = true;
      return;
    }
    if (url.includes('/transactions-transport')) {
      if (!this.canAccessBusinessSections && !this.isAgent) {
        return;
      }
      this.ttfOpen = true;
      return;
    }
    if (url.includes('/calcul-encaisse')) {
      if (!this.canAccessBusinessSections && !this.isAgent) {
        return;
      }
      this.calculOpen = true;
      return;
    }
    if (url.includes('/statistiques')) {
      if (!this.canAccessStatistics) {
        return;
      }
      this.statOpen = true;
      this.statOisivesOpen = url.includes('oisives-');
      this.statVolumeDevisesOpen = url.includes('volume-devises-');
      return;
    }
    if (url.includes('/consultation')) {
      if (!this.canAccessBusinessSections && !this.isAgent) {
        return;
      }
      this.consultOpen = true;
    }
  }

  private closePrimarySections(): void {
    this.adminOpen = false;
    this.prepOpen = false;
    this.ttfOpen = false;
    this.calculOpen = false;
    this.statOpen = false;
    this.consultOpen = false;
  }

  private togglePrimarySection(section: 'admin' | 'prep' | 'ttf' | 'calcul' | 'stat' | 'consult'): void {
    const nextState = {
      admin: section === 'admin' ? !this.adminOpen : false,
      prep: section === 'prep' ? !this.prepOpen : false,
      ttf: section === 'ttf' ? !this.ttfOpen : false,
      calcul: section === 'calcul' ? !this.calculOpen : false,
      stat: section === 'stat' ? !this.statOpen : false,
      consult: section === 'consult' ? !this.consultOpen : false,
    };

    this.closePrimarySections();
    this.adminOpen = nextState.admin;
    this.prepOpen = nextState.prep;
    this.ttfOpen = nextState.ttf;
    this.calculOpen = nextState.calcul;
    this.statOpen = nextState.stat;
    this.consultOpen = nextState.consult;

    if (!this.statOpen) {
      this.statOisivesOpen = false;
      this.statVolumeDevisesOpen = false;
    }
  }

  toggleAdmin(): void {
    this.togglePrimarySection('admin');
  }

  toggleTtf(): void {
    this.togglePrimarySection('ttf');
  }

  togglePrep(): void {
    this.togglePrimarySection('prep');
  }

  toggleCalcul(): void {
    this.togglePrimarySection('calcul');
  }

  toggleStat(): void {
    this.togglePrimarySection('stat');
  }

  toggleStatOisives(): void {
    this.statOisivesOpen = !this.statOisivesOpen;
  }

  toggleStatVolumeDevises(): void {
    this.statVolumeDevisesOpen = !this.statVolumeDevisesOpen;
  }

  toggleConsult(): void {
    this.togglePrimarySection('consult');
  }

  isAdminSection(): boolean {
    return this.isInSection('/home/administratif');
  }

  isTtfSection(): boolean {
    return this.isInSection('/home/transactions-transport');
  }

  isPrepSection(): boolean {
    return this.isInSection('/home/preparation-environnement');
  }

  isCalculSection(): boolean {
    return this.isInSection('/home/calcul-encaisse');
  }

  isStatSection(): boolean {
    return this.isInSection('/home/statistiques');
  }

  isConsultSection(): boolean {
    return this.isInSection('/home/consultation');
  }

  private isInSection(basePath: string): boolean {
    const url = this.router.url;
    return url === basePath || url.startsWith(`${basePath}/`);
  }
}
