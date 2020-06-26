package logic.dto;

public class ActividadSinAsignarDto {

	public int id_actividad;
	public String nombre;
	public String intensidad;
	public boolean plazas_limitadas;
	public int id_recurso;

	public String toString() {
		return id_actividad + " - " + nombre;
	}
}
