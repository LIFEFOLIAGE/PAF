import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { BaseAuthService } from '../services/auth.service';
import { SessionManagerService } from '../services/session-manager.service';

@Component({
	selector: 'app-login',
	templateUrl: './login.component.html',
	styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit{
	authenticated: boolean = false;
	constructor(
		private router : Router,
		private sessionManager: SessionManagerService,
		private authService: BaseAuthService
	) {
	}
	ngOnInit(): void {
		const lastUrl = sessionStorage.getItem("lastUrl");
		if (lastUrl != undefined) {
			sessionStorage.removeItem("lastUrl");
			this.router.navigate([lastUrl]);
		}
		this.authenticated = (this.authService.isAuthenticated());
	}

	
	login(): void {
		this.sessionManager.logout();
		this.authService.login();
		// Promise.resolve().then(
		// 	() => {
		// 		this.reload();
		// 		this.loginLogout.emit(true);
		// 	}
		// );
	}
	logout(): void {
		// this.userLogin = undefined;
		// this.profiloSelezionato = undefined;
		this.sessionManager.logout();
		this.authService.logout();
		//this.loginLogout.emit(false);
		//this.reload();
	}
}
