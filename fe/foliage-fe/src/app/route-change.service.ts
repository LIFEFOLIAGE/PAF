import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class RouteChangeService {
  constructor(private router: Router) {
    router.events.subscribe((event) => {
      console.log(event);
    });
  }
  
}
