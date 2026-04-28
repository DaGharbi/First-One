import { Pipe, PipeTransform } from '@angular/core';
import { formatDateForDisplay } from './date-format';

@Pipe({
  name: 'displayDate',
  standalone: true
})
export class DisplayDatePipe implements PipeTransform {
  transform(value: Date | string | null | undefined): string {
    return formatDateForDisplay(value);
  }
}
