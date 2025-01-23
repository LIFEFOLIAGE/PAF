import { Component, Input, Output, EventEmitter, OnChanges, SimpleChanges, OnInit, ViewChild, ElementRef, QueryList } from '@angular/core';
//import { BaseMap } from '../map/base-map';
import { InteractiveMap } from '../map/interactive-map';
//import { TipoDatiScheda } from '../components/shared/editor-scheda/editor-scheda.component';
import { RowData } from '../modules/table/table.component';
import { Scheda } from '../components/shared/editor-scheda/editor-scheda.component';
import { BaseIstanzaComponent, IstanzaComponentInterface } from "../components/interfaces/istanza-component-interface";
import { SessionManagerService } from "../services/session-manager.service";
import { LayerBE } from "./models/layer-be";
import { HtmlService } from '../services/html.service';
import { ExportValue } from '../components/istanze/editor-istanza/editor-istanza.component';
import { BaseAuthService } from '../services/auth.service';


const allGeomChoices = [
	{
		label: "Punti",
		value: "Point"
	},
	{
		label: "Linee",
		value: "LineString"
	},
	{
		label: "Poligoni",
		value: "Polygon"
	}
];

export enum MapStdAction {
	Remove = "Remove",
	ModifyAttributes = "ModifyAttributes",
	ModifyGeometry = "ModifyGeometry",
	Cut = "Cut",
	Split = "Split",
	Join = "Join",
	New = "New"
}

export enum MapLayerType {
	Wms = "wms",
	Wfs = "wfs",
	DictionaryLayer = "dictionary"
}
@Component({
	selector: 'app-gis-table',
	templateUrl: './gis-table.component.html',
	styleUrls: ['./gis-table.component.css']
})
export class GisTableComponent implements OnChanges, OnInit, BaseIstanzaComponent {
	public selectedRow : any;
	public highlightedRow : any;
	private static idGen = 0;
	public getId() {
		return --GisTableComponent.idGen;
	}
	rigaModificata: any;
	idxModifica?: number =undefined;
	selectedRowIdx? : number;
	highlightedRowIdx? : number;
	map?: InteractiveMap;
	message: string = "";
	completamentoAzione: any[] = [];
	inserting: any = undefined;
	updating: boolean  = false;
	choices : any[] = [];
	currentChoice: any = undefined;
	//beLayers: LayerBE[] = [];
	onErrori: boolean = false;
	errori: {idx: number, msgs: string[]}[] = [];
	

	_conf: any = {};
	@Input()
	set conf(value: any) {
		this.confTabella = {columns: value.columns, constraints: value.constraints};
		this.confMappa = {
			mappa: value.mappa,
			minSize: value.minSize,
			maxSize: value.maxSize,
			areaConstraint: value.areaConstraint
		};
		this.schedaInfo = value.schedaInfo;
		// {
		// 	nome: "Scheda Gis",
		// 	tipo: "formio",
		// 	conf: value.schedaInfo,
		// 	tipoDati: TipoDatiScheda.Object,
		// 	dictionariesData: value.dictionariesData
		// };

		this._conf = value;
		this.errori = [];
	}
	get conf() : any {
		return this._conf;
	}
	@Input() nomeMappa?: string;
	@Input() isReadOnly: boolean = false;
	@Input() dati: any[] = [];
	@Input() context: any;
	@Input() rilevamenti: any;
	@Input() resources: any;
	@Input() dictionariesData?: Record<string, any>;
	@Output() readonly dataChanged = new EventEmitter<{pos?: number, value: any}>();
	@Output() readonly tableChanged = new EventEmitter<{pos?: number, value: any}[]>();
	@Output() readonly changeEdit = new EventEmitter<boolean>();
	@Output() readonly componentInit: EventEmitter<IstanzaComponentInterface> = new EventEmitter<IstanzaComponentInterface>();
	@Output() readonly export = new EventEmitter<ExportValue>();
	
	
	_importedValue: any;
	@Input() set importedValue (value: any) {
		this._importedValue = value;
	}

