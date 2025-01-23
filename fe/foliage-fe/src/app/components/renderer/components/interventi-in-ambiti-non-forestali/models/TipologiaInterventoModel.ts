export interface TipologiaInterventoModel {
	id_tipo_intervento: number;
	id_uso_suolo: number,
	cod_tipo_intervento: string;
	nome_tipo_intervento: string;
	riferimento_normativo: string;
	//parametro_richiesto: CampoDaGestire;
	parametro_richiesto: string;
}

//export type CampoDaGestire = "superficie-ha" | "numero-esemplari" | "numero-siti" | "superficie-mq"
