package logic.dto;

public class InstalacionDto {

	public int id_instalacion;
	public String nombre;
	public int plazas;
	public int id_recurso;
	public int cantidad_recurso;
	public double precio_hora;
	@Override
	public String toString() {
		return "Nombre= " + nombre + ", plazas= " + plazas + ", cantidad recursos= " + cantidad_recurso
				+ ", precio hora =" + precio_hora;
	}
	
	
}
