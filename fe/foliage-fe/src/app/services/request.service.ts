import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { Subscription, voidCallback } from './utils';

//const findEndUrl = new RegExp("(\\?)|$")

@Injectable({
	providedIn: 'root'
})
export class RequestService {
	subscription: Subscription<number> = new Subscription<number>(0);
	// static findEndUrl = new RegExp("(\\?)|$");
	// private globErrNo: number = 0;

	constructor(
	) {
	}
	public subscribe(cb: voidCallback<number>) {
		this.subscription.subscribe(cb);
	}

	progressRequest(req: Promise<any>): Promise<any> {
		this.subscription.update(+1);

		req.finally(
			() => {
				this.subscription.update(-1);
			}
		)
		return req;
	}
}
