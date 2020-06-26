package logic.cursillo_crear_reservar;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import db.DatabaseLogic;
import logic.dto.CursilloDto;
import logic.dto.InstalacionAsignadaDto;
import logic.dto.InstalacionDto;
import logic.dto.MonitorDto;
import logic.dto.OcupacionesDto;
import logic.exception.DatosDeCursillosErroneosException;

public class CrearCursillo {
	private int numeroHoras = 1;

	public CrearCursillo() throws IOException, SQLException {
		DatabaseLogic.getConexion();
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

	public void crearNuevoCursillo(CursilloDto cursillo, ArrayList<OcupacionesDto> ocupados)
			throws DatosDeCursillosErroneosException, SQLException, IOException {

		establecerPrimerYSegundoDia(cursillo);
		cursillo.horaFin = cursillo.horaInicio + numeroHoras;
		cursillo.id_monitor = cursillo.monitor.id_monitor;
		cursillo.id_instalacion = cursillo.instalacion.id_instalacion;
		DateTime ff = cursillo.fecha_fin;
		DateTime ffn = new DateTime(ff.getYear(), ff.getMonthOfYear(), ff.getDayOfMonth(), 23, 59);
		cursillo.fecha_fin = ffn;
		comprobarDatos(cursillo);
		obtenerCoincidencias(cursillo, ocupados);
	}

	/**
	 * Añade al dto del cursillo la hora de fin basandose en la hora de inicio y
	 * calcula el primer y y segundo dia
	 * 
	 * @param cursillo
	 */
	private void establecerPrimerYSegundoDia(CursilloDto cursillo) {

		DateTime primerDia = null;
		DateTime segundoDia = null;

		int i = 0;
		while (primerDia == null) {
			primerDia = cursillo.fecha_inicio.plusDays(i);
			if (primerDia.getDayOfWeek() != cursillo.diaSemana1) {
				primerDia = null;
			}
			i++;
		}
		i = 0;
		while (segundoDia == null) {
			segundoDia = cursillo.fecha_inicio.plusDays(i);
			if (segundoDia.getDayOfWeek() != cursillo.diaSemana2) {
				segundoDia = null;
			}
			i++;
		}
		// Comprobar que la fecha 1 es antes que la fecha 2
		if (!primerDia.isBefore(segundoDia)) {
			cursillo.fecha1 = segundoDia;
			cursillo.fecha2 = primerDia;
		} else {
			cursillo.fecha1 = primerDia;
			cursillo.fecha2 = segundoDia;
		}

	}

	private void comprobarDatos(CursilloDto cursillo) throws DatosDeCursillosErroneosException {
		// Comprobar que las plazas es mayor que 0
		if (cursillo.plazas <= 0) {
			throw new DatosDeCursillosErroneosException("Las plazas del cursillo son inferiores a 1");
		}
		// Comprobar que las plazas seleccionadas entran en la instalacion
		if (cursillo.instalacion.plazas < cursillo.plazas) {
			throw new DatosDeCursillosErroneosException("Plazas insuficientes en la instalacion");
		}
		// Comprobar que el precio sea positivo
		if (cursillo.precio < 0) {
			throw new DatosDeCursillosErroneosException("El precio debe ser positivo");
		}

		// Comprobar que las horas son correctas
		if (cursillo.horaInicio < 0 || cursillo.horaInicio > 24) {
			throw new DatosDeCursillosErroneosException("La hora de inicio no es correcta");
		}
		if (cursillo.horaFin < 0 || cursillo.horaFin > 24) {
			throw new DatosDeCursillosErroneosException("La hora de fin no es correcta");
		}

		// Comprobar que los dias de la semana sean diferentes
		if (cursillo.diaSemana1 == cursillo.diaSemana2) {
			throw new DatosDeCursillosErroneosException("Los dias de la semana deben ser diferentes");
		}
		// Comprobar que la fecha de inicio sea despues de hoy
		DateTime hoy = new DateTime();
		if (!cursillo.fecha1.isAfter(hoy)) {

			throw new DatosDeCursillosErroneosException("La fecha de inicio del cursillo debe ser despues de hoy");

		}
		// Comprobar que la fecha de fin sea despues de la fecha de inicio1 y inicio2
		if (!cursillo.fecha_fin.isAfter(cursillo.fecha1) || !cursillo.fecha_fin.isAfter(cursillo.fecha2)) {
			throw new DatosDeCursillosErroneosException(
					"La fecha de fin del cursillo es anterior a los dias de inicio");
		}
		// Comprobar que la fecha de inscripcion sea antes del inicio
		if (!cursillo.fecha_inscripcion_socio.isBefore(cursillo.fecha_inicio)
				|| !cursillo.fecha_inscripcion_nosocio.isBefore(cursillo.fecha_inicio)) {
			throw new DatosDeCursillosErroneosException(
					"La fecha de inscripcion de socio debe ser antes del inicio del cursillo");
		}

		// Comprobar que la fecha de inscripcion de socio sea antes o igual que la
		// general
		if (!cursillo.fecha_inscripcion_socio.isBefore(cursillo.fecha_inscripcion_nosocio)) {
			throw new DatosDeCursillosErroneosException(
					"La fecha de inscripcion de socio debe ser antes o igual que la general");
		}
	}

	private boolean obtenerCoincidencias(CursilloDto cursillo, ArrayList<OcupacionesDto> ocupados)
			throws SQLException, IOException {
		ArrayList<InstalacionAsignadaDto> asignaciones;
		asignaciones = DatabaseLogic.getInstalacionAsignada(cursillo.fecha1, cursillo.fecha_fin);

		for (int i = 0; i < asignaciones.size(); i++) {
			InstalacionAsignadaDto asignacion = asignaciones.get(i);
			if (asignacion.fecha_inicio.getDayOfWeek() == cursillo.diaSemana1
					|| asignacion.fecha_inicio.getDayOfWeek() == cursillo.diaSemana2) {

				if (asignacion.id_monitor == cursillo.id_monitor
						&& asignacion.fecha_inicio.getHourOfDay() >= cursillo.horaInicio
						&& asignacion.fecha_fin.getHourOfDay() <= cursillo.horaFin) {
					OcupacionesDto ocupado = new OcupacionesDto();
					ocupado.tipo = "El monitor";
					ocupado.id = cursillo.id_monitor;
					ocupado.anio = asignacion.fecha_inicio.getYear();
					ocupado.mes = asignacion.fecha_inicio.getMonthOfYear();
					ocupado.dia = asignacion.fecha_inicio.getDayOfMonth();
					ocupado.hora_inicio = asignacion.fecha_inicio.getHourOfDay();
					ocupado.hora_fin = asignacion.fecha_fin.getHourOfDay();
					ocupados.add(ocupado);
				}
				if (asignacion.id_instalacion == cursillo.id_instalacion
						&& asignacion.fecha_inicio.getHourOfDay() >= cursillo.horaInicio
						&& asignacion.fecha_fin.getHourOfDay() <= cursillo.horaFin) {

					OcupacionesDto ocupado = new OcupacionesDto();
					ocupado.tipo = "La instalacion";
					ocupado.id = cursillo.id_monitor;
					ocupado.anio = asignacion.fecha_inicio.getYear();
					ocupado.mes = asignacion.fecha_inicio.getMonthOfYear();
					ocupado.dia = asignacion.fecha_inicio.getDayOfMonth();
					ocupado.hora_inicio = asignacion.fecha_inicio.getHourOfDay();
					ocupado.hora_fin = asignacion.fecha_fin.getHourOfDay();
					ocupados.add(ocupado);

				}

			}

		}
		return ocupados.isEmpty();

	}

	public void insertarCrusillo(CursilloDto cursillo) throws SQLException, IOException {
		DatabaseLogic.crearCursillo(cursillo);
		cursillo.id_cursillo = DatabaseLogic.ultimoCursilloCreado();
		//System.out.println(cursillo.toStringCreacion() + " creado"); // TODO borrar

		DateTime fecha = cursillo.fecha_inicio;
		while (fecha.isBefore(cursillo.fecha_fin)) {
			int anio = fecha.getYear();
			int mes = fecha.getMonthOfYear();
			int day = fecha.getDayOfMonth();
			DateTime horaInicio = new DateTime(anio, mes, day, cursillo.horaInicio, 0);
			DateTime horaFin = new DateTime(anio, mes, day, cursillo.horaFin, 0);

			if (horaInicio.getDayOfWeek() == cursillo.diaSemana1 || horaInicio.getDayOfWeek() == cursillo.diaSemana2) {
				DatabaseLogic.asignarEvento(cursillo.id_cursillo, cursillo.id_instalacion, cursillo.id_monitor,
						horaInicio, horaFin, false);
				//System.out.println("creado evento para el dia" + horaInicio); // TODO borrar
			}
			fecha = fecha.plusDays(1);
		}
	}
}
