package it.almaviva.foliage.legacy.bean;

import io.swagger.v3.oas.annotations.media.Schema;

public class Regione {

	@Schema(description = "Identificativo della regione")
    private String codiRegi;
	
	@Schema(description = "Nome regione")
	private String descRegi;
	
	private int idRegi;
	
	
	public Regione() {
		
	}
	
	public Regione(String codiRegi, String descRegi) {
		super();
		this.codiRegi = codiRegi;
		this.descRegi = descRegi;
	}
	public String getCodiRegi() {
		return codiRegi;
	}
	public void setCodiRegi(String codiRegi) {
		this.codiRegi = codiRegi;
	}
	public String getDescRegi() {
		return descRegi;
	}
	public void setDescRegi(String descRegi) {
		this.descRegi = descRegi;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Regione other = (Regione) obj;
		if (codiRegi == null) {
			if (other.codiRegi != null)
				return false;
		} else if (!codiRegi.equals(other.codiRegi))
			return false;
		if (descRegi == null) {
			if (other.descRegi != null)
				return false;
		} else if (!descRegi.equals(other.descRegi))
			return false;
		return true;
	}

	public int getIdRegi() {
		return idRegi;
	}

	public void setIdRegi(int idRegi) {
		this.idRegi = idRegi;
	}

}
