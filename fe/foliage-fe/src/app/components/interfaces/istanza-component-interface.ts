import { EventEmitter } from "@angular/core";

export interface IstanzaComponentInterface {
    getValidity: () => boolean;
    // reset?: () => Promise<boolean>;
}

export interface BaseIstanzaComponent {
    componentInit: EventEmitter<IstanzaComponentInterface>
}
