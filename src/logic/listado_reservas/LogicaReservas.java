package logic.listado_reservas;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import db.DatabaseLogic;
import logic.dto.ActividadDto;

public class LogicaReservas {

	public List<ActividadDto> processRs(ResultSet rs) {

		List<ActividadDto> reservas = new ArrayList<>();
		ActividadDto var = null;

		try {
			while (rs.next()) {
				var = new ActividadDto();
				var.id_actividad = rs.getInt(1);
				var.nombre = rs.getString(2);
				var.intensidad = rs.getString(3);
				var.fecha_inicio = new DateTime(rs.getTimestamp(4).getTime());
				var.fecha_fin = new DateTime(rs.getTimestamp(5).getTime());
				reservas.add(var);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return reservas;
	}

	public List<ActividadDto> checkBoolean(List<ActividadDto> list, boolean pasadas, boolean futuras, boolean anuladas,
			boolean todas) {
		DateTime date = new DateTime();
		
		List<ActividadDto> aux = new ArrayList<>();
		if (pasadas) {
			for (ActividadDto a : list) {
				if (a.fecha_inicio.isBefore(date))
					aux.add(a);
			}
			return aux;
		}
		if (futuras) {
			for (ActividadDto a : list) {
				if (date.isBefore(a.fecha_inicio))
					aux.add(a);
			}
			return aux;
		}
		if (anuladas) {
			for (ActividadDto a : list) {
				if (a.fecha_inicio == null)
					aux.add(a);
			}
			return aux;
		}
		if (todas)
			return list;
		return list;
	}

	public void deleteReserva(ActividadDto a, Long id_socio) throws RuntimeException{
		Long actividad_id = Integer.toUnsignedLong(a.id_actividad);
		if(!DatabaseLogic.haAsistido(actividad_id))
			DatabaseLogic.deleteReservaSocio(id_socio, actividad_id);
		else
			throw new RuntimeException();
	}

}