	//@ViewChild('importGeoJsonFile') importGeoJson!: ElementRef<HTMLInputElement>;
	@ViewChild('exportGeoJsonlink') exportGeoJson!: ElementRef<HTMLAnchorElement>;
	constructor(
		public html: HtmlService,
		private authService: BaseAuthService,
		private sessionManager: SessionManagerService
	) {
	}
	confTabella: any = {
		columns: []
	};
	confMappa: any = {
	}
	schedaInfo? : Scheda = undefined;

	onMapDataChanged(changes: {pos?: number, value: any}) {
		console.log(changes);
		this.dataChanged.emit(changes);
	}
	onMapCancelChanges(data: any) {
		this.message = "";
		this.setAzioniCompletamento([]);
		this.inserting = undefined;
		if (this.map) {
			this.map.stopDrawing();
			this.map.mostraOverlay(undefined);
		}
	}
	onMapSelection(row?: RowData) {
//		console.log({idx, row});
		this.selectedRow = row?.data;
		this.selectedRowIdx = row?.idx;
	}
	onMapHighlight(row?: RowData) {
//		console.log({idx, row});
		this.highlightedRow = row?.data;
		this.highlightedRowIdx = row?.idx;
	}
	onTabSelection(row?: RowData) {
		//console.log({onTabSelection: {idx, row}});
		this.selectedRow = row?.data;
		this.selectedRowIdx = row?.idx;
		if (this.map) {
			this.map.seleziona(this.selectedRow, this.selectedRowIdx);
		}
	}
	onTabHighlight(row?: RowData) {
//		console.log({idx, row});
		this.highlightedRow = row?.data;
		this.highlightedRowIdx = row?.idx;
		if (this.map) {
			this.map.evidenzia(row?.data, row?.idx);
		}
	}
	onMapCreated(map: InteractiveMap) {
		this.map = map;
		if (this._importedValue) {
			const res = map.importWkt(this._importedValue.value, this._importedValue?.opts?.dataProjection, this._conf.singleGeometries, this._conf.geometries);
			if (res == undefined) {
				//alert("Si è verificato un problema");
			}
			else {
				res.then(
					this.importaElementi.bind(this),
					(e: any) => {
						//alert("Si è verificato un problema");
						console.error(e);
					}
				);
			}
		}
		this.checkValidity();
	}


	onExport(value: ExportValue) {
		console.log(value);
		this.export.emit(value);
	}

	ngOnChanges(changes: SimpleChanges) {
		console.log(changes);
		for (let propName in changes) {
			const v = changes[propName].currentValue;
			switch (propName) {
				case "dati": {
					if (this.dati) {
						if (this.map) {
							if (this.inserting || this.updating) {
								this.setAzioniCompletamento([]);
								this.inserting = undefined;
								this.updating = false;
								this.map.stopDrawing();
								this.message = "";
							}
							// this.map.updateData(this.confMappa, this.dati);
							// this.map.mostraOverlay(undefined);
						}
					}
					if (this.selectedRow) {
						const idx = this.dati.findIndex(x => x == this.selectedRow);
						if (idx && idx != -1) {
							this.selectedRow = this.dati[idx];
							this.selectedRowIdx = idx; //TODO: controllare...." = idx;" aggiunto dop una vita
						}
						else {
							this.selectedRowIdx = undefined;
							this.selectedRow = undefined;
							//throw new Error("Selezione non trovata");
						}
					}
					if (this.map && this.selectedRowIdx != undefined && this.selectedRowIdx >= 0) {
						this.map.seleziona(this.selectedRow, this.selectedRowIdx);
					}
					this.rigaModificata = undefined;
					this.idxModifica = undefined;
				}; break;
				// case "context": {
				// 	if (v != undefined) {
				// 		let layers: (undefined|LayerBE[]) = v.shared?.layers;
				// 		if (layers == undefined) {
				// 			// this.sessionManager.profileFetch(
				// 			// 	`/istanze/${this.context.codIstanza}/layer`
				// 			// ).then(
				// 			// 	(results: LayerBE[]) => {
				// 			// 		this.beLayers = results;

				// 			// 		if (v.shared != undefined) {
				// 			// 			v.shared.layers = results;
				// 			// 		}
				// 			// 		//console.log("recuperato layer istanza", results)
				// 			// 	},
				// 			// 	(err) => {
				// 			// 		console.log("errore recupero layers", err)
				// 			// 	}
				// 			// );
				// 		}
				// 		else {
				// 			this.beLayers = {...layers};
				// 		}
				// 	}
				// }; break;
				case "resources": {
					if (this._conf.trigger && this._conf.trigger.init) {
						this._conf.trigger.init(this, v);
					}
				}; break;
			}
		}

		Promise.resolve().then(this.checkErrori.bind(this));
		// this.additionalLayers = {
		// 	"Gruppo 1": [nomeBoschiLayer, nomeUsiCiviciLayer],
		// 	"Gruppo 2": [nomeCostaLaghiLayer]
		// }
	}

