export interface VincolisticaWizard {
	indexDomanda: number;
	testoDomanda: string;
	scelte?: SceltaVincolisticaWizard[];
	link?: string;
}

export interface SceltaVincolisticaWizard {
	testoScelta: string;
	nextOpzione: number;
}
