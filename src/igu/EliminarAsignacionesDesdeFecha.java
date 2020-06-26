package igu;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.joda.time.DateTime;

import com.toedter.calendar.JCalendar;

import db.DatabaseLogic;
import logic.dto.ActividadDto;
import logic.dto.ActividadSinAsignarDto;

import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.JComboBox;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.awt.event.ActionEvent;

public class EliminarAsignacionesDesdeFecha extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JLabel lblEliminarAsignacionesHasta;
	private JLabel lblDeUnaFecha;
	private JComboBox cbActividad;
	private JCalendar jC;
	private JButton btnVolver;
	private JButton btnEliminar;
	private JLabel lblFecha;

	public EliminarAsignacionesDesdeFecha() {
		setBounds(100, 100, 518, 463);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBackground(Color.WHITE);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		contentPanel.add(getLblEliminarAsignacionesHasta());
		contentPanel.add(getLblDeUnaFecha());
		contentPanel.add(getCbActividad());
		contentPanel.add(getJCalendar());
		contentPanel.add(getBtnVolver());
		contentPanel.add(getBtnEliminar());
		contentPanel.add(getLblFecha());
	}

	private JLabel getLblEliminarAsignacionesHasta() {
		if (lblEliminarAsignacionesHasta == null) {
			lblEliminarAsignacionesHasta = new JLabel("Eliminar Asignaciones a partir");
			lblEliminarAsignacionesHasta.setFont(new Font("Tahoma", Font.BOLD, 26));
			lblEliminarAsignacionesHasta.setHorizontalAlignment(SwingConstants.CENTER);
			lblEliminarAsignacionesHasta.setBounds(0, 0, 502, 124);
		}
		return lblEliminarAsignacionesHasta;
	}

	private JLabel getLblDeUnaFecha() {
		if (lblDeUnaFecha == null) {
			lblDeUnaFecha = new JLabel("de una fecha");
			lblDeUnaFecha.setFont(new Font("Tahoma", Font.BOLD, 26));
			lblDeUnaFecha.setHorizontalAlignment(SwingConstants.CENTER);
			lblDeUnaFecha.setBounds(0, 59, 502, 90);
		}
		return lblDeUnaFecha;
	}

	private JComboBox getCbActividad() {
		if (cbActividad == null) {
			cbActividad = new JComboBox();
			cbActividad.setBounds(10, 160, 482, 36);
			cbActividad.setModel(cargarModeloActividades());
		}
		return cbActividad;
	}

	private JCalendar getJCalendar() {
		if (jC == null) {
			jC = new JCalendar();
			jC.setBounds(40, 250, 180, 150);
		}
		return jC;
	}

	private JButton getBtnVolver() {
		if (btnVolver == null) {
			btnVolver = new JButton("Volver");
			btnVolver.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					dispose();
				}
			});
			btnVolver.setBounds(403, 390, 89, 23);
		}
		return btnVolver;
	}

	private JButton getBtnEliminar() {
		if (btnEliminar == null) {
			btnEliminar = new JButton("Eliminar");
			btnEliminar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					DateTime fecha = new DateTime(getJCalendar().getDate());
					try {
						List<Integer> socios = DatabaseLogic.getSociosActividadesDesdeFecha(
								((ActividadSinAsignarDto) getCbActividad().getSelectedItem()).id_actividad, fecha);
						if (socios.size() != 0) {
							BufferedWriter bw = new BufferedWriter(new FileWriter("SociosAfectados.txt"));
							bw.write("Socios Afectados\n");
							for (int s : socios) {
								bw.write("ID socio: " + s + "\n");
							}
							bw.close();
							JOptionPane.showMessageDialog(null, "Socios afectados apuntados", "",
									JOptionPane.DEFAULT_OPTION);
						}
						List<Integer> ids_asignacion = DatabaseLogic.getAsignacionesDesdeFecha(
								((ActividadSinAsignarDto) getCbActividad().getSelectedItem()).id_actividad, fecha);
						for (int id : ids_asignacion) {
							DatabaseLogic.eliminarReservasDeAsignacion(id);
						}
						DatabaseLogic.eliminarAsignacionesDesdeFecha(
								((ActividadSinAsignarDto) getCbActividad().getSelectedItem()).id_actividad, fecha);
						JOptionPane.showMessageDialog(null, "Actividades eliminadas", "", JOptionPane.DEFAULT_OPTION);

					} catch (SQLException e1) {
						JOptionPane.showMessageDialog(null, "Error en la conexion", "Error conexion",
								JOptionPane.ERROR_MESSAGE);
						e1.printStackTrace();
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(null, "Error en la configuracion", "Error configuracion",
								JOptionPane.ERROR_MESSAGE);
					} catch (NullPointerException e1) {
						JOptionPane.showMessageDialog(null, "Actividad no seleccionada", "Actividad no seleccionada",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			});
			btnEliminar.setBounds(299, 390, 89, 23);
		}
		return btnEliminar;
	}

	private JLabel getLblFecha() {
		if (lblFecha == null) {
			lblFecha = new JLabel("Fecha: ");
			lblFecha.setBounds(40, 225, 46, 14);
		}
		return lblFecha;
	}

	private DefaultComboBoxModel cargarModeloActividades() {
		DefaultComboBoxModel modelo = new DefaultComboBoxModel();
		try {
			for (ActividadSinAsignarDto a : DatabaseLogic.getActividadesBase()) {
				modelo.addElement(a);
			}
		} catch (SQLException e2) {
			JOptionPane.showMessageDialog(null, "Error en la conexion", "Error conexion", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e2) {
			JOptionPane.showMessageDialog(null, "Error en la configuracion", "Error configuracion",
					JOptionPane.ERROR_MESSAGE);
		}
		return modelo;
	}
}
