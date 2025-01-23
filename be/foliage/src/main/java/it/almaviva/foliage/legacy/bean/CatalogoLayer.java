package it.almaviva.foliage.legacy.bean;
import org.springframework.jdbc.core.RowMapper;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

public class CatalogoLayer {
	
	@Schema(description = "Id tipologia")
	@Getter
	@Setter
	private Integer idClay;
			
	@Schema(description = "Descrizione tipologia")
	@Getter
	@Setter
	private String descClay;
	
	@Getter
	@Setter
	private String note;
	
	@Getter
	@Setter
	private int flagVali;

	@Getter
	@Setter
	private String userIns;

	@Getter
	@Setter
	private String userUpd;
	
	public static RowMapper<CatalogoLayer> RowMapper() {
		return (rs, rowNum)-> {
			CatalogoLayer c=new CatalogoLayer();
			c.setIdClay(rs.getInt("id_clay"));
			c.setDescClay(rs.getString("desc_clay"));
			c.setNote(rs.getString("note"));
			return c;
		};
	}
}
