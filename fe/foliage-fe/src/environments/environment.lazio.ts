import { apiOrigin, apiUrl, regione, mapMaxZoom, baseHRef, urlPrifApk, mapsSrid } from "./defs/pars";
import { iamConfig} from "./iam/iam";
import { layersLazio, srids } from "./layers/layers-lazio";
import { layerUtente } from "./layers/layers-utente";
import { mockUsers } from "./mock/users";
import { tavoleLazio as tavole } from "./tavole/tavole-lazio";
import { wizardNuovaDomandaLazio as wizardNuovaDomanda } from "./wizard-tipo-istanza/wizard-lazio";


const layers = {...layersLazio, ...layerUtente};

const decimaliEttari = 4;
const coeffEttari = Math.pow(10, decimaliEttari);

export const environment = {
	baseHRef,
	production: true,
	useMock: false,
    regione,
	apiOrigin,
	apiUrl,
	iamConfig,
	mockUsers,
	wizardNuovaDomanda,
	srids,
	mapsSrid,
	mapMaxZoom,
	layers,
	tavole,
	decimaliEttari,
	coeffEttari,
	urlPrifApk
};