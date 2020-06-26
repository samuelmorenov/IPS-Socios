package logic.lista_espera;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.joda.time.DateTime;

import db.DatabaseLogic;
import logic.cursillo_crear_reservar.TratamientoArchivos;
import logic.dto.CursilloDto;
import logic.dto.UsuarioDto;

public class LogicaListaEspera {
	
	TratamientoArchivos writer = new TratamientoArchivos(); 

	public ArrayList<UsuarioDto> processRs(ResultSet rs){
		
		ArrayList<UsuarioDto> lista = new ArrayList<>();
		UsuarioDto var = null;
		try {
			while (rs.next()) {
				var = new UsuarioDto();
				var.id_usuario = rs.getInt(1);
				var.nombre = rs.getString(2);
				var.tipo = rs.getString(3);
				var.dni = rs.getString(4);
				var.telefono = rs.getString(5);
				lista.add(var);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lista;
	}	
	
	public void deleteReserva(CursilloDto c, Long id_socio) throws RuntimeException{
		Long id_cursillo = Integer.toUnsignedLong(c.id_cursillo);
		if(c.fecha_fin.isAfterNow())
		{
			DatabaseLogic.deleteActividadesFromSocioInCursillo(id_socio, id_cursillo);
			DatabaseLogic.deleteFromCursillo(id_socio, id_cursillo);
		}
		else
			throw new RuntimeException();
	}

	public void deleteCursillo(CursilloDto cursillo, DateTime now, double precio) {
		Long id_cursillo = Integer.toUnsignedLong(cursillo.id_cursillo);
		ArrayList<UsuarioDto> lista = DatabaseLogic.getUsuariosAfectados(cursillo.id_cursillo);
		if(cursillo.fecha_fin.isAfterNow())
		{
			for (UsuarioDto usuarioDto : lista) {
				try {
					writer.crearDocumentoListaApuntados(lista, cursillo, "Afectados por el cursillo " + cursillo.id_cursillo);
				} catch (IOException e) {
					e.printStackTrace();
				}
				DatabaseLogic.generarCobro(usuarioDto.id_usuario, -precio, false, "Reembolso");
			}
			DatabaseLogic.deleteActividadesFromCursillo(id_cursillo);
			DatabaseLogic.deleteInstalacionAsiganada(id_cursillo);
			DatabaseLogic.deleteCursillo(id_cursillo);
		}
	}
}