	onChangeChoice(e: any) {
		if (this.map) {
			this.currentChoice = e.target.value;
			this.map.changeDrawingMode(this.currentChoice);
		}
	}


	getGeomChoices() {

		//console.log(this._conf);
		const geoms = this._conf.geometries;

		const outVal = (geoms == undefined) ? allGeomChoices : allGeomChoices.filter(g => geoms.includes(g.label));

		if (outVal.length > 0) {
			return outVal;
		}
		else {
			return allGeomChoices;
		}
	}

	setAzioniCompletamento(azioni: any[]) {
		this.completamentoAzione = azioni;
		this.changeEdit.emit(azioni.length > 0);
	}

	exeTrigger(name: string, pars: any): Promise<any> {
		const trigger : (c: any, p: any) => Promise<any> = this._conf.trigger[name];
		if (trigger) {
			return trigger(this, pars);
		}
		else {
			return Promise.resolve(undefined);
		}
	}

	splitTrigger(pars: any) : Promise<any>[] {
		const trigger = this._conf.trigger.split;
		if (trigger) {
			return trigger(this, pars);
		}
		else {
			return [];
		}
	}
	inquadra() {
		const map = this.map;
		if (map != undefined) {
			map.inquadraGeometrie(this.context, this.confMappa);
		}
	}
	importa() {
		const element = document.createElement('input');
		element.type = 'file';

		
		element.onchange = (e: any) => {
			// getting a hold of the file reference
			let file = e.target.files; 

			this.allegaGeoJsonImportati(file);
		}
		
		if (element != undefined) {
			element.files = null;
			element.click();
		}
		else {
			//alert("Si è verificato un problema");
		}
	}

	importaElementi(addedFeats: any[]) {
		console.log(addedFeats);
		const map = this.map;
		if (map) {
			const attr = this.conf.mappa.shape?.areaAttribute
			const proms = addedFeats.map(
				(feat: any, idx: number) => {
					const newVal = Object.fromEntries(
						[
							[
								this.confMappa.mappa.shape.attribute,
								feat.wkt
							]
						]
					);
					if (attr) {
						newVal[attr] = feat.area;
					}
					return new Promise(
						(resolve, reject) => {
							return this.exeTrigger("insert", newVal).then(
								(res: any) => {
									if (res != undefined) {
										reject();
									}
									console.log(Object.fromEntries([[idx, res]]));
									resolve(newVal);
								}
							)
							.catch(
								(errMsg: any) => {
									if (errMsg) {
										reject(errMsg);
									}
								}
							);
						}
					);
				}
			);
			
			Promise.all(proms).then(
				(newRows: any[] ) => {
					console.log({newRows});
					this.tableChanged.emit(
						newRows.map(
							(newVal: any) => (
								{
									pos: undefined,
									value: newVal
								}
							)
						)
					);
					setTimeout(
						() => {
							this.selectedRowIdx = this.dati.length -1;
							this.selectedRow = this.dati[this.selectedRowIdx].data;
							map.inquadraGeometrie(
								this.context,
								this.confMappa
							);
							//alert("Importazione completata")
						},
						100
					);
				},
				(err: any) => {
					console.log(err);
					//alert("Si è verificato un problema");
				}
			);

		}
	}

