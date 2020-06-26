package igu;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import db.DatabaseLogic;
import logic.dto.AlquilerDto;
import logic.dto.InstalacionDto;

import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.awt.event.ActionEvent;

public class PagarAlquiler extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private ButtonGroup metodoPago = new ButtonGroup();
	private JButton btnVolver;
	private JButton btnPagar;
	private JLabel lblHoraEntrada;
	private JLabel lblHoraSalida;
	private JTextField txtHoraEntrada;
	private JTextField txtHoraSalida;
	private JRadioButton rdbtnPagarEnEfectivo;
	private JRadioButton rdbtnAadirACuota;
	private JComboBox cbAlquileres;
	private JLabel lblPagarAlquiler;

	public PagarAlquiler() {
		setModal(true);
		setBounds(100, 100, 450, 300);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBackground(Color.WHITE);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		contentPanel.add(getBtnVolver());
		contentPanel.add(getBtnPagar());
		contentPanel.add(getLblHoraEntrada());
		contentPanel.add(getLblHoraSalida());
		contentPanel.add(getTxtHoraEntrada());
		contentPanel.add(getTxtHoraSalida());
		contentPanel.add(getRdbtnPagarEnEfectivo());
		contentPanel.add(getRdbtnAadirACuota());
		contentPanel.add(getCbAlquileres());
		contentPanel.add(getLblPagarAlquiler());
		metodoPago.add(getRdbtnAadirACuota());
		metodoPago.add(getRdbtnPagarEnEfectivo());
	}

	private JButton getBtnVolver() {
		if (btnVolver == null) {
			btnVolver = new JButton("Volver");
			btnVolver.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
			btnVolver.setBounds(335, 227, 89, 23);
		}
		return btnVolver;
	}

	private JButton getBtnPagar() {
		if (btnPagar == null) {
			btnPagar = new JButton("Pagar");
			btnPagar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						if (getTxtHoraEntrada().getText().matches("\\d\\d:\\d\\d") && getTxtHoraEntrada().getText().matches("\\d\\d:\\d\\d")) {
							AlquilerDto alquiler = (AlquilerDto) getCbAlquileres().getSelectedItem();
							double precio = DatabaseLogic.getPrecioAlquiler(alquiler.id_alquiler);
							if (getRdbtnAadirACuota().isSelected()) {
								DatabaseLogic.crearPago(alquiler.id_socio, precio,
										"Alquiler instalacion " + alquiler.id_instalacion);
								JOptionPane.showMessageDialog(null, "Pago añadido a la cuota");
							} else {
								BufferedWriter bw = new BufferedWriter(new FileWriter(
										"Recibo" + alquiler.id_socio + alquiler.id_instalacion + ".txt"));
								bw.write("Recibo alquiler\n");
								bw.write("ID socio: " + alquiler.id_socio + "\n");
								bw.write("Instalacion: " + alquiler.id_instalacion + "\n");
								DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm");
								bw.write("Fecha: "+alquiler.fecha_inicio.toString(fmt));
								bw.write("Precio: " + precio);
								bw.close();
								JOptionPane.showMessageDialog(null, "Recibo Creado");
							}
							DateTime hora_entrada = new DateTime(new Date());
							hora_entrada = hora_entrada.minusHours(hora_entrada.getHourOfDay());
							hora_entrada = hora_entrada.minusMinutes(hora_entrada.getMinuteOfDay());
							hora_entrada = hora_entrada.minusSeconds(hora_entrada.getSecondOfDay());
							DateTime hora_salida = hora_entrada;
							hora_entrada = hora_entrada.plusHours(Integer.parseInt(getTxtHoraEntrada().getText().split(":")[0]));
							hora_entrada = hora_entrada.plusMinutes(Integer.parseInt(getTxtHoraEntrada().getText().split(":")[1]));
							hora_salida = hora_salida.plusHours(Integer.parseInt(getTxtHoraSalida().getText().split(":")[0]));
							hora_salida = hora_salida.plusMinutes(Integer.parseInt(getTxtHoraSalida().getText().split(":")[1]));
							DatabaseLogic.registrarEntradaSalidaAlquiler(alquiler.id_alquiler, hora_entrada, hora_salida);
							dispose();
						} else {
							JOptionPane.showMessageDialog(null, "La hora de entrada y salida tienen que tener formato hh:mm", "Error formato",
									JOptionPane.ERROR_MESSAGE);
						}
					} catch (SQLException e2) {
						JOptionPane.showMessageDialog(null, "Error en la conexion", "Error conexion",
								JOptionPane.ERROR_MESSAGE);
						e2.printStackTrace();
					} catch (IOException e2) {
						JOptionPane.showMessageDialog(null, "Error en la configuracion", "Error configuracion",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			});
			btnPagar.setBounds(238, 227, 89, 23);
		}
		return btnPagar;
	}

	private JLabel getLblHoraEntrada() {
		if (lblHoraEntrada == null) {
			lblHoraEntrada = new JLabel("Hora entrada: ");
			lblHoraEntrada.setBounds(238, 160, 89, 14);
		}
		return lblHoraEntrada;
	}

	private JLabel getLblHoraSalida() {
		if (lblHoraSalida == null) {
			lblHoraSalida = new JLabel("Hora salida: ");
			lblHoraSalida.setBounds(238, 185, 89, 14);
		}
		return lblHoraSalida;
	}

	private JTextField getTxtHoraEntrada() {
		if (txtHoraEntrada == null) {
			txtHoraEntrada = new JTextField();
			txtHoraEntrada.setHorizontalAlignment(SwingConstants.CENTER);
			txtHoraEntrada.setBounds(335, 157, 86, 20);
			txtHoraEntrada.setColumns(10);
		}
		return txtHoraEntrada;
	}

	private JTextField getTxtHoraSalida() {
		if (txtHoraSalida == null) {
			txtHoraSalida = new JTextField();
			txtHoraSalida.setHorizontalAlignment(SwingConstants.CENTER);
			txtHoraSalida.setBounds(335, 182, 86, 20);
			txtHoraSalida.setColumns(10);
		}
		return txtHoraSalida;
	}

	private JRadioButton getRdbtnPagarEnEfectivo() {
		if (rdbtnPagarEnEfectivo == null) {
			rdbtnPagarEnEfectivo = new JRadioButton("Pagar en efectivo");
			rdbtnPagarEnEfectivo.setBackground(Color.WHITE);
			rdbtnPagarEnEfectivo.setSelected(true);
			rdbtnPagarEnEfectivo.setBounds(10, 156, 137, 23);
		}
		return rdbtnPagarEnEfectivo;
	}

	private JRadioButton getRdbtnAadirACuota() {
		if (rdbtnAadirACuota == null) {
			rdbtnAadirACuota = new JRadioButton("A\u00F1adir a cuota");
			rdbtnAadirACuota.setBackground(Color.WHITE);
			rdbtnAadirACuota.setBounds(10, 181, 137, 23);
		}
		return rdbtnAadirACuota;
	}

	private JComboBox getCbAlquileres() {
		if (cbAlquileres == null) {
			cbAlquileres = new JComboBox();
			cbAlquileres.setBounds(10, 74, 414, 23);
			cbAlquileres.setModel(getModelAlquileres());
		}
		return cbAlquileres;
	}

	private JLabel getLblPagarAlquiler() {
		if (lblPagarAlquiler == null) {
			lblPagarAlquiler = new JLabel("Pagar Alquiler");
			lblPagarAlquiler.setFont(new Font("Tahoma", Font.BOLD, 26));
			lblPagarAlquiler.setHorizontalAlignment(SwingConstants.CENTER);
			lblPagarAlquiler.setBounds(10, 11, 414, 52);
		}
		return lblPagarAlquiler;
	}

	private DefaultComboBoxModel<String> getModelAlquileres() {
		DefaultComboBoxModel modelo = new DefaultComboBoxModel();
		try {
			for (AlquilerDto a : DatabaseLogic.getAlquileres()) {
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
