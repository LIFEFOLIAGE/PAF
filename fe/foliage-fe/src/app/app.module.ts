import { APP_INITIALIZER, LOCALE_ID, NgModule } from '@angular/core';
//import { BrowserModule } from '@angular/platform-browser';
import { CommonModule, registerLocaleData } from '@angular/common';
import { FormsModule } from '@angular/forms';

// import { Compiler, COMPILER_OPTIONS, CompilerFactory } from '@angular/core';
// import { JitCompilerFactory } from '@angular/platform-browser-dynamic';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
//import { DashboardUtenteComponent } from './components/inutili/dashboard-utente/dashboard-utente.component';
//import { ViewIstanzaComponent } from './components/inutili/view-istanza/view-istanza.component';
// import { MatButtonModule } from '@angular/material/button';
// import { MatInputModule } from '@angular/material/input';
// import { MatFormFieldModule } from '@angular/material/form-field';
// import { MatDialogModule } from '@angular/material/dialog';
//import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
//import { DialogNuovaDomandaComponent } from './components/inutili/dialog-nuova-domanda/dialog-nuova-domanda.component';
//import { MatSelectModule } from '@angular/material/select';
import { FormioRendererComponent } from './components/shared/formio-renderer/formio-rederer.component';
//import { TableModule } from './modules/table/table.module';
import { UnauthorizedComponent } from './components/shared/unauthorized/unauthorized.component';
import { EditorSchedaComponent } from './components/shared/editor-scheda/editor-scheda.component';
//import { TableRendererComponent } from './components/istanze/table-renderer/table-renderer.component';
import { AuthService, BaseAuthService, MockAuthService } from './services/auth.service';
import { OAuthModule, OAuthStorage } from "angular-oauth2-oidc";
import { HttpClientModule } from '@angular/common/http';
import { HeaderComponent } from './components/layout/header/header.component';
//import { ProfiloComponent } from './components/utenze/profilo/profilo.component';

