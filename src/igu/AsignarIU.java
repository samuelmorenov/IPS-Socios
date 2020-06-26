
package igu;

import java.awt.Color;
import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.awt.event.ActionEvent;

import javax.swing.AbstractButton;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.joda.time.DateTime;

import logic.asignar_instalacion.Asignar;
import logic.comboBox_data.ComboBoxData;
import logic.dto.ActividadSinAsignarDto;
import logic.dto.InstalacionDto;
import logic.dto.MonitorDto;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import com.toedter.calendar.JCalendar;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;

public class AsignarIU extends JDialog {
	private Asignar asignar;

	private static final long serialVersionUID = 403333508228349888L;
	private final JPanel contentPanel = new JPanel();

	private JButton botonCancelar;
	private JButton botonReservar;
	private JLabel etiquetaActividad;
	private JComboBox<InstalacionDto> boxInstalacion;
	private JComboBox<ActividadSinAsignarDto> boxActividad;
	private JComboBox<MonitorDto> boxMonitor;

	private JCalendar calendar;
	private JLabel etiquetaAsignarInstalacion;

	private JLabel etiquetaInstalacion;

	private JLabel etiquetaDia;
	private JLabel etiquetaHorario;
	private JComboBox<String> boxHoraInicio;
	private JComboBox<String> boxHoraFin;
	private JLabel etiquetaMonitor;
	private JCheckBox checkboxTodoElDia;

