import { Component, OnInit, inject } from '@angular/core';
import { Router, NavigationEnd, RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { filter } from 'rxjs/operators';
import { formatDateForDisplay } from '../../shared/date-format';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home implements OnInit {
  private readonly router = inject(Router);
  journee = formatDateForDisplay(new Date());
  agence = 'CAISSE CENTRALE TUNIS';
  utilisateur = 'utilisateur';

  /** Sous-menus repliés par défaut ; s’ouvrent au clic ou si la route active est dedans */
  ttfOpen = false;
  calculOpen = false;
  statOpen = false;
  statOisivesOpen = false;
  statVolumeDevisesOpen = false;
  consultOpen = false;

  ngOnInit(): void {
    this.syncExpandFromUrl(this.router.url);
    this.router.events
      .pipe(filter((e): e is NavigationEnd => e instanceof NavigationEnd))
      .subscribe(() => this.syncExpandFromUrl(this.router.url));
  }

  private syncExpandFromUrl(url: string): void {
    if (url.includes('/transactions-transport')) {
      this.ttfOpen = true;
    }
    if (url.includes('/calcul-encaisse')) {
      this.calculOpen = true;
    }
    if (url.includes('/statistiques')) {
      this.statOpen = true;
      if (url.includes('oisives-')) {
        this.statOisivesOpen = true;
      }
      if (url.includes('volume-devises-')) {
        this.statVolumeDevisesOpen = true;
      }
    }
    if (url.includes('/consultation')) {
      this.consultOpen = true;
    }
  }

  toggleTtf(): void {
    this.ttfOpen = !this.ttfOpen;
  }

  toggleCalcul(): void {
    this.calculOpen = !this.calculOpen;
  }

  toggleStat(): void {
    this.statOpen = !this.statOpen;
  }

  toggleStatOisives(): void {
    this.statOisivesOpen = !this.statOisivesOpen;
  }

  toggleStatVolumeDevises(): void {
    this.statVolumeDevisesOpen = !this.statVolumeDevisesOpen;
  }

  toggleConsult(): void {
    this.consultOpen = !this.consultOpen;
  }

  isStatSection(): boolean {
    return this.router.url.includes('/statistiques');
  }

  isConsultSection(): boolean {
    return this.router.url.includes('/consultation');
  }
}
