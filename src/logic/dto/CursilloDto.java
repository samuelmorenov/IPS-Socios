package logic.dto;

import org.joda.time.DateTime;

public class CursilloDto {

	public int id_cursillo;
	public String nombre;
	public int plazas;
	public int plazas_ocupadas;
	public DateTime fecha1;
	public DateTime fecha2;
	public DateTime fecha_fin;
	public DateTime fecha_inscripcion_socio;
	public DateTime fecha_inscripcion_nosocio;
	public double precio;
	public int id_instalacion;
	public int id_monitor;

	
	public DateTime fecha_inicio;
	public InstalacionDto instalacion;
	public MonitorDto monitor;
	public int horaInicio;
	public int horaFin;
	public int diaSemana1;
	public int diaSemana2;
	

	public String toStringCreacion() {
		return "'" + nombre + "' con fecha " + fecha1.getMonthOfYear() + "-" + fecha1.getYear()
				+ ", en la instalacion id: " + id_instalacion
				+ ", con un numero de plazas: " + plazas
				+ ", impartido por el monitor id: " + id_monitor;
	}


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
		return nombre + ", Plazas: " + plazas + ", fecha 1: " + toStringFecha(fecha1) + ", fecha 2: " + toStringFecha(fecha2);
	}

}
