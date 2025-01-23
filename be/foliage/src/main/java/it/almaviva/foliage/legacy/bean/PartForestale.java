package it.almaviva.foliage.legacy.bean;

import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Date;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

public class PartForestale {
	@Getter
	@Setter
	private int idPfor;
	
	@Getter
	@Setter
	private String codPfor;
	
	@Getter
	@Setter
	@Schema(description = "Geometria della particella")
	private String geometry;
	
	@Getter
	@Setter
	private String esposizione;
	
	@Getter
	@Setter
	private int altimetria;
	
	@Getter
	@Setter
	private int pendenza;
	
	@Getter
	@Setter
	private String giacitura;
	
	@Getter
	@Setter
	private String substratoPed;
	
	@Getter
	@Setter
	private int profondita;
	
	@Getter
	@Setter
	private String tessitura;
	
	@Getter
	@Setter
	private int flagAdiacenti;
	
	@Getter
	@Setter
	private int supeAdiacenti;
	
	@Getter
	@Setter
	private int idConf;
	
	@Getter
	@Setter
	private int idIsta;
	
	@Getter
	@Setter
	private String userIns;
	
	@Getter
	@Setter
	private String userUpd;
	
	@Getter
	@Setter
	private Date dataUpd;
	
	@Getter
	@Setter
	private List<Integer> idPart;

	
	public static RowMapper<PartForestale> RowMapper() {
		return new RowMapper<PartForestale>() {
			@Override
			public PartForestale mapRow(ResultSet rs, int rowNum) throws SQLException {
				
				PartForestale p=new PartForestale();
				p.setAltimetria(rs.getInt("altimetria"));
				p.setCodPfor(rs.getString("cod_pfor"));
				p.setEsposizione(rs.getString("esposizione"));
				p.setFlagAdiacenti(rs.getInt("flag_adiacenti"));
				p.setGeometry(rs.getString("geom"));
				p.setGiacitura(rs.getString("giacitura"));
				p.setIdConf(rs.getInt("id_conf"));
				p.setIdIsta(rs.getInt("id_ista"));
				p.setIdPfor(rs.getInt("id_pfor"));
				p.setPendenza(rs.getInt("pendenza"));
				p.setProfondita(rs.getInt("profondita"));
				p.setSubstratoPed(rs.getString("substrato_ped"));
				p.setSupeAdiacenti(rs.getInt("supe_adiacenti"));
				p.setTessitura(rs.getString("tessitura"));
				p.setUserIns(rs.getString("user_ins"));
				p.setUserUpd(rs.getString("data_ins"));
				
				return p;
			}
		};
	}
}
