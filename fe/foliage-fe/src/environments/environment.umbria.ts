import { apiOrigin, apiServerPath, regione, mapMaxZoom } from "./defs/pars";
import { iamConfig} from "./iam/iam";
import { layersUmbria, srids, mapsSrid } from "./layers/layers-umbria";
import { layerUtente } from "./layers/layers-utente";
import { mockUsers } from "./mock/users";
import { tavoleUmbria as tavole } from "./tavole/tavole-umbria";
import { wizardNuovaDomandaUmbria as wizardNuovaDomanda } from "./wizard-tipo-istanza/wizard-umbria";


const layers = {...layersUmbria, ...layerUtente};

export const environment = {
	production: true,
	useMock: false,
	apiOrigin,
	apiServerPath,
	regione,
	mockUsers,
	iamConfig,
	wizardNuovaDomanda,
	layers,
	srids,
	mapsSrid,
	mapMaxZoom,
	tavole
};
