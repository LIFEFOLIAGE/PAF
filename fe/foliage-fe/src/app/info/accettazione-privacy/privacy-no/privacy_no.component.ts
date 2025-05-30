import { Component } from "@angular/core";
import { environment } from 'src/environments/environment';


@Component({
	selector: 'app-privacy_no',
	templateUrl: './privacy_no.component.html',
	styleUrls: ['./privacy_no.component.css']
})
export class PrivacyNoComponent {
	regione: string = environment.regione;
}