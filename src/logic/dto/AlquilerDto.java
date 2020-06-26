package logic.dto;

import org.joda.time.DateTime;

public class AlquilerDto {
	
	public int id_alquiler;
	public int id_socio;
	public int id_instalacion;
	public DateTime fecha_inicio;
	public DateTime fecha_fin;
	public DateTime hora_entrada;
	public DateTime hora_salida;
	
	@Override
	public String toString() {
		return "Socio: "+id_socio+", Instalacion: "+id_instalacion+", Fecha: "+fecha_inicio.toDate().toGMTString();
	}
}
