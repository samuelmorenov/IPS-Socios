package logic.reserva_actividad;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import org.joda.time.DateTime;

import db.DatabaseLogic;
import logic.dto.ActividadDto;

public class Reservas {
	DateTime horaInicio;
	DateTime horaFin;

	public Reservas() throws IOException, SQLException {
		DatabaseLogic.getConexion();
	}

	public ArrayList<ActividadDto> getActicidades(int day, int month, int year) throws SQLException, IOException {
		horaInicio = new DateTime(year, month, day, 0, 0);
		horaFin = new DateTime(year, month, day, 23, 59);
		ArrayList<ActividadDto> listaActividades = DatabaseLogic.getActividades(horaInicio, horaFin);
		return listaActividades;
	}

	/**
	 * Comprueba que la reserva se va a hacer el día anterior a la actividad o hasta
	 * 1 hora antes de su inicio
	 */
	public boolean dentroDelHorario(ActividadDto actividad) {
		DateTime horaActual = new DateTime();
		DateTime horaActividad = actividad.fecha_inicio;
		boolean diaAntes = (horaActual.getDayOfYear() + 1 == horaActividad.getDayOfYear())
				&& (horaActual.getYear() == horaActividad.getYear())
				|| ((horaActual.getDayOfYear() >= 365) && (horaActividad.getDayOfYear() == 1));
		boolean hasta1hora = ((horaActual.getDayOfYear() == horaActividad.getDayOfYear())
				&& (horaActual.getMinuteOfDay() + 60 < horaActividad.getMinuteOfDay()));
		
		boolean estaEnHorario = diaAntes || hasta1hora;
		return estaEnHorario;
	}

	/**
	 * Comprueba que hay plazas libres para esa actividad
	 */
	public boolean hayPlazas(ActividadDto actividad) throws SQLException, IOException {
		int plazasMax = DatabaseLogic.getPlazasMax(actividad.id_actividad);
		if(plazasMax == 0) {
			return true;
		}
		int plazasOcupadas = DatabaseLogic.getPlazasOcupadas(actividad.id_actividad);
		if (plazasOcupadas < plazasMax) {
			return true;
		}
		return false;
	}

	/**
	 * Comprueba que el socio no tenga otra actividad en la hora de la reserva
	 */
	public boolean socioLibre(int idSocio) throws SQLException, IOException {
		ArrayList<ActividadDto> listaDeCoincidencias;
		listaDeCoincidencias = DatabaseLogic.getCoincidenciasUsuario(horaInicio, horaFin, idSocio);
		if(listaDeCoincidencias.isEmpty()) {
			return true;
		}
		return false;
	}

	/**
	 * Crea la reserva de la actividad para el socio
	 */
	public void realizarReserva(int idSocio, ActividadDto actividad) throws SQLException, IOException {
		//TODO: DatabaseLogic.realizarReserva(idSocio, actividad.id_actividad);
		DatabaseLogic.realizarReserva(actividad.id_asignacion, idSocio);

	}

}
