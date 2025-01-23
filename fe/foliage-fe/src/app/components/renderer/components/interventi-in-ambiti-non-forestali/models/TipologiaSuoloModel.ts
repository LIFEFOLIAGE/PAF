import { TipologiaInterventoModel } from "./TipologiaInterventoModel";

export interface TipologiaSuoloModel {
	id_uso_suolo: number;
	cod_uso_suolo: string;
	desc_uso_suolo: string;
	tipiInterventi?: TipologiaInterventoModel;
}
