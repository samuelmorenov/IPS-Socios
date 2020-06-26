package igu;

import java.awt.BorderLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import db.DatabaseLogic;
import logic.dto.ActividadDto;
import logic.dto.InstalacionDto;
import logic.dto.UsuarioDto;
import logic.reserva_actividad.Reservas;

import java.awt.Color;
import javax.swing.JLabel;
import java.awt.Font;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JOptionPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class InfoActividad extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JLabel lblNombre;
	private JLabel lblFechaInicio;
	private JLabel lblFechaFin;
	private JLabel lblSociosApuntados;
	private JLabel lblInstalacion;
	private JLabel lblIntensidad;
	private JLabel lblInformacionDeLa;
	private ActividadDto actividad;
	private InstalacionDto instalacion;
	private JTextField txtNombre;
	private JTextField txtFechaInicio;
	private JTextField txtFechaFin;
	private JTextField txtInstalacion;
	private JTextField txtIntensidad;
	private JScrollPane scrollPane;
	private JPanel panel;
	private JList list;
	private JLabel lblSinLimite;
	private DefaultListModel modeloSocios;
	private JButton btnVolver;
	private JButton btnRegistrarAsistencia;
	private JButton btnApuntarSocio;

	public InfoActividad(ActividadDto actividad) {
		this.actividad = actividad;
		try {
			instalacion = DatabaseLogic.getInstalacionDeActividad(actividad);
		} catch (SQLException e1) {
			JOptionPane.showMessageDialog(null, "Error en la conexion", "Error conexion", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(null, "Error en la configuracion", "Error configuracion",
					JOptionPane.ERROR_MESSAGE);
		}
		modeloSocios = new DefaultListModel();
		setResizable(false);
		setModal(true);
		setBounds(100, 100, 625, 456);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBackground(Color.WHITE);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		contentPanel.add(getLblNombre());
		contentPanel.add(getLblFechaInicio());
		contentPanel.add(getLblFechaFin());
		contentPanel.add(getLblSociosApuntados());
		contentPanel.add(getLblInstalacion());
		contentPanel.add(getLblIntensidad());
		contentPanel.add(getLblInformacionDeLa());
		contentPanel.add(getTxtNombre());
		contentPanel.add(getTxtFechaInicio());
		contentPanel.add(getTxtFechaFin());
		contentPanel.add(getTxtInstalacion());
		contentPanel.add(getTxtIntensidad());
		contentPanel.add(getScrollPane());
		contentPanel.add(getLblSinLimite());
		contentPanel.add(getBtnVolver());
		contentPanel.add(getBtnRegistrarAsistencia());
		contentPanel.add(getBtnApuntarSocio());
		setInfo();
	}

	private JLabel getLblNombre() {
		if (lblNombre == null) {
			lblNombre = new JLabel("Nombre: ");
			lblNombre.setBounds(20, 134, 67, 17);
		}
		return lblNombre;
	}

	private JLabel getLblFechaInicio() {
		if (lblFechaInicio == null) {
			lblFechaInicio = new JLabel("Fecha inicio:");
			lblFechaInicio.setBounds(20, 173, 102, 21);
		}
		return lblFechaInicio;
	}

	private JLabel getLblFechaFin() {
		if (lblFechaFin == null) {
			lblFechaFin = new JLabel("Fecha fin:");
			lblFechaFin.setBounds(20, 212, 67, 21);
		}
		return lblFechaFin;
	}

	private JLabel getLblSociosApuntados() {
		if (lblSociosApuntados == null) {
			lblSociosApuntados = new JLabel("Socios apuntados: ");
			lblSociosApuntados.setBounds(318, 74, 111, 21);
		}
		return lblSociosApuntados;
	}

	private JLabel getLblInstalacion() {
		if (lblInstalacion == null) {
			lblInstalacion = new JLabel("Instalacion:");
			lblInstalacion.setBounds(20, 252, 102, 21);
		}
		return lblInstalacion;
	}

	private JLabel getLblIntensidad() {
		if (lblIntensidad == null) {
			lblIntensidad = new JLabel("Intensidad: ");
			lblIntensidad.setBounds(20, 294, 102, 18);
		}
		return lblIntensidad;
	}

	private JLabel getLblInformacionDeLa() {
		if (lblInformacionDeLa == null) {
			lblInformacionDeLa = new JLabel("Informacion de la Actividad");
			lblInformacionDeLa.setHorizontalAlignment(SwingConstants.CENTER);
			lblInformacionDeLa.setFont(new Font("Tahoma", Font.BOLD, 24));
			lblInformacionDeLa.setBounds(10, 11, 599, 71);
		}
		return lblInformacionDeLa;
	}

	private JTextField getTxtNombre() {
		if (txtNombre == null) {
			txtNombre = new JTextField();
			txtNombre.setEditable(false);
			txtNombre.setBounds(125, 132, 183, 20);
			txtNombre.setColumns(10);
		}
		return txtNombre;
	}

	private JTextField getTxtFechaInicio() {
		if (txtFechaInicio == null) {
			txtFechaInicio = new JTextField();
			txtFechaInicio.setEditable(false);
			txtFechaInicio.setBounds(125, 173, 183, 21);
			txtFechaInicio.setColumns(10);
		}
		return txtFechaInicio;
	}

	private JTextField getTxtFechaFin() {
		if (txtFechaFin == null) {
			txtFechaFin = new JTextField();
			txtFechaFin.setEditable(false);
			txtFechaFin.setBounds(125, 211, 183, 22);
			txtFechaFin.setColumns(10);
		}
		return txtFechaFin;
	}

	private JTextField getTxtInstalacion() {
		if (txtInstalacion == null) {
			txtInstalacion = new JTextField();
			txtInstalacion.setEditable(false);
			txtInstalacion.setBounds(125, 252, 183, 20);
			txtInstalacion.setColumns(10);
		}
		return txtInstalacion;
	}

	private JTextField getTxtIntensidad() {
		if (txtIntensidad == null) {
			txtIntensidad = new JTextField();
			txtIntensidad.setEditable(false);
			txtIntensidad.setBounds(125, 293, 183, 21);
			txtIntensidad.setColumns(10);
		}
		return txtIntensidad;
	}

	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setBounds(318, 106, 291, 278);
			scrollPane.setViewportView(getPanel());
		}
		return scrollPane;
	}

	private JPanel getPanel() {
		if (panel == null) {
			panel = new JPanel();
			panel.setLayout(new BorderLayout(0, 0));
			panel.add(getList());
		}
		return panel;
	}

	private JList getList() {
		if (list == null) {
			list = new JList();
			list.setModel(modeloSocios);
		}
		return list;
	}

	private JLabel getLblSinLimite() {
		if (lblSinLimite == null) {
			lblSinLimite = new JLabel("");
			lblSinLimite.setBounds(454, 74, 155, 18);
		}
		return lblSinLimite;
	}

	private void setInfo() {
		getTxtNombre().setText(actividad.nombre);
		getTxtFechaInicio().setText(actividad.fecha_inicio.toString());
		getTxtFechaFin().setText(actividad.fecha_fin.toString());
		getTxtInstalacion().setText(instalacion.nombre);
		getTxtIntensidad().setText(actividad.intensidad);
		if (instalacion.plazas == 0) {
			getLblSinLimite().setText("No hay limite de plazas");
		} else {
			try {
				for (UsuarioDto socio : DatabaseLogic.getSociosApuntados(actividad)) {
					if (!modeloSocios.contains(socio))
						modeloSocios.addElement(socio);
				}
			} catch (SQLException e1) {
				JOptionPane.showMessageDialog(null, "Error en la conexion", "Error conexion",
						JOptionPane.ERROR_MESSAGE);
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(null, "Error en la configuracion", "Error configuracion",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private JButton getBtnVolver() {
		if (btnVolver == null) {
			btnVolver = new JButton("Volver");
			btnVolver.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
			btnVolver.setBounds(520, 395, 89, 23);
		}
		return btnVolver;
	}

	private JButton getBtnRegistrarAsistencia() {
		if (btnRegistrarAsistencia == null) {
			btnRegistrarAsistencia = new JButton("Registrar Asistencia");
			btnRegistrarAsistencia.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					try {
						DatabaseLogic.registrarAsistencia(actividad, (UsuarioDto) getList().getSelectedValue());
						JOptionPane.showMessageDialog(null, "Asistencia guardada", "", JOptionPane.DEFAULT_OPTION);
					} catch (SQLException e1) {
						JOptionPane.showMessageDialog(null, "Error en la conexion", "Error conexion",
								JOptionPane.ERROR_MESSAGE);
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(null, "Error en la configuracion", "Error configuracion",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			});
			btnRegistrarAsistencia.setBounds(328, 395, 182, 23);
		}
		return btnRegistrarAsistencia;
	}

	private JButton getBtnApuntarSocio() {
		if (btnApuntarSocio == null) {
			btnApuntarSocio = new JButton("Apuntar Socio");
			btnApuntarSocio.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					try {
						Reservas reserva = new Reservas();
						int id_socio = Integer.parseInt(JOptionPane.showInputDialog(null, "Introduce el id del socio"));
						reserva.realizarReserva(id_socio, actividad);
						int numeroSocios = modeloSocios.getSize();
						modeloSocios.removeAllElements();
						setInfo();
						if (numeroSocios < modeloSocios.getSize()) {
							JOptionPane.showMessageDialog(null, "Socio apuntado", "Socio Apuntado",
									JOptionPane.DEFAULT_OPTION);
						} else {
							JOptionPane.showMessageDialog(null, "El id no es valido", "",
									JOptionPane.ERROR_MESSAGE);
						}
					} catch (SQLException e1) {
						JOptionPane.showMessageDialog(null, "Error en la conexion", "Error conexion",
								JOptionPane.ERROR_MESSAGE);
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(null, "Error en la configuracion", "Error configuracion",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			});
			btnApuntarSocio.setBounds(191, 395, 117, 23);
		}
		return btnApuntarSocio;
	}
}
