export const wizardNuovaDomandaLazio = {
    "1": {
        "desc": "L'area di intervento è pianificata tramite PGAF o strumento equivalente?",
        "scelte": {
            "si": 2,
            "no": 3
        }
    },
    "2": {
        "desc": "Gli interventi previsti sono conformi al PGAF vigente?",
        "scelte": {
            "si": 4,
            "no": 5
        }
    },
    "3": {
        "desc": "Gli interventi ricadono in rete natura 2000?",
        "scelte": {
            "si": 6,
            "no": 7
        }
    },
	"4": {
        "desc": "Istanza di attuazione del PGF",
        "codTipoIstanza": "ATTUAZIONE_PIANI"
    },
    "5": {
        "desc": "Istanza in deroga",
        "codTipoIstanza": "IN_DEROGA"
    },
    "6": {
        "desc": "Gli interventi previsti hanno una superficie inferiore a 4000mq?",
        "scelte": {
            "si": 8,
            "no": 5
        }
    },
    "7": {
        "desc": "La forma di governo è fustaia?",
        "scelte": {
            "si": 9,
            "no": 10
        }
    },
    "8": {
        "desc": "Istanza sotto soglia",
        "codTipoIstanza": "SOTTO_SOGLIA"
    },
    "9": {
        "desc": "Si intende eseguire un diradamento su fustaia coetanea o coetaneiforme, conforme all'art. 29 del RR 07/2005?",
        "scelte": {
            "si": 8,
            "no": 11
        }
    },
    "10": {
        "desc": "La superficie dell'intervento è inferiore a 3ha?",
        "scelte": {
            "si": 12,
            "no": 13
        }
    },
    "11": {
        "desc": "Si intende applicare un trattamento a tagli successivi, conforme all'art. 31 del RR 07/2005?",
        "scelte": {
            "si": 8,
            "no": 14
        }
    },
    "12": {
        "desc": "L'intervento rispetta le condizioni: cedui di castagno < 20ha e cedui di altre specie < 10ha?",
        "scelte": {
            "si": 14,
            "no": 5
        }
    },
    "13": {
        "desc": "L'intervento rispetta i parametri di superficie indicati dall'art 9 del RR 07/2005?",
        "scelte": {
            "si": 14,
            "no": 5
        }
    },
    "14": {
        "desc": "Istanza sopra soglia",
        "codTipoIstanza": "SOPRA_SOGLIA"
    }
};