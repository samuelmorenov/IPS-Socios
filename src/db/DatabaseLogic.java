package db;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.joda.time.DateTime;

import logic.dto.ActividadDto;
import logic.dto.CursilloDto;
import logic.dto.ActividadSinAsignarDto;
import logic.dto.AlquilerDto;
import logic.dto.InstalacionAsignadaDto;
import logic.dto.InstalacionDto;
import logic.dto.MonitorDto;
import logic.dto.RecursoDto;
import logic.dto.UsuarioDto;
import logic.exception.MonitorOcupadoException;

public class DatabaseLogic {

	private static Properties config;

	public static Connection getConexion() throws IOException, SQLException {

		config = new Properties();
		FileInputStream fis = new FileInputStream("config.properties");
		config.load(fis);
		Connection conn = DriverManager.getConnection(config.getProperty("URL"), config.getProperty("USERNAME"),
				config.getProperty("PASSWORD"));
		return conn;
	}

	public static void crearActividad(ActividadSinAsignarDto actividad) throws SQLException, IOException {
		PreparedStatement ps;
		ps = getConexion()
				.prepareStatement("insert into actividad (nombre, plazas_limitadas, intensidad, id_recurso) values (?,?,?,?)");
		ps.setString(1, actividad.nombre);
		ps.setBoolean(2, actividad.plazas_limitadas);
		ps.setString(3, actividad.intensidad);
		ps.setInt(4, actividad.id_recurso);

		ps.executeUpdate();
	}
	
	public static void alquilarInstalacion(AlquilerDto alquiler) throws SQLException, IOException {
		PreparedStatement ps;
		ps = getConexion()
				.prepareStatement("insert into alquiler_instalacion (id_socio, id_instalacion, fecha_inicio, fecha_fin, hora_entrada, hora_salida) values (?,?,?,?,?,?)");
		ps.setInt(1, alquiler.id_socio);
		ps.setInt(2, alquiler.id_instalacion);
		ps.setDate(3, new Date(alquiler.fecha_inicio.toDate().getTime()));
		ps.setDate(4, new Date(alquiler.fecha_fin.toDate().getTime()));
		ps.setDate(5, new Date(alquiler.hora_entrada.toDate().getTime()));
		ps.setDate(6, new Date(alquiler.hora_salida.toDate().getTime()));

		ps.executeUpdate();
	}

	public static void crearActividadRepetible(ActividadSinAsignarDto actividad, DateTime fechaLimite)
			throws SQLException, IOException {
		PreparedStatement ps;
		ps = getConexion()
				.prepareStatement("insert into actividad_repetible (id_actividad, fecha_limite) values (?,?)");
		ps.setInt(1, actividad.id_actividad);
		ps.setDate(2, new Date(fechaLimite.toDate().getTime()));

		ps.executeUpdate();
	}

	/**
	 * 
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	public static List<InstalacionDto> getInstalaciones() throws SQLException, IOException {
		List<InstalacionDto> result = new ArrayList<>();
		PreparedStatement ps;
		ps = getConexion().prepareStatement("select id_instalacion, nombre, plazas, precio_hora from instalacion");
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			InstalacionDto instalacion = new InstalacionDto();
			instalacion.id_instalacion = rs.getInt(1);
			instalacion.nombre = rs.getString(2);
			instalacion.plazas = rs.getInt(3);
			instalacion.precio_hora = rs.getInt(4);
			result.add(instalacion);
		}
		return result;
	}
	
	public static List<AlquilerDto> getAlquileres() throws SQLException, IOException {
		List<AlquilerDto> result = new ArrayList<>();
		PreparedStatement ps;
		ps = getConexion().prepareStatement("select id_alquiler, id_socio, id_instalacion, fecha_inicio, fecha_fin, hora_entrada, hora_salida from alquiler_instalacion");
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			AlquilerDto alquiler = new AlquilerDto();
			alquiler.id_alquiler = rs.getInt(1);
			alquiler.id_socio = rs.getInt(2);
			alquiler.id_instalacion = rs.getInt(3);
			alquiler.fecha_inicio = new DateTime(rs.getTimestamp(4));
			alquiler.fecha_fin = new DateTime(rs.getTimestamp(5));
			alquiler.hora_entrada = new DateTime(rs.getTimestamp(6));
			alquiler.hora_salida = new DateTime(rs.getTimestamp(7));
			result.add(alquiler);
		}
		return result;
	}

	public static boolean comprobarInstalacionLibre(DateTime fecha_inicio, DateTime fecha_fin, int id_instalacion)
			throws SQLException, IOException {
		int inicioHora = fecha_inicio.getHourOfDay();
		int finHora = fecha_fin.getHourOfDay();
		PreparedStatement ps;
		ps = getConexion().prepareStatement(
				"select fecha_inicio, fecha_fin from actividad a, instalacion_asignada ia where a.id_actividad = ia.id_evento and ia.id_evento = ?");
		ps.setInt(1, id_instalacion);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			int inicioActividad = new DateTime(rs.getTimestamp(1)).getHourOfDay();
			int finActividad = new DateTime(rs.getTimestamp(2)).getHourOfDay();
			if (inicioHora < inicioActividad && finHora > inicioActividad) {
				return false;
			}
			if (inicioHora < finActividad && finHora > finActividad) {
				return false;
			}
			if (inicioHora == inicioActividad && finHora > inicioActividad) {
				return false;
			}
			if (inicioHora < finActividad && finHora == finActividad) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean comprobarSocioSinAlquiler(DateTime fecha_inicio, DateTime fecha_fin, int id_socio)
			throws SQLException, IOException {
		int inicioHora = fecha_inicio.getHourOfDay();
		int finHora = fecha_fin.getHourOfDay();
		PreparedStatement ps;
		ps = getConexion().prepareStatement(
				"select fecha_inicio, fecha_fin from alquiler_instalacion a where a.id_socio = ?");
		ps.setInt(1, id_socio);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			int inicioActividad = new DateTime(rs.getTimestamp(1)).getHourOfDay();
			int finActividad = new DateTime(rs.getTimestamp(2)).getHourOfDay();
			if (inicioHora < inicioActividad && finHora > inicioActividad) {
				return false;
			}
			if (inicioHora < finActividad && finHora > finActividad) {
				return false;
			}
			if (inicioHora == inicioActividad && finHora > inicioActividad) {
				return false;
			}
			if (inicioHora < finActividad && finHora == finActividad) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	public static List<MonitorDto> getMonitores() throws SQLException, IOException {
		List<MonitorDto> result = new ArrayList<>();
		PreparedStatement ps;
		ps = getConexion().prepareStatement("select id_monitor, nombre from monitor");
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			MonitorDto monitor = new MonitorDto();
			monitor.id_monitor = rs.getInt(1);
			monitor.nombre = rs.getString(2);
			result.add(monitor);
		}
		return result;
	}

	public static List<ActividadDto> getActividades() throws SQLException, IOException {
		List<ActividadDto> result = new ArrayList<>();
		PreparedStatement ps;
		ps = getConexion().prepareStatement("select id_actividad, nombre, fecha_inicio, fecha_fin,"
				+ " plazas_limitadas, intensidad, id_instalacion, id_asignacion from actividad a, instalacion_asignada ia "
				+ "where a.id_actividad = ia.id_evento and ia.actividad = true");
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			ActividadDto actividad = new ActividadDto();
			actividad.id_actividad = rs.getInt(1);
			actividad.nombre = rs.getString(2);
			actividad.fecha_inicio = new DateTime(rs.getTimestamp(3).getTime());
			actividad.fecha_fin = new DateTime(rs.getTimestamp(4).getTime());
			actividad.plazasLimite = rs.getBoolean(5);
			actividad.intensidad = rs.getString(6);
			actividad.id_instalacion = rs.getInt(7);
			actividad.id_asignacion = rs.getInt(8);
			result.add(actividad);
		}
		return result;
	}
	
	public static List<ActividadSinAsignarDto> getActividadesBase() throws SQLException, IOException {
		List<ActividadSinAsignarDto> result = new ArrayList<>();
		PreparedStatement ps;
		ps = getConexion().prepareStatement("select id_actividad, nombre,"
				+ " plazas_limitadas, intensidad from actividad a");
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			ActividadSinAsignarDto actividad = new ActividadSinAsignarDto();
			actividad.id_actividad = rs.getInt(1);
			actividad.nombre = rs.getString(2);
			actividad.plazas_limitadas = rs.getBoolean(3);
			actividad.intensidad = rs.getString(4);
			result.add(actividad);
		}
		return result;
	}

	public static boolean comprobarMonitorLibre(DateTime fecha_inicio, DateTime fecha_fin, int id_monitor)
			throws SQLException, IOException {
		int inicioHora = fecha_inicio.getHourOfDay();
		int finHora = fecha_fin.getHourOfDay();
		PreparedStatement ps;
		ps = getConexion()
				.prepareStatement("select fecha_inicio, fecha_fin from instalacion_asignada ia where id_monitor = ?");
		ps.setInt(1, id_monitor);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			int inicioActividad = new DateTime(rs.getTimestamp(1)).getHourOfDay();
			int finActividad = new DateTime(rs.getTimestamp(2)).getHourOfDay();
			if (inicioHora < inicioActividad && finHora > inicioActividad) {
				return false;
			}
			if (inicioHora < finActividad && finHora > finActividad) {
				return false;
			}
			if (inicioHora == inicioActividad && finHora > inicioActividad) {
				return false;
			}
			if (inicioHora < finActividad && finHora == finActividad) {
				return false;
			}
		}
		return true;
	}

	public static void asignarMonitor(MonitorDto monitor, ActividadDto actividad)
			throws SQLException, IOException, MonitorOcupadoException {
		if (comprobarMonitorLibre(actividad.fecha_inicio, actividad.fecha_fin, monitor.id_monitor)) {
			PreparedStatement ps;
			ps = getConexion().prepareStatement("update instalacion_asignada set id_monitor = ? where id_evento = ?");
			ps.setInt(1, monitor.id_monitor);
			ps.setInt(2, actividad.id_actividad);

			ps.executeUpdate();
		} else {
			throw new MonitorOcupadoException();
		}
	}

	public static InstalacionDto getInstalacionDeActividad(ActividadDto actividad) throws SQLException, IOException {
		InstalacionDto instalacion = new InstalacionDto();
		PreparedStatement ps;
		ps = getConexion().prepareStatement(
				"select id_instalacion, i.nombre, plazas from instalacion i where i.id_instalacion = ?");
		ps.setInt(1, actividad.id_instalacion);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			instalacion.id_instalacion = rs.getInt(1);
			instalacion.nombre = rs.getString(2);
			instalacion.plazas = rs.getInt(3);
		}
		return instalacion;
	}

	public static List<UsuarioDto> getSociosApuntados(ActividadDto actividad) throws SQLException, IOException {
		List<UsuarioDto> result = new ArrayList<>();
		PreparedStatement ps;
		ps = getConexion().prepareStatement("select id_usuario, nombre from usuario u, reserva_actividad ra "
				+ "where u.id_usuario = ra.id_usuario and ra.id_asignacion = ? and u.tipo = 'SOCIO'");
		ps.setInt(1, actividad.id_asignacion);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			UsuarioDto usuario = new UsuarioDto();
			usuario.id_usuario = rs.getInt(1);
			usuario.nombre = rs.getString(2);
			usuario.tipo = "SOCIO";
			result.add(usuario);
		}
		return result;
	}

	public static ResultSet mostrarCalendarioSalas(int id, Date sqlDate, DateTime date) {
		date = date.plusDays(1);
		Date sqlDateFin = new Date(date.toDate().getTime());
		PreparedStatement prStmnt;
		ResultSet rs = null;
		try {
			prStmnt = getConexion().prepareStatement(config.getProperty("SQL_SHOW_CALENDARIO_ACTIVIDADES"));
			prStmnt.setInt(1, id);
			prStmnt.setDate(2, sqlDate);
			prStmnt.setDate(3, sqlDateFin);
			rs = prStmnt.executeQuery();
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
		return rs;
	}

	public static ActividadDto[] getNombreActividades() {
		int size = 0;
		ResultSet rs = null;
		try {
			Statement stmnt = getConexion().createStatement();
			rs = stmnt.executeQuery(config.getProperty("SQL_COUNT_ACT"));
			while (rs.next()) {
				size = rs.getInt(1);
			}
			rs = stmnt.executeQuery(config.getProperty("SQL_SELECT_ACTIVIDADES"));
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}

		ActividadDto[] salas = new ActividadDto[size];
		ActividadDto var = null;
		try {
			int i = 0;
			while (rs.next()) {
				var = new ActividadDto();
				var.id_actividad = rs.getInt(1);
				var.nombre = rs.getString(2);
				var.plazasLimite = rs.getBoolean(3);
				var.intensidad = rs.getString(4);
				salas[i] = var;
				i++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return salas;
	}

	public static InstalacionDto[] getNombreSalas() {
		int size = 0;
		ResultSet rs = null;
		try {
			Statement stmnt = getConexion().createStatement();
			rs = stmnt.executeQuery(config.getProperty("SQL_COUNT_INT"));
			while (rs.next()) {
				size = rs.getInt(1);
			}
			rs = stmnt.executeQuery(config.getProperty("SQL_SELECT_INSTALACIONES"));
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}

		InstalacionDto[] salas = new InstalacionDto[size];
		try {
			int i = 0;
			InstalacionDto var = null;
			while (rs.next()) {
				var = new InstalacionDto();
				var.id_instalacion = rs.getInt(1);
				var.nombre = rs.getString(2);
				var.plazas = rs.getInt(3);
				salas[i] = var;
				i++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return salas;
	}

	public static ResultSet mostrarCalendarioInstalacion(int sala, Date sqlDateIn, DateTime date) {
		date = date.plusDays(1);
		Date sqlDateFin = new Date(date.toDate().getTime());
		String SQL_CALENDAR = config.getProperty("SQL_SHOW_CALENDAR_BY_INSTALACION");
		PreparedStatement prStmnt;
		ResultSet rs = null;
		try {
			prStmnt = getConexion().prepareStatement(SQL_CALENDAR);
			prStmnt.setInt(1, sala);
			prStmnt.setDate(2, sqlDateIn);
			prStmnt.setDate(3, sqlDateFin);
			rs = prStmnt.executeQuery();
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
		return rs;
	}

	public static ResultSet getReservasActividadesDeSocio(Long socio_id) {
		String SQL_SOCIO = config.getProperty("SQL_GET_ACTIVIDADES_SOCIO");
		PreparedStatement prStmnt;
		ResultSet rs = null;
		try {
			prStmnt = getConexion().prepareStatement(SQL_SOCIO);
			prStmnt.setLong(1, socio_id);
			rs = prStmnt.executeQuery();
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
		return rs;
	}

	public static String[] getNumerosSocios() {
		int size = 0;
		ResultSet rs = null;
		try {
			Statement stmnt = getConexion().createStatement();
			rs = stmnt.executeQuery(config.getProperty("SQL_COUNT_SOCIOS"));
			while (rs.next()) {
				size = rs.getInt(1);
			}
			rs = stmnt.executeQuery(config.getProperty("SQL_SELECT_SOCIOS"));
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}

		String[] salas = new String[size];
		try {
			int i = 0;
			while (rs.next()) {
				salas[i] = rs.getString(1);
				i++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return salas;
	}

	public static void deleteReservaSocio(Long idsocio, Long idact) {
		String SQL_SOCIO = config.getProperty("SQL_DELETE_RESERVA_SOCIO");
		PreparedStatement prStmnt;
		try {
			prStmnt = getConexion().prepareStatement(SQL_SOCIO);
			prStmnt.setLong(1, idsocio);
			prStmnt.setLong(2, idact);
			prStmnt.executeUpdate();

		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean haAsistido(Long idact) {
		String SQL_SOCIO = config.getProperty("SQL_CHECK_ASISTIDO");
		PreparedStatement prStmnt;
		ResultSet rs = null;
		Boolean asistido = false;
		try {
			prStmnt = getConexion().prepareStatement(SQL_SOCIO);
			prStmnt.setLong(1, idact);
			rs = prStmnt.executeQuery();

			if (rs.next())
				asistido = rs.getBoolean(1);

		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
		return asistido;
	}

	public static ResultSet getListaEspera(Long cursilloId) {
		String SQL = config.getProperty("SQL_LISTA_ESPERA");
		PreparedStatement prStmnt;
		ResultSet rs = null;
		try {
			prStmnt = getConexion().prepareStatement(SQL);
			prStmnt.setLong(1, cursilloId);
			rs = prStmnt.executeQuery();
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
		return rs;
	}

	public static CursilloDto[] getListaCursillos() {
		Statement stmnt;
		ResultSet rs = null;
		int size = 0;
		CursilloDto var = null;
		try {
			stmnt = getConexion().createStatement();
			rs = stmnt.executeQuery(config.getProperty("SQL_COUNT_CURSILLOS"));
			while (rs.next()) {
				size = rs.getInt(1);
			}
			rs = stmnt.executeQuery(config.getProperty("SQL_LISTA_CURSILLOS"));
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}

		CursilloDto[] cursillos = new CursilloDto[size];
		try {
			int i = 0;
			while (rs.next()) {
				var = new CursilloDto();
				var.id_cursillo = Integer.valueOf(rs.getString(1));
				var.nombre = rs.getString(2);
				var.plazas = rs.getInt(3);
				var.fecha1 = new DateTime(rs.getTimestamp(4).getTime());
				var.fecha2 = new DateTime(rs.getTimestamp(5).getTime());
				var.fecha_fin = new DateTime(rs.getTimestamp(6).getTime());
				var.fecha_inscripcion_socio = new DateTime(rs.getTimestamp(7).getTime());
				var.fecha_inscripcion_nosocio = new DateTime(rs.getTimestamp(8).getTime());
				var.precio = rs.getDouble(3);
				var.id_instalacion = Integer.valueOf(rs.getString(10));
				var.id_monitor = Integer.valueOf(rs.getString(11));
				var.precio = rs.getDouble(12);
				cursillos[i] = var;
				i++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return cursillos;
	}

	public static ResultSet getListaApuntados(Long cursilloId) {
		String SQL = config.getProperty("SQL_LISTA_APUNTADOS");
		PreparedStatement prStmnt;
		ResultSet rs = null;
		try {
			prStmnt = getConexion().prepareStatement(SQL);
			prStmnt.setLong(1, cursilloId);
			rs = prStmnt.executeQuery();
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
		return rs;
	}

	public static void deleteFromCursillo(Long id_socio, Long id_cursillo) {
		String SQL_SOCIO = config.getProperty("SQL_DELETE_APUNTADO");
		PreparedStatement prStmnt;
		try {
			prStmnt = getConexion().prepareStatement(SQL_SOCIO);
			prStmnt.setLong(1, id_socio);
			prStmnt.setLong(2, id_cursillo);
			prStmnt.executeUpdate();

		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}

	public static void deleteActividadesFromSocioInCursillo(Long id_socio, Long id_cursillo) {
		String SQL = config.getProperty("SQL_GET_ACTIVIDADES_CURSILLO_SOCIO");
		String SQL_DEL = config.getProperty("SQL_DELETE_ACTIVIDADES_CURSILLO");
		PreparedStatement prStmnt;
		ResultSet rs = null;
		try {
			prStmnt = getConexion().prepareStatement(SQL);
			prStmnt.setLong(2, id_socio);
			prStmnt.setLong(1, id_cursillo);
			rs = prStmnt.executeQuery();
			prStmnt = getConexion().prepareStatement(SQL_DEL);
			while (rs.next()) {
				prStmnt.setLong(1, rs.getLong(1));
				prStmnt.executeUpdate();
			}

		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}

	public static ResultSet getCursilloFromSocio(Long id_socio) {
		PreparedStatement prStmnt;
		ResultSet rs = null;
		try {
			prStmnt = getConexion().prepareStatement(config.getProperty("SQL_GET_CURSILLOS_SOCIO"));
			prStmnt.setLong(1, id_socio);
			rs = prStmnt.executeQuery();
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}

		return rs;
	}

	public static void generarCobro(int idSocio, double precio, boolean metalico,String concepto) {
		String SQL = config.getProperty("SQL_GENERAR_PAGO");
		Date date = new Date(DateTime.now().getMillis());
		PreparedStatement prStmnt;
		try {
			prStmnt = getConexion().prepareStatement(SQL);
			prStmnt.setLong(1, idSocio);
			prStmnt.setDouble(2, precio);
			prStmnt.setDate(3, date);
			prStmnt.setBoolean(4, metalico);
			prStmnt.setString(5, concepto);
			prStmnt.executeUpdate();

		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void deleteActividadesFromCursillo(Long id_cursillo) {
		String SQL = config.getProperty("SQL_GET_ACTIVIDADES_CURSILLO");
		String SQL_DEL = config.getProperty("SQL_DELETE_ACTIVIDADES_CURSILLO");
		PreparedStatement prStmnt;
		ResultSet rs = null;
		try {
			prStmnt = getConexion().prepareStatement(SQL);
			prStmnt.setLong(1, id_cursillo);
			rs = prStmnt.executeQuery();
			prStmnt = getConexion().prepareStatement(SQL_DEL);
			while (rs.next()) {
				prStmnt.setLong(1, rs.getLong(1));
				prStmnt.executeUpdate();
			}

		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}

	public static void deleteCursillo(Long id_cursillo) {
		String SQL = config.getProperty("SQL_BORRAR_CURSILLO");
		String SQL2 = config.getProperty("SQL_BORRAR_RESERVA_CURSILLO");
		PreparedStatement prStmnt;
		try {
			prStmnt = getConexion().prepareStatement(SQL2);
			prStmnt.setLong(1, id_cursillo);
			prStmnt.executeUpdate();
			
			prStmnt = getConexion().prepareStatement(SQL);
			prStmnt.setLong(1, id_cursillo);
			prStmnt.executeUpdate();

		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public static ArrayList<UsuarioDto> getUsuariosAfectados(int id_cursillo) {
		String SQL = config.getProperty("SQL_GET_USUARIOS_AFECTADOS");
		ArrayList<UsuarioDto> list = new ArrayList<UsuarioDto>();
		PreparedStatement prStmnt;
		ResultSet rs = null;
		try {
			prStmnt = getConexion().prepareStatement(SQL);
			prStmnt.setLong(1, id_cursillo);
			rs = prStmnt.executeQuery();
			
			
			while(rs.next()){
				UsuarioDto aux = new UsuarioDto();
				aux.id_usuario = rs.getInt(1);
				aux.nombre = rs.getString(2);
				list.add(aux);
			}

		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public static void deleteInstalacionAsiganada(Long id_cursillo) {
		String SQL = config.getProperty("SQL_DELETE_INSTALACIONASIGNADA_CURSILLO");
		PreparedStatement prStmnt;
		try {
			prStmnt = getConexion().prepareStatement(SQL);
			prStmnt.setLong(1, id_cursillo);
			prStmnt.executeUpdate();

		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @author Samuel Moreno
	 * @return Devuelve una lista de ActividadSinAsignarDto con todas las
	 *         actividades que no estan asignadas de la base de datos
	 */
	public static List<ActividadSinAsignarDto> getActividadesSinAsignar() throws SQLException, IOException {
		List<ActividadSinAsignarDto> result = new ArrayList<>();
		PreparedStatement ps;
		String sql = "select id_actividad, nombre, plazas_limitadas, intensidad from actividad where id_actividad not in (select id_evento from instalacion_asignada where actividad = TRUE)";
		ps = getConexion().prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			ActividadSinAsignarDto actividad = new ActividadSinAsignarDto();
			actividad.id_actividad = rs.getInt(1);
			actividad.nombre = rs.getString(2);
			actividad.plazas_limitadas = rs.getBoolean(3);
			actividad.intensidad = rs.getString(4);
			result.add(actividad);
		}
		return result;
	}

	/**
	 * @author Samuel Moreno
	 * @param id_actividad
	 *            Actividad que se va a asignar
	 * @param id_instalacion
	 *            Instalacion en la que se realizara la actividad
	 * @param id_monitor
	 *            Monitor que impartira la actividad
	 * @param fechaInicio
	 *            Fecha completa (año, mes, dia, hora) de inicio
	 * @param fechaFin
	 *            Fecha completa (año, mes, dia, hora) de fin
	 * @return Crea una instalacion_asignada en la base de datos con los datos
	 *         proporcionados
	 */
	public static void asignarEvento(int id_evento, int id_instalacion, int id_monitor, DateTime fechaInicio,
			DateTime fechaFin, boolean actividad) throws SQLException, IOException {
		String sql = "insert into instalacion_asignada (id_evento, id_instalacion, id_monitor, fecha_inicio, fecha_fin, actividad) values (?, ?, ?, ?, ?, ?)";
		PreparedStatement ps;
		ps = getConexion().prepareStatement(sql);
		ps.setInt(1, id_evento);
		ps.setInt(2, id_instalacion);
		ps.setInt(3, id_monitor);
		ps.setDate(4, new Date(fechaInicio.toDate().getTime()));
		ps.setDate(5, new Date(fechaFin.toDate().getTime()));
		ps.setBoolean(6, actividad);
		ps.executeUpdate();

	}

	/**
	 * @author Samuel Moreno
	 * @param horaInicio
	 *            Dia y hora entre los que estan las actividades
	 * @param horaFin
	 *            Dia y hora entre los que estan las actividades
	 * @return Devuelve la lista de actividades entre los paremetros dados
	 */
	public static ArrayList<ActividadDto> getActividades(DateTime horaInicio, DateTime horaFin)
			throws SQLException, IOException {

		ArrayList<ActividadDto> resultado = new ArrayList<ActividadDto>();
		String sql = "select a.id_actividad, a.nombre, i.fecha_inicio, i.fecha_fin, i.id_asignacion from instalacion_asignada i inner join actividad a on i.id_evento=a.id_actividad where i.actividad=TRUE"
				+ " and i.fecha_inicio >= ? and i.fecha_fin <= ?";
		PreparedStatement ps;
		ps = getConexion().prepareStatement(sql);
		ps.setDate(1, new Date(horaInicio.toDate().getTime()));
		ps.setDate(2, new Date(horaFin.toDate().getTime()));
		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			ActividadDto actividad = new ActividadDto();
			actividad.id_actividad = rs.getInt(1);
			actividad.nombre = rs.getString(2);
			actividad.fecha_inicio = new DateTime(rs.getTimestamp(3).getTime());
			actividad.fecha_fin = new DateTime(rs.getTimestamp(4).getTime());
			actividad.id_asignacion = rs.getInt(5);
			resultado.add(actividad);
		}

		return resultado;
	}

	/**
	 * @author Samuel Moreno
	 * 
	 * @param id_actividad
	 *            Identificador de la actividad que se va a tratar
	 * @return devuelve el numero de plazas que tiene una actividad asignada a una
	 *         instalacion dependiendo de la instalacion a la que esta asignada
	 */
	public static int getPlazasMax(int id_actividad) throws SQLException, IOException {
		String sql = "select plazas from instalacion where id_instalacion in (select id_instalacion from instalacion_asignada where actividad = TRUE and id_evento = ?)";

		PreparedStatement ps;
		ps = getConexion().prepareStatement(sql);
		ps.setInt(1, id_actividad);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			return rs.getInt(1);
		}
		return 0;
	}

	/**
	 * @author Samuel Moreno
	 * 
	 * @param actividad
	 *            Identificador de la actividad que se va a tratar
	 * @return devuelve el numero de socios que tienen una reserva para la actividad
	 *         dada
	 */
	public static int getPlazasOcupadas(int id_actividad) throws SQLException, IOException {
		String sql = "select count(id_usuario) from reserva_actividad where id_asignacion = ?";

		PreparedStatement ps;
		ps = getConexion().prepareStatement(sql);
		ps.setInt(1, id_actividad);
		ResultSet rs = ps.executeQuery();
		rs.next();
		return rs.getInt(1);

	}

	/**
	 * @author Samuel Moreno
	 * @param inicio
	 *            hora de inicio
	 * @param fin
	 *            hora de fin
	 * @param idUsuario
	 *            identificador del socio
	 * @return devuelve una lista de actividades para las que esta apuntado el socio
	 *         y estan entre las horas dadas
	 */
	public static ArrayList<ActividadDto> getCoincidenciasUsuario(DateTime fechaInicio, DateTime fechaFin,
			int idUsuario) throws SQLException, IOException {

		ArrayList<ActividadDto> listaDeCoincidencias = new ArrayList<ActividadDto>();
		String sql = "select fecha_inicio, fecha_fin, id_asignacion from instalacion_asignada  where id_asignacion in(select id_asignacion from reserva_actividad where id_usuario = ?"
		+ " and fecha_inicio >= ? and fecha_fin <= ?)";

		PreparedStatement ps;
		ps = getConexion().prepareStatement(sql);
		ps.setInt(1, idUsuario);
		ps.setDate(2, new Date(fechaInicio.toDate().getTime()));
		ps.setDate(3, new Date(fechaFin.toDate().getTime()));
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			ActividadDto nuevaActividad = new ActividadDto();
			nuevaActividad.fecha_inicio = new DateTime(rs.getTimestamp(1).getTime());
			nuevaActividad.fecha_fin = new DateTime(rs.getTimestamp(2).getTime());
			nuevaActividad.id_asignacion = rs.getInt(3);
			listaDeCoincidencias.add(nuevaActividad);
		}
		
//		while (rs.next()) {
//			ActividadDto nuevaActividad = new ActividadDto();
//			nuevaActividad.id_actividad = rs.getInt(1);
//			nuevaActividad.nombre = rs.getString(2);
//			nuevaActividad.fecha_inicio = new DateTime(rs.getTimestamp(3).getTime());
//			nuevaActividad.fecha_fin = new DateTime(rs.getTimestamp(4).getTime());
//			nuevaActividad.id_asignacion = rs.getInt(5);
//			listaDeCoincidencias.add(nuevaActividad);
//		}

		return listaDeCoincidencias;
	}

	/**
	 * @author Samuel Moreno
	 * @param id_usuario
	 *            identificador del socio que va a reservar
	 * @param id_asignacion
	 *            identificador de la instalacion_asignada (id_asignacion)
	 */
	public static void realizarReserva(int id_asignacion, int id_usuario) throws SQLException, IOException {
		String sql = "insert into reserva_actividad(id_asignacion, id_usuario, asistido) values (?, ?, FALSE)";
		PreparedStatement ps;
		ps = getConexion().prepareStatement(sql);
		ps.setInt(1, id_asignacion);
		ps.setInt(2, id_usuario);
		ps.executeUpdate();
	}

	/**
	 * @author Samuel Moreno
	 * @param primerDia
	 *            primer dia del mes a las 00:00:00
	 * @param ultimoDia
	 *            ultimo dia del mes a las 23:59:59
	 * @return Devuelve una lista de todas las asignaciones de instalaciones que
	 *         esten entre las fechas dadas
	 */
	public static ArrayList<InstalacionAsignadaDto> getInstalacionAsignada(DateTime primerDia, DateTime ultimoDia)
			throws SQLException, IOException {
		ArrayList<InstalacionAsignadaDto> asignaciones = new ArrayList<InstalacionAsignadaDto>();
		String sql = "select id_asignacion, id_evento, id_instalacion, id_monitor, fecha_inicio, fecha_fin, actividad from instalacion_asignada where fecha_inicio >= ? and instalacion_asignada.fecha_fin <= ?";

		PreparedStatement ps;
		ps = getConexion().prepareStatement(sql);
		ps.setDate(1, new Date(primerDia.toDate().getTime()));
		ps.setDate(2, new Date(ultimoDia.toDate().getTime()));
		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			InstalacionAsignadaDto asignacion = new InstalacionAsignadaDto();
			asignacion.id_asignacion = rs.getInt(1);
			asignacion.id_evento = rs.getInt(2);
			asignacion.id_instalacion = rs.getInt(3);
			asignacion.id_monitor = rs.getInt(4);
			asignacion.fecha_inicio = new DateTime(rs.getTimestamp(5).getTime());
			asignacion.fecha_fin = new DateTime(rs.getTimestamp(6).getTime());
			asignacion.actividad = rs.getBoolean(7);
			asignaciones.add(asignacion);
		}
		return asignaciones;
	}

	/**
	 * @author Samuel Moreno
	 * @param cursillo
	 *            Dto con todos los datos del cursillo a insertar
	 */
	public static void crearCursillo(CursilloDto cursillo) throws SQLException, IOException {
		String sql = "insert into cursillo(nombre, plazas, fecha_inicio1, fecha_inicio2, fecha_fin, fecha_inscripcion_socio, fecha_inscripcion_nosocio, precio, id_instalacion, id_monitor) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		PreparedStatement ps;
		ps = getConexion().prepareStatement(sql);
		ps.setString(1, cursillo.nombre);
		ps.setInt(2, cursillo.plazas);
		ps.setDate(3, new Date(cursillo.fecha1.toDate().getTime()));
		ps.setDate(4, new Date(cursillo.fecha2.toDate().getTime()));
		ps.setDate(5, new Date(cursillo.fecha_fin.toDate().getTime()));
		ps.setDate(6, new Date(cursillo.fecha_inscripcion_socio.toDate().getTime()));
		ps.setDate(7, new Date(cursillo.fecha_inscripcion_nosocio.toDate().getTime()));
		ps.setDouble(8, cursillo.precio);
		ps.setInt(9, cursillo.id_instalacion);
		ps.setInt(10, cursillo.id_monitor);
		ps.executeUpdate();
	}

	/**
	 * @author Samuel
	 * @return Devuelve el id del ultimo cursillo creado
	 */
	public static int ultimoCursilloCreado() throws SQLException, IOException {
		String sql = "select max(id_cursillo) from cursillo";

		PreparedStatement ps;
		ps = getConexion().prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		rs.next();
		return rs.getInt(1);
	}

	public static int ultimaActividadCreada() throws SQLException, IOException {
		String sql = "select max(id_actividad) from actividad";

		PreparedStatement ps;
		ps = getConexion().prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		rs.next();
		return rs.getInt(1);
	}

	/**
	 * @author Samuel Moreno
	 * @return Devuelve una lista de cursillosDto con los datos: id_cursillo,
	 *         nombre, plazas, plazas_ocupadas, fecha, fecha2, id_instalacion,
	 *         id_monitor
	 */
	public static List<CursilloDto> getCursillos() throws SQLException, IOException {
		ArrayList<CursilloDto> cursillos = new ArrayList<CursilloDto>();
		String sql = "select c.id_cursillo, c.nombre, c.plazas, count(r.id_usuario), c.fecha_inicio1, c.fecha_inicio2, c.fecha_fin, c.fecha_inscripcion_socio, c.fecha_inscripcion_nosocio, c.precio, c.id_instalacion, c.id_monitor, c.precio from cursillo c left join reserva_cursillo r on r.id_cursillo = c.id_cursillo group by c.id_cursillo";

		PreparedStatement ps;
		ps = getConexion().prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			CursilloDto cursillo = new CursilloDto();
			cursillo.id_cursillo = rs.getInt(1);
			cursillo.nombre = rs.getString(2);
			cursillo.plazas = rs.getInt(3);
			cursillo.plazas_ocupadas = rs.getInt(4);
			cursillo.fecha1 = new DateTime(rs.getTimestamp(5).getTime());
			cursillo.fecha2 = new DateTime(rs.getTimestamp(6).getTime());
			cursillo.fecha_fin = new DateTime(rs.getTimestamp(7).getTime());
			cursillo.fecha_inscripcion_socio = new DateTime(rs.getTimestamp(8).getTime());
			cursillo.fecha_inscripcion_nosocio = new DateTime(rs.getTimestamp(9).getTime());
			cursillo.precio = rs.getDouble(10);
			cursillo.id_instalacion = rs.getInt(11);
			cursillo.id_monitor = rs.getInt(12);
			cursillo.precio = rs.getDouble(13);
			cursillos.add(cursillo);
		}

		return cursillos;
	}

	/**
	 * @author Samuel Moreno
	 * @param id_cursillo
	 *            Cursillo del que se obtendran las actividades
	 * @return Devuelve una lista con todas las actividades del mes reservadas por
	 *         un cursillo
	 */
	public static ArrayList<InstalacionAsignadaDto> getAsignacionesCursillo(int id_cursillo)
			throws SQLException, IOException {
		ArrayList<InstalacionAsignadaDto> asignaciones = new ArrayList<InstalacionAsignadaDto>();
		String sql = "select id_asignacion, id_evento, id_instalacion, id_monitor, fecha_inicio, fecha_fin, actividad from instalacion_asignada where actividad = false and id_evento = ?";

		PreparedStatement ps;
		ps = getConexion().prepareStatement(sql);
		ps.setInt(1, id_cursillo);
		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			InstalacionAsignadaDto asignacion = new InstalacionAsignadaDto();
			asignacion.id_asignacion = rs.getInt(1);
			asignacion.id_evento = rs.getInt(2);
			asignacion.id_instalacion = rs.getInt(3);
			asignacion.id_monitor = rs.getInt(4);
			asignacion.fecha_inicio = new DateTime(rs.getTimestamp(5).getTime());
			asignacion.fecha_fin = new DateTime(rs.getTimestamp(6).getTime());
			asignacion.actividad = rs.getBoolean(7);
			asignaciones.add(asignacion);
		}
		return asignaciones;
	}

	/**
	 * @author Samuel Moreno inserta en la tabla de reserva_cursillo con los datos
	 *         proporcionados
	 */
	public static void reservarCursillo(int idSocio, int id_cursillo, DateTime ahora, boolean aceptado)
			throws SQLException, IOException {
		String sql = "insert into reserva_cursillo(id_cursillo, id_usuario, fecha_apuntado, lista_aceptados) values (?, ?, ?, ?)";
		PreparedStatement ps;
		ps = getConexion().prepareStatement(sql);
		ps.setInt(1, id_cursillo);
		ps.setInt(2, idSocio);
		ps.setDate(3, new Date(ahora.toDate().getTime()));
		ps.setBoolean(4, aceptado);
		ps.executeUpdate();

	}

	/**
	 * 
	 * @param noSocio
	 *            inserta los datos del nuevo usuario (no socio) en la base de datos
	 *            si no hay uno con su mismo dni
	 */
	public static int insertarNoSocio(UsuarioDto noSocio) throws SQLException, IOException {

		String sql = "select id_usuario from usuario where dni = ? and tipo = 'NOSOCIO'";
		PreparedStatement ps;
		ps = getConexion().prepareStatement(sql);
		ps.setString(1, noSocio.dni);
		ResultSet rs = ps.executeQuery();

		if (!rs.next()) {
			String sql1 = "insert into usuario(nombre, tipo, dni, telefono) values (?, ?, ?, ?)";
			PreparedStatement ps1 = getConexion().prepareStatement(sql1);
			ps1.setString(1, noSocio.nombre);
			ps1.setString(2, noSocio.tipo);
			ps1.setString(3, noSocio.dni);
			ps1.setString(4, noSocio.telefono);
			ps1.executeUpdate();
			//System.out.println("Insertado nuevo usuario"); //TODO
			return ultimoNoSocioCreado();

		} else {
			int id = rs.getInt(1);
			String sql2 = "update usuario set nombre = ?, telefono = ? where dni = ?";
			PreparedStatement ps2 = getConexion().prepareStatement(sql2);
			ps2.setString(1, noSocio.nombre);
			ps2.setString(2, noSocio.telefono);
			ps2.setString(3, noSocio.dni);
			ps2.executeUpdate();
			// System.out.println("Update usuario: " + id); //TODO
			return id;
		}

	}

	/**
	 * @author Samuel
	 * @return Devuelve el id del ultimo no socio creado
	 */
	public static int ultimoNoSocioCreado() throws SQLException, IOException {
		String sql = "select max(id_usuario) from usuario where tipo = 'NOSOCIO'";

		PreparedStatement ps;
		ps = getConexion().prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		rs.next();
		return rs.getInt(1);
	}

	public static void eliminarAsignacion(ActividadDto actividad) throws SQLException, IOException {
		PreparedStatement ps;
		ps = getConexion().prepareStatement("delete from reserva_actividad where id_asignacion = ?");
		ps.setInt(1, actividad.id_asignacion);

		ps.executeUpdate();

		ps = getConexion().prepareStatement("delete from instalacion_asignada where id_asignacion = ?");
		ps.setInt(1, actividad.id_asignacion);

		ps.executeUpdate();
	}

	public static void registrarAsistencia(ActividadDto actividad, UsuarioDto usuario)
			throws SQLException, IOException {
		PreparedStatement ps;
		ps = getConexion().prepareStatement(
				"update reserva_actividad set asistido = 'true' where id_asignacion = ? and id_usuario = ?");
		ps.setInt(1, actividad.id_asignacion);
		ps.setInt(2, usuario.id_usuario);

		ps.executeUpdate();
	}

	public static UsuarioDto existeNoSocio(String dni) throws SQLException, IOException {
		String sql = "select id_usuario, nombre, telefono from usuario where dni = ? and tipo = 'NOSOCIO'";

		PreparedStatement ps;
		ps = getConexion().prepareStatement(sql);
		ps.setString(1, dni);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {

			UsuarioDto noSocio = new UsuarioDto();
			noSocio.id_usuario = rs.getInt(1);
			noSocio.dni = dni;
			noSocio.nombre = rs.getString(2);
			noSocio.telefono = rs.getString(3);
			return noSocio;
		}

		return null;
	}
	
	public static List<RecursoDto> getRecursos() throws SQLException, IOException {
		List<RecursoDto> result = new ArrayList<>();
		PreparedStatement ps;
		ps = getConexion().prepareStatement("select id_recurso, nombre from recurso");
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			RecursoDto recurso = new RecursoDto();
			recurso.id_recurso = rs.getInt(1);
			recurso.nombre = rs.getString(2);
			result.add(recurso);
		}
		return result;
	}

	public static void crearPago(int id_socio, double valor, String concepto) throws SQLException, IOException {
		PreparedStatement ps;
		ps = getConexion()
				.prepareStatement("insert into pago (id_usuario, valor, fecha_pago, pagado, concepto) values (?,?,?,false,?)");
		ps.setInt(1, id_socio);
		ps.setDouble(2, valor);
		ps.setDate(3, new Date(new DateTime().toDate().getTime()));
		ps.setString(4, concepto);

		ps.executeUpdate();
	}

	public static double getPrecioAlquiler(int id_alquiler) throws SQLException, IOException {
		PreparedStatement ps;
		ps = getConexion().prepareStatement("select precio_hora, fecha_inicio, fecha_fin from instalacion i, "
				+ "alquiler_instalacion a where a.id_instalacion = i.id_instalacion and a.id_alquiler = ?");
		ps.setInt(1, id_alquiler);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			DateTime fecha_inicio = new DateTime(rs.getTimestamp(2));
			DateTime fecha_fin = new DateTime(rs.getTimestamp(3));
			if (fecha_inicio.getHourOfDay()==fecha_fin.minusHours(1).getHourOfDay()) {
				return rs.getDouble(1);
			} else {
				return rs.getDouble(1)*2;
			}
		}
		return 0.0;
	}

	public static void registrarEntradaSalidaAlquiler(int id_alquiler, DateTime hora_entrada, DateTime hora_salida) throws SQLException, IOException {
			PreparedStatement ps;
			ps = getConexion().prepareStatement("update alquiler_instalacion set hora_entrada = ?, hora_salida = ? where id_alquiler = ?");
			ps.setDate(1, new Date(hora_entrada.toDate().getTime()));
			ps.setDate(2, new Date(hora_salida.toDate().getTime()));
			ps.setInt(3, id_alquiler);

			ps.executeUpdate();
	}

	public static void eliminarAsignacionesDesdeFecha(int id_actividad, DateTime fecha) throws SQLException, IOException {
		PreparedStatement ps;
		ps = getConexion().prepareStatement("delete from instalacion_asignada where id_evento = ? and fecha_inicio > ?");
		ps.setInt(1, id_actividad);
		ps.setDate(2, new Date(fecha.toDate().getTime()));

		ps.executeUpdate();
	}
	
	public static void eliminarReservasDeAsignacion(int id_asignacion) throws SQLException, IOException {
		PreparedStatement ps;
		ps = getConexion().prepareStatement("delete from reserva_actividad where id_asignacion = ?");
		ps.setInt(1, id_asignacion);

		ps.executeUpdate();
	}
	
	public static List<Integer> getSociosActividadesDesdeFecha(int id_actividad, DateTime fecha) throws SQLException, IOException {
		List<Integer> socios = new ArrayList<Integer>();
		PreparedStatement ps;
		ps = getConexion().prepareStatement("select id_usuario from reserva_actividad ra, instalacion_asignada ia "
				+ "where ra.id_asignacion = ia.id_asignacion and ia.id_evento = ? and ia.fecha_inicio > ?");
		ps.setInt(1, id_actividad);
		ps.setDate(2, new Date(fecha.toDate().getTime()));
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			socios.add(rs.getInt(1));
		}
		return socios;
	}
	
	public static List<Integer> getAsignacionesDesdeFecha(int id_actividad, DateTime fecha) throws SQLException, IOException {
		List<Integer> ids = new ArrayList<Integer>();
		PreparedStatement ps;
		ps = getConexion().prepareStatement("select id_asignacion from instalacion_asignada ia "
				+ "where ia.id_evento = ? and ia.fecha_inicio > ?");
		ps.setInt(1, id_actividad);
		ps.setDate(2, new Date(fecha.toDate().getTime()));
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			ids.add(rs.getInt(1));
		}
		return ids;
	}
}