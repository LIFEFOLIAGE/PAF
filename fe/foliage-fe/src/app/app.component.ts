import { Component, OnInit } from '@angular/core';
import { BaseAuthService } from './services/auth.service';
import { LoggingService, LogLevel } from './services/logging.service';
import { Router } from '@angular/router';
import { RequestService } from './services/request.service';
import { TitleService } from './services/title.service';

enum AuthState {
	NotAuthenticated = "notAuthenticated",
	HasPrivacy = "hasPrivacy",
	Authenticated = "authenticated"
}

@Component({
	selector: 'app-root',
	templateUrl: './app.component.html',
	styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
	title = 'foliage-fe';
	concurringRequests: number = 0;
	// isAuthenticated: boolean = false;
	// hasPrivacy: boolean = false;

	stateNotAuthenticated = AuthState.NotAuthenticated;
	stateHasPrivacy = AuthState.HasPrivacy;
	stateAuthenticated = AuthState.Authenticated;
	
	userState: AuthState;
	isReady = false;

	constructor(
		private logger: LoggingService,
		private authService: BaseAuthService,
		public requestService: RequestService,
		public titleService: TitleService,
		private router: Router
	) {

		logger.logLevel = LogLevel.Warning;
		this.userState = AuthState.NotAuthenticated;
		this.requestService.subscribe(
			(val?: number) => {
				Promise.resolve().then(
					() => {
						this.concurringRequests += val??0;
					}
				);
			}
		);
	}

	ngOnInit(): void {
		this.isReady = false;
		this.onLogin();
	}

	cercaPrivacy() {
		console.log("cercaPrivacy");
		this.authService.cercaAccettazionePrivacy().then(
			res => {
				// this.isAuthenticated = true;
				// this.hasPrivacy = res;

				this.userState = res ? AuthState.HasPrivacy : AuthState.Authenticated;
				// this.userState = "authenticated"
			}
		).finally(() => {
			this.isReady = true;
		});
	}

	onLogin() {
		console.log("onLogin");
		this.authService.onDisconnection().then(
			() => {
				this.router.navigate(['/']);
				this.userState =  this.stateNotAuthenticated;
			}
		);

		// this.cercaPrivacy();
		
	}
	
	onLoginLogout(event: boolean) {
		if (event) {
			this.onLogin()
		}
	}
}
