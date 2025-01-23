// import { inject } from '@angular/core';
// import { Router, ActivatedRoute, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
// import { BaseAuthService } from '../services/auth.service'; 

// export const authGuardCittadino = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
// 	const authService = inject(BaseAuthService);
// 	//const router = inject(Router);
// 	//const route = inject(ActivatedRoute);
// 	//authService.lastRoute = route;
// 	console.log(state);
// 	sessionStorage.setItem("lastUrl", state.url);

// 	if (authService.isAuthenticated()) {
// 		// if (authService.userData.profilo == "cittadino") {
// 		// 	return true;
// 		// }
// 		return true;
// 	}
  
	
  
// 	authService.login();
// 	//return authService.authenticationEventObservable;
// 	return false;
//   };