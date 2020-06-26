package logic.dto;

import org.joda.time.DateTime;

public class ActividadDto {
	
	public int id_actividad;
	public String nombre;
	public int repetible_id;
	public DateTime fecha_inicio;
	public DateTime fecha_fin;
	public String intensidad;
	public int id_instalacion;
	public boolean plazasLimite;
	public int id_asignacion;
	public int id_recurso;
	
	private String toStringFecha(DateTime fecha){
		String aux = "";
		aux += fecha.dayOfMonth().get() +"-";
		aux += fecha.monthOfYear().get() +"-";
		aux += fecha.year().get();
		aux += " " + fecha.getHourOfDay() + ":";
		aux += (fecha.getMinuteOfHour() == 0) ? "00" : fecha.getMinuteOfHour();
		return aux;
	}
	
	@Override
	public String toString() {
		String nodef = "No definido";
		String aux = nombre;
		aux += ", Inicio: ";
		aux += (fecha_inicio == null) ? nodef : toStringFecha(fecha_inicio);
		aux += ", Fin: ";
		aux += (fecha_fin == null) ? nodef : toStringFecha(fecha_fin);
		aux += ", Intensidad: ";
		aux += intensidad;
		aux += ", Plazas limitadas: ";
		aux += (plazasLimite) ? "Si" : "No";
		return aux;
	}	
}