import { ReactiveFormsModule } from '@angular/forms';
//import { SezioneUtentiComponent } from './components/utenze/sezione-utenti/sezione-utenti.component';
import { MapComponent, OlMapDirective, OlPopupDirective } from './map/map.component';
import { GisTableComponent } from './gis-table/gis-table.component';
import { AngularSplitModule } from 'angular-split';
import {
	TableComponent,
	Columns,
	Column,
	PageButtonComponent
} from './modules/table/table.component';
import {TooltipDirective, TableRowComponent}  from './modules/table/table-row/table-row.component';
import { SessionManagerService } from './services/session-manager.service';
import { SelettoreAmbitoComponent } from './selettore-ambito/selettore-ambito.component';
//import { RicercaUtentiComponent } from './ricerca-utenti/ricerca-utenti.component';
import { RicercaIstanzeComponent } from './ricerca-istanze/ricerca-istanze.component';
import { NuovaIstanzaComponent } from './nuova-istanza/nuova-istanza.component';
import { CompilaIstanzaComponent } from './compila-istanza/compila-istanza.component';
import { AccettazionePrivacyComponent } from './info/accettazione-privacy/accettazione-privacy.component';
import { RouteChangeService } from './route-change.service';
//import { CurrProfiloComponent } from './curr-profilo/curr-profilo.component';
//import { AmministrazioneProfiloComponent } from './amministrazione-profilo/amministrazione-profilo.component';
//import { RichiestaProfiloComponent } from './richiesta-profilo/richiesta-profilo.component';
import { ElencoRichiesteComponent } from './elenco-richieste/elenco-richieste.component';
import { BreadcrumbComponent } from './breadcrumb/breadcrumb.component';
import { FormRichiestaComponent } from './form-richiesta/form-richiesta.component';
import { FormUtenteComponent } from './form-utente/form-utente.component';
import { ElencoUtentiComponent } from './elenco-utenti/elenco-utenti.component';
import { PannelloAmministratoreComponent } from './pannello-amministratore/pannello-amministratore.component';
import { FormRuoliUtenteComponent } from './form-ruoli-utente/form-ruoli-utente.component';
import { CardDashboardComponent } from './components/layout/card-dashboard/card-dashboard.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { GestioneIstanzaComponent } from './gestione-istanza/gestione-istanza.component';
// import { MatExpansionModule } from "@angular/material/expansion";
// import { MatDatepickerModule } from "@angular/material/datepicker";
// import { MatNativeDateModule } from "@angular/material/core";
import { GestioneAssegnaIstruttoreComponent } from './gestione-assegna-istruttore/gestione-assegna-istruttore.component';
// import { MatSnackBar } from "@angular/material/snack-bar";
import { GestioneIstanzaInviaComponent } from './gestione-istanza-invia/gestione-istanza-invia.component';
import { EditorIstanzaComponent } from './components/istanze/editor-istanza/editor-istanza.component';
import { ValutaIstanzaComponent } from './valuta-istanza/valuta-istanza.component';
import { ComponentRendererComponent } from './components/renderer/component-renderer/component-renderer.component';
// import {
// 	TextareaComponentComponent
// } from './components/renderer/_test-components/textarea-component/textarea-component.component';
// import {
// 	InPutComponentComponent
// } from './components/renderer/_test-components/in-put-component/in-put-component.component';
// import {
// 	TableComponentComponent
// } from './components/renderer/_test-components/table-component/table-component.component';
import { ComponentHostDirective } from './components/renderer/components/component-host.directive';
import {
	VerificaLayerVincolatiComponent
} from './components/renderer/components/verifica-layer-vincolati/verifica-layer-vincolati.component';
import {
	InterventiInAmbitiNonForestaliComponent
} from './components/renderer/components/interventi-in-ambiti-non-forestali/interventi-in-ambiti-non-forestali.component';
import {
	SoprasuoloBoschivoComponent
} from './components/renderer/components/soprasuolo-boschivo/soprasuolo-boschivo.component';
import {
	AssortimentiRitraibili
} from './components/renderer/components/assortimenti-ritraibili/assortimenti-ritraibili.component';
import { environment } from 'src/environments/environment';
import { TableRendererComponent2 } from './components/istanze/table-renderer/table-renderer2.component';
import {
	StazioneForestaleComponent
} from './components/renderer/components/stazione-forestale/stazione-forestale.component';
import {
	ContiguitaTagliBoschiviComponent
} from './components/renderer/components/contiguita-tagli-boschivi/contiguita-tagli-boschivi.component';
import {
	InquadramentoVincolisticaComponent
} from './components/renderer/components/inquadramento-vincolistica/inquadramento-vincolistica.component';
import {
	DettagliUnitaOmogeneaComponent
} from './components/renderer/components/dettagli-unita-omogenea/dettagli-unita-omogenea.component';
import {
	ViabilitaForestaleComponent
} from './components/renderer/components/viabilita-forestale/viabilita-forestale.component';
import { CaricaAllegatiComponent } from './components/renderer/components/carica-allegati/carica-allegati.component';
import { RiepilogoFinaleComponent } from './components/renderer/components/riepilogo-finale/riepilogo-finale.component';
import {
	ProspettiRiepilogativiComponent
} from './components/renderer/components/prospetti-riepilogativi/prospetti-riepilogativi-component';
import {
	SchedaAltriStratiComponent
} from './components/renderer/components/scheda-altri-stati/scheda-altri-stati.component';
import { EttariInput } from './components/shared/input-ettari/input-ettari.component';
import { GestioneCambiaGestoreComponent } from './gestione-cambia-gestore/gestione-cambia-gestore.component';
import { RichiestaProrogaComponent } from './richiesta-proroga/richiesta-proroga.component';
import { CruscottoVigilanzaComponent } from './cruscotto-vigilanza/cruscotto-vigilanza.component';
import { DashboardGovernanceComponent } from './governance/dashboard-governance/dashboard-governance.component';
import { DocumentazioneIstanzaComponent } from './components/istanze/documentazione-istanza/documentazione-istanza.component';
import { MappaTavolaComponent, OlTavolaMapDirective } from './components/istanze/mappa-tavola/mappa-tavola.component';
import { BytesPipe } from "./pipe/bytes.pipe";
import { BrowserModule } from '@angular/platform-browser';
import { RilevamentoComponent } from './components/renderer/components/rilevamento/rilevamento.component';
import { FoliageStorageService } from './services/auth.storage';
import { FileAllegatoComponent } from './components/file-allegato/file-allegato.component';
import { RequestService } from './services/request.service';

// export function createCompiler(compilerFactory: CompilerFactory) {
// 	return compilerFactory.createCompiler();
// }

// const testComponentsToDelete = [
// 	InPutComponentComponent,
// 	TableComponentComponent,
// 	TextareaComponentComponent,
// ];

import localeDeAt from '@angular/common/locales/it';
import { DichiarazioneAccessibilitaComponent } from './info/dichiarazione-accessibilita/dichiarazione-accessibilita.component';
import { LoginComponent } from './login/login.component';
import { HomeComponent } from './home/home.component';
import { MonitoraggioComponent } from './monitoraggio/dashboard-monitoraggio/dashboard-monitoraggio.component';
import { ModificaRichiestaMonitoraggioComponent } from './monitoraggio/modifica-richiesta-monitoraggio/modifica-richiesta-monitoraggio.component';
import { DatiSchedulazioneMonitoraggioComponent } from './monitoraggio/dati-schedulazione-monitoraggio/dati-schedulazione-monitoraggio.component';
import { GestioneRichiestaMonitoraggioComponent } from './monitoraggio/gestione-richiesta-monitoraggio/gestione-richiesta-monitoraggio.component';
import { PrivacyV1Component } from './info/accettazione-privacy/privacy-v1/privacy_v1.component';
import { NoteLegaliComponent } from './info/note-legali/note-legali.component';
import { CreditsComponent } from './info/credits/credits.component';
import { ContactsComponent } from './info/contacts/contacts.component';
import { RichiesteGovernanceComponent } from './governance/richieste-governance/richieste-governance.components';
import { ModificaRichiestaGovernanceComponent } from './governance/modifica-richiesta-governance/modifica-richiesta-governance.component';
import { DatiSchedulazioneGovernanceComponent } from './governance/dati-schedulazione-governance/dati-schedulazione-governance.component';
import { GestioneRichiestaGovernanceComponent } from './governance/gestione-richiesta-governance/gestione-richiesta-governance.component';
import { ParticellaCatastaleComponent } from './components/renderer/components/particelle-catastali/particelle-catastali.component';
//import { AllegaModuloFirmato } from './allega-modulo-firmato/allega-modulo-firmato.component';

