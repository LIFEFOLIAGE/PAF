import { Router } from '@angular/router';
import { environment } from 'src/environments/environment';

export type BackendErrorDetails = (string | Record<string, any>);

export class BackendError extends Error {
	errNo: number = -1;
	status?: number;
	details?: BackendErrorDetails;
	get [Symbol.toStringTag]() {
			return 'BackendError';
	}
	static get [Symbol.species]() { 
		return BackendError; 
	}
	private postInit(errNo: number, status?: number, details?: BackendErrorDetails) {
		this.errNo = errNo;
		this.status = status;
		this.details = details;
	}
	constructor(errNo: number, message?: string, status?: number, details?: BackendErrorDetails, options: any = undefined) {
		super(message, options);
		const actualProto = new.target.prototype;

		if (Object.setPrototypeOf) { 
			Object.setPrototypeOf(this, actualProto); 
		} 
		else {
			(this as any).__proto__ = actualProto;
		} 
		//Object.setPrototypeOf(this, BackendError.prototype);
		this.postInit(errNo, status, details);
	}
};

const apiUrl = `${environment.apiOrigin??window.origin}${environment.apiServerPath}`;

let globErrNo = 1;
export function backendRequest(api: string, opts?: any): Promise<any> {
	return fetch(`${apiUrl}${api}`, opts)
		.then(
			res => {
				//console.log(res);
				if (res.ok) {
					const contentType = res.headers.get("content-type");
					//console.log(contentType);
					if (contentType != undefined && contentType != '') {
						if (contentType?.startsWith('text/plain')) {
							return res.text();
						}
						else {
							if (contentType?.startsWith('application/octet-stream')) {
								return res.blob();
							}
							else {
								return res.json();
							}
						}
					}
					else {
						return Promise.resolve(undefined);
					}
				}
				else {
					
					let alertMess: (string|undefined) = undefined;
					switch (res.status) {
						case 403: {
							alertMess = "Accesso non autorizzato";
						}; break;
						case 404: {
							alertMess = "Servizio non disponibile";
						}; break;
					}
					// if (alertMess != undefined) {
					// 	alert(alertMess);
					// 	console.error(alertMess);
					// }

					const errNo = globErrNo++;
					const status = `${res.status}: ${res.statusText}`;
					const errMess = new Error(`Errore(${errNo}) nel recupero dei dati da ${api} (${opts.method ?? "GET"} - ${status})`);
					//console.error(errMess);

					let bodyObj: any = undefined;
					let prom: Promise<any> = Promise.resolve();
					// if (!environment.production) {
					// }
					const body = res.body;
					if (body) {
						const reader = body.getReader();
						const utf8decoder = new TextDecoder();
						const bodyLines: string[] = [];
						type readArgs = ReadableStreamReadResult<Uint8Array>;
						// const readCb = ({ done, value }: readArgs) => {
						// 	// Is there no more data to read?
						// 	if (value) {
						// 		//console.error(Object.fromEntries([[`Errore(${errNo})`, utf8decoder.decode(value)]]));
						// 		const str = utf8decoder.decode(value).toString().replaceAll('\\n', "\n").replaceAll('\\t', "\t").replaceAll('\\r', "\r");
						// 		bodyLines.push(str);
						// 		console.error(str);
						// 	}
						// 	if (done) {
						// 		// Tell the browser that we have finished sending data
						// 		return;
						// 	}
						// };
						const read = async () => {
							let stop = false;
							while (!stop) {
								const res : ReadableStreamReadResult<Uint8Array> = await reader.read();
								if (res.value) {
									const str = utf8decoder.decode(res.value).toString().replaceAll('\\n', "\n").replaceAll('\\t', "\t").replaceAll('\\r', "\r");
									bodyLines.push(str);
									console.error(str);
								}
								stop = res.done;
							}
							return;
						};

						prom = read().then(
							() => {
								const bodyStr = bodyLines.join('');
								const bodyStrEsc = bodyStr.replace("\n", "\\n" );
								try {
									bodyObj = JSON.parse(bodyStrEsc);
									//const obj = JSON.parse(bodyStrEsc);
								}
								catch(e) {
									bodyObj = bodyStr;
								}
								
							}
						);
					}

					return prom.then(
						() => {
							//throw ;
							const exc = bodyObj?.exception;
							if (exc && exc.includes("Foliage")) {
								alertMess = bodyObj.message;
							}
							alertMess = alertMess??"Si è verificato un problema nella richiesta";
							alert(alertMess);
							const err: BackendError = new BackendError(errNo, alertMess, res.status, bodyObj);
							console.error(err);
							console.log(err);
							return Promise.reject(err);
						}
					);
				}
			},
			e => {
				//console.error(e);
				const msg = `Problemi nella connessione a ${api} (${opts.method ?? "GET"})`;
				const errNo = globErrNo++;

				const err: BackendError = new BackendError(errNo, msg, undefined, undefined, {cause: e});
				//const err = new Error(msg, { cause: e });
				console.error(err);
				//throw err;
				return Promise.reject(err);
			}
		)/*.catch(
			e => {
				//console.error(e);
				const msg = `Problemi nella connessione a ${api} (${opts.method??"GET"})`;
				const err = new Error(msg, {cause: e});
				console.error(err);
				throw err;
			}
		)*/;
}


