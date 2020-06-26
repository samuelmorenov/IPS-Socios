package logic.dto;

import org.joda.time.DateTime;

public class InstalacionAsignadaDto {
	public int id_asignacion;
	public int id_evento;
	public int id_instalacion;
	public int id_monitor;
	public DateTime fecha_inicio;
	public DateTime fecha_fin;
	public boolean actividad;
	@Override
	public String toString() {
		return "InstalacionAsignadaDto [id_asignacion=" + id_asignacion + ", id_avento=" + id_evento
				+ ", id_instalacion=" + id_instalacion + ", id_monitor=" + id_monitor + ", fecha_inicio=" + fecha_inicio
				+ ", fecha_fin=" + fecha_fin + ", actividad=" + actividad + "]";
	}
	

}