registerLocaleData(localeDeAt);


export function initAuthService(authService: AuthService) {
	return () => authService.load();
}

// const authProvider = {
// 	provide: APP_INITIALIZER,
// 	useFactory: initAuthService,
// 	useExisting: BaseAuthService,
// 	deps: [AuthService, BaseAuthService],
// 	multi: true,
// };


const realAuthProvider = [
	{
	  provide: OAuthStorage,
	  useClass: FoliageStorageService
	},
	{
		provide: BaseAuthService,
		useClass: AuthService
	},
	{
		provide: APP_INITIALIZER,
		useFactory: initAuthService,
		deps: [AuthService],
		multi: true,
	}
];

const mockAuthProvider = [
	{ provide: BaseAuthService, useClass: MockAuthService }
];

const authProvider = environment.useMock ? mockAuthProvider : realAuthProvider;

@NgModule({
	declarations: [
		AppComponent,
		FormioRendererComponent,
		UnauthorizedComponent,
		EditorSchedaComponent,
		TableRendererComponent2,
		HeaderComponent,
		MapComponent, OlMapDirective, OlPopupDirective, GisTableComponent,
		TableComponent,
		Columns,
		Column,
		PageButtonComponent,
		TableRowComponent,
		DashboardGovernanceComponent,
		SelettoreAmbitoComponent,
		RicercaIstanzeComponent,
		NuovaIstanzaComponent,
		CompilaIstanzaComponent,
		AccettazionePrivacyComponent,
		ElencoRichiesteComponent,
		BreadcrumbComponent,
		FormRichiestaComponent,
		FormUtenteComponent,
		ElencoUtentiComponent,
		PannelloAmministratoreComponent,
		FormRuoliUtenteComponent,
		CardDashboardComponent,
		DashboardComponent,
		GestioneIstanzaComponent,
		GestioneAssegnaIstruttoreComponent,
		GestioneIstanzaInviaComponent,
		EditorIstanzaComponent,
		ValutaIstanzaComponent,
		ComponentRendererComponent,
		ComponentHostDirective,
		SoprasuoloBoschivoComponent,
		VerificaLayerVincolatiComponent,
		InterventiInAmbitiNonForestaliComponent,
		AssortimentiRitraibili,
		StazioneForestaleComponent,
		ContiguitaTagliBoschiviComponent,
		InquadramentoVincolisticaComponent,
		DettagliUnitaOmogeneaComponent,
		ViabilitaForestaleComponent,
		CaricaAllegatiComponent,
		RiepilogoFinaleComponent,
		ProspettiRiepilogativiComponent,
		SchedaAltriStratiComponent,
		ParticellaCatastaleComponent,
		EttariInput,
		GestioneCambiaGestoreComponent,
		RichiestaProrogaComponent,
		TooltipDirective,
		CruscottoVigilanzaComponent,
		DocumentazioneIstanzaComponent,
		MappaTavolaComponent,
		OlTavolaMapDirective,
		RilevamentoComponent,
		FileAllegatoComponent,
		DichiarazioneAccessibilitaComponent,
		LoginComponent,
		HomeComponent,
		MonitoraggioComponent, ModificaRichiestaMonitoraggioComponent, DatiSchedulazioneMonitoraggioComponent, GestioneRichiestaMonitoraggioComponent,
		RichiesteGovernanceComponent, ModificaRichiestaGovernanceComponent, DatiSchedulazioneGovernanceComponent, GestioneRichiestaGovernanceComponent,
		PrivacyV1Component, NoteLegaliComponent, CreditsComponent, ContactsComponent,
		//AllegaModuloFirmato
	],
	providers: [
		...authProvider,
		RouteChangeService,
		SessionManagerService,
		RequestService,
		{
			provide: LOCALE_ID,
			useValue: "it-IT" 
		}
		//MatSnackBar,
		// // Compiler is not included in AOT-compiled bundle.
		// // Must explicitly provide compiler to be able to compile templates at runtime.
		// { provide: COMPILER_OPTIONS, useValue: {}, multi: true },
		// { provide: CompilerFactory, useClass: JitCompilerFactory, deps: [COMPILER_OPTIONS] },
		// { provide: Compiler, useFactory: createCompiler, deps: [CompilerFactory] }
	],
	bootstrap: [AppComponent],
	exports: [TableComponent, Columns, Column],
	imports: [
		HttpClientModule,
		BrowserModule,
		AppRoutingModule,
		FormsModule,
		CommonModule,
		ReactiveFormsModule,
		OAuthModule.forRoot(),
		AngularSplitModule,
		BytesPipe
	]
})
export class AppModule {
}

