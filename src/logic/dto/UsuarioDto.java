package logic.dto;

public class UsuarioDto {

	public int id_usuario;
	public String nombre;
	public String tipo;
	public String dni;
	public String telefono;

	public String toString() {
		switch (tipo) {
		case "SOCIO":
			return toStringSocio();
		case "NOSOCIO":
			return toStringUsuario();
		default:
			return null;
		}
	}

	private String toStringSocio() {
		return "Nombre: " + nombre;
	}

	private String toStringUsuario() {
		return "Nombre: " + nombre + ", DNI: " + dni + ", TLF: " + telefono; 
	}
}
