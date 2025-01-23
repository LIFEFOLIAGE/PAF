import { Component, OnInit } from "@angular/core";
import { BaseAuthService } from "../services/auth.service";
import { Router } from "@angular/router";

@Component({
	selector: 'app-home',
	templateUrl: './home.component.html'
})
export class HomeComponent implements OnInit {
	constructor(
		private authService: BaseAuthService,
		private router: Router
	) {
	}
	ngOnInit(): void {
		if (this.authService.isAuthenticated()) {
			const redirectUrl = this.authService.postLoginRedirection;
			this.authService.postLoginRedirection = undefined;
			if (redirectUrl) {
				console.log(redirectUrl);
				this.router.navigate([redirectUrl]);
			}
			else {
				this.router.navigate(['dashboard']);
			}
		}
		else {
			this.router.navigate(['login']);
		}
	}
  }