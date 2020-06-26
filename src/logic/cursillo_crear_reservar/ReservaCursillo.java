package logic.cursillo_crear_reservar;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import db.DatabaseLogic;
import logic.dto.ActividadDto;
import logic.dto.CursilloDto;
import logic.dto.InstalacionAsignadaDto;
import logic.dto.OcupacionesDto;
import logic.dto.UsuarioDto;

public class ReservaCursillo {
	TratamientoArchivos coincidencias = new TratamientoArchivos();

	public ReservaCursillo() throws IOException, SQLException {
		DatabaseLogic.getConexion();
	}

	public CursilloDto[] getCursillos() throws IOException, SQLException {
		List<CursilloDto> cursillosList = DatabaseLogic.getCursillos();
		CursilloDto[] cursillosArray = new CursilloDto[cursillosList.size()];
		for (int i = 0; i < cursillosList.size(); i++) {
			CursilloDto cursillo = cursillosList.get(i);

			cursillo.horaInicio = cursillo.fecha1.getHourOfDay();
			cursillo.horaFin = cursillo.fecha1.plusHours(1).getHourOfDay();
			cursillo.diaSemana1 = cursillo.fecha1.getDayOfWeek();
			cursillo.diaSemana2 = cursillo.fecha2.getDayOfWeek();

			cursillosArray[i] = cursillo;
		}
		return cursillosArray;
	}

	public boolean dentroDelHorario(CursilloDto cursillo, boolean socio) {
		DateTime horaActual = new DateTime();
		if (socio) {
			return horaActual.isBefore(cursillo.fecha_fin)
					&& horaActual.isAfter(cursillo.fecha_inscripcion_socio);
		} else {
			return horaActual.isBefore(cursillo.fecha_fin)
					&& horaActual.isAfter(cursillo.fecha_inscripcion_nosocio);
		}
	}

	public void reservar(int idSocio, CursilloDto cursillo, ArrayList<OcupacionesDto> ocupados, boolean metalico)
			throws SQLException, IOException {
		
		ArrayList<InstalacionAsignadaDto> asignaciones = obtenerCoincidencias(cursillo, ocupados, idSocio);

		if (!ocupados.isEmpty()) {
			return;
		}

		DateTime ahora = new DateTime();

		for (int i = 0; i < asignaciones.size(); i++) {
			InstalacionAsignadaDto asignacion = asignaciones.get(i);
			DatabaseLogic.realizarReserva(asignacion.id_asignacion, idSocio);
			//System.out.println("Creada reserva del socio " + idSocio + " en la asignacion " + asignacion.id_asignacion); //TODO
		}

		DatabaseLogic.reservarCursillo(idSocio, cursillo.id_cursillo, ahora, true);
		 //System.out.println("Creada reserva del socio " + idSocio + " en el cursillo " + cursillo); //TODO
		DatabaseLogic.generarCobro(idSocio, cursillo.precio, metalico, "Pago del cursillo " + cursillo.id_cursillo);

	}
	
	private ArrayList<InstalacionAsignadaDto> obtenerCoincidencias(CursilloDto cursillo, ArrayList<OcupacionesDto> ocupados, int idSocio) throws SQLException, IOException {
		ArrayList<InstalacionAsignadaDto> asignaciones;
		asignaciones = DatabaseLogic.getAsignacionesCursillo(cursillo.id_cursillo);
		// System.out.println("asignaciones.size() = " + asignaciones.size());
		for (int i = 0; i < asignaciones.size(); i++) {
			InstalacionAsignadaDto asignacion = asignaciones.get(i);
			if (!socioLibre(idSocio, asignacion.fecha_inicio, asignacion.fecha_fin)) {
				OcupacionesDto ocupado = new OcupacionesDto();
				ocupado.tipo = "El socio";
				ocupado.id = idSocio;
				ocupado.anio = asignacion.fecha_inicio.getYear();
				ocupado.mes = asignacion.fecha_inicio.getMonthOfYear();
				ocupado.dia = asignacion.fecha_inicio.getDayOfMonth();
				ocupado.hora_inicio = asignacion.fecha_inicio.getHourOfDay();
				ocupado.hora_fin = asignacion.fecha_fin.getHourOfDay();
				ocupados.add(ocupado);
			}

		}
		return asignaciones;
	}

	private boolean socioLibre(int idSocio, DateTime horaInicio, DateTime horaFin) throws SQLException, IOException {
		ArrayList<ActividadDto> listaDeCoincidencias;
		listaDeCoincidencias = DatabaseLogic.getCoincidenciasUsuario(horaInicio, horaFin, idSocio);
		// System.out.println("listaDeCoincidencias.size() = " + listaDeCoincidencias.size());
		if (listaDeCoincidencias.isEmpty()) {
			return true;
		}
		return false;
	}



	public void aniadirAListaEspera(int idSocio, CursilloDto cursillo) throws SQLException, IOException {
		DateTime ahora = new DateTime();
		DatabaseLogic.reservarCursillo(idSocio, cursillo.id_cursillo, ahora, false);

	}

	public int insertarNoSocio(String nombre, String dni, String telefono) throws SQLException, IOException {
		UsuarioDto noSocio = new UsuarioDto();
		noSocio.nombre = nombre;
		noSocio.dni = dni;
		noSocio.telefono = telefono;
		noSocio.tipo = "NOSOCIO";
		noSocio.id_usuario = DatabaseLogic.insertarNoSocio(noSocio);
		return noSocio.id_usuario;
	}

	public UsuarioDto getNoSocio(String dni) throws SQLException, IOException {
		UsuarioDto noSocio = DatabaseLogic.existeNoSocio(dni);
		return noSocio;
	}
}
