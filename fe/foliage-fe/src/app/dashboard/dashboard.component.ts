import {Component, OnDestroy, OnInit} from '@angular/core';
import {SessionManagerService} from "../services/session-manager.service";
import {
  AllDashboardSchede,
  DashboardScheda
} from "../components/layout/card-dashboard/models/dashboard-scheda";
import { BreadcrumbModel } from 'src/app/models/breadcrumb';
import { BreadcrumbService } from 'src/app/services/breadcrumb.service';
import { TitleService } from 'src/app/services/title.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit, OnDestroy {

  private changeProfiloListener?: number = undefined;

  private _currentProfilo: any = undefined;
  get currentProfilo(): any {
    return this._currentProfilo;
  }

  set currentProfilo(value: any) {
    this._currentProfilo = value;
    this.refreshEnabledSchede()
  }

  enabledSchede: DashboardScheda[] = []

  constructor(
    private sessionManager: SessionManagerService,
		private breadcrumbService: BreadcrumbService,
		private titleService: TitleService
  ) {
  }

  refreshEnabledSchede() {
    this.enabledSchede = AllDashboardSchede.filter(scheda => {
      return scheda.ruoliAbilitati.includes(this._currentProfilo.authority) // TODO: verificare se includes si puo usare
    });
    console.log(this.enabledSchede)
  }

  ngOnInit(): void {
    // this.currentProfilo = {
    //   authority: "DIRI"
    // }
    
		const breadcrumbModel = new BreadcrumbModel(
			[],
			undefined,
      'bi bi-house'
		);
		this.breadcrumbService.breadcrumb = breadcrumbModel;
		this.titleService.title = "Life Foliage"


    this.sessionManager.getCurrProfilo()
      .then((profilo: any) => {
          this.currentProfilo = profilo;
        }
      );
    this.changeProfiloListener = this.sessionManager
      .addListener('changeProfilo',
        (newProfilo: any) => {
          this.currentProfilo = newProfilo;
        }
      );
  }

  ngOnDestroy(): void {
    if (this.changeProfiloListener != undefined) {
      this.sessionManager.removeListener('changeProfilo', this.changeProfiloListener);
    }
  }
}
