import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/login']);
      return false;
    }

    const allowedRoles = route.data['roles'] as string[] | undefined;
    if (!allowedRoles?.length) {
      return true;
    }

    const currentRole = this.authService.getCurrentAuthRole()?.trim().toUpperCase() ?? null;
    const normalizedAllowedRoles = allowedRoles.map((role) => role.toUpperCase());

    if (currentRole && normalizedAllowedRoles.includes(currentRole)) {
      return true;
    }

    if (this.authService.isAgent()) {
      this.router.navigate(['/home/transactions-transport/agent-commandes-versements']);
      return false;
    }

    if (this.authService.isAdmin()) {
      this.router.navigate(['/home/menu']);
      return false;
    }

    if (this.authService.isSecurity()) {
      this.router.navigate(['/home/menu']);
      return false;
    }

    if (this.authService.isCc()) {
      this.router.navigate(['/home/menu']);
      return false;
    }

    this.authService.logout();
    this.router.navigate(['/login']);
    return false;
  }
}