	allegaGeoJsonImportati(fileList: (""| FileList| null)) {
		//const element = this.importGeoJson.nativeElement;
		//console.log(fileList);
		const map = this.map;
		if (map != undefined) {
			const res = map.importGeoJsonFiles(fileList, this._conf.singleGeometries, this._conf.geometries);
			if (res == undefined) {
				//alert("Si è verificato un problema");
			}
			else {
				res.then(
					this.importaElementi.bind(this),
					(e: any) => {
						//alert("Si è verificato un problema");
						console.error(e);
					}
				);
			}
		}
		else {
			alert("Si è verificato un problema");
		}
	}
	esporta() {
		const element = this.exportGeoJson.nativeElement;
		const map = this.map;
		if (element != undefined && map != undefined) {
			map.exportGeoJson(element);
		}
		else {
			alert("Si è verificato un problema");
		}
	}

	avviaIncorporamento(idx: number) {
		if (this.map) {
			this.map.startJoin(idx);
			this.message = `Puoi selezionare le geometrie da incorporare, poi premi Ok o Cancella`;
			this.setAzioniCompletamento(
				[
					{
						label: "Ok",
						icon: "bi bi-check-square",
						btnClass: "btn btn-success",
						action: () => {
							if (this.map) {
								if (idx != undefined) {
									const jointFeats = this.map.getJointFeatures();

									const {newWkt, deletes, newArea} = jointFeats;
									this.map.stopJoin();
									if (deletes == undefined || newWkt == undefined) {
										// Se non ci sono elementi da eliminare o se il nuovo wkt non è definito, non occorre fare nulla
									}
									else {
										const oldRow = this.rigaModificata;
										const attrName = this.confMappa.mappa.shape.attribute;
										const areaAttrName = this.confMappa.mappa.shape.areaAttribute
										const newRow = {...oldRow};
										newRow[attrName] = newWkt;
										if (areaAttrName != undefined) {
											newRow[areaAttrName] = newArea;
										}
										this.idxModifica = idx;
										Promise.all(
											[
												this.exeTrigger("update", {oldRow, newRow})
											]
										).then(
											(res: any[]) => {
												res.forEach(
													x => {
														if (x != undefined) {
															alert(`Errore:\n${x}`);
															throw new Error(x);
														}
													}
												);
												this.updating = true;
												this.tableChanged.emit(
													[
														{
															pos: idx,
															value: newRow
														},
														...deletes.map(
															(idx: number) => (
																{
																	pos: idx,
																	value: undefined
																}
															)
														)
													]
												);
											}
										)
										.catch(
											(errMsg: any) => {
												if (errMsg) {
													alert(errMsg);
													throw new Error(errMsg);
												}
											}
										);
									}
								}
							}
						}
					},
					{
						label: "Cancella",
						icon: "bi bi-x-square",
						btnClass: "btn btn-danger",
						action: () => {
							this.message = "";
							this.choices = [];
							this.setAzioniCompletamento([]);
							if (this.map) {
								this.map.stopDisjoint();
								if (this.rigaModificata) {
									//this.map.seleziona(this.rigaModificata, this.idxModifica);
									this.rigaModificata = undefined;
									this.idxModifica = undefined;
								}
							}
						}
					}
				]
			);
		}
	}

