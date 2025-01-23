import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { ComponentDataType, ComponentType } from "../ComponentInterface";
import { SimpleObjectChange } from "../../../shared/editor-scheda/editor-scheda.component";
import { IstanzaComponentInterface } from "../../../interfaces/istanza-component-interface";
import { BaseAuthService } from 'src/app/services/auth.service';
import { ExportValue } from 'src/app/components/istanze/editor-istanza/editor-istanza.component';

@Component({
	selector: 'app-prospetti-riepilogativi',
	templateUrl: './prospetti-riepilogativi-component.html'
})
export class ProspettiRiepilogativiComponent implements ComponentType<SimpleObjectChange>, OnChanges {
	protected readonly Number = Number;

	@Input() dati: ComponentDataType = {};
	@Input() isReadOnly: boolean = false;
	@Input() context: any;
	@Input() resources: any;
	@Input() componentOptions: any;
	@Input() dictionariesData?: Record<string, any>;

	@Output() changeEdit: EventEmitter<boolean> = new EventEmitter<boolean>();
	@Output() dataChanged: EventEmitter<SimpleObjectChange> = new EventEmitter<SimpleObjectChange>();
	@Output() componentInit: EventEmitter<IstanzaComponentInterface> = new EventEmitter<IstanzaComponentInterface>();
	@Output() export: EventEmitter<ExportValue> = new EventEmitter<ExportValue>();

	private _assortimentiRitraibiliInterface?: IstanzaComponentInterface;

	codIstanza!: string;
	datiEffettivi: any = {};
	errori: Record<string, (string | boolean)> = {};
	modifiche: SimpleObjectChange = {};
	datiAssortimenti: any = {};

	gruppi = [
		{
			nome: "PRES",
			header: "Presenti",
			isReadOnly: false
		},
		{
			nome: "RILASCIA",
			header: "Da Rilasciare al Taglio",
			isReadOnly: false
		},
		{
			nome: "TAGLIA",
			header: "Da Tagliare",
			isReadOnly: true
		}
	];

	unitaMisura = [
		{
			nome: "N",
			header: "n/ha"
		},
		{
			nome: "Mc",
			header: "m³/ha"
		}
	];

	categorieCeduo = [
		{
			nome: "POLL",
			header: "Polloni"
		},
		{
			nome: "ALL",
			header: "Allievi"
		},
		{
			nome: "MATR2",
			header: "Matricine di 2° turno"
		},
		{
			nome: "MATR3",
			header: "Matricine di 3° turno"
		}
	];

	categorieFustaia = [
		{
			nome: "specie0",
			headerKey: "nomeSpecie0"
		},
		{
			nome: "specie1",
			headerKey: "nomeSpecie1"
		},
	];

	metodiDiCubatura = [
		{
			chiave: "A",
			label: "Tavole di cubatura locale a 1 entrata",
		},
		{
			chiave: "B",
			label: "Tavole di cubatura locale a 2 entrate",
		},
		{
			chiave: "C",
			label: "Sistema di tariffe",
		},
		{
			chiave: "D",
			label: "Tavole di cubature a doppia entrata o equazioni allometriche dell’IFNI 1985",
		},
		{
			chiave: "E",
			label: "Tavole di cubature a doppia entrata o equazioni allometriche dell’INFC 2005 e 2015 (Tabacchi et al. 2011)",
		},
		{
			chiave: "F",
			label: "Albero modello",
		},
		{
			chiave: "G",
			label: "Altro metodo",
		},
	];

	// categorie = [
	// 	{
	// 		chiave: 1,
	// 		label: "BOSCHI DI ABETE BIANCO"
	// 	},
	// 	{
	// 		chiave: 2,
	// 		label: "ALTRI BOSCHI CADUCIFOGLI"
	// 	}
	// ];

	// sottocategorie = [
	// 	{
	// 		chiave: 1,
	// 		label: "Abetina a Campanula"
	// 	},
	// 	{
	// 		chiave: 2,
	// 		label: "Abetina e abeti-faggeta a Vaccinium e Maianthemum"
	// 	}
	// ];

	showDescrizioneMetodo: boolean = false;
	showTabellaCeduo: boolean = false;
	showTabellaFustaia: boolean = false;
	//initPromise!: Promise<any>;

	categorie!: any[];
	idxCategorie: any;
	idxSottoCategorie: any;
	currCategoria: any = undefined;
	private metodiCubaturaCheRichiedonoDescrizione = ["A", "B", "C", "G"];

	constructor(private authService: BaseAuthService) {
		// this.initPromise = this.authService.authFetch('/istanze/sottocategorie').then(
		// 	(res) => {
		// 		this.categorie = res.categorie.map((c: any) => ({...c, sottocategorie: []}));
		// 		this.idxCategorie = Object.fromEntries(
		// 			this.categorie.map((c:any) => [c.id_categoria, c])
		// 		);
		// 		this.idxSottoCategorie = {};
		// 		res.sottocategorie.forEach(
		// 			(s: any) => {
		// 				const cat = this.idxCategorie[s.id_categoria];
		// 				if (cat != undefined) {
		// 					cat.sottocategorie.push(s);
		// 				}
		// 				this.idxSottoCategorie[s.id_sottocategoria] = s;
		// 			}
		// 		);
		// 	}
		// );
	}
	ngOnChanges(changes: SimpleChanges): void {
		for (let propName in changes) {
			const currValue = changes[propName].currentValue;
			switch (propName) {
				case "context": {
					if (currValue != undefined) {
						this.codIstanza = currValue.codIstanza;
					}
				}; break;
				case "dati": {
					this.datiEffettivi = { ...currValue };
					this.datiAssortimenti = { ...currValue };
					this.errori = {};
					this.modifiche = {};

					// Per i metodi A, B, C, G: l’utente deve inserire una descrizione del metodo utilizzato in un campo libero
					this.showDescrizioneMetodo = this.datiEffettivi['metodoDiCubatura']
						&& this.metodiCubaturaCheRichiedonoDescrizione.includes(this.datiEffettivi['metodoDiCubatura']);

					this.showTabellaCeduo = this.datiEffettivi['formaDiGoverno'] != 'Fustaia';
					this.showTabellaFustaia = this.datiEffettivi['formaDiGoverno'] != 'Ceduo';
					if (this.datiEffettivi.sottocategoria != undefined) {
						this.currCategoria = this.idxCategorie[this.idxSottoCategorie[currValue.sottocategoria].id_categoria];
						this.datiEffettivi.categoria = this.currCategoria.id_categoria;
					}
					else {
						this.currCategoria = undefined;
					}
					const idxSpeci = this.resources?.speciForestali?.idxSpeci;
					if (idxSpeci) {
						['specie0', 'specie1'].forEach(
							s => {
								const idSpecie = this.datiEffettivi[s];
								const specie = idxSpeci[idSpecie];
								if (specie) {
									const key = 'nome' + s[0].toUpperCase() + s.substring(1);
									this.datiEffettivi[key] = specie.nome_specie;
								}
							}
						);
					}
				}; break;
				// case "resources": {
				// 	const sottocategorie = currValue.sottocategorie;
				// 	if (sottocategorie) {
				// 		this.categorie = sottocategorie.categorie;
				// 		this.idxCategorie = sottocategorie.idxCategorie;
				// 		this.idxSottoCategorie = sottocategorie.idxSottoCategorie;
				// 	}
				// }
			}
		}
		this.checkErrors();
		// this.initPromise.then(
		// 	() => {
		// 	}
		// );
	}

	getKeyForTableField(nomeCategoria: string, nomeGruppo: string, nomeUnita: string) {
		return `${nomeCategoria}${nomeGruppo}${nomeUnita}`;
	}

	onAssortimentiChanged(event: SimpleObjectChange) {
		//console.log(event);
		Object.entries(event).forEach(
			([k, v]: [string, number]) => {
				this.changeProprieta(k, v);
			}
		);
		//this.datiEffettivi = {...this.datiEffettivi, ...event};
	}

	changeProprieta(nome: string, newVal: any) {
		this.datiEffettivi[nome] = newVal;
		this.checkErrors();

		if (!['nomeSpecie0', 'nomeSpecie1'].includes(nome)) {
			this.modifiche = {...this.modifiche };
			this.modifiche[nome] = newVal;
			this.dataChanged.emit(
				this.modifiche
			);
		}
	}

	initDiffCategorie(categorie: any[], catkey: string) {
		for (let idxCat = 0; idxCat < categorie.length; idxCat++) {
			for(let idxGrup = 0; idxGrup < this.gruppi.length; idxGrup++) {
				for(let idxUnit = 0; idxUnit < this.unitaMisura.length; idxUnit++) {
					
					const key = this.getKeyForTableField(
						categorie[idxCat][catkey],
						this.gruppi[idxGrup].nome,
						this.unitaMisura[idxUnit].nome
					);
					//this.totals[key] = this.datiEffettivi[key];
				}
			}
		}
		for(let idxGrup = 0; idxGrup < this.gruppi.length; idxGrup++) {
			for(let idxUnit = 0; idxUnit < this.unitaMisura.length; idxUnit++) {

			}
		}
	}
	initCubatura() {
		this.initDiffCategorie(this.categorieCeduo, 'nome');
		this.initDiffCategorie(this.categorieFustaia, 'headerKey');
	}

