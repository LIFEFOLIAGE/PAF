import { apiOrigin, apiUrl, regione, mapMaxZoom, baseHRef, urlPrifApk } from "./defs/pars";
import { iamConfig} from "./iam/iam";
import { layersUmbria, srids, mapsSrid } from "./layers/layers-umbria";
import { layerUtente } from "./layers/layers-utente";
import { mockUsers } from "./mock/users";
import { tavoleUmbria as tavole } from "./tavole/tavole-umbria";
import { wizardNuovaDomandaUmbria as wizardNuovaDomanda } from "./wizard-tipo-istanza/wizard-umbria";


const layers = {...layersUmbria, ...layerUtente};

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