	avviaSeparazione(idx: number) {
		if (this.map) {
			if (this.map.startDisjoint(idx)) {
				this.message = `Puoi selezionare le geometrie da separare, poi premi Ok o Cancella`;
				this.setAzioniCompletamento(
					[
						{
							label: "Ok",
							icon: "bi bi-check-square",
							btnClass: "btn btn-success",
							action: () => {
								if (this.map) {
									if (idx != undefined) {
										const disjointFeats = this.map.getDisjointFeatures();
										this.map.stopDisjoint();
										if (disjointFeats != undefined) {
											const [groupA, groupB] = disjointFeats;
											if (groupA == undefined || groupB == undefined) {
												// Se uno dei due gruppi è vuoto non occorre fare nulla
											}
											else {
												const oldRow = this.rigaModificata;
												const attrName = this.confMappa.mappa.shape.attribute;
												const areaAttrName = this.confMappa.mappa.shape.areaAttribute;
												const newRows = disjointFeats.map(
													//({wkt, area}: {wkt: any, area: any}) => {
													item => {
														const retVal = {...oldRow};
														retVal[attrName] = item.wkt;
														if (areaAttrName != undefined) {
															retVal[areaAttrName] = item.area;
														}
														return retVal;
													}
												);

												this.idxModifica = idx;
												Promise.all(this.splitTrigger({oldRow, newRows})).then(
													(res: any[]) => {
														res.forEach(
															x => {
																if (x != undefined) {
																	alert(`Errore:\n${x}`);
																	throw new Error(x);
																}
															}
														);
														this.updating = true;
														this.tableChanged.emit(
															[
																{
																	pos: idx,
																	value: undefined
																},
																...newRows.map(
																	(newVal: any) => (
																		{
																			pos: undefined,
																			value: newVal
																		}
																	)
																)
															]
														);
													}
												)
												.catch(
													(errMsg: any) => {
														if (errMsg) {
															alert(errMsg);
															throw new Error(errMsg);
														}
													}
												);
											}
										}
									}
								}
							}
						},
						{
							label: "Cancella",
							icon: "bi bi-x-square",
							btnClass: "btn btn-danger",
							action: () => {
								this.message = "";
								this.choices = [];
								this.setAzioniCompletamento([]);
								if (this.map) {
									this.map.stopDisjoint();
									if (this.rigaModificata) {
										//this.map.seleziona(this.rigaModificata, this.idxModifica);
										this.rigaModificata = undefined;
										this.idxModifica = undefined;
									}
								}
							}
						}
					]
				);
			}
			else {
				alert("Non è possibile eseguire una separazione per l'elemento selezionato");
			}
		}
	}

	avviaTaglio(idx: number) {
		if (this.map) {
			this.message = `Puoi disegnare le linee di taglio, poi premi Ok o Cancella`;
			this.map.startCut(idx);
			this.setAzioniCompletamento(
				[
					{
						label: "Ok",
						icon: "bi bi-check-square",
						btnClass: "btn btn-success",
						action: () => {
							if (this.map) {
								this.choices = [];
								const cutFeats = this.map.getCutFeatures();
								if (idx != undefined) {
									const oldRow = this.rigaModificata;
									const attrName = this.confMappa.mappa.shape.attribute;
									const areaAttrName = this.confMappa.mappa.shape.areaAttribute;
									const newRows = cutFeats.map(
										(feat: any) => {
											const retVal = {...oldRow};
											retVal[attrName] = feat.wkt;
											if (areaAttrName != undefined) {
												retVal[areaAttrName] = feat.area;
											}

											return retVal;
										}
									);

									this.map.stopDrawing();
									this.idxModifica = idx;
									Promise.all(this.splitTrigger({oldRow, newRows})).then(
										(res: any[]) => {
											res.forEach(
												x => {
													if (x != undefined) {
														alert(`Errore:\n${x}`);
														throw new Error(x);
													}
												}
											);
											this.updating = true;
											this.tableChanged.emit(
												[
													{
														pos: idx,
														value: undefined
													},
													...newRows.map(
														(newVal: any) => (
															{
																pos: undefined,
																value: newVal
															}
														)
													)
												]
											);
										}
									)
									.catch(
										(errMsg: any) => {
											if (errMsg) {
												alert(errMsg);
												throw new Error(errMsg);
											}
										}
									);
								}
							}
						}
					},
					{
						label: "Cancella",
						icon: "bi bi-x-square",
						btnClass: "btn btn-danger",
						action: () => {
							this.message = "";
							this.choices = [];
							this.setAzioniCompletamento([]);
							if (this.map) {
								this.map.stopDrawing();
								if (this.rigaModificata) {
									//this.map.seleziona(this.rigaModificata, this.idxModifica);
									this.rigaModificata = undefined;
									this.idxModifica = undefined;
								}
							}
						}
					}
				]
			);
		}
	}

