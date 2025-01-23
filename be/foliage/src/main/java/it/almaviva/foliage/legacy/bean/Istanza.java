package it.almaviva.foliage.legacy.bean;

import io.swagger.v3.oas.annotations.media.Schema;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import lombok.Getter;
import lombok.Setter;

public class Istanza {
	
	@Schema(description = "Identificativo DB dell'istanza")
	@Getter
	@Setter
	private int idIsta;
	
	@Schema(description = "Nome dell'istanza")
	@Getter
	@Setter
	private String nomeIstanza;
	
	@Schema(description = "Codice dell'istanza")
	@Getter
	@Setter
	private String codiIsta;
	
	@Getter
	@Setter
	private String codiRegi;
	
	@Getter
	@Setter
	private String dataIstanza;
	
	@Getter
	@Setter
	private int stato;
	
	@Getter
	@Setter
	private String descStato;
	
	@Getter
	@Setter
	private String note;
	
	@Getter
	@Setter
	private int idCist;
	
	@Getter
	@Setter
	private int idEnte;
	
	@Getter
	@Setter
	private int idNprp;
	
	@Getter
	@Setter
	private int idTazi;
	
	@Getter
	@Setter
	private int idTprp;
	
	@Getter
	@Setter
	private int idQual;
	
	@Getter
	@Setter
	private int idMint;
	
	@Getter
	@Setter
	private int flagValido;
	
	@Getter
	@Setter
	private String tipoIstanza;
	
	@Getter
	@Setter
	private String userIns;
	
	@Getter
	@Setter
	private String dataIns;
	
	@Getter
	@Setter
	private String userUpd;
	
	@Getter
	@Setter
	private String datUpd;
	
	@Getter
	@Setter
	private String dataIniVali;
	
	@Getter
	@Setter
	private String dataFineVali;
	
	@Getter
	@Setter
	private int fase;

	public static RowMapper<Istanza> RowMapper() {
		return new RowMapper<Istanza>() {

			@Override
			public Istanza mapRow(ResultSet rs, int rowNum) throws SQLException {
				
				Istanza i=new Istanza();
				
				i.setIdIsta(rs.getInt("id_ista"));
				if(rs.getString("nome_ista")!=null) {
					i.setNomeIstanza(rs.getString("nome_ista"));
				}else {
					i.setNomeIstanza("");
				}
				i.setCodiIsta(rs.getString("codi_ista"));
				//i.setCodiRegi(rs.getString("codi_regi"));
				//i.setDataFineVali(rs.getString("data_fine_vali"));
				//i.setDataIniVali(rs.getString("data_ini_vali"));
				//i.setDataIns(rs.getString("data_ins"));
				//i.setDataIstanza(rs.getString("data_istanza"));
				//i.setDatUpd(rs.getString("data_upd"));
				//i.setFlagValido(rs.getInt("flag_valido"));
				i.setIdCist(rs.getInt("id_cist"));
				i.setIdEnte(rs.getInt("id_ente"));
				//i.setIdMint(rs.getInt("id_mint"));
				//i.setIdNprp(rs.getInt("id_nprp"));
				//i.setIdQual(rs.getInt("id_qual"));
				//i.setIdTazi(rs.getInt("id_tazi"));
				//i.setIdTprp(rs.getInt("id_tprp"));
				//i.setNote(rs.getString("note"));
				//i.setStato(rs.getInt("stato"));
				// if(i.getStato() == 0) {
				// 	i.setDescStato("Inviata");
				// }else if(i.getStato() == 1) {
				// 	i.setDescStato("In compilazione");
				// }
				// i.setUserIns(rs.getString("user_ins"));
				// i.setUserUpd(rs.getString("user_upd"));
				// i.setFase(rs.getInt("fase"));
				
				return i;
			}
		};
	}
}
