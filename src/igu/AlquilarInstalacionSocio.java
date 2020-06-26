package igu;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.joda.time.DateTime;

import com.toedter.calendar.JCalendar;

import db.DatabaseLogic;
import logic.dto.AlquilerDto;
import logic.dto.InstalacionDto;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import javax.swing.JTextField;
import java.awt.Color;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class AlquilarInstalacionSocio extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JLabel lblAlquilarInstalacion;
	private JLabel lblIdSocio;
	private JTextField txtIdSocio;
	private JLabel lblInstalacion;
	private JComboBox cbInstalaciones;
	private JButton btnVolver;
	private JButton btnAlquilar;
	private JCalendar jC;
	private JLabel lblFecha;
	private JComboBox cbHoraInicio;
	private JComboBox cbDuracion;
	private JLabel lblPrecio;
	private JTextField txtPrecio;
	private ButtonGroup metodoPago = new ButtonGroup();
	private JLabel lblInicio;
	private JLabel lblDuracion;

	public AlquilarInstalacionSocio() {
		setModal(true);
		setBounds(100, 100, 450, 418);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBackground(Color.WHITE);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		contentPanel.add(getLblAlquilarInstalacion());
		contentPanel.add(getLblIdSocio());
		contentPanel.add(getTxtIdSocio());
		contentPanel.add(getLblInstalacion());
		contentPanel.add(getCbInstalaciones());
		contentPanel.add(getBtnVolver());
		contentPanel.add(getBtnAlquilar());
		contentPanel.add(getJCalendar());
		contentPanel.add(getLblFecha());
		contentPanel.add(getCbHoraInicio());
		contentPanel.add(getCbDuracion());
		contentPanel.add(getLblPrecio());
		contentPanel.add(getTxtPrecio());
		contentPanel.add(getLblInicio());
		contentPanel.add(getLblDuracion());
		calcularPrecio();
	}

	private JLabel getLblAlquilarInstalacion() {
		if (lblAlquilarInstalacion == null) {
			lblAlquilarInstalacion = new JLabel("Alquilar Instalacion");
			lblAlquilarInstalacion.setFont(new Font("Tahoma", Font.BOLD, 25));
			lblAlquilarInstalacion.setHorizontalAlignment(SwingConstants.CENTER);
			lblAlquilarInstalacion.setBounds(10, 11, 414, 36);
		}
		return lblAlquilarInstalacion;
	}

	private JLabel getLblIdSocio() {
		if (lblIdSocio == null) {
			lblIdSocio = new JLabel("Id socio:");
			lblIdSocio.setHorizontalAlignment(SwingConstants.CENTER);
			lblIdSocio.setBounds(20, 58, 92, 22);
		}
		return lblIdSocio;
	}

	private JTextField getTxtIdSocio() {
		if (txtIdSocio == null) {
			txtIdSocio = new JTextField();
			txtIdSocio.setBounds(103, 58, 86, 20);
			txtIdSocio.setColumns(10);
		}
		return txtIdSocio;
	}

	private JLabel getLblInstalacion() {
		if (lblInstalacion == null) {
			lblInstalacion = new JLabel("Instalacion:");
			lblInstalacion.setBounds(30, 91, 67, 14);
		}
		return lblInstalacion;
	}

	private JComboBox getCbInstalaciones() {
		if (cbInstalaciones == null) {
			cbInstalaciones = new JComboBox();
			cbInstalaciones.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					calcularPrecio();
				}
			});
			cbInstalaciones.setBounds(103, 89, 321, 22);
			cbInstalaciones.setModel(getModelInstalaciones());
		}
		return cbInstalaciones;
	}

	private JButton getBtnVolver() {
		if (btnVolver == null) {
			btnVolver = new JButton("Volver");
			btnVolver.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
			btnVolver.setBounds(335, 345, 89, 23);
		}
		return btnVolver;
	}

	private JButton getBtnAlquilar() {
		if (btnAlquilar == null) {
			btnAlquilar = new JButton("Alquilar");
			btnAlquilar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					DateTime fechaInicio = new DateTime(getJCalendar().getDate());
					fechaInicio = fechaInicio.minusHours(fechaInicio.getHourOfDay());
					fechaInicio = fechaInicio.minusMinutes(fechaInicio.getMinuteOfDay());
					fechaInicio = fechaInicio.minusSeconds(fechaInicio.getSecondOfDay());
					fechaInicio = fechaInicio.plusHours(getCbHoraInicio().getSelectedIndex());
					DateTime fechaFinal;
					if (getCbDuracion().getSelectedIndex() == 0) {
						fechaFinal = fechaInicio.plusHours(1);
					} else {
						fechaFinal = fechaInicio.plusHours(2);
					}
					if (new DateTime(new Date()).plusDays(15).compareTo(fechaInicio) == -1) {
						JOptionPane.showMessageDialog(null, "Solo se puede reservar con 15 dias de antelacion",
								"Reserva anticipada", JOptionPane.ERROR_MESSAGE);
						return;
					}
					try {
						if (DatabaseLogic.comprobarInstalacionLibre(fechaInicio, fechaFinal,
								((InstalacionDto) getCbInstalaciones().getSelectedItem()).id_instalacion)) {
							AlquilerDto alquiler = new AlquilerDto();
							alquiler.id_socio = Integer.parseInt(getTxtIdSocio().getText());
							alquiler.id_instalacion = ((InstalacionDto) getCbInstalaciones()
									.getSelectedItem()).id_instalacion;
							alquiler.fecha_inicio = fechaInicio;
							alquiler.fecha_fin = fechaFinal;
							alquiler.hora_entrada = fechaInicio;
							alquiler.hora_salida = fechaFinal;
							if (DatabaseLogic.comprobarSocioSinAlquiler(fechaInicio, fechaFinal, alquiler.id_socio)) {
								DatabaseLogic.alquilarInstalacion(alquiler);
								JOptionPane.showMessageDialog(null, "Instalacion Alquilada");
								dispose();
							} else {
								JOptionPane.showMessageDialog(null, "El socio ya tiene un alquiler en ese tramo horario", "Socio Ocupado",
										JOptionPane.ERROR_MESSAGE);
							}
						} else {
							JOptionPane.showMessageDialog(null, "Instalacion Ocupada", "Instalacion Ocupada",
									JOptionPane.ERROR_MESSAGE);
						}
					} catch (SQLException e2) {
						JOptionPane.showMessageDialog(null, "Error en la conexion", "Error conexion",
								JOptionPane.ERROR_MESSAGE);
						e2.printStackTrace();
					} catch (IOException e2) {
						JOptionPane.showMessageDialog(null, "Error en la configuracion", "Error configuracion",
								JOptionPane.ERROR_MESSAGE);
					} catch (NumberFormatException e2) {
						JOptionPane.showMessageDialog(null, "ID de socio incorrecto", "ID incorrecto",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			});
			btnAlquilar.setBounds(236, 345, 89, 23);
		}
		return btnAlquilar;
	}

	private JCalendar getJCalendar() {
		if (jC == null) {
			jC = new JCalendar();
			jC.setBounds(25, 160, 180, 150);
		}
		return jC;
	}

	private JLabel getLblFecha() {
		if (lblFecha == null) {
			lblFecha = new JLabel("Fecha:");
			lblFecha.setBounds(35, 135, 46, 14);
		}
		return lblFecha;
	}

	private JComboBox getCbHoraInicio() {
		if (cbHoraInicio == null) {
			cbHoraInicio = new JComboBox();
			cbHoraInicio.setBounds(236, 160, 46, 22);
			cbHoraInicio.setModel(getModelHoras());
		}
		return cbHoraInicio;
	}

	private JComboBox getCbDuracion() {
		if (cbDuracion == null) {
			cbDuracion = new JComboBox();
			cbDuracion.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					calcularPrecio();
				}
			});
			cbDuracion.setModel(new DefaultComboBoxModel(new String[] { "1 Hora", "2 Horas" }));
			cbDuracion.setBounds(297, 162, 92, 20);
		}
		return cbDuracion;
	}

	private DefaultComboBoxModel<String> getModelHoras() {
		String[] horas = new String[25];
		for (int i = 0; i <= 24; i++) {
			horas[i] = String.valueOf(i);
		}
		return new DefaultComboBoxModel<>(horas);
	}

	private DefaultComboBoxModel<String> getModelInstalaciones() {
		DefaultComboBoxModel modelo = new DefaultComboBoxModel();
		try {
			for (InstalacionDto i : DatabaseLogic.getInstalaciones()) {
				modelo.addElement(i);
			}
		} catch (SQLException e2) {
			JOptionPane.showMessageDialog(null, "Error en la conexion", "Error conexion", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e2) {
			JOptionPane.showMessageDialog(null, "Error en la configuracion", "Error configuracion",
					JOptionPane.ERROR_MESSAGE);
		}
		return modelo;
	}

	private JLabel getLblPrecio() {
		if (lblPrecio == null) {
			lblPrecio = new JLabel("Precio: ");
			lblPrecio.setBounds(264, 243, 46, 14);
		}
		return lblPrecio;
	}

	private JTextField getTxtPrecio() {
		if (txtPrecio == null) {
			txtPrecio = new JTextField();
			txtPrecio.setEditable(false);
			txtPrecio.setHorizontalAlignment(SwingConstants.CENTER);
			txtPrecio.setBounds(322, 240, 86, 20);
			txtPrecio.setColumns(10);
		}
		return txtPrecio;
	}

	private void calcularPrecio() {
		double precio_hora = ((InstalacionDto) getCbInstalaciones().getSelectedItem()).precio_hora;
		if (cbDuracion.getSelectedIndex() == 0) {
			getTxtPrecio().setText(String.valueOf(precio_hora));
		} else {
			getTxtPrecio().setText(String.valueOf(precio_hora * 2));
		}
	}

	private JLabel getLblInicio() {
		if (lblInicio == null) {
			lblInicio = new JLabel("Inicio");
			lblInicio.setBounds(236, 135, 46, 14);
		}
		return lblInicio;
	}

	private JLabel getLblDuracion() {
		if (lblDuracion == null) {
			lblDuracion = new JLabel("Duracion");
			lblDuracion.setBounds(297, 135, 46, 14);
		}
		return lblDuracion;
	}
}
