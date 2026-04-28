export const DISPLAY_DATE_PATTERN = '\\d{2}-\\d{2}-\\d{4}';
export const DISPLAY_DATE_PLACEHOLDER = 'dd-mm-yyyy';

export function tomorrowDisplayDate(): string {
  const tomorrow = new Date();
  tomorrow.setDate(tomorrow.getDate() + 1);
  return formatDateForDisplay(tomorrow);
}

export function formatDateForDisplay(value: Date | string | null | undefined): string {
  if (!value) {
    return '';
  }

  if (value instanceof Date) {
    return formatDateParts(value.getDate(), value.getMonth() + 1, value.getFullYear());
  }

  const trimmed = value.trim();
  const displayMatch = trimmed.match(/^(\d{2})-(\d{2})-(\d{4})$/);
  if (displayMatch) {
    return trimmed;
  }

  const isoMatch = trimmed.match(/^(\d{4})-(\d{2})-(\d{2})/);
  if (isoMatch) {
    return `${isoMatch[3]}-${isoMatch[2]}-${isoMatch[1]}`;
  }

  return trimmed;
}

export function displayDateToIso(value: string | null | undefined): string {
  if (!value) {
    return '';
  }

  const trimmed = value.trim();
  const displayMatch = trimmed.match(/^(\d{2})-(\d{2})-(\d{4})$/);
  if (displayMatch) {
    return `${displayMatch[3]}-${displayMatch[2]}-${displayMatch[1]}`;
  }

  return trimmed;
}

function formatDateParts(day: number, month: number, year: number): string {
  return `${pad(day)}-${pad(month)}-${year}`;
}

function pad(value: number): string {
  return value.toString().padStart(2, '0');
}
