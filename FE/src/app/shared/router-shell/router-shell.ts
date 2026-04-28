import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

/** Conteneur minimal pour routes imbriquées (sous-menus). */
@Component({
  selector: 'app-router-shell',
  standalone: true,
  imports: [RouterOutlet],
  template: '<router-outlet />',
})
export class RouterShell {}
