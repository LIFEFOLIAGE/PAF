import { Injectable } from '@angular/core';
import { LocalDate, LocalDateTime } from '@js-joda/core';

@Injectable({
	providedIn: 'root'
})
export class HtmlService {
	getInputElmentValue(target: any) {
		if (target == null || target == undefined) {
			return "";
		}
		else {
			return (target as HTMLInputElement).value;
		}
	}
	
	getInputElmentFiles(target: any) {
		if (target == null || target == undefined) {
			return "";
		}
		else {
			return (target as HTMLInputElement).files;
		}
	}

	toLocalDate(date: string): (LocalDate|undefined) {
		if (date) {
			return LocalDate.parse(date);
		}
		else {
			return undefined;
		}
	}
	toLocalDateTime(date: string): (LocalDateTime|undefined) {
		if (date) {
			return LocalDateTime.parse(date);
		}
		else {
			return undefined;
		}
	}
}