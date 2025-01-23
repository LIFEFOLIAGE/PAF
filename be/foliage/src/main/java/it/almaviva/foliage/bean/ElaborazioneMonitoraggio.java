package it.almaviva.foliage.bean;

import java.util.List;

import it.almaviva.foliage.FoliageException;
import it.almaviva.foliage.services.AbstractDal;

public class ElaborazioneMonitoraggio {
	public Integer idRichiesta;
	public SchedulazioneMonitoraggio datiSchedulazione;
	public EsecuzioneBatchManuale datiEsecuzione;
	public List<ErroreMonitoraggio> tentativiFalliti;

	public void salva(AbstractDal dal, Integer idUtente) {
		if (datiSchedulazione == null) {
			throw new FoliageException("I dati della richiesta non sono corretti");
		}
		else {
			datiSchedulazione.salva(dal, idRichiesta, idUtente);
		}
	}

	public static ElaborazioneMonitoraggio carica(AbstractDal dal, Integer idRichiesta) {
		ElaborazioneMonitoraggio em = new ElaborazioneMonitoraggio();
		em.idRichiesta = idRichiesta;
		em.datiSchedulazione = SchedulazioneMonitoraggio.carica(dal, idRichiesta);
		em.datiEsecuzione = EsecuzioneBatchManuale.caricaMonitoraggio(dal, em.datiSchedulazione.dataAvvioRichiesta);
		return em;
	}
	
	public static void elimina(AbstractDal dal, Integer idRichiesta) {
		SchedulazioneMonitoraggio.elimina(dal, idRichiesta);
	}
}
