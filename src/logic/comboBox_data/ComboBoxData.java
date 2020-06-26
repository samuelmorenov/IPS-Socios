package logic.comboBox_data;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class ComboBoxData {
	private static String[] meses = { "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre",
			"Octubre", "Noviembre", "Diciembre" };
	private static String[] diasSemana = { "Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado", "Domingo" };
	private static String[] anios = new String[10];
	private static String[] horas = new String[24];

	public static String[] getMeses() {
		return meses;
	}

	public static String[] getDiasSemana() {
		return diasSemana;
	}

	public static String[] getHoras() {
		for (int i = 0; i < 24; i++) {
			horas[i] = ""+i;
		}
		return horas;
	}

	public static String[] getAnios() {
		Calendar c = new GregorianCalendar();
		int anio = c.get(Calendar.YEAR);
		for (int i = 0; i < 10; i++) {
			anios[i] = Integer.toString(anio);
			anio++;
		}
		return anios;
	}
}