	avviaDisegno(gomeType: string, isSingle: boolean, idx?: number) {
		if (this.map) {
			this.message = `Puoi disegnare nuove geometrie o spostare(tenendo premuto ALT) e modificare (tenendo premuto CTRL) le geometrie già presenti. Poi premi Ok o Cancella`;
			this.map.startDrawing(gomeType, idx);
			this.setAzioniCompletamento(
				[
					{
						label: "Ok",
						icon: "bi bi-check-square",
						btnClass: "btn btn-success",
						action: () => {
							if (this.map) {
								this.choices = [];
								let addedFeats: any = undefined;
								try{
									addedFeats = this.map.getDrawnFeatures(isSingle);
								}
								catch(e) {
									console.error(e);
									this.cancelInteraction();
								}
								
								if (addedFeats) {
									if (idx != undefined) {
										const oldVal = this.rigaModificata;
										const newVal = {...oldVal};
										this.idxModifica = idx;
										const attrName = this.confMappa.mappa.shape.attribute;
										const areaAttrName = this.confMappa.mappa.shape.areaAttribute;
										newVal[attrName] = addedFeats.wkt;
										if (areaAttrName != undefined) {
											newVal[areaAttrName] = addedFeats.area;
										}
										this.exeTrigger("update", newVal).then(
											(res: any) => {
												if (res != undefined) {
													alert(`Errore:\n${res}`);
													throw new Error(res);
												}
												if (this.map) {
													this.map.selFeatures.clear();
													this.map.stopDrawing();
												}
												this.updating = true;
												this.dataChanged.emit(
													{
														pos: idx,
														value: newVal
													}
												);
											}
										)
										.catch(
											(errMsg: any) => {
												if (errMsg) {
													alert(errMsg);
													throw new Error(errMsg);
												}
											}
										);
									}
									else {
										const newVal = Object.fromEntries(
											[
												[
													this.confMappa.mappa.shape.attribute,
													addedFeats.wkt
												]
											]
										);
										const attr = this.conf.mappa.shape?.areaAttribute
										if (attr) {
											newVal[attr] = addedFeats.area;
										}
										if (this.schedaInfo && this.schedaInfo.conf) {
											this.message = "Compila i dati e conferma per completare l'inserimento, altrimenti cancella!";
											this.map.restoreInteractions();
											this.setAzioniCompletamento(
												[
													{
														label: "Cancella",
														icon: "bi bi-x-square",
														btnClass: "btn btn-danger",
														action: () => {
															this.message = "";
															this.setAzioniCompletamento([]);
															this.inserting = undefined;
															if (this.map) {
																this.map.stopDrawing();
																this.map.mostraOverlay(undefined);
															}
														}
													}
												]
											);
											this.exeTrigger("insert", newVal).then(
												(res: any) => {
													this.inserting = newVal;
													if (this.map) {
														this.map.mostraOverlayAtPoint(addedFeats.interiorPoint);
													}
												}
											)
											.catch(
												(errMsg: any) => {
													if (errMsg) {
														alert(errMsg);
														throw new Error(errMsg);
													}
												}
											);
										}
										else {
											this.exeTrigger("insert", newVal).then(
												(res: any) => {
													if (res != undefined) {
														alert(`Errore:\n${res}`);
														throw new Error(res);
													}
													if (this.map) {
														this.map.selFeatures.clear();
														this.map.stopDrawing();
													}
													this.updating = true;
													this.dataChanged.emit(
														{
															pos: undefined,
															value: newVal
														}
													);
												}
											)
											.catch(
												(errMsg: any) => {
													if (errMsg) {
														alert(errMsg);
														throw new Error(errMsg);
													}
												}
											);


										}
									}
								}
								else {
									this.map.stopDrawing();
								}
							}
						}
					},
					{
						label: "Cancella",
						icon: "bi bi-x-square",
						btnClass: "btn btn-danger",
						action: () => {
							this.message = "";
							this.choices = [];
							this.setAzioniCompletamento([]);
							if (this.map) {
								this.map.stopDrawing();
								if (this.rigaModificata) {
									//this.map.seleziona(this.rigaModificata, this.idxModifica);
									this.rigaModificata = undefined;
									this.idxModifica = undefined;
								}
							}
						}
					}
				]
			);
		}
	}

