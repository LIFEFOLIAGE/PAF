package it.almaviva.foliage.istanze.db;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.javatuples.Pair;

import it.almaviva.foliage.bean.Base64FormioFile;
import it.almaviva.foliage.services.WebDal;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CaricamentoFileBase64 implements IOperazioneDb {
	public String fileIdProperty;
	public String fileProperty;

	public CaricamentoFileBase64(
		String fileIdProperty,
		String fileProperty
	) {
		this.fileIdProperty = fileIdProperty;
		this.fileProperty = fileProperty;
	}
	@Override
	public void applica(WebDal dal, HashMap<String, Object> contesto) throws Exception {
		Base64FormioFile[] files = (Base64FormioFile[]) contesto.get(fileProperty);

		Integer idFile = DbUtils.saveBase64FormioFiles(dal, files);
		contesto.put(fileIdProperty, idFile);
		
		if (idFile == null) {
			log.debug(String.format("%s = null", fileIdProperty));
		}
		else {
			log.debug(String.format("%s = '%s' (%s)", fileIdProperty, idFile, idFile.getClass().getName()));
		}
	}
	@Override
	public List<LinkedList<Pair<String, Object>>> applicaArray(WebDal dal, HashMap<String, Object> contesto) throws Exception {
		applica(dal, contesto);
		return null;
	}
}