	changeValoreCubatura(nomeGoverno: string, nomeCategoria: string, nomeGruppo: string, nomeUnita: string, newStrVal?: string) {
		// this.errori = {}; // verficare perche elimina tutti anche quelli sulle altre righe

		// Gestione del valore numerico in input
		const newVal: number = (newStrVal == undefined) ? 0 : Number.parseFloat(newStrVal);
		const nomeToChange = this.getKeyForTableField(nomeCategoria, nomeGruppo, nomeUnita);
		this.changeProprieta(nomeToChange, newVal);
		if (isNaN(newVal) || newVal < 0) {
			this.errori[nomeToChange] = true;
		}
		else {
			delete this.errori[nomeToChange];
		}

		// Calcolo differenza per determinare il valore del gruppo Da Tagliare della cateforia modificata e per la forma di governo modificata
		const presVal: number = (nomeGruppo == 'PRES') ? newVal : this.datiEffettivi[this.getKeyForTableField(nomeCategoria, 'PRES', nomeUnita)]??0;
		const rilasciaVal: number = (nomeGruppo == 'RILASCIA') ? newVal : this.datiEffettivi[this.getKeyForTableField(nomeCategoria, 'RILASCIA', nomeUnita)]??0;
		const tagliaVal: number = presVal - rilasciaVal;
		const tagliaKey = this.getKeyForTableField(nomeCategoria, 'TAGLIA', nomeUnita);
		if (isNaN(tagliaVal) || tagliaVal < 0) {
			this.errori[tagliaKey] = true;
		}
		else {
			delete this.errori[tagliaKey];
		}
		this.changeProprieta(tagliaKey, tagliaVal);

		// Calcolo della somma per determinare il totale del gruppo modificato per la forma di governo modificata
		const categorie = (nomeGoverno == 'CEDUO') ? this.categorieCeduo : this.categorieFustaia;
		const totKey = this.getKeyForTableField('tot' + nomeGoverno, nomeGruppo, nomeUnita);
		const totVal = categorie.reduce(
			(prev: number, categoria: any) => {
				const field = this.getKeyForTableField(categoria.nome, nomeGruppo, nomeUnita);
				const val = Number.parseFloat(this.datiEffettivi[field]??0)??0;
				//console.log(Object.fromEntries([[field, val]]));
				return (
					prev
					+
					val
				);
			},
			0
		);
		this.changeProprieta(totKey, totVal);

		// Calcolo differenza per determinare il totale del Da Tagliare per la forma di governo modificata
		const presGovVal: number = this.datiEffettivi[this.getKeyForTableField('tot' + nomeGoverno, 'PRES', nomeUnita)]??0;
		const rilasciaGovVal: number = this.datiEffettivi[this.getKeyForTableField('tot' + nomeGoverno, 'RILASCIA', nomeUnita)]??0;
		const tagliaGovVal: number = presGovVal - rilasciaGovVal;
		const tagliaGovKey = this.getKeyForTableField('tot' + nomeGoverno, 'TAGLIA', nomeUnita);
		this.changeProprieta(tagliaGovKey, tagliaGovVal);



		if (nomeUnita == 'Mc') {

			// Calcolo del volume del gruppo modificato e per la forma di governo modificata
			const superficie : number = this.datiEffettivi.superficieUtile;
			const volumeVal : number = Number.parseFloat(((totVal * superficie) / 10000).toFixed(10));
			const volumeKey = this.getKeyForTableField('vol' + nomeGoverno, nomeGruppo, nomeUnita);
			this.changeProprieta(volumeKey, volumeVal);


			// Calcolo del volume del gruppo da tagliare per la forma di governo modificata
			const totTagliaVal = categorie.reduce(
				(prev: number, categoria: any) => {
					const field = this.getKeyForTableField(categoria.nome, 'TAGLIA', nomeUnita);
					const val = Number.parseFloat(this.datiEffettivi[field]??0)??0;
					//console.log(Object.fromEntries([[field, val]]));
					return (
						prev
						+
						val
					);
				},
				0
			);
			const volTagliaVal: number = Number.parseFloat(((totTagliaVal * superficie)  / 10000).toFixed(10));
			const volTagliaKey = this.getKeyForTableField('vol'+ nomeGoverno, 'TAGLIA', nomeUnita);
			this.changeProprieta(volTagliaKey, volTagliaVal);

			// Calcolo del volume totale di tutti gruppi
			this.gruppi.forEach(
				(gruppo) => {
					// const volCeduoVal: number = (nomeGoverno == 'CEDUO')  ? volTagliaVal : this.datiEffettivi[this.getKeyForTableField('volCEDUO', gruppo.nome, nomeUnita)]??0;
					// const volFustaiaVal: number = (nomeGoverno == 'FUSTAIA')  ? volTagliaVal : this.datiEffettivi[this.getKeyForTableField('volFUSTAIA', gruppo.nome, nomeUnita)]??0;
					const volCeduoVal: number = this.datiEffettivi[this.getKeyForTableField('volCEDUO', gruppo.nome, nomeUnita)]??0;
					const volFustaiaVal: number = this.datiEffettivi[this.getKeyForTableField('volFUSTAIA', gruppo.nome, nomeUnita)]??0;
					const volTotVal: number = volCeduoVal + volFustaiaVal;
					const volTotKey = this.getKeyForTableField('volTOTALE', gruppo.nome, nomeUnita);
					this.changeProprieta(volTotKey, volTotVal);
				}
			);
		}
	}

