package igu;

import java.awt.BorderLayout;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.HeadlessException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import com.toedter.calendar.JCalendar;

import logic.dto.ActividadDto;
import logic.reserva_actividad.Reservas;

import javax.swing.JList;
import javax.swing.JScrollPane;

public class ReservasIU extends JDialog {

	Reservas reserva;
	private static final long serialVersionUID = 403333508228349888L;
	private final JPanel contentPanel = new JPanel();
	private JLabel etiquetaReserva;
	private JLabel etiquetaListaDeActividades;
	private JLabel etiquetaIdSocio;
	private JButton botonCancelar;
	private JButton botonAsignar;
	private JButton botonMostrarActividades;
	private JTextField txtIdSocio;
	private JCalendar calendar;
	private JList<ActividadDto> list;
	private DefaultListModel<ActividadDto> model;
	private JScrollPane scrollPane;

	public ReservasIU() {

		try {
			reserva = new Reservas();
		} catch (IOException | SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error al abrir la base de datos", "Error en la base de datos",
					JOptionPane.ERROR_MESSAGE);
			dispose();
			return;
		}

		setResizable(false);
		setModal(true);
		setBounds(100, 100, 495, 575);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBackground(Color.WHITE);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		contentPanel.add(getBotonAsignar());
		contentPanel.add(getBotonMostrarActividades());
		contentPanel.add(getBotonCancelar());

		contentPanel.add(getEtiquetaReserva());
		contentPanel.add(getEtiquetaListaDeActividades());
		contentPanel.add(getEtiquetaIdSocio());
		contentPanel.add(getCalendar());
		contentPanel.add(getTxtIdSocio());
		contentPanel.add(getScrollPane());
	}

	private JButton getBotonAsignar() {
		if (botonAsignar == null) {
			botonAsignar = new JButton("Asignar");
			botonAsignar.addActionListener(actionAsignar);
			botonAsignar.setBounds(279, 492, 89, 23);
		}
		return botonAsignar;
	}

	private ActionListener actionAsignar = new ActionListener() {
		public void actionPerformed(ActionEvent actionEvent) {
			// Pedir el id de usuario
			int idSocio = 0;
			ActividadDto actividad = list.getSelectedValue();
			try {
				idSocio = Integer.valueOf(txtIdSocio.getText());
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "El id de socio no tiene un formato correcto.", "No existe Socio",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			// Comprobar condiciones
			if (!reserva.dentroDelHorario(actividad)) {
				JOptionPane.showMessageDialog(null, "La actividad seleccionada no esta dentro del horario de reserva.",
						"Error al realizar la reserva", JOptionPane.ERROR_MESSAGE);
				return;
			}

			try {
				if (!reserva.hayPlazas(actividad)) {
					JOptionPane.showMessageDialog(null, "La actividad seleccionada no tiene plazas disponibles.",
							"Error al realizar la reserva", JOptionPane.ERROR_MESSAGE);
					return;
				}
			} catch (IOException | HeadlessException | SQLException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "La actividad seleccionada no tiene plazas disponibles.",
						"Error al realizar la reserva", JOptionPane.ERROR_MESSAGE);
				return;
			}

			try {
				if (!reserva.socioLibre(idSocio)) {
					JOptionPane.showMessageDialog(null, "Ya tiene una actividad en ese horario.",
							"Error al realizar la reserva", JOptionPane.ERROR_MESSAGE);
					return;

				}
			} catch (IOException | HeadlessException | SQLException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Ya tiene una actividad en ese horario.",
						"Error al realizar la reserva", JOptionPane.ERROR_MESSAGE);
				return;
			}

			// Realizar reserva
			try {
				reserva.realizarReserva(idSocio, actividad);
			} catch (IOException | SQLException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Error al crear la reserva.", "Error al realizar la reserva",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			JOptionPane.showMessageDialog(null, "Reserva realizada con exito.", "Reserva realizada",
					JOptionPane.INFORMATION_MESSAGE);
			dispose();

		}
	};

	private JButton getBotonMostrarActividades() {
		if (botonMostrarActividades == null) {
			botonMostrarActividades = new JButton("Mostrar actividades");
			botonMostrarActividades.setBounds(284, 136, 150, 25);
			botonMostrarActividades.addActionListener(actionAceptar);
		}

		return botonMostrarActividades;
	}

	private ActionListener actionAceptar = new ActionListener() {
		public void actionPerformed(ActionEvent actionEvent) {

			int day = calendar.getCalendar().get(Calendar.DAY_OF_MONTH);
			int month = calendar.getCalendar().get(Calendar.MONTH) + 1;
			int year = calendar.getCalendar().get(Calendar.YEAR);

			try {
				ArrayList<ActividadDto> actividades = reserva.getActicidades(day, month, year);
				model.removeAllElements();
				for (int i = 0; i < actividades.size(); i++) {
					model.addElement(actividades.get(i));
				}

			} catch (SQLException | IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "No hay actividades para el dia seleccionado", "Error en reservas",
						JOptionPane.ERROR_MESSAGE);
			}

		}
	};

	private JButton getBotonCancelar() {
		if (botonCancelar == null) {
			botonCancelar = new JButton("Cancelar");
			botonCancelar.addActionListener(actionCancelar);
			botonCancelar.setBounds(380, 492, 89, 23);
		}
		return botonCancelar;
	}

	private ActionListener actionCancelar = new ActionListener() {
		public void actionPerformed(ActionEvent actionEvent) {
			dispose();
		}
	};

	private JCalendar getCalendar() {
		if (calendar == null) {
			calendar = new JCalendar();
			calendar.setBounds(36, 76, 184, 155);
		}
		return calendar;
	}

	private JTextField getTxtIdSocio() {
		if (txtIdSocio == null) {
			txtIdSocio = new JTextField();
			txtIdSocio.setText("");
			txtIdSocio.setToolTipText("");
			txtIdSocio.setBounds(232, 430, 116, 22);
			txtIdSocio.setColumns(10);
		}
		return txtIdSocio;
	}

	private JLabel getEtiquetaReserva() {
		if (etiquetaReserva == null) {
			etiquetaReserva = new JLabel("Realizar reserva");
			etiquetaReserva.setFont(new Font("Tahoma", Font.BOLD, 24));
			etiquetaReserva.setHorizontalAlignment(SwingConstants.CENTER);
			etiquetaReserva.setBounds(0, 11, 434, 52);
		}
		return etiquetaReserva;
	}

	private JLabel getEtiquetaListaDeActividades() {
		if (etiquetaListaDeActividades == null) {
			etiquetaListaDeActividades = new JLabel("Lista de actividades");
			etiquetaListaDeActividades.setBounds(37, 257, 140, 14);
		}
		return etiquetaListaDeActividades;
	}

	private JLabel getEtiquetaIdSocio() {
		if (etiquetaIdSocio == null) {
			etiquetaIdSocio = new JLabel("Introduzca su ID de Socio");
			etiquetaIdSocio.setBounds(37, 430, 183, 14);
		}
		return etiquetaIdSocio;
	}

	private JList<ActividadDto> getList() {
		if (model == null) {
			model = new DefaultListModel<ActividadDto>();
		}
		if (list == null) {
			list = new JList<ActividadDto>(model);
			list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		}
		return list;
	}

	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane(getList());
			scrollPane.setBounds(36, 287, 412, 130);
		}
		return scrollPane;
	}
}
