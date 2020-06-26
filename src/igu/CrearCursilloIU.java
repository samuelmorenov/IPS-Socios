package igu;

import java.awt.BorderLayout;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.jdatepicker.impl.DateComponentFormatter;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import org.joda.time.DateTime;

import logic.comboBox_data.ComboBoxData;
import logic.cursillo_crear_reservar.CrearCursillo;
import logic.cursillo_crear_reservar.TratamientoArchivos;
import logic.dto.CursilloDto;
import logic.dto.InstalacionDto;
import logic.dto.MonitorDto;
import logic.dto.OcupacionesDto;
import logic.exception.DatosDeCursillosErroneosException;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import java.awt.Font;
import javax.swing.SwingConstants;

public class CrearCursilloIU extends JDialog {

	private static final long serialVersionUID = 403333508228349888L;
	private CrearCursillo cursillo;
	private final JPanel contentPanel = new JPanel();

	private JLabel etiquetaCrearCursillo;
	private JLabel etiquetaDiasDeLa;
	private JLabel etiquetaNumeroDePlazas;
	private JLabel etiquetaInstalacion;
	private JLabel etiquetaMonitor;
	private JLabel etiquetaHorariohoraDe;
	private JButton botonCancelar;
	private JButton botonCrear;
	private JTextField txtNumeroDePlazas;
	private JTextField txtHoraDeInicio;
	private JComboBox<String> boxDia1;
	private JComboBox<String> boxDia2;
	private JComboBox<InstalacionDto> boxInstalacion;
	private JComboBox<MonitorDto> boxMonitor;
	private JLabel etiquetaNombre;
	private JTextField txtNombre;
	private JLabel lblFechaDeInicio;
	private JLabel lblFechaDeFinalizacion;
	private JLabel lblFechaDeInscripcion;
	private JLabel lblFechaDeInscripcion_1;
	private JDatePickerImpl fechaInicio;
	private JDatePickerImpl fechaFin;
	private JDatePickerImpl fechaSocios;
	private JDatePickerImpl fechaGeneral;

	private ActionListener accionCrear = new ActionListener() {
		public void actionPerformed(ActionEvent actionEvent) {

			// Obtener el valor de todas los datos
			CursilloDto cursilloDto = new CursilloDto();

			// Nombre
			cursilloDto.nombre = txtNombre.getText();

			// Fechas
			cursilloDto.fecha_inicio = new DateTime(fechaInicio.getJDateInstantPanel().getModel().getValue());
			cursilloDto.fecha_fin = new DateTime(fechaFin.getJDateInstantPanel().getModel().getValue());
			cursilloDto.fecha_inscripcion_socio = new DateTime(fechaSocios.getJDateInstantPanel().getModel().getValue());
			cursilloDto.fecha_inscripcion_nosocio = new DateTime(fechaGeneral.getJDateInstantPanel().getModel().getValue());

			// Dias de la semana
			String diaSemana1 = boxDia1.getSelectedItem().toString();
			String diaSemana2 = boxDia2.getSelectedItem().toString();
			int dia1 = 1;
			int dia2 = 1;
			for (int i = 0; i < ComboBoxData.getDiasSemana().length; i++) {
				if (ComboBoxData.getDiasSemana()[i] == diaSemana1) {
					dia1 = i + 1;// Los dias de la semana iran del 1 al 7
				}
				if (ComboBoxData.getDiasSemana()[i] == diaSemana2) {
					dia2 = i + 1;// Los dias de la semana iran del 1 al 7
				}
			}
			cursilloDto.diaSemana1 = dia1;
			cursilloDto.diaSemana2 = dia2;

			// Instalacion y monitor
			cursilloDto.instalacion = (InstalacionDto) boxInstalacion.getSelectedItem();
			cursilloDto.monitor = (MonitorDto) boxMonitor.getSelectedItem();

			// Plazas y hora
			try {
				cursilloDto.plazas = Integer.parseInt(txtNumeroDePlazas.getText());

			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null,
						"No se ha introducido un numero entero en la casilla de numero de plazas.",
						"Error al crear Cursillo", JOptionPane.ERROR_MESSAGE);
				return;
			}

			try {
				cursilloDto.horaInicio = Integer.parseInt(txtHoraDeInicio.getText());

			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null,
						"No se ha introducido un numero entero en la casilla hora de inicio.",
						"Error al crear Cursillo", JOptionPane.ERROR_MESSAGE);
				return;
			}

			try {
				cursilloDto.precio = Double.parseDouble(txtPrecio.getText());

			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "No se ha introducido un numero en la casilla hora de precio.",
						"Error al crear Cursillo", JOptionPane.ERROR_MESSAGE);
				return;
			}

			// Lista de ocupados
			ArrayList<OcupacionesDto> ocupados = new ArrayList<OcupacionesDto>();

			// Creamos el cursillo
			try {

				cursillo.crearNuevoCursillo(cursilloDto, ocupados);

				// Si hay coincidencias las imprimimos
				if (!ocupados.isEmpty()) {
					TratamientoArchivos archivo = new TratamientoArchivos();
					String e = archivo.crearDocumento(ocupados, "Coincidencias encontradas al crear cursillo");
					int confirmado = JOptionPane.showConfirmDialog(null, e, "Error al crear Cursillo",
							JOptionPane.YES_OPTION, JOptionPane.DEFAULT_OPTION);
					if (JOptionPane.OK_OPTION == confirmado) {
						archivo.abrirElUltimoArchivo();
					}
				}
				// Si no hay coincidencias insertarmos el cursillo
				else {
					cursillo.insertarCrusillo(cursilloDto);
					JOptionPane.showMessageDialog(null, "Cursillo creado con exito.", "Cursillo creado",
							JOptionPane.INFORMATION_MESSAGE);
					dispose();
				}

			} catch (DatosDeCursillosErroneosException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, e.getMessage(), "Error en los datos del cursillo",
						JOptionPane.ERROR_MESSAGE);
				return;
			} catch (SQLException | IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Error en la base de datos", "Error al crear cursillo",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

		}

	};

	private ActionListener actionCancelar = new ActionListener() {
		public void actionPerformed(ActionEvent actionEvent) {
			dispose();
		}
	};
	private JLabel lblPrecio;
	private JTextField txtPrecio;

	public CrearCursilloIU() {

		try {
			cursillo = new CrearCursillo();

			setResizable(false);
			setModal(true);
			setBounds(100, 100, 435, 609);
			setLocationRelativeTo(null);
			getContentPane().setLayout(new BorderLayout());
			contentPanel.setBackground(Color.WHITE);
			contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
			getContentPane().add(contentPanel, BorderLayout.CENTER);
			contentPanel.setLayout(null);

			contentPanel.add(getBtnCrear());
			contentPanel.add(getBtnCancelar());
			contentPanel.add(getEtiquetaCrearCursillo());
			contentPanel.add(getEtiquetaDiasDeLa());
			contentPanel.add(getEtiquetaNumeroDePlazas());
			contentPanel.add(getComboBoxDia1());
			contentPanel.add(getComboBoxDia2());
			contentPanel.add(getTxtNumeroDePlazas());
			contentPanel.add(getEtiquetaInstalacion());
			contentPanel.add(getEtiquetaMonitor());
			contentPanel.add(getComboBoxInstalacion());
			contentPanel.add(getComboBoxMonitor());
			contentPanel.add(getEtiquetaHorariohoraDe());
			contentPanel.add(getTxtHoraDeInicio());
			contentPanel.add(getEtiquetaNombre());
			contentPanel.add(getTxtNombre());
			contentPanel.add(getLblFechaDeInicio());
			contentPanel.add(getLblFechaDeFinalizacion());
			contentPanel.add(getFechaInicio());
			contentPanel.add(getFechaFin());
			contentPanel.add(getFechaSocios());
			contentPanel.add(getFechaGeneral());
			contentPanel.add(getLblFechaDeInscripcion());
			contentPanel.add(getLblFechaDeInscripcion_1());
			contentPanel.add(getLblPrecio());
			contentPanel.add(getTxtPrecio());


		} catch (IOException | SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error al abrir la base de datos", "Error en la base de datos",
					JOptionPane.ERROR_MESSAGE);
			dispose();
			return;
		}

	}

	private JButton getBtnCrear() {
		if (botonCrear == null) {
			botonCrear = new JButton("Crear");
			botonCrear.addActionListener(accionCrear);
			botonCrear.setBounds(223, 535, 89, 23);
		}
		return botonCrear;
	}

	private JButton getBtnCancelar() {
		if (botonCancelar == null) {
			botonCancelar = new JButton("Cancelar");
			botonCancelar.addActionListener(actionCancelar);
			botonCancelar.setBounds(324, 535, 89, 23);
		}
		return botonCancelar;
	}

	private JComboBox<String> getComboBoxDia1() {
		if (boxDia1 == null) {
			boxDia1 = new JComboBox<String>();
			boxDia1.setBounds(22, 294, 180, 22);
			boxDia1.setModel(new DefaultComboBoxModel<String>(ComboBoxData.getDiasSemana()));
		}
		return boxDia1;
	}

	private JComboBox<String> getComboBoxDia2() {
		if (boxDia2 == null) {
			boxDia2 = new JComboBox<String>();
			boxDia2.setBounds(223, 294, 175, 22);
			boxDia2.setModel(new DefaultComboBoxModel<String>(ComboBoxData.getDiasSemana()));
			boxDia2.setSelectedIndex(2);
		}
		return boxDia2;
	}

	private JComboBox<InstalacionDto> getComboBoxInstalacion() {
		if (boxInstalacion == null) {
			boxInstalacion = new JComboBox<InstalacionDto>();
			boxInstalacion.setBounds(22, 480, 378, 22);
			try {
				boxInstalacion.setModel(new DefaultComboBoxModel<InstalacionDto>(cursillo.getInstalaciones()));
			} catch (SQLException | IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Error al obtener las instalaciones",
						"Error en creacion de cursillo", JOptionPane.ERROR_MESSAGE);
				dispose();
			}
		}
		return boxInstalacion;
	}

	private JComboBox<MonitorDto> getComboBoxMonitor() {
		if (boxMonitor == null) {
			boxMonitor = new JComboBox<MonitorDto>();
			boxMonitor.setBounds(223, 422, 175, 22);

			try {
				boxMonitor.setModel(new DefaultComboBoxModel<MonitorDto>(cursillo.getMonitores()));
			} catch (SQLException | IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Error al obtener los monitores", "Error en creacion de cursillo",
						JOptionPane.ERROR_MESSAGE);
				dispose();
			}

		}
		return boxMonitor;
	}

	private JTextField getTxtNumeroDePlazas() {
		if (txtNumeroDePlazas == null) {
			txtNumeroDePlazas = new JTextField();
			txtNumeroDePlazas.setText("50");
			txtNumeroDePlazas.setBounds(223, 358, 175, 22);
			txtNumeroDePlazas.setColumns(10);
		}
		return txtNumeroDePlazas;
	}

	private JTextField getTxtHoraDeInicio() {
		if (txtHoraDeInicio == null) {
			txtHoraDeInicio = new JTextField();
			txtHoraDeInicio.setText("16");
			txtHoraDeInicio.setBounds(22, 358, 180, 22);
			txtHoraDeInicio.setColumns(10);
		}
		return txtHoraDeInicio;
	}

	private JTextField getTxtNombre() {
		if (txtNombre == null) {
			txtNombre = new JTextField();
			txtNombre.setText("Nuevo Cursillo");
			txtNombre.setBounds(22, 94, 188, 22);
			txtNombre.setColumns(10);
		}
		return txtNombre;
	}

	private JLabel getEtiquetaCrearCursillo() {
		if (etiquetaCrearCursillo == null) {
			etiquetaCrearCursillo = new JLabel("Crear Cursillo");
			etiquetaCrearCursillo.setHorizontalAlignment(SwingConstants.CENTER);
			etiquetaCrearCursillo.setFont(new Font("Tahoma", Font.BOLD, 24));
			etiquetaCrearCursillo.setBounds(12, 13, 401, 39);
		}
		return etiquetaCrearCursillo;
	}

	private JLabel getEtiquetaDiasDeLa() {
		if (etiquetaDiasDeLa == null) {
			etiquetaDiasDeLa = new JLabel("Dias de la semana");
			etiquetaDiasDeLa.setBounds(22, 265, 150, 16);
		}
		return etiquetaDiasDeLa;
	}

	private JLabel getEtiquetaNumeroDePlazas() {
		if (etiquetaNumeroDePlazas == null) {
			etiquetaNumeroDePlazas = new JLabel("Numero de plazas");
			etiquetaNumeroDePlazas.setBounds(223, 329, 188, 16);
		}
		return etiquetaNumeroDePlazas;
	}

	private JLabel getEtiquetaInstalacion() {
		if (etiquetaInstalacion == null) {
			etiquetaInstalacion = new JLabel("Instalacion");
			etiquetaInstalacion.setBounds(22, 457, 150, 16);
		}
		return etiquetaInstalacion;
	}

	private JLabel getEtiquetaMonitor() {
		if (etiquetaMonitor == null) {
			etiquetaMonitor = new JLabel("Monitor");
			etiquetaMonitor.setBounds(223, 393, 188, 16);
		}
		return etiquetaMonitor;
	}

	private JLabel getEtiquetaHorariohoraDe() {
		if (etiquetaHorariohoraDe == null) {
			etiquetaHorariohoraDe = new JLabel("Horario (1 hora de duracion)");
			etiquetaHorariohoraDe.setBounds(22, 329, 188, 16);
		}
		return etiquetaHorariohoraDe;
	}

	private JLabel getEtiquetaNombre() {
		if (etiquetaNombre == null) {
			etiquetaNombre = new JLabel("Nombre");
			etiquetaNombre.setBounds(22, 65, 188, 16);
		}
		return etiquetaNombre;
	}

	private JLabel getLblFechaDeInicio() {
		if (lblFechaDeInicio == null) {
			lblFechaDeInicio = new JLabel("Fecha de Inicio");
			lblFechaDeInicio.setBounds(22, 129, 188, 16);
		}
		return lblFechaDeInicio;
	}

	private JLabel getLblFechaDeFinalizacion() {
		if (lblFechaDeFinalizacion == null) {
			lblFechaDeFinalizacion = new JLabel("Fecha de Finalizacion");
			lblFechaDeFinalizacion.setBounds(222, 129, 188, 16);
		}
		return lblFechaDeFinalizacion;
	}

	private JLabel getLblFechaDeInscripcion() {
		if (lblFechaDeInscripcion == null) {
			lblFechaDeInscripcion = new JLabel("Inscripcion para socios");
			lblFechaDeInscripcion.setBounds(22, 197, 180, 16);
		}
		return lblFechaDeInscripcion;
	}

	private JLabel getLblFechaDeInscripcion_1() {
		if (lblFechaDeInscripcion_1 == null) {
			lblFechaDeInscripcion_1 = new JLabel("Inscripcion general");
			lblFechaDeInscripcion_1.setBounds(220, 197, 180, 16);
		}
		return lblFechaDeInscripcion_1;
	}

	private JLabel getLblPrecio() {
		if (lblPrecio == null) {
			lblPrecio = new JLabel("Precio");
			lblPrecio.setBounds(22, 393, 188, 16);
		}
		return lblPrecio;
	}

	private JTextField getTxtPrecio() {
		if (txtPrecio == null) {
			txtPrecio = new JTextField();
			txtPrecio.setText("50");
			txtPrecio.setBounds(22, 422, 180, 22);
			txtPrecio.setColumns(10);
		}
		return txtPrecio;
	}

	private JDatePickerImpl getFechaInicio() {
		if (fechaInicio == null) {
			Properties p = new Properties();
			p.put("text.today", "today");
			p.put("text.month", "month");
			p.put("text.year", "year");

			UtilDateModel model = new UtilDateModel();
			JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
			fechaInicio = new JDatePickerImpl(datePanel, new DateComponentFormatter());
			fechaInicio.setBounds(22, 158, 180, 26);
		}
		return fechaInicio;
	}

	private JDatePickerImpl getFechaFin() {
		if (fechaFin == null) {
			Properties p = new Properties();
			p.put("text.today", "today");
			p.put("text.month", "month");
			p.put("text.year", "year");

			UtilDateModel model = new UtilDateModel();
			JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
			fechaFin = new JDatePickerImpl(datePanel, new DateComponentFormatter());
			fechaFin.setBounds(220, 158, 180, 26);
		}
		return fechaFin;
	}

	private JDatePickerImpl getFechaSocios() {
		if (fechaSocios == null) {
			Properties p = new Properties();
			p.put("text.today", "today");
			p.put("text.month", "month");
			p.put("text.year", "year");

			UtilDateModel model = new UtilDateModel();
			JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
			fechaSocios = new JDatePickerImpl(datePanel, new DateComponentFormatter());
			fechaSocios.setBounds(22, 226, 180, 26);
		}
		return fechaSocios;
	}

	private JDatePickerImpl getFechaGeneral() {
		if (fechaGeneral == null) {
			Properties p = new Properties();
			p.put("text.today", "today");
			p.put("text.month", "month");
			p.put("text.year", "year");

			UtilDateModel model = new UtilDateModel();
			JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
			fechaGeneral = new JDatePickerImpl(datePanel, new DateComponentFormatter());
			fechaGeneral.setBounds(222, 226, 180, 26);
		}
		return fechaGeneral;
	}
}
