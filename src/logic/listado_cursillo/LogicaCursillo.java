package logic.listado_cursillo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import db.DatabaseLogic;
import logic.dto.CursilloDto;

public class LogicaCursillo {

	public List<CursilloDto> processRs(ResultSet rs) {

		List<CursilloDto> cursillos = new ArrayList<>();
		CursilloDto var = null;
		try {
			while (rs.next()) {
				var = new CursilloDto();
				var.id_cursillo = Integer.valueOf(rs.getString(1));
				var.nombre = rs.getString(2);
				var.plazas = rs.getInt(3);
				var.fecha1 = new DateTime(rs.getTimestamp(4).getTime());
				var.fecha2 = new DateTime(rs.getTimestamp(5).getTime());
				var.id_instalacion = Integer.valueOf(rs.getString(6));
				var.id_monitor = Integer.valueOf(rs.getString(7));
				cursillos.add(var);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return cursillos;
	}

	public List<CursilloDto> checkBoolean(List<CursilloDto> list, boolean pasadas, boolean futuras, boolean anuladas,
			boolean todas) {
		DateTime date = new DateTime();
		
		List<CursilloDto> aux = new ArrayList<>();
		if (pasadas) {
			for (CursilloDto a : list) {
				if (a.fecha1.isBefore(date))
					aux.add(a);
			}
			return aux;
		}
		if (futuras) {
			for (CursilloDto a : list) {
				if (date.isBefore(a.fecha1))
					aux.add(a);
			}
			return aux;
		}
		if (anuladas) {
			for (CursilloDto a : list) {
				if ((a.fecha1 == null) || (a.fecha2 == null))
					aux.add(a);
			}
			return aux;
		}
		if (todas)
			return list;
		return list;
	}

	public void deleteReserva(Long id_socio, CursilloDto cursillo) throws RuntimeException{
		Long id_cursillo = Integer.toUnsignedLong(cursillo.id_cursillo);
		if(cursillo.fecha1.isAfterNow())
		{
			DatabaseLogic.deleteActividadesFromSocioInCursillo(id_socio, id_cursillo);
			DatabaseLogic.deleteFromCursillo(id_socio, id_cursillo);
		}
		else
			throw new RuntimeException();
	}





}
