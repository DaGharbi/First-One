import { Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  DISPLAY_DATE_PATTERN,
  DISPLAY_DATE_PLACEHOLDER,
  displayDateToIso,
  formatDateForDisplay,
  tomorrowDisplayDate,
} from '../date-format';

@Component({
  selector: 'app-european-date-input',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './european-date-input.html',
  styleUrls: ['./european-date-input.css'],
})
export class EuropeanDateInput implements OnInit {
  @Input() value = '';
  @Input() name = '';
  @Input() disabled = false;
  @Output() valueChange = new EventEmitter<string>();

  @ViewChild('nativeDateInput') private nativeDateInput?: ElementRef<HTMLInputElement>;

  readonly pattern = DISPLAY_DATE_PATTERN;
  readonly placeholder = DISPLAY_DATE_PLACEHOLDER;

  ngOnInit(): void {
    if (this.value.trim()) {
      return;
    }

    this.value = tomorrowDisplayDate();
    queueMicrotask(() => this.valueChange.emit(this.value));
  }

  get pickerValue(): string {
    return displayDateToIso(this.value);
  }

  onTextInput(event: Event): void {
    this.value = (event.target as HTMLInputElement).value;
    this.valueChange.emit(this.value);
  }

  onPickerChange(isoDate: string): void {
    this.value = formatDateForDisplay(isoDate);
    this.valueChange.emit(this.value);
  }

  normalizeText(): void {
    const normalized = formatDateForDisplay(this.value);

    if (normalized !== this.value) {
      this.value = normalized;
      this.valueChange.emit(this.value);
    }
  }

  openPicker(): void {
    if (this.disabled) {
      return;
    }

    const input = this.nativeDateInput?.nativeElement;

    if (!input) {
      return;
    }

    if (typeof input.showPicker === 'function') {
      input.showPicker();
      return;
    }

    input.click();
  }
}