	onChangeMetodoDiCubaturaById($event: any) {
		this.changeProprieta('metodoDiCubatura', $event);

		this.showDescrizioneMetodo = this.metodiCubaturaCheRichiedonoDescrizione.includes($event);
		this.changeProprieta('descrizioneMetodoCubatura', "");
	}

	onChangeDescrizioneMetodoDiCubatura($event: any) {
		this.changeProprieta('descrizioneMetodoCubatura', $event);
	}

	onChangeCategoriaById(idCategoria: number) {
		delete this.errori['categoria'];
		this.currCategoria = this.idxCategorie[idCategoria];
		this.changeProprieta('categoria', idCategoria);
		this.changeProprieta('sottocategoria', undefined);
	}

	onChangeSottocategoriaById($event: any) {
		delete this.errori['categoria'];
		delete this.errori['sottocategoria'];
		this.changeProprieta('sottocategoria', $event);
	}

	onInitAssortimentiRitraibili($event: IstanzaComponentInterface) {
		this._assortimentiRitraibiliInterface = $event;
		this.componentInit.emit({ getValidity: this.getValidity.bind(this) });
	}

	checkErrors() {
		[/*'categoria', 'sottocategoria',*/ 'metodoDiCubatura'].forEach(
			(nome) => {
				if (this.datiEffettivi[nome] == undefined) {
					this.errori[nome] = "Valore richiesto";
				}
				else {
					delete this.errori[nome];
				}
			}
		);
		if (this.showDescrizioneMetodo && !this.datiEffettivi['descrizioneMetodoCubatura']) {
			this.errori['descrizioneMetodoCubatura'] = "Valore richiesto";
		}
		else {
			delete this.errori['descrizioneMetodoCubatura'];
		}


		let categorieDaValutare: any[] = [];

		if (this.showTabellaCeduo) {
			categorieDaValutare = categorieDaValutare.concat(this.categorieCeduo);
		}

		if (this.showTabellaFustaia) {
			// se una specie non è definita non la considero nel filtro
			this.categorieFustaia.forEach(cf => {
				if (this.datiEffettivi[cf.headerKey]) {
					categorieDaValutare.push(cf);
				}
			});
		}

		// ciclo su tutte le chiavi delle tabelle per vedere se sono valorizzate
		categorieDaValutare.forEach((c: { nome: string; }) => {
			this.gruppi.forEach(g => {
				this.unitaMisura.forEach(um => {
					const key = this.getKeyForTableField(c.nome, g.nome, um.nome);
					if (this.datiEffettivi[key] != undefined && this.datiEffettivi[key] < 0) {
						this.errori[key] = true;
					}
					else {
						delete this.errori[key];
					}
				});
			});
		});
	}

	getValidity: () => boolean = () => {
		const assortimentiRitraibiliValid = this._assortimentiRitraibiliInterface?.getValidity() ?? false;
		this.checkErrors();

		// nessun errore
		const hasNoErrors = Object.keys(this.errori).length == 0;
		
		return assortimentiRitraibiliValid && hasNoErrors /*&& areAllTablesFull*/;
	};
	// getTotaleCategoria(categorie: any[], gruppo: string, unita: string) {
	// 	return categorie.reduce(
	// 		(prev: number, categoria: any) => {
	// 			const field = this.getKeyForTableField(categoria.nome, gruppo, unita);
	// 			const val = Number.parseFloat(this.datiEffettivi[field]??0)??0;
	// 			//console.log(Object.fromEntries([[field, val]]));
	// 			return (
	// 				prev
	// 				+
	// 				val
	// 			)
	// 		},
	// 		0
	// 	);
	// }
}
