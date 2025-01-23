package it.almaviva.foliage.bean;

import java.util.HashMap;
import java.util.List;

import it.almaviva.foliage.FoliageException;
import it.almaviva.foliage.services.AbstractDal;

public class ElaborazioneGovernance {
	public Integer idRichiesta;
	public SchedulazioneGovernance datiSchedulazione;
	public EsecuzioneBatchManuale datiEsecuzione;
	public List<FoliageReport> reportGenerati;

	public void salva(AbstractDal dal, Integer idUtente) {
		if (datiSchedulazione == null) {
			throw new FoliageException("I dati della richiesta non sono corretti");
		}
		else {
			datiSchedulazione.salva(dal, idRichiesta, idUtente);
		}
	}

	public static ElaborazioneGovernance carica(AbstractDal dal, Integer idRichiesta) {
		ElaborazioneGovernance em = new ElaborazioneGovernance();
		em.idRichiesta = idRichiesta;
		em.datiSchedulazione = SchedulazioneGovernance.carica(dal, idRichiesta);
		em.datiEsecuzione = EsecuzioneBatchManuale.carica(dal, em.datiSchedulazione.idBatch, em.datiSchedulazione.dataRife);

		HashMap<String, Object> pars = new HashMap<>();
		pars.put("idBatch", em.datiSchedulazione.idBatch);
		em.reportGenerati = dal.query(
			FoliageReport.queryForReportFromIdBatch,
			pars,
			FoliageReport.RowMapper
		);

		return em;
	}
	
	public static void elimina(AbstractDal dal, Integer idRichiesta) {
		EsecuzioneBatchManuale.elimina(dal, idRichiesta);
		SchedulazioneGovernance.elimina(dal, idRichiesta);
	}
}
