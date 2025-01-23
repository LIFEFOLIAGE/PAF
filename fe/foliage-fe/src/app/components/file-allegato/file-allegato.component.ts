import { Component, EventEmitter, Input, Output } from "@angular/core";
import { HtmlService } from "src/app/services/html.service";
import { SessionManagerService } from "src/app/services/session-manager.service";

export type FoliageFileBase64 = {
	name?: string,
	originalName?: string,
	size?: number,
	storage?: string,
	type?: string,
	hash?: string,
	url?: string | ArrayBuffer
};

@Component({
	selector: 'app-file-allegato',
	templateUrl: './file-allegato.component.html'
})
export class FileAllegatoComponent {
	static lastIdx: number = 0;
	static defaultDeleteMessage: string = "Vuoi rimuovere l'allegato?"
	idx: number = FileAllegatoComponent.lastIdx++;
	@Input() accept: string = "application/pdf, image/*, .p7m";
	@Input() file?: FoliageFileBase64;
	@Input() isReadOnly: boolean = false;
	@Output() fileChange: EventEmitter<FoliageFileBase64> = new EventEmitter<FoliageFileBase64>();
	deleteMessage: string = FileAllegatoComponent.defaultDeleteMessage;

	constructor(
		public html: HtmlService,
		private sessionManager: SessionManagerService
	) {

	}

	public static globStringToRegex(str: string) {
		str = str.replace(/\s/g, '');

		let regexp: string = '';
		let excludes: any[] = [];
		if (str.length > 2 && str[0] === '/' && str[str.length - 1] === '/') {
			regexp = str.substring(1, str.length - 1);
		}
		else {
			const split = str.split(',');
			if (split.length > 1) {
				for (let i = 0; i < split.length; i++) {
					const r = this.globStringToRegex(split[i]);
					if (r.regexp) {
						regexp += `(${r.regexp})`;
						if (i < split.length - 1) {
							regexp += '|';
						}
					}
					else {
						excludes = excludes.concat(r.excludes);
					}
				}
			}
			else {
				if (str.startsWith('!')) {
					excludes.push(`^((?!${this.globStringToRegex(str.substring(1)).regexp}).)*$`);
				}
				else {
					if (str.startsWith('.')) {
						str = `*${str}`;
					}
					regexp = `^${str.replace(new RegExp('[.\\\\+*?\\[\\^\\]$(){}=!<>|:\\-]', 'g'), '\\$&')}$`;
					regexp = regexp.replace(/\\\*/g, '.*').replace(/\\\?/g, '.');
				}
			}
		}
		return { regexp, excludes };
	}
	public static isNil(val: any) {
		return val == undefined || val == null;
	}

	public static validatePattern(file: File, val: string) {
		if (!val) {
			return true;
		}
		const pattern = this.globStringToRegex(val);
		let valid = true;
		if (pattern.regexp && pattern.regexp.length) {
			const regexp = new RegExp(pattern.regexp, 'i');
			valid = (!this.isNil(file.type) && regexp.test(file.type)) ||
				(!this.isNil(file.name) && regexp.test(file.name));
		}
		valid = pattern.excludes.reduce((result, excludePattern) => {
			const exclude = new RegExp(excludePattern, 'i');
			return result && (this.isNil(file.type) || !exclude.test(file.type)) &&
				(this.isNil(file.name) || !exclude.test(file.name));
		}, valid);
		return valid;
	}

	rimuoviFile() {
		if (confirm(this.deleteMessage)) {
			//this.file = undefined;
			this.fileChange.emit(undefined);
			alert("File rimosso");
		}
	}
	allega(fileList: ("" | FileList | null)) {
		console.log('allega');
		if (fileList != null && fileList != "") {

			for (let i = 0; i < fileList.length; i++) {
				const file: (File | null) = fileList.item(i);
				if (file != null) {
					console.log(file.type);
					if (
						//file.type == "application/pdf" || file.type.startsWith("image/") || file.name.endsWith('.p7m')
						FileAllegatoComponent.validatePattern(file, this.accept)
					) {
						let newFile: FoliageFileBase64 = {
							name: file.name,
							originalName: file.name,
							size: file.size,
							storage: "base64",
							type: file.type,// "application/pdf",
							hash: ""
						};
						// this.tavole[idx].nomeFile = file.name;
						// this.tavole[idx].dimensioneFile = file.size;
						const reader = new FileReader();

						reader.addEventListener(
							"load",
							() => {
								// convert image file to base64 string
								if (reader.result) {
									newFile.url = reader.result;
									//this.file = newFile;
									this.fileChange.emit(newFile);
								}
							},
							false,
						);
						reader.readAsDataURL(file);
					}
					else {
						//const mess = "È possibile caricare soltanto file pdf, p7m o immagini";

						//const mess = `Tipologia del file non supportata. I formati accettati sono: ${this.accept}`;
						const mess = `Tipologia del file non supportata!`;
						alert(mess);
						throw new Error(mess);
					}
				}
			}
		}
	}
}