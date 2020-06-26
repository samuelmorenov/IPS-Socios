package igu;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.SwingConstants;
import java.awt.Font;

public class MainWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JButton btnListaReservas;
	private JButton btnListaEsperaCursillo;
	private JLabel lblActividades;
	private JLabel lblSocios;
	private JButton btnAlquilarInstalacionPara;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow frame = new MainWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainWindow() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 664, 618);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnCrearActividad = new JButton("Crear una Actividad");
		btnCrearActividad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new CrearActividad().setVisible(true);;
			}
		});
		btnCrearActividad.setBounds(28, 42, 300, 31);
		contentPane.add(btnCrearActividad);
		
		JButton btnInformacionActividades = new JButton("Informacion Actividades");
		btnInformacionActividades.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new ListaActividades().setVisible(true);
			}
		});
		btnInformacionActividades.setBounds(28, 221, 300, 31);
		contentPane.add(btnInformacionActividades);
		
		JButton btnAsignarMonitor = new JButton("Asignar Monitor a una Actividad");
		btnAsignarMonitor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new AsignarMonitor().setVisible(true);
			}
		});
		btnAsignarMonitor.setBounds(28, 133, 300, 31);
		contentPane.add(btnAsignarMonitor);
		
		JButton btnCalendarioActividades = new JButton("Calendario Actividades");
		btnCalendarioActividades.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CalendarioActividades calendarioA = new CalendarioActividades();
				calendarioA.getFrame().setVisible(true);
			}
		});
		btnCalendarioActividades.setBounds(412, 42, 215, 31);
		contentPane.add(btnCalendarioActividades);
		
		JButton btnCalendarioInstalaciones = new JButton("Calendario de Instalaciones");
		btnCalendarioInstalaciones.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CalendarioInstalaciones calendarioI = new CalendarioInstalaciones();
				calendarioI.getFrame().setVisible(true);
			}
		});
		btnCalendarioInstalaciones.setBounds(28, 177, 300, 31);
		contentPane.add(btnCalendarioInstalaciones);
		
		JButton btnReservarActividad = new JButton("Reservar actividad");
		btnReservarActividad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new ReservasIU().setVisible(true);
			}
		});
		btnReservarActividad.setBounds(412, 86, 215, 31);
		contentPane.add(btnReservarActividad);
		
		JButton btnAsignarInstalacion = new JButton("Asignar Instalacion a una actividad");
		btnAsignarInstalacion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new AsignarIU().setVisible(true);
			}
		});
		btnAsignarInstalacion.setBounds(28, 86, 300, 34);
		contentPane.add(btnAsignarInstalacion);
		
		JButton btnCrearCursillo = new JButton("Crear Cursillo");
		btnCrearCursillo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new CrearCursilloIU().setVisible(true);
			}
		});
		btnCrearCursillo.setBounds(28, 281, 300, 31);
		contentPane.add(btnCrearCursillo);
		contentPane.add(getBtnListaReservas());
		contentPane.add(getBtnListaEsperaCursillo());
		
		JButton btnListaApuntadosCursillo = new JButton("Lista de Apuntados a Cursillos");
		btnListaApuntadosCursillo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new ListaCursillo().setVisible(true);
			}
		});
		btnListaApuntadosCursillo.setBounds(28, 369, 300, 31);
		contentPane.add(btnListaApuntadosCursillo);
		
		JButton btnReservaCursilloSocio = new JButton("Reservar Plaza Cursillo");
		btnReservaCursilloSocio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new SocioReservaCursilloIU().setVisible(true);
			}
		});
		btnReservaCursilloSocio.setBounds(412, 177, 215, 31);
		contentPane.add(btnReservaCursilloSocio);
		
		JButton btnReservaCursilloAdmin = new JButton("Reservar Plaza Cursillo");
		btnReservaCursilloAdmin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new AdminReservaCursilloIU().setVisible(true);
			}
		});
		btnReservaCursilloAdmin.setBounds(28, 325, 300, 31);
		contentPane.add(btnReservaCursilloAdmin);
		
		JButton btnListarCursillosSocio = new JButton("Listar Cursillos Apuntados");
		btnListarCursillosSocio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new ListaReservasCursillo().setVisible(true);
			}
		});
		btnListarCursillosSocio.setBounds(412, 221, 215, 31);
		contentPane.add(btnListarCursillosSocio);
		contentPane.add(getLblActividades());
		contentPane.add(getLblSocios());
		
		JButton btnAsignarActividadRepetible = new JButton("Asignar Actividad Repetible");
		btnAsignarActividadRepetible.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new AsignarActividadRepetible().setVisible(true);
			}
		});
		btnAsignarActividadRepetible.setBounds(28, 455, 300, 31);
		contentPane.add(btnAsignarActividadRepetible);
		
		JButton btnAlquilarInstalacion = new JButton("Alquilar Instalacion");
		btnAlquilarInstalacion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new AlquilarInstalacionSocio().setVisible(true);
			}
		});
		btnAlquilarInstalacion.setBounds(412, 263, 215, 31);
		contentPane.add(btnAlquilarInstalacion);
		
		JButton btnPagarAlquiler = new JButton("Pagar Alquiler");
		btnPagarAlquiler.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new PagarAlquiler().setVisible(true);
			}
		});
		btnPagarAlquiler.setBounds(412, 305, 215, 31);
		contentPane.add(btnPagarAlquiler);
		contentPane.add(getBtnAlquilarInstalacionPara());
		
		JButton btnEliminarAsignacionesDesde = new JButton("Eliminar Asignaciones desde una fecha");
		btnEliminarAsignacionesDesde.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new EliminarAsignacionesDesdeFecha().setVisible(true);
			}
		});
		btnEliminarAsignacionesDesde.setBounds(28, 541, 300, 37);
		contentPane.add(btnEliminarAsignacionesDesde);
	}
	private JButton getBtnListaReservas() {
		if (btnListaReservas == null) {
			btnListaReservas = new JButton("Listar Reservas Socio");
			btnListaReservas.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					new ListaReservas().setVisible(true);
				}
			});
			btnListaReservas.setBounds(412, 133, 215, 31);
		}
		return btnListaReservas;

	}
	private JButton getBtnListaEsperaCursillo() {
		if (btnListaEsperaCursillo == null) {
			btnListaEsperaCursillo = new JButton("Lista de Espera de Cursillos");
			btnListaEsperaCursillo.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					new ListaEspera().setVisible(true);
				}
			});
			btnListaEsperaCursillo.setBounds(28, 413, 300, 31);
		}
		return btnListaEsperaCursillo;
	}
	private JLabel getLblActividades() {
		if (lblActividades == null) {
			lblActividades = new JLabel("Administracion");
			lblActividades.setFont(new Font("Tahoma", Font.ITALIC, 16));
			lblActividades.setHorizontalAlignment(SwingConstants.CENTER);
			lblActividades.setBounds(28, 13, 300, 16);
		}
		return lblActividades;
	}
	private JLabel getLblSocios() {
		if (lblSocios == null) {
			lblSocios = new JLabel("Socios");
			lblSocios.setFont(new Font("Tahoma", Font.ITALIC, 16));
			lblSocios.setHorizontalAlignment(SwingConstants.CENTER);
			lblSocios.setBounds(412, 13, 215, 16);
		}
		return lblSocios;
	}
	private JButton getBtnAlquilarInstalacionPara() {
		if (btnAlquilarInstalacionPara == null) {
			btnAlquilarInstalacionPara = new JButton("Alquilar Instalacion para Socio");
			btnAlquilarInstalacionPara.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					new AlquilarInstalacionAdmin().setVisible(true);
				}
			});
			btnAlquilarInstalacionPara.setBounds(28, 497, 300, 33);
		}
		return btnAlquilarInstalacionPara;
	}
}