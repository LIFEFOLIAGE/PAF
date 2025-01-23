// export interface InfoIstanzaModel {
//   istanza: {
//     stato: string,
//     enteTerritoriale: string,
//     riferimenti: string, // TODO: stringa semplice o oggetto con
//     dataInizioLavori?: Date,
//     dataFineLavori?: Date,
//   };
//   titolare: {
//     codFiscale: string,
//     nome: string,
//     cognome: string,
//   };
//   gestore?: {
//     codFiscale: string,
//     nome: string,
//     cognome: string,
//   }

import { LocalDate } from "@js-joda/core";

// }
export interface Soggetto{
  codFiscale: string;
  nome: string;
  cognome: string;
}

export interface User extends Soggetto {
  username: string;
}

export interface InfoIstanzaModel {
  descrizioneTipo: string;
  stato: string;
  descrizioneStato: string;
  idEnteTerritoriale: number;
  enteTerritoriale: string;
  riferimenti: string; // TODO: stringa semplice o oggetto con dati complessi
  dataInizioLavori?: string;
  dataFineLavori?: string;
  titolare: Soggetto;
  gestore: User;
  istruttore?: User;
  dataInvio?: string;
  dataValutazione?: string;
  dataFineValidita?: string;
  dataFirma?: string;
  mesiProroga?: number;
  dataProroga?: string;
  numSchedeObblig?: number;
  numSchedeObbligComp?: number;
  numSchede?: number;
  numSchedeComp?: number;
}

/* RIsposta be di test

        return new ResponseEntity<>("""
            {
                "stato": "approvata",
                "enteTerritoriale": "3014",
                "riferimenti": "riferimenti",
                "dataInizioLavori": "2023-09-10T00:00:00+01:00",
                "dataFineLavori": "2023-09-12T00:00:00+01:00",
              "titolare": {
                "username": "username titolare",
                "codFiscale": "CF1",
                "nome": "Nome titolare",
                "cognome": "Cognome titolare"
              },
              "gestore": {
                "username": "username gestore",
                  "codFiscale": "CF2",
                "nome": "Nome gestore",
                "cognome": "Cognome gestore"
              },
              "istruttore": {
                "username": "username istruttore",
                "codFiscale": "CF3",
                "nome": "Nome istruttore",
                "cognome": "Cognome istruttore"
              }
            }
            """, HttpStatus.OK);

*/
