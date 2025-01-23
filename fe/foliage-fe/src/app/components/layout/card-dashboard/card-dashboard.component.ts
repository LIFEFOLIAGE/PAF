import {Component, Input} from '@angular/core';
import {DashboardScheda} from "./models/dashboard-scheda";

@Component({
	selector: 'app-card-dashboard',
	templateUrl: './card-dashboard.component.html',
	styleUrls: ['./card-dashboard.component.css']
})
export class CardDashboardComponent {
	@Input('scheda') scheda: DashboardScheda | undefined;
}
