import { Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute } from '@angular/router';
import { map } from 'rxjs/operators';
import { AttijariSessionPanel } from '../../shared/attijari-session-panel/attijari-session-panel';

@Component({
  selector: 'app-statistiques-report',
  standalone: true,
  imports: [AttijariSessionPanel],
  templateUrl: './statistiques-report.html',
  styleUrls: ['../styles/attijari-page.css'],
})
export class StatistiquesReportPage {
  private readonly route = inject(ActivatedRoute);

  readonly title = toSignal(
    this.route.data.pipe(map((d) => (d['title'] as string) ?? '')),
    { initialValue: (this.route.snapshot.data['title'] as string) ?? '' },
  );

  readonly lead = toSignal(
    this.route.data.pipe(map((d) => (d['lead'] as string) ?? '')),
    { initialValue: (this.route.snapshot.data['lead'] as string) ?? '' },
  );
}
