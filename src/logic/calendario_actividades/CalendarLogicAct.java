package logic.calendario_actividades;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.joda.time.DateTime;

import db.DatabaseLogic;
import logic.dto.ActividadDto;
import logic.dto.CalendarDto;

public class CalendarLogicAct {
	private final static int DAYS_OF_WEEK = 7;

	private String[] dates = new String[] { "00:00 - 01:00", "01:00 - 02:00", "02:00 - 03:00", "03:00 - 04:00",
			"04:00 - 05:00", "05:00 - 06:00", "06:00 - 07:00", "07:00 - 08:00", "08:00 - 09:00", "09:00 - 10:00", "10:00 - 11:00",
			"11:00 - 12:00", "12:00 - 13:00", "13:00 - 14:00", "14:00 - 15:00", "15:00 - 16:00", "16:00 - 17:00",
			"17:00 - 18:00", "18:00 - 19:00", "19:00 - 20:00", "20:00 - 21:00", "21:00 - 22:00", "22:00 - 23:00",
	"23:00 - 00:00" };

	String[] daysOfWeek = {"Domingo", "Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado"};

	private List<CalendarDto> listaFechasOcupadas;
	private ColorRendererAct colorRender = new ColorRendererAct();

	public CalendarLogicAct() throws IOException {
		listaFechasOcupadas = new ArrayList<>();
	}

	public void printFollowingWeek(int day, int month, int year, ActividadDto sala) {
		DateTime date = new DateTime(year, month, day, 0, 0);
		Date sqlDate = new Date(date.toDate().getTime());
		for (int i = 0; i < DAYS_OF_WEEK; i++) {
			processRS(DatabaseLogic.mostrarCalendarioSalas(sala.id_actividad, sqlDate, date));
			date = date.plusDays(1);
			sqlDate = new Date(date.toDate().getTime());
		}
	}

	public void showTable(JTable table, int day) {
		showInfo(table, day);
		for (int i = 1; i < table.getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setCellRenderer(colorRender);
		}	
	}

	private void showInfo(JTable table, int day) {
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		HashMap<Integer, Integer> places = new HashMap<>(DAYS_OF_WEEK);
		String[] colNames = new String[8];
		colNames[0] = "Horas";
		int c = 1;
		for(int i = day; i <= daysOfWeek.length; i++)
		{
			colNames[c] = daysOfWeek[i-1];
			places.put(i-1, c);
			c++;
		}
		for(int i = 0; i < day-1; i++)
		{
			colNames[c] = daysOfWeek[i];
			places.put(i, c);
			c++;
		}
		model.setColumnIdentifiers(colNames);
		String[] data = new String[DAYS_OF_WEEK+1];
		CalendarDto var = new CalendarDto();
		if (listaFechasOcupadas.size() == 0) {
			for (int i = 0; i < 23; i++) {

				data[0] = dates[i];
				for(int j = 1; j <= DAYS_OF_WEEK; j++)
					data[j] = "";

				model.addRow(data);
			}
		} else {
			for (int i = 0; i < 23; i++) {
				data = new String[DAYS_OF_WEEK+1];
				data[0] = dates[i];
				int sizeStr = 0;
				for (int k = 0; k < listaFechasOcupadas.size(); k++) {
					var = listaFechasOcupadas.get(k);
					int c1 = var.fecha_in.getDayOfWeek();
					if(c1 == 7)
						c1 = 0;
					c = places.get(c1);
					sizeStr = var.fecha_fin.getHourOfDay() - var.fecha_in.getHourOfDay();
					int[] times = new int[sizeStr];
					for (int j = 0; j < times.length; j++) {
						times[j] = var.fecha_in.getHourOfDay() + j;
					}
					for (int j = 0; j < times.length; j++) {
						if (i == times[j])
						{
							data[c] = var.actividad + " - Plazas: "+(var.plazas-var.plazasOcupadas)+"/"+var.plazas;
						}
						else if (data[c] == null)
						{
							data[c] = "";
						}
					}
				}
				model.addRow(data);
			}
		}
		model.fireTableDataChanged();
		places.clear();
		listaFechasOcupadas.clear();
	}

	private void processRS(ResultSet rs) {
		CalendarDto var = new CalendarDto();
		try {
			while (rs.next()) {
				var = new CalendarDto();
				String nombreA = rs.getString(1);
				DateTime fechaI = new DateTime(rs.getTimestamp(2).getTime());
				DateTime fechaF = new DateTime(rs.getTimestamp(3).getTime());
				int plazas = rs.getInt(4);
				//int ocupadas = rs.getInt(5);
				var.actividad = nombreA;
				var.fecha_in = fechaI;
				var.fecha_fin = fechaF;
				var.plazas = plazas;
				//var.plazasOcupadas = ocupadas;
				listaFechasOcupadas.add(var);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
