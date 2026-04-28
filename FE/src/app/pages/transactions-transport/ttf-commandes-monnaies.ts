import { DecimalPipe } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { AttijariSessionPanel } from '../../shared/attijari-session-panel/attijari-session-panel';
import { DisplayDatePipe } from '../../shared/display-date.pipe';
import { AuthService } from '../../services/auth.service';
import { CdeMonnaies, CdeMonnaiesService } from '../../services/cde-monnaies.service';

@Component({
  selector: 'app-ttf-commandes-monnaies',
  standalone: true,
  imports: [AttijariSessionPanel, DecimalPipe, DisplayDatePipe],
  templateUrl: './ttf-commandes-monnaies.html',
  styleUrls: ['../styles/attijari-page.css'],
})
export class TtfCommandesMonnaies implements OnInit {
  commandesMonnaies: CdeMonnaies[] = [];
  isLoading = false;
  errorMessage = '';

  constructor(
    private authService: AuthService,
    private cdeMonnaiesService: CdeMonnaiesService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadCommandes();
  }

  totalPieces(item: CdeMonnaies): number {
    return item.p100M + item.p10M + item.p1Dn + item.p20M + item.p500M + item.p50M + item.p5M;
  }

  private loadCommandes(): void {
    const codeAgence = this.authService.getCurrentUser()?.codeAgence?.trim();
    if (!codeAgence) {
      this.errorMessage = 'Code agence introuvable dans la session.';
      this.cdr.detectChanges();
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
    this.cdr.detectChanges();

    this.cdeMonnaiesService.listForSameCaisseCentrale(codeAgence).subscribe({
      next: (commandes) => {
        this.commandesMonnaies = commandes;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        this.errorMessage = typeof error?.error === 'string'
          ? error.error
          : 'Impossible de charger les commandes monnaies.';
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }
}
