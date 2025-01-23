export interface DomandaWizard {
	indexDomanda: number;
	testoDomanda: string;
	scelte?: SceltaWizard[];
	codTipoIstanza?: string;
}

export interface SceltaWizard {
	testoScelta: string;
	nextOpzione: number;
}
