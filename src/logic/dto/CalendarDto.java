package logic.dto;

import org.joda.time.DateTime;

public class CalendarDto {
	
	public DateTime fecha_in;
	public DateTime fecha_fin;
	public String actividad;
	public int plazas;
	public int plazasOcupadas;

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
	public String toString()
	{
		return "Actividad: "+ actividad+ " ,Fecha in: " + toStringFecha(fecha_in) + " ,Fecha fin: " + toStringFecha(fecha_fin)+ " ,Plazas: "+(plazas-plazasOcupadas)+"/"+plazas;
	}
}
