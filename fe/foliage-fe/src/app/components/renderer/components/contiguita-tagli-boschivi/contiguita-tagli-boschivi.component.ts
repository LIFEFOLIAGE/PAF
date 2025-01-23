import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { ComponentDataType, ComponentType } from "../ComponentInterface";
import { SimpleObjectChange } from "../../../shared/editor-scheda/editor-scheda.component";
import { IstanzaComponentInterface } from "../../../interfaces/istanza-component-interface";
import { SessionManagerService } from "../../../../services/session-manager.service";
import { ExportValue } from 'src/app/components/istanze/editor-istanza/editor-istanza.component';

@Component({
    selector: 'app-contiguita-tagli-boschivi',
    templateUrl: './contiguita-tagli-boschivi.component.html',
    styleUrls: ['./contiguita-tagli-boschivi.component.css']
})
export class ContiguitaTagliBoschiviComponent implements ComponentType<SimpleObjectChange>, OnInit, OnChanges {
    @Input() dati: ComponentDataType = {};
    @Input() isReadOnly: boolean = false;
    @Input() context: any;
    @Input() resources: any;
	@Input() componentOptions: any;
    @Input() dictionariesData?: Record<string, any>;

    @Output() changeEdit: EventEmitter<boolean> = new EventEmitter<boolean>();
    @Output() dataChanged: EventEmitter<SimpleObjectChange> = new EventEmitter<SimpleObjectChange>();
    @Output() componentInit: EventEmitter<IstanzaComponentInterface> = new EventEmitter<IstanzaComponentInterface>();
	@Output() export: EventEmitter<ExportValue> = new EventEmitter<ExportValue>();

    codIstanza!: string;

    datiTabella = [
        {
            formaGoverno: "Ceduo",
            trattamento: "",
            speciePrevalente: "Castagno",
            periodoRiferimento: "2 anni",
            sogliaSuperficie: "20 ha",
        },
        {
            formaGoverno: "Ceduo",
            trattamento: "",
            speciePrevalente: "Altre specie",
            periodoRiferimento: "2 anni",
            sogliaSuperficie: "10 ha",
        },
        {
            formaGoverno: "Fustaia",
            trattamento: "Tagli successivi",
            speciePrevalente: "Castagno",
            periodoRiferimento: "2 anni",
            sogliaSuperficie: "5 ha",
        },
        {
            formaGoverno: "Fustaia",
            trattamento: "Tagli a raso",
            speciePrevalente: "Castagno",
            periodoRiferimento: "2 anni",
            sogliaSuperficie: "2,5 ha",
        },
    ]

    rilieviArray: any;

    constructor(
        private sessionManager: SessionManagerService
    ) {
    }

    ngOnInit(): void {
        this.changeEdit.emit(true)
        this.codIstanza = this.context.codIstanza;

        this.sessionManager.profileFetch(
            `/istanze/${this.codIstanza}/contiguita-tagli-boschivi`
        ).then(
            (results: any) => {
                this.rilieviArray = results
            },
            (err) => {
                console.log("errore recupero contiguita-tagli-boschivi istanza", err)
            }
        );
    }

    ngOnChanges(changes: SimpleChanges): void {

    }

    yesClick() {
        alert("togli automaticamente le sovrapposizioni")
    }

    noClick() {
        alert("non togliere le sovrapposizioni")
    }
}