export function assegnazioneToken(form: any, regolaRaggiungemento: any, token: string) {
	function recuperaPriv(content: any, rules: any, valoreInput: any, idx: number) {
		if (idx == 0) {
			content[rules[0]] = valoreInput;
		}
		else {
			if (typeof content == 'object') {
				let ele: any = null;
				if (Array.isArray(content)) {
					const { k, v } = rules[idx];
					ele = content.find(x => x[k] == v);
				}
				else {
					ele = content[rules[idx]];
				}
				if (ele == undefined) {
					throw new Error("La regola non raggiunge nessun elemento");
				}
				else {
					recuperaPriv(ele, rules, valoreInput, idx - 1);
				}
			}
			else {
				throw new Error("Fine raggiunta in anticipo rispetto alle regole");
			}
		}
	}

	function recupera(content: any, rules: any, valoreInput: any) {
		if (Array.isArray(rules)) {
			const length = rules.length;
			if (length > 0) {
				return recuperaPriv(content, rules, valoreInput, length - 1);
			}
		}
		throw new Error("I parametri non sono corretti");
	}

	regolaRaggiungemento.forEach(
		(arrAssCorr: any) => {
			recupera(form, arrAssCorr, token);
		}
	);
}

function deepCloneImpl(x: any, prevComps: any[]): any {
	const t = typeof (x);
	switch (t) {
		case 'undefined': {
			return undefined;
		}; break;
		case 'string': {
			return x.slice();
		}; break;
		case 'number': {
			return x;
		}; break;
		default: {
			if (prevComps.findIndex((y: any) => (y === x)) >= 0) {
				throw new Error("Impossibile copiare l'elemento");
			}
			return (Array.isArray(x)) ?
				x.map(
					(y: any) => deepCloneImpl(y, [...prevComps, y])
				)
				: Object.fromEntries(
					Object.entries(x).map(
						([k, v]: [string, any]) => (
							[
								k,
								deepCloneImpl(v, [...prevComps, v])
							]
						)
					)
				);
			// let outVal = null;
			// if (Array.isArray(x)) {
			// 	outVal = x.map(
			// 		(y: any) => deepCloneImpl(y, [...prevComps, y])
			// 	);
			// }
			// else {
			// 	outVal = Object.fromEntries(
			// 		Object.entries(x).map(
			// 			([k, v]: [string, any]) => (
			// 				[
			// 					k, 
			// 					deepCloneImpl(v, [...prevComps, v])
			// 				]
			// 			)
			// 		)
			// 	);
			// }
			// return outVal;
		}
	}
}
export function deepClone(x: any): any {
	return JSON.parse(JSON.stringify(x));
	//return deepCloneImpl(x, []);
	// const t = typeof(x);
	// switch (t) {
	// 	case 'undefined': {
	// 		return undefined;
	// 	}; break;
	// 	case 'string': {
	// 		return x.slice();
	// 	}; break;
	// 	case 'number': {
	// 		return x;
	// 	}; break;
	// 	default: {
	// 		return (Array.isArray(x)) ?
	// 			x.map(
	// 				(y: any) => deepClone(y)
	// 			)
	// 			: Object.fromEntries(
	// 				Object.entries(
	// 					(k: string, v: any) => ([k, deepClone(v)])
	// 				)
	// 			);
	// 		// let outVal = null;
	// 		// if (Array.isArray(x)) {
	// 		// 	outVal = x.map(
	// 		// 		(y: any) => deepClone(y)
	// 		// 	);
	// 		// }
	// 		// else {
	// 		// 	outVal = Object.fromEntries(
	// 		// 		Object.entries(
	// 		// 			(k: string, v: any) => ([k, deepClone(v)])
	// 		// 		)
	// 		// 	);
	// 		// }
	// 	}
	// }
}


export type voidCallback<T> = ((val: T|undefined) => void);
export class Subscription<T> { 
	
	private idxSubscription: number = 0;
	private currVal?: T;
	private subscribers: Record<number, voidCallback<T>> = {};
	constructor(initVal?: T) {

	}
	public subscribe (cb: voidCallback<T>) {
		this.subscribers[this.idxSubscription++] = cb;
		return this.idxSubscription;
	}
	public unsubscribe(idx: number) {
		const sub: (voidCallback<T>|undefined) = this.subscribers[idx];
		if (sub == undefined) {
			throw new Error(`Subscription ${idx} not present`);
		}
		else {
			delete this.subscribers[idx];
		}
	}
	public update(newVal?: T) {
		this.currVal = newVal;
		Object.values(this.subscribers).forEach(
			(s: voidCallback<T>) => {
				s(newVal);
			}
		)
	}
	public getValue() {
		return this.currVal;
	}
}




export function ngReloadComponent(
	router: Router,
	self: boolean = true,
	urlToNavigateTo ?:string
){
	//skipLocationChange:true means dont update the url to / when navigating
	console.log("Current route I am on:",router.url);
	const url=self ? router.url :urlToNavigateTo;
	router.navigateByUrl(
			'/',
			{
				skipLocationChange:true
			}
		).then(
			()=>{
				router.navigate([`/${url}`]).then(
					()=>{
						console.log(`After navigation I am on:${router.url}`)
					}
				)
			}
		);
 }