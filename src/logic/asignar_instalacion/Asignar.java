package logic.asignar_instalacion;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.joda.time.DateTime;

import db.DatabaseLogic;
import logic.dto.ActividadSinAsignarDto;
import logic.dto.InstalacionDto;
import logic.dto.MonitorDto;

public class Asignar {

	DateTime dia;

	public Asignar() throws IOException, SQLException {
		DatabaseLogic.getConexion();
	}

	public void setDia(int day, int month, int year) {
		DateTime date = new DateTime(year, month, day, 0, 0);
		dia = date;
	}

	public DateTime getDia() {
		return dia;
	}

	public ActividadSinAsignarDto[] getActicidadesSinAsignar() throws SQLException, IOException {
		List<ActividadSinAsignarDto> actividadesList = DatabaseLogic.getActividadesSinAsignar();
		ActividadSinAsignarDto[] actividadesArray = new ActividadSinAsignarDto[actividadesList.size()];
		for (int i = 0; i < actividadesList.size(); i++) {
			actividadesArray[i] = actividadesList.get(i);
		}
		return actividadesArray;
	}

	public InstalacionDto[] getInstalaciones() throws SQLException, IOException {
		List<InstalacionDto> instalacionesList = DatabaseLogic.getInstalaciones();
		InstalacionDto[] instalacionesArray = new InstalacionDto[instalacionesList.size()];
		for (int i = 0; i < instalacionesList.size(); i++) {
			instalacionesArray[i] = instalacionesList.get(i);
		}
		return instalacionesArray;
	}

	public MonitorDto[] getMonitores() throws SQLException, IOException {
		List<MonitorDto> monitoresList = DatabaseLogic.getMonitores();
		MonitorDto[] monitoresArray = new MonitorDto[monitoresList.size()];
		for (int i = 0; i < monitoresList.size(); i++) {
			monitoresArray[i] = monitoresList.get(i);
		}
		return monitoresArray;
	}

	/**
	 * Como administración quiero hacer reservas de instalaciones para actividades
	 * del centro en un día de la semana
	 *
	 * La reserva de una instalación seleccionada se realizará para un día de la
	 * semana y su duración se podrá indicar con la hora inicial y la hora final
	 * (siempre con horas "en punto") o para todo el día. Se supone que la
	 * instalación está libre; se deja para más adelante el caso de que hubiera
	 * conflictos con otras reservas hechas con anterioridad. Asociada a la reserva
	 * de la instalación, habrá una actividad del centro que administración
	 * asignará.
	 */
	public void asignar(ActividadSinAsignarDto actividad, InstalacionDto instalacion, MonitorDto monitor,
			DateTime fecha, int horaInicio, int horaFin) throws SQLException, IOException {
		DateTime fechaInicio = new DateTime(fecha.getYear(), fecha.getMonthOfYear(), fecha.getDayOfMonth(), horaInicio,
				0);
		DateTime fechaFin = new DateTime(fecha.getYear(), fecha.getMonthOfYear(), fecha.getDayOfMonth(), horaFin, 0);
		DatabaseLogic.asignarEvento(actividad.id_actividad, instalacion.id_instalacion, monitor.id_monitor, fechaInicio,
				fechaFin, true);

	}

}