	cancelInteraction(stopCb?: (()=>void)) {
		this.message = "";
		this.choices = [];
		this.setAzioniCompletamento([]);
		if (this.map) {
			if (stopCb != undefined) {
				stopCb();
			}
			if (this.rigaModificata) {
				//this.map.seleziona(this.rigaModificata, this.idxModifica);
				this.rigaModificata = undefined;
				this.idxModifica = undefined;
			}
		}
	}

	isReadOnlyAction(action: MapStdAction) : boolean{
		return action == MapStdAction.ModifyAttributes;
	}
	callAzione(action: MapStdAction) {
		console.log(action);
		if (this.map) {
			switch (action) {
				case MapStdAction.ModifyAttributes: {
					const map = this.map;
					if (map) {
						map.mostraOverlay(map.selectedFeature);
						if (this.schedaElement) {
							this.schedaElement.first.nativeElement.scrollIntoView();
						}
					}
				}; break;
				case MapStdAction.Remove: {
					const selIdx = this.selectedRowIdx;

					if (selIdx != undefined && selIdx >= 0) {
						if (confirm("Rimuovere l'elemento selezionato?")) {
							this.dataChanged.emit(
								{
									pos: selIdx,
									value: undefined
								}
							);
							this.onTabSelection(undefined);
						}
					}
				}; break;
				case MapStdAction.ModifyGeometry: {
					const idx = this.selectedRowIdx;
					if (idx != undefined) {

						// const choices = [
						// 	{
						// 		label: "Punti",
						// 		value: "Point"
						// 	},
						// 	{
						// 		label: "Linee",
						// 		value: "LineString"
						// 	},
						// 	{
						// 		label: "Poligoni",
						// 		value: "Polygon"
						// 	}
						// ];
						// this.currentChoice = choices[2].value;
						// this.choices = choices;

						this.choices = this.getGeomChoices();
						this.currentChoice = (this.choices && this.choices.length > 0) ? this.choices[0].value : undefined;

						this.onTabSelection(undefined);
						this.rigaModificata = this.dati[idx];
						this.idxModifica = idx;
						this.avviaDisegno(this.currentChoice, this.conf.singleGeometries??false, idx);
					}
				}; break;
				case MapStdAction.New: {
					if (this.map) {
						// const choices = [
						// 	{
						// 		label: "Punti",
						// 		value: "Point"
						// 	},
						// 	{
						// 		label: "Linee",
						// 		value: "LineString"
						// 	},
						// 	{
						// 		label: "Poligoni",
						// 		value: "Polygon"
						// 	}
						// ];
						// this.currentChoice = choices[2].value;
						// this.choices = choices;

						this.choices = this.getGeomChoices();
						this.currentChoice = (this.choices && this.choices.length > 0) ? this.choices[0].value : undefined;

						this.onTabSelection(undefined);
						this.avviaDisegno(this.currentChoice, this.conf.singleGeometries??false);
					}
				}; break;
				case MapStdAction.Cut: {
					const idx = this.selectedRowIdx;
					if (idx != undefined) {
						this.onTabSelection(undefined);
						this.rigaModificata = this.dati[idx];
						this.idxModifica = idx;
						this.avviaTaglio(idx);
					}
				}; break;
				case MapStdAction.Split: {
					const idx = this.selectedRowIdx;
					if (idx != undefined) {
						this.onTabSelection(undefined);
						this.rigaModificata = this.dati[idx];
						this.idxModifica = idx;
						this.avviaSeparazione(idx);
					}
				}; break;
				case MapStdAction.Join: {
					const idx = this.selectedRowIdx;
					if (idx != undefined) {
						this.onTabSelection(undefined);
						this.rigaModificata = this.dati[idx];
						this.idxModifica = idx;
						this.avviaIncorporamento(idx);
					}
				}; break;
			}
		}

	}

