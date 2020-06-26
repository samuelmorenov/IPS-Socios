package logic.cursillo_crear_reservar;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.joda.time.DateTime;

import logic.dto.CursilloDto;
import logic.dto.OcupacionesDto;
import logic.dto.UsuarioDto;

public class TratamientoArchivos {
	private String nombreArchivo;

	public String getNombreArchivo() {
		return nombreArchivo;
	}

	public String crearDocumento(ArrayList<OcupacionesDto> ocupados, String nombre) throws IOException {
		DateTime ahora = new DateTime();
		nombreArchivo = nombre+" " + ahora.toString("dd-MM-yyyy HH-mm-ss.SSS")
				+ ".txt";

		String mensajeError = "";

		File archivo = new File(nombreArchivo);
		BufferedWriter bw;
		bw = new BufferedWriter(new FileWriter(archivo));
		for (int i = 0; i < ocupados.size(); i++) {

			String mensaje1 = ocupados.get(i).tipo + " con id: " + ocupados.get(i).id + " no esta disponible el dia:";
			String mensaje2 = "[" + ocupados.get(i).dia + "-" + ocupados.get(i).mes + "-" + ocupados.get(i).anio
					+ "] de " + ocupados.get(i).hora_inicio + ":00 a " + ocupados.get(i).hora_fin + ":00";
			bw.write(mensaje1);
			bw.newLine();
			bw.write(mensaje2);
			bw.newLine();
			bw.newLine();

			if (i < 6)
				mensajeError = mensajeError + "\n" + mensaje1 + "\n" + mensaje2 + "\n";
			if (i == 6)
				mensajeError = mensajeError + "\n... " + (ocupados.size() - i) + " coincidencias mas";
		}

		bw.close();
		mensajeError = mensajeError + "\n\n¿Desea abrir el archivo creado con todas las coincidencias?";

		return mensajeError;
	}
	public void abrirElUltimoArchivo() throws IOException {
		File objetofile = new File(nombreArchivo);
		Desktop.getDesktop().open(objetofile);
	}
	
	public void crearDocumentoListaApuntados(ArrayList<UsuarioDto> ocupados, CursilloDto cursillo, String nombre) throws IOException {
		DateTime ahora = new DateTime();
		nombreArchivo = nombre+" " + ahora.toString("dd-MM-yyyy HH-mm-ss.SSS")
				+ ".txt";

		File archivo = new File(nombreArchivo);
		BufferedWriter bw;
		bw = new BufferedWriter(new FileWriter(archivo));
		for (int i = 0; i < ocupados.size(); i++) {

			String mensaje1 = ocupados.get(i).nombre + " con id: " + ocupados.get(i).id_usuario + " estaba apuntado al cursillo:";
			String mensaje2 = "[" + cursillo.id_cursillo + "-" + cursillo.nombre + "]";
			bw.write(mensaje1);
			bw.newLine();
			bw.write(mensaje2);
			bw.newLine();
			bw.newLine();
		}

		bw.close();
	}

}
