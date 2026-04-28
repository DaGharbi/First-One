import { Component } from '@angular/core';
import { AttijariSessionPanel } from '../../shared/attijari-session-panel/attijari-session-panel';

@Component({
  selector: 'app-preparation-environnement',
  standalone: true,
  imports: [AttijariSessionPanel],
  templateUrl: './preparation-environnement.html',
  styleUrls: ['../styles/attijari-page.css'],
})
export class PreparationEnvironnement {}
