package igu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import org.joda.time.DateTime;

import com.toedter.calendar.JCalendar;

import db.DatabaseLogic;
import logic.asignar_instalacion.Asignar;
import logic.dto.ActividadSinAsignarDto;
import logic.dto.InstalacionDto;
import logic.dto.MonitorDto;
import logic.dto.RecursoDto;

public class CrearActividad extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JLabel lblCrearActividad;
	private JLabel lblNombre;
	private JTextField txtNombre;
	private JLabel lblIntensidad;
	private JComboBox cbIntensidad;
	private JButton btnCancelar;
	private JButton btnCrear;
	private JCheckBox chckbxPlazasLimitadas;
	private JCheckBox chckbxRepetirSemanalmente;
	private DefaultComboBoxModel modeloHoras;
	private JComboBox cbRecursos;
	private DefaultComboBoxModel modeloRecursos = new DefaultComboBoxModel<>();
	private JLabel lblRecursos;

	/**
	 * Create the dialog.
	 */
	public CrearActividad() {
		setResizable(false);
		setModal(true);
		setBounds(100, 100, 506, 263);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBackground(Color.WHITE);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		contentPanel.add(getLblCrearActividad());
		contentPanel.add(getLblNombre());
		contentPanel.add(getTxtNombre());
		contentPanel.add(getLblIntensidad());
		contentPanel.add(getCbIntensidad());
		contentPanel.add(getBtnCancelar());
		contentPanel.add(getBtnCrear());
		contentPanel.add(getChckbxPlazasLimitadas());
		contentPanel.add(getCbRecursos());
		contentPanel.add(getLblRecursos());
		cargarRecursos();
	}

	private JLabel getLblCrearActividad() {
		if (lblCrearActividad == null) {
			lblCrearActividad = new JLabel("Crear Actividad");
			lblCrearActividad.setHorizontalAlignment(SwingConstants.CENTER);
			lblCrearActividad.setFont(new Font("Tahoma", Font.BOLD, 20));
			lblCrearActividad.setBounds(10, 11, 472, 27);
		}
		return lblCrearActividad;
	}

	private JLabel getLblNombre() {
		if (lblNombre == null) {
			lblNombre = new JLabel("Nombre:");
			lblNombre.setBounds(35, 73, 77, 14);
		}
		return lblNombre;
	}

	private JTextField getTxtNombre() {
		if (txtNombre == null) {
			txtNombre = new JTextField();
			txtNombre.setBounds(122, 70, 162, 20);
			txtNombre.setColumns(10);
		}
		return txtNombre;
	}

	private JLabel getLblIntensidad() {
		if (lblIntensidad == null) {
			lblIntensidad = new JLabel("Intensidad:");
			lblIntensidad.setBounds(35, 108, 77, 14);
		}
		return lblIntensidad;
	}

	private JComboBox getCbIntensidad() {
		if (cbIntensidad == null) {
			cbIntensidad = new JComboBox();
			cbIntensidad.setModel(new DefaultComboBoxModel(new String[] { "Baja", "Media", "Alta" }));
			cbIntensidad.setBounds(122, 105, 86, 20);
		}
		return cbIntensidad;
	}

	private JButton getBtnCancelar() {
		if (btnCancelar == null) {
			btnCancelar = new JButton("Cancelar");
			btnCancelar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
			btnCancelar.setBounds(401, 200, 89, 23);
		}
		return btnCancelar;
	}

	private JButton getBtnCrear() {
		if (btnCrear == null) {
			btnCrear = new JButton("Crear");
			btnCrear.addActionListener(new ActionListener() {
				@SuppressWarnings("deprecation")
				public void actionPerformed(ActionEvent e) {
					if (getTxtNombre().getText().isEmpty()) {
						JOptionPane.showMessageDialog(null, "El nombre no puede estar vacio", "Nonbre vacio",
								JOptionPane.ERROR_MESSAGE);
					} else {
						ActividadSinAsignarDto actividad = new ActividadSinAsignarDto();
						actividad.nombre = getTxtNombre().getText();
						actividad.plazas_limitadas = getChckbxPlazasLimitadas().isSelected();
						actividad.intensidad = getCbIntensidad().getSelectedItem().toString();
						actividad.id_recurso = ((RecursoDto) getCbRecursos().getSelectedItem()).id_recurso;

						try {
							DatabaseLogic.crearActividad(actividad);
							actividad.id_actividad = DatabaseLogic.ultimaActividadCreada();
							JOptionPane.showMessageDialog(null, "Actividad creada", "", JOptionPane.DEFAULT_OPTION);
						} catch (SQLException e1) {
							JOptionPane.showMessageDialog(null, "Error en la conexion", "Error conexion",
									JOptionPane.ERROR_MESSAGE);
						} catch (IOException e1) {
							JOptionPane.showMessageDialog(null, "Error en la configuracion", "Error configuracion",
									JOptionPane.ERROR_MESSAGE);
						}
						dispose();
					}
				}
			});
			btnCrear.setBounds(302, 200, 89, 23);
		}
		return btnCrear;
	}

	private DefaultComboBoxModel<String> getModelHoras() {
		String[] horas = new String[25];
		for (int i = 0; i <= 24; i++) {
			horas[i] = String.valueOf(i);
		}
		return new DefaultComboBoxModel<>(horas);
	}

	private JCheckBox getChckbxPlazasLimitadas() {
		if (chckbxPlazasLimitadas == null) {
			chckbxPlazasLimitadas = new JCheckBox("Plazas Limitadas");
			chckbxPlazasLimitadas.setBackground(Color.WHITE);
			chckbxPlazasLimitadas.setHorizontalAlignment(SwingConstants.LEFT);
			chckbxPlazasLimitadas.setBounds(339, 69, 143, 23);
		}
		return chckbxPlazasLimitadas;
	}

	private JComboBox getCbRecursos() {
		if (cbRecursos == null) {
			cbRecursos = new JComboBox();
			cbRecursos.setBounds(122, 136, 181, 22);
			cbRecursos.setModel(modeloRecursos);
		}
		return cbRecursos;
	}

	private void cargarRecursos() {
		try {
			for (RecursoDto r : DatabaseLogic.getRecursos()) {
				modeloRecursos.addElement(r);
			}
		} catch (SQLException | IOException e) {
			JOptionPane.showMessageDialog(null, "Error en la conexion");
		}
	}

	private JLabel getLblRecursos() {
		if (lblRecursos == null) {
			lblRecursos = new JLabel("Recursos:");
			lblRecursos.setBounds(35, 140, 77, 14);
		}
		return lblRecursos;
	}
}