	ngOnInit(): void {
		this.componentInit.emit(
			{
				getValidity: this.checkValidity.bind(this)
			}
		);
		this.errori = [];
	}

	tableComponentInterface?: IstanzaComponentInterface;

	onTableComponentInit($event: IstanzaComponentInterface) {
		this.tableComponentInterface= $event;
	}
	seleziona(idx: number) {
		console.log("seleziona " + idx);
		if (idx != undefined && idx != -1 && this.selectedRowIdx != idx) {
			this.selectedRow = this.dati[idx];
			this.selectedRowIdx = idx; //TODO: controllare...." = idx;" aggiunto dop una vita
		}
		else {
			this.selectedRowIdx = undefined;
			this.selectedRow = undefined;
		}
		if (this.map) {
			this.map.seleziona(this.selectedRow, this.selectedRowIdx);
		}
	}
	evidenzia(idx?: number) {
		console.log("evidenzia " + idx);
		if (idx != undefined && idx != -1 && idx != this.selectedRowIdx) {
			this.highlightedRow = this.dati[idx];
			this.highlightedRowIdx = idx; //TODO: controllare...." = idx;" aggiunto dop una vita
		}
		else {
			this.highlightedRowIdx = undefined;
			this.highlightedRow = undefined;
		}
		
		if (this.map) {
			this.map.evidenzia(this.highlightedRow, this.highlightedRowIdx);
		}
		
	}
	checkErrori() {
		if (this.map) {
			console.log(this._conf);
			let valMap: Record<number, string[]> = this.map.getGeomsValidity(
				this._conf.singleGeometries,
				this._conf.geometries,
				this._conf.geometryConstraints
			) as Record<number, string[]>;
			console.log(valMap);
			this.errori = Object.entries(valMap).map(
				(e: any) => (
					{
						idx: Number.parseInt(e[0]),
						msgs: e[1]
					}
				)
			);
		}
	}
	checkValidity(): boolean {
		this.checkErrori();
		let validMap = Object.entries(this.errori).length == 0;
		let valTable: boolean = this.tableComponentInterface?.getValidity()??false;
		
		if (this.map) {
			let areaConsValid = this.map.getAreaConsValidity();
			validMap = validMap && areaConsValid;
		}


		console.log("validazione");
		console.log(valTable);
		console.log(this._conf);
		return valTable && validMap;
	}
	schedaElement?: QueryList<ElementRef> = undefined;

	onSchedaElementCreated(element: QueryList<ElementRef>) {
		this.schedaElement = element;
	}

	getGeoJsonExportFilename(): string {
		const codiIsta = this.context?.codIstanza;
		if (codiIsta) {
			const suffissoFileExport = this.confMappa.mappa.suffissoFileExport;
			if (suffissoFileExport) {
				return `${this.context.codIstanza}-${suffissoFileExport}.geojson`;

			}
			else {
				return `${this.context.codIstanza}-geometrie.geojson`;
			}
		}
		else {
			return `geometrie.geojson`;
		}
	}
}
