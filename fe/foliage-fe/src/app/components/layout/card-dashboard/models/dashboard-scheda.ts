import { environment } from "src/environments/environment";

export interface DashboardScheda {
	titolo: string;
	descrizione: string;
	link: string;
	iconaCssClass: string;
	colore: string;
	ruoliAbilitati: UserRuolo[];
}

export type UserRuolo = 'PROP' | 'PROF' | 'ISTR' | 'DIRI' | 'SORV' | 'RESP' | 'AMMI' | undefined

export const AllDashboardSchede: DashboardScheda[] = [
	{
		titolo: "Strumenti di amministrazione",
		descrizione: "Gestione delle utenze e dell’assegnazione o revoca dei ruoli.",
		colore: "red",
		iconaCssClass: "bi bi-archive-fill",
		link: "/amministrazione",
		ruoliAbilitati: [
			"AMMI",
			"DIRI",
			"RESP" // per controllare gli utenti di tipo istruttore a disposizine
		],
	},
	{
		titolo: "Cruscotto P. A.",
		descrizione: "Back-office delle istanze di taglio e per la gestione delle istruttorie.",
		colore: "green",
		iconaCssClass: "bi bi-award-fill",
		link: "/cruscotto-pa",
		//ruoliAbilitati: ["AMMI", "DIRI", "ISTR", "RESP"],
		ruoliAbilitati: ["DIRI", "ISTR"]
	},
	{
		titolo: "Istanze di taglio",
		descrizione: "Permette la realizzazione e la gestione delle istanze di taglio boschivo presentate da proprietari e professionisti forestali.",
		colore: "blue",
		iconaCssClass: "bi bi-binoculars-fill",
		link: "/istanze",
		ruoliAbilitati: ["PROP", "PROF"],
	},
	{
		titolo: "Cruscotto vigilanza",
		descrizione: "Permette di analizzare i dati delle istanze.",
		colore: "darkslategray",
		iconaCssClass: "bi bi-shield-shaded",
		link: "/vigilanza",
		ruoliAbilitati: ["AMMI", "RESP", "SORV"],
	},
	{
		titolo: "Monitoraggio Satellitare",
		descrizione: "Mette a sistema i dati della piattaforma amministrativa (PAF) con immagini satellitari della costellazione Copernico, per identificare i disturbi dell'ambiente tramite tecniche di telerilevamento.",
		colore: "#676c17",
		iconaCssClass: "bi bi-badge-3d-fill",
		link: "/monitoraggio/richieste",
		ruoliAbilitati: ["AMMI"],
	},
	{
		titolo: "Rilievi sul campo",
		descrizione: "Permette la registrazione e la consultazione delle attivita nel campo.",
		colore: "orange",
		iconaCssClass: "bi bi-bandaid-fill",
		link: "/asdasd5",
		ruoliAbilitati: ["AMMI", "PROP", "PROF", "SORV"],

	},
	{
		titolo: "Supporto governance",
		descrizione: "Permette di analizzare i dati delle istanze concluse e di generare dei report sintetici.",
		colore: "purple",
		iconaCssClass: "bi bi-boombox-fill",
		link: "/governance",
		ruoliAbilitati: ["AMMI", "RESP"],
	},
]
