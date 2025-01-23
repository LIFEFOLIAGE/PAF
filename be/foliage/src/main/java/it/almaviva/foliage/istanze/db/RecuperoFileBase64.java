package it.almaviva.foliage.istanze.db;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.javatuples.Pair;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import it.almaviva.foliage.bean.Base64FormioFile;
import it.almaviva.foliage.services.WebDal;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RecuperoFileBase64 implements IOperazioneDb {
	public String fileIdProperty;
	public String fileProperty;

	public RecuperoFileBase64(
		String fileIdProperty,
		String fileProperty
	) {
		this.fileIdProperty = fileIdProperty;
		this.fileProperty = fileProperty;
	}
	@Override
	public void applica(WebDal dal, HashMap<String, Object> contesto) {
		Integer idFile = (Integer) contesto.get(fileIdProperty);
		Base64FormioFile[] res = DbUtils.getBase64FormioFiles(dal, idFile);
		contesto.put(fileProperty, res);
		log.debug(String.format("%s = %s", fileProperty, (res == null) ? "null": String.format("%d files",res.length)));
	}
	@Override
	public List<LinkedList<Pair<String, Object>>> applicaArray(WebDal dal, HashMap<String, Object> contesto) {
		applica(dal, contesto);
		return null;
	}
}
