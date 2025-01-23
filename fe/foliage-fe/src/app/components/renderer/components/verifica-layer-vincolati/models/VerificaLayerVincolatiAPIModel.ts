export interface VerificaLayerVincolatiAPIModel {
	rilievi: Rilievo[];
	avvisi: string[];
	interventiConsentiti: InterventoConsentito[];
	messaggioFinale: string;
}

export interface Rilievo {
	categoria: string;
	codice: string;
	descrizione: string;
}

export interface InterventoConsentito {
	idSchedaIntervento: number;
	tipoIntervento: string;
	formeDiTrattamentoConsentite: string[];
	formaDiGoverno: string;
	urlFilePdf: string;
}