	public AsignarIU() {
		try {
			asignar = new Asignar();
		} catch (IOException | SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error al abrir la base de datos", "Error en asignacion",
					JOptionPane.ERROR_MESSAGE);
			dispose();
			return;
		}
		setResizable(false);
		setModal(true);
		setBounds(100, 100, 488, 513);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBackground(Color.WHITE);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		contentPanel.add(getBotonReservar());
		contentPanel.add(getBotonCancelar());
		contentPanel.add(getEtiquetaAsignarInstalacion());
		contentPanel.add(getEtiquetaActividad());
		contentPanel.add(getEtiquetaInstalacion());
		contentPanel.add(getBoxInstalacion());
		contentPanel.add(getBoxActividad());
		contentPanel.add(getCalendar());
		contentPanel.add(getEtiquetaDia());
		contentPanel.add(getEtiquetaHorario());
		contentPanel.add(getBoxHoraInicio());
		contentPanel.add(getBoxHoraFin());
		contentPanel.add(getEtiquetaMonitor());
		contentPanel.add(getBoxMonitor());
		contentPanel.add(getCheckboxTodoElDia());

	}

	private ActionListener actionReservar = new ActionListener() {
		public void actionPerformed(ActionEvent actionEvent) {
			int day = calendar.getCalendar().get(Calendar.DAY_OF_MONTH);
			int month = calendar.getCalendar().get(Calendar.MONTH) + 1;
			int year = calendar.getCalendar().get(Calendar.YEAR);
			DateTime fecha = new DateTime(year, month, day, 0, 0);

			int horaInicio;
			int horaFin;
			if (checkboxTodoElDia.isSelected()) {
				horaInicio = 0;
				horaFin = 23;
			} else {
				horaInicio = Integer.parseInt((String) boxHoraInicio.getSelectedItem());
				horaFin = Integer.parseInt((String) boxHoraFin.getSelectedItem());
				if (horaInicio >= horaFin) {
					JOptionPane.showMessageDialog(null,
							"Error: El horario seleccionado es incorrecto.\nLa hora de finalizacion debe ser despues de la de inicio",
							"Error en asignacion", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}

			ActividadSinAsignarDto actividadSeleccionada = (ActividadSinAsignarDto) boxActividad.getSelectedItem();
			InstalacionDto instalacionSeleccionada = (InstalacionDto) boxInstalacion.getSelectedItem();
			MonitorDto monitorSeleccionado = (MonitorDto) boxMonitor.getSelectedItem();

			// Comprobar que la fecha es correcta
			if (fecha.isBeforeNow()) {
				JOptionPane.showMessageDialog(null, "Fecha seleccionada incorrecta. Ese dia ya ha pasado.",
						"Error en asignacion", JOptionPane.ERROR_MESSAGE);
				return;
			}

			try {
				asignar.asignar(actividadSeleccionada, instalacionSeleccionada, monitorSeleccionado, fecha, horaInicio,
						horaFin);
				JOptionPane.showMessageDialog(null, "Reserva realizada con exito.", "Reserva realizada",
						JOptionPane.INFORMATION_MESSAGE);
				dispose();
			} catch (SQLException | IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Error al insertar en la base de datos", "Error en asignacion",
						JOptionPane.ERROR_MESSAGE);

			}

		}
	};

	private ActionListener actionCancelar = new ActionListener() {
		public void actionPerformed(ActionEvent actionEvent) {
			dispose();
		}
	};
	private ActionListener actionCheckBox = new ActionListener() {
		public void actionPerformed(ActionEvent actionEvent) {
			AbstractButton abstractButton = (AbstractButton) actionEvent.getSource();
			boolean selected = abstractButton.getModel().isSelected();
			if (!selected) {
				boxHoraInicio.setEnabled(true);
				boxHoraFin.setEnabled(true);
			} else {
				boxHoraInicio.setEnabled(false);
				boxHoraFin.setEnabled(false);
			}
		}
	};

	private JButton getBotonReservar() {
		if (botonReservar == null) {
			botonReservar = new JButton("Reservar");
			botonReservar.addActionListener(actionReservar);
			botonReservar.setBounds(280, 442, 89, 23);
		}
		return botonReservar;
	}

	private JButton getBotonCancelar() {
		if (botonCancelar == null) {
			botonCancelar = new JButton("Cancelar");
			botonCancelar.addActionListener(actionCancelar);
			botonCancelar.setBounds(381, 442, 89, 23);
		}
		return botonCancelar;
	}

	private JComboBox<ActividadSinAsignarDto> getBoxActividad() {
		if (boxActividad == null) {
			boxActividad = new JComboBox<ActividadSinAsignarDto>();
			boxActividad.setBounds(22, 108, 150, 22);
			try {
				boxActividad
						.setModel(new DefaultComboBoxModel<ActividadSinAsignarDto>(asignar.getActicidadesSinAsignar()));
			} catch (SQLException | IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Error al obtener las actividades", "Error en asignacion",
						JOptionPane.ERROR_MESSAGE);
				dispose();
			}
		}
		return boxActividad;
	}

	private JComboBox<InstalacionDto> getBoxInstalacion() {
		if (boxInstalacion == null) {
			boxInstalacion = new JComboBox<InstalacionDto>();
			boxInstalacion.setBounds(236, 108, 234, 22);
			try {
				boxInstalacion.setModel(new DefaultComboBoxModel<InstalacionDto>(asignar.getInstalaciones()));
			} catch (SQLException | IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Error al obtener las instalaciones", "Error en asignacion",
						JOptionPane.ERROR_MESSAGE);
				dispose();
			}

		}
		return boxInstalacion;
	}

	private JComboBox<MonitorDto> getBoxMonitor() {
		if (boxMonitor == null) {
			boxMonitor = new JComboBox<MonitorDto>();
			boxMonitor.setBounds(236, 172, 234, 22);
			try {
				boxMonitor.setModel(new DefaultComboBoxModel<MonitorDto>(asignar.getMonitores()));
			} catch (SQLException | IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Error al obtener los monitores", "Error en asignacion",
						JOptionPane.ERROR_MESSAGE);
				dispose();
			}
		}
		return boxMonitor;
	}

	private JComboBox<String> getBoxHoraInicio() {
		if (boxHoraInicio == null) {
			boxHoraInicio = new JComboBox<String>();
			boxHoraInicio.setBounds(22, 369, 55, 22);
			boxHoraInicio.setModel(new DefaultComboBoxModel<String>(ComboBoxData.getHoras()));
		}
		return boxHoraInicio;
	}

	private JComboBox<String> getBoxHoraFin() {
		if (boxHoraFin == null) {
			boxHoraFin = new JComboBox<String>();
			boxHoraFin.setBounds(89, 369, 55, 22);
			boxHoraFin.setModel(new DefaultComboBoxModel<String>(ComboBoxData.getHoras()));
			boxHoraFin.setSelectedItem("23");
		}
		return boxHoraFin;
	}

	private JCheckBox getCheckboxTodoElDia() {
		if (checkboxTodoElDia == null) {
			checkboxTodoElDia = new JCheckBox("Todo el dia");
			checkboxTodoElDia.addActionListener(actionCheckBox);
			checkboxTodoElDia.setBounds(236, 368, 113, 25);
		}
		return checkboxTodoElDia;
	}

	private JCalendar getCalendar() {
		if (calendar == null) {
			calendar = new JCalendar();
			calendar.setBounds(22, 172, 184, 155);
		}
		return calendar;
	}

	private JLabel getEtiquetaAsignarInstalacion() {
		if (etiquetaAsignarInstalacion == null) {
			etiquetaAsignarInstalacion = new JLabel("Asignar Instalacion");
			etiquetaAsignarInstalacion.setHorizontalAlignment(SwingConstants.CENTER);
			etiquetaAsignarInstalacion.setFont(new Font("Tahoma", Font.BOLD, 24));
			etiquetaAsignarInstalacion.setBounds(22, 13, 340, 39);
		}
		return etiquetaAsignarInstalacion;
	}

	private JLabel getEtiquetaActividad() {
		if (etiquetaActividad == null) {
			etiquetaActividad = new JLabel("Actividad");
			etiquetaActividad.setBounds(22, 81, 150, 14);
		}
		return etiquetaActividad;
	}

	private JLabel getEtiquetaInstalacion() {
		if (etiquetaInstalacion == null) {
			etiquetaInstalacion = new JLabel("Instalacion");
			etiquetaInstalacion.setBounds(236, 80, 150, 16);
		}
		return etiquetaInstalacion;
	}

	private JLabel getEtiquetaDia() {
		if (etiquetaDia == null) {
			etiquetaDia = new JLabel("Dia de la actividad");
			etiquetaDia.setBounds(22, 143, 150, 16);
		}
		return etiquetaDia;
	}

	private JLabel getEtiquetaHorario() {
		if (etiquetaHorario == null) {
			etiquetaHorario = new JLabel("Horario");
			etiquetaHorario.setBounds(22, 340, 150, 16);
		}
		return etiquetaHorario;
	}

	private JLabel getEtiquetaMonitor() {
		if (etiquetaMonitor == null) {
			etiquetaMonitor = new JLabel("Monitor");
			etiquetaMonitor.setBounds(236, 143, 56, 16);
		}
		return etiquetaMonitor;
	}
}
