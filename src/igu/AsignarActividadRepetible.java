package igu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

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
import logic.dto.ActividadDto;
import logic.dto.ActividadSinAsignarDto;
import logic.dto.InstalacionDto;
import logic.dto.MonitorDto;
import logic.dto.RecursoDto;

public class AsignarActividadRepetible extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JLabel lblCrearActividad;
	private JButton btnCancelar;
	private JButton btnCrear;
	private JCheckBox chckbxLunes;
	private JCheckBox chckbxMartes;
	private JCheckBox chckbxMiercoles;
	private JCheckBox chckbxJueves;
	private JCheckBox chckbxViernes;
	private JCheckBox chckbxSabado;
	private JCheckBox chckbxDomingo;
	private JPanel panel;
	private JComboBox cbLunesInicio;
	private DefaultComboBoxModel modeloHoras;
	private JComboBox cbLunesFinal;
	private JComboBox cbMartesInicio;
	private JComboBox cbMartesFinal;
	private JComboBox cbMiercolesInicio;
	private JComboBox cbMiercolesFinal;
	private JComboBox cbJuevesInicio;
	private JComboBox cbJuevesFinal;
	private JComboBox cbViernesInicio;
	private JComboBox cbViernesFinal;
	private JComboBox cbSabadoInicio;
	private JComboBox cbSabadoFinal;
	private JComboBox cbDomingoInicio;
	private JComboBox cbDomingoFinal;
	private JLabel lblFechaFinal;
	private JCalendar jC;
	private JComboBox[][] comboBoxHoras;
	private JCheckBox[] checkBoxDays;
	private JComboBox[] cbInstalaciones;
	private JComboBox[] cbMonitores;
	private DefaultComboBoxModel modeloRecursos = new DefaultComboBoxModel<>();
	private JComboBox cbActividades;
	private JLabel lblActividadC;
	private JComboBox cbInstalacionLunes;
	private JComboBox cbMonitorLunes;
	private JComboBox cbInstalacionMartes;
	private JComboBox cbInstalacionMiercoles;
	private JComboBox cbInstalacionJueves;
	private JComboBox cbInstalacionViernes;
	private JComboBox cbInstalacionSabado;
	private JComboBox cbInstalacionDomingo;
	private JComboBox cbMonitorMartes;
	private JComboBox cbMonitorMiercoles;
	private JComboBox cbMonitorJueves;
	private JComboBox cbMonitorViernes;
	private JComboBox cbMonitorSabado;
	private JComboBox cbMonitorDomingo;
	private JLabel lblDiaDeLa;
	private JLabel lblInicio;
	private JLabel lblFin;
	private JLabel lblInstalacion;
	private JLabel lblMonitor;

	/**
	 * Create the dialog.
	 */
	public AsignarActividadRepetible() {
		setComboBoxHoursArray();
		setCheckBoxDaysArray();
		setComboBoxInstalacionesArray();
		setComboBoxMonitoresArray();
		cargarModeloActividades();
		cargarModeloInstalaciones();
		cargarModeloMonitores();

		setResizable(false);
		setModal(true);
		setBounds(100, 100, 508, 551);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBackground(Color.WHITE);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		contentPanel.add(getLblCrearActividad());
		contentPanel.add(getPanel());
		contentPanel.add(getPanel());
		contentPanel.add(getCbActividades());
		contentPanel.add(getLblActividadC());
		contentPanel.add(getLblDiaDeLa());
		contentPanel.add(getLblInicio());
		contentPanel.add(getLblFin());
		contentPanel.add(getLblInstalacion());
		contentPanel.add(getLblMonitor());
		cargarRecursos();
	}

	private JLabel getLblCrearActividad() {
		if (lblCrearActividad == null) {
			lblCrearActividad = new JLabel("Asignar Actividad");
			lblCrearActividad.setHorizontalAlignment(SwingConstants.CENTER);
			lblCrearActividad.setFont(new Font("Tahoma", Font.BOLD, 20));
			lblCrearActividad.setBounds(10, 11, 472, 27);
		}
		return lblCrearActividad;
	}

	private JButton getBtnCancelar() {
		if (btnCancelar == null) {
			btnCancelar = new JButton("Cancelar");
			btnCancelar.setBounds(383, 347, 89, 23);
			btnCancelar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
		}
		return btnCancelar;
	}

	private JButton getBtnCrear() {
		if (btnCrear == null) {
			btnCrear = new JButton("Asignar");
			btnCrear.setBounds(284, 347, 89, 23);
			btnCrear.addActionListener(new ActionListener() {
				@SuppressWarnings("deprecation")
				public void actionPerformed(ActionEvent e) {
					ActividadSinAsignarDto actividad = (ActividadSinAsignarDto) getCbActividades().getSelectedItem();
					DateTime date = new DateTime();
					DateTime finalDate = new DateTime(getJCalendar().getDate());
					int i = date.getDayOfWeek() - 1;
					Asignar asignar = null;

					try {
						asignar = new Asignar();
					} catch (SQLException e2) {
						JOptionPane.showMessageDialog(null, "Error en la conexion", "Error conexion",
								JOptionPane.ERROR_MESSAGE);
					} catch (IOException e2) {
						JOptionPane.showMessageDialog(null, "Error en la configuracion", "Error configuracion",
								JOptionPane.ERROR_MESSAGE);
					}
					while (date.compareTo(finalDate) < 1) {
						if (checkBoxDays[i].isSelected()) {
							try {
								asignar.asignar(actividad, (InstalacionDto) cbInstalaciones[i].getSelectedItem(),
										(MonitorDto) cbMonitores[i].getSelectedItem(), date,
										Integer.parseInt(comboBoxHoras[i][0].getSelectedItem().toString()),
										Integer.parseInt(comboBoxHoras[i][1].getSelectedItem().toString()));
							} catch (NumberFormatException e1) {
							} catch (SQLException e2) {
								JOptionPane.showMessageDialog(null, "Error en la conexion", "Error conexion",
										JOptionPane.ERROR_MESSAGE);
							} catch (IOException e2) {
								JOptionPane.showMessageDialog(null, "Error en la configuracion", "Error configuracion",
										JOptionPane.ERROR_MESSAGE);
							}
						}
						date = date.plusDays(1);
						i = date.getDayOfWeek() - 1;
					}
					JOptionPane.showMessageDialog(null, "Instancias Asignadas", "", JOptionPane.DEFAULT_OPTION);
				}
			});
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

	private JPanel getPanel() {
		if (panel == null) {
			panel = new JPanel();
			panel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
			panel.setBounds(10, 131, 482, 380);
			panel.setLayout(null);
			panel.add(getChckbxLunes());
			panel.add(getChckbxMartes());
			panel.add(getChckbxMiercoles());
			panel.add(getChckbxJueves());
			panel.add(getChckbxViernes());
			panel.add(getChckbxSabado());
			panel.add(getChckbxDomingo());
			panel.add(getCbLunesInicio());
			panel.add(getCbLunesFinal());
			panel.add(getCbMartesInicio());
			panel.add(getCbMartesFinal());
			panel.add(getCbMiercolesInicio());
			panel.add(getCbMiercolesFinal());
			panel.add(getCbJuevesInicio());
			panel.add(getCbJuevesFinal());
			panel.add(getCbViernesInicio());
			panel.add(getCbViernesFinal());
			panel.add(getCbSabadoInicio());
			panel.add(getCbSabadoFinal());
			panel.add(getCbDomingoInicio());
			panel.add(getCbDomingoFinal());
			panel.add(getLblFechaFinal());
			panel.add(getJCalendar());
			panel.add(getBtnCrear());
			panel.add(getBtnCancelar());
			panel.add(getCbInstalacionLunes());
			panel.add(getCbMonitorLunes());
			panel.add(getCbInstalacionMartes());
			panel.add(getCbInstalacionMiercoles());
			panel.add(getCbInstalacionJueves());
			panel.add(getCbInstalacionViernes());
			panel.add(getCbInstalacionSabado());
			panel.add(getCbInstalacionDomingo());
			panel.add(getCbMonitorMartes());
			panel.add(getCbMonitorMiercoles());
			panel.add(getCbMonitorJueves());
			panel.add(getCbMonitorViernes());
			panel.add(getCbMonitorSabado());
			panel.add(getCbMonitorDomingo());
		}
		return panel;
	}

	private JCheckBox getChckbxLunes() {
		if (chckbxLunes == null) {
			chckbxLunes = new JCheckBox("Lunes");
			chckbxLunes.setBounds(10, 7, 97, 23);
		}
		return chckbxLunes;
	}

	private JCheckBox getChckbxMartes() {
		if (chckbxMartes == null) {
			chckbxMartes = new JCheckBox("Martes");
			chckbxMartes.setBounds(10, 33, 97, 23);
		}
		return chckbxMartes;
	}

	private JCheckBox getChckbxMiercoles() {
		if (chckbxMiercoles == null) {
			chckbxMiercoles = new JCheckBox("Miercoles");
			chckbxMiercoles.setBounds(10, 59, 97, 23);
		}
		return chckbxMiercoles;
	}

	private JCheckBox getChckbxJueves() {
		if (chckbxJueves == null) {
			chckbxJueves = new JCheckBox("Jueves");
			chckbxJueves.setBounds(10, 85, 97, 23);
		}
		return chckbxJueves;
	}

	private JCheckBox getChckbxViernes() {
		if (chckbxViernes == null) {
			chckbxViernes = new JCheckBox("Viernes");
			chckbxViernes.setBounds(10, 111, 97, 23);
		}
		return chckbxViernes;
	}

	private JCheckBox getChckbxSabado() {
		if (chckbxSabado == null) {
			chckbxSabado = new JCheckBox("Sabado");
			chckbxSabado.setBounds(10, 137, 97, 23);
		}
		return chckbxSabado;
	}

	private JCheckBox getChckbxDomingo() {
		if (chckbxDomingo == null) {
			chckbxDomingo = new JCheckBox("Domingo");
			chckbxDomingo.setBounds(10, 163, 97, 23);
		}
		return chckbxDomingo;
	}

	private JComboBox getCbLunesInicio() {
		if (cbLunesInicio == null) {
			cbLunesInicio = new JComboBox();
			cbLunesInicio.setBounds(113, 8, 48, 20);
			cbLunesInicio.setModel(getModelHoras());
		}
		return cbLunesInicio;
	}

	private JComboBox getCbLunesFinal() {
		if (cbLunesFinal == null) {
			cbLunesFinal = new JComboBox();
			cbLunesFinal.setBounds(171, 8, 48, 20);
			cbLunesFinal.setModel(getModelHoras());
		}
		return cbLunesFinal;
	}

	private JComboBox getCbMartesInicio() {
		if (cbMartesInicio == null) {
			cbMartesInicio = new JComboBox();
			cbMartesInicio.setBounds(113, 34, 48, 20);
			cbMartesInicio.setModel(getModelHoras());
		}
		return cbMartesInicio;
	}

	private JComboBox getCbMartesFinal() {
		if (cbMartesFinal == null) {
			cbMartesFinal = new JComboBox();
			cbMartesFinal.setBounds(171, 34, 48, 20);
			cbMartesFinal.setModel(getModelHoras());
		}
		return cbMartesFinal;
	}

	private JComboBox getCbMiercolesInicio() {
		if (cbMiercolesInicio == null) {
			cbMiercolesInicio = new JComboBox();
			cbMiercolesInicio.setBounds(113, 60, 48, 20);
			cbMiercolesInicio.setModel(getModelHoras());
		}
		return cbMiercolesInicio;
	}

	private JComboBox getCbMiercolesFinal() {
		if (cbMiercolesFinal == null) {
			cbMiercolesFinal = new JComboBox();
			cbMiercolesFinal.setBounds(171, 60, 48, 20);
			cbMiercolesFinal.setModel(getModelHoras());
		}
		return cbMiercolesFinal;
	}

	private JComboBox getCbJuevesInicio() {
		if (cbJuevesInicio == null) {
			cbJuevesInicio = new JComboBox();
			cbJuevesInicio.setBounds(113, 86, 48, 20);
			cbJuevesInicio.setModel(getModelHoras());
		}
		return cbJuevesInicio;
	}

	private JComboBox getCbJuevesFinal() {
		if (cbJuevesFinal == null) {
			cbJuevesFinal = new JComboBox();
			cbJuevesFinal.setBounds(171, 86, 48, 20);
			cbJuevesFinal.setModel(getModelHoras());
		}
		return cbJuevesFinal;
	}

	private JComboBox getCbViernesInicio() {
		if (cbViernesInicio == null) {
			cbViernesInicio = new JComboBox();
			cbViernesInicio.setBounds(113, 112, 48, 20);
			cbViernesInicio.setModel(getModelHoras());
		}
		return cbViernesInicio;
	}

	private JComboBox getCbViernesFinal() {
		if (cbViernesFinal == null) {
			cbViernesFinal = new JComboBox();
			cbViernesFinal.setBounds(171, 112, 48, 20);
			cbViernesFinal.setModel(getModelHoras());
		}
		return cbViernesFinal;
	}

	private JComboBox getCbSabadoInicio() {
		if (cbSabadoInicio == null) {
			cbSabadoInicio = new JComboBox();
			cbSabadoInicio.setBounds(113, 138, 48, 20);
			cbSabadoInicio.setModel(getModelHoras());
		}
		return cbSabadoInicio;
	}

	private JComboBox getCbSabadoFinal() {
		if (cbSabadoFinal == null) {
			cbSabadoFinal = new JComboBox();
			cbSabadoFinal.setBounds(171, 138, 48, 20);
			cbSabadoFinal.setModel(getModelHoras());
		}
		return cbSabadoFinal;
	}

	private JComboBox getCbDomingoInicio() {
		if (cbDomingoInicio == null) {
			cbDomingoInicio = new JComboBox();
			cbDomingoInicio.setBounds(113, 164, 48, 20);
			cbDomingoInicio.setModel(getModelHoras());
		}
		return cbDomingoInicio;
	}

	private JComboBox getCbDomingoFinal() {
		if (cbDomingoFinal == null) {
			cbDomingoFinal = new JComboBox();
			cbDomingoFinal.setBounds(171, 164, 48, 20);
			cbDomingoFinal.setModel(getModelHoras());
		}
		return cbDomingoFinal;
	}

	private JLabel getLblFechaFinal() {
		if (lblFechaFinal == null) {
			lblFechaFinal = new JLabel("Fecha Final: ");
			lblFechaFinal.setBounds(40, 195, 69, 14);
		}
		return lblFechaFinal;
	}

	private void setComboBoxHoursArray() {
		JComboBox[][] comboBoxHoras = new JComboBox[7][2];
		comboBoxHoras[0][0] = getCbLunesInicio();
		comboBoxHoras[0][1] = getCbLunesFinal();

		comboBoxHoras[1][0] = getCbMartesInicio();
		comboBoxHoras[1][1] = getCbMartesFinal();

		comboBoxHoras[2][0] = getCbMiercolesInicio();
		comboBoxHoras[2][1] = getCbMiercolesFinal();

		comboBoxHoras[3][0] = getCbJuevesInicio();
		comboBoxHoras[3][1] = getCbJuevesFinal();

		comboBoxHoras[4][0] = getCbViernesInicio();
		comboBoxHoras[4][1] = getCbViernesFinal();

		comboBoxHoras[5][0] = getCbSabadoInicio();
		comboBoxHoras[5][1] = getCbSabadoFinal();

		comboBoxHoras[6][0] = getCbDomingoInicio();
		comboBoxHoras[6][1] = getCbDomingoFinal();

		this.comboBoxHoras = comboBoxHoras;
	}

	private void setCheckBoxDaysArray() {
		JCheckBox[] checkBoxDays = new JCheckBox[7];

		checkBoxDays[0] = getChckbxLunes();
		checkBoxDays[1] = getChckbxMartes();
		checkBoxDays[2] = getChckbxMiercoles();
		checkBoxDays[3] = getChckbxJueves();
		checkBoxDays[4] = getChckbxViernes();
		checkBoxDays[5] = getChckbxSabado();
		checkBoxDays[6] = getChckbxDomingo();

		this.checkBoxDays = checkBoxDays;
	}

	private void setComboBoxInstalacionesArray() {
		JComboBox[] cbInstalaciones = new JComboBox[7];

		cbInstalaciones[0] = getCbInstalacionLunes();
		cbInstalaciones[1] = getCbInstalacionMartes();
		cbInstalaciones[2] = getCbInstalacionMiercoles();
		cbInstalaciones[3] = getCbInstalacionJueves();
		cbInstalaciones[4] = getCbInstalacionViernes();
		cbInstalaciones[5] = getCbInstalacionSabado();
		cbInstalaciones[6] = getCbInstalacionDomingo();

		this.cbInstalaciones = cbInstalaciones;
	}

	private void setComboBoxMonitoresArray() {
		JComboBox[] cbMonitores = new JComboBox[7];

		cbMonitores[0] = getCbMonitorLunes();
		cbMonitores[1] = getCbMonitorMartes();
		cbMonitores[2] = getCbMonitorMiercoles();
		cbMonitores[3] = getCbMonitorJueves();
		cbMonitores[4] = getCbMonitorViernes();
		cbMonitores[5] = getCbMonitorSabado();
		cbMonitores[6] = getCbMonitorDomingo();

		this.cbMonitores = cbMonitores;
	}

	private JCalendar getJCalendar() {
		if (jC == null) {
			jC = new JCalendar();
			jC.setBounds(40, 220, 180, 150);
		}
		return jC;
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

	private JComboBox getCbActividades() {
		if (cbActividades == null) {
			cbActividades = new JComboBox();
			cbActividades.setBounds(135, 64, 347, 27);
			cbActividades.setModel(cargarModeloActividades());
		}
		return cbActividades;
	}

	private JLabel getLblActividadC() {
		if (lblActividadC == null) {
			lblActividadC = new JLabel("Actividad: ");
			lblActividadC.setHorizontalAlignment(SwingConstants.CENTER);
			lblActividadC.setBounds(10, 70, 115, 21);
		}
		return lblActividadC;
	}

	private JComboBox getCbInstalacionLunes() {
		if (cbInstalacionLunes == null) {
			cbInstalacionLunes = new JComboBox();
			cbInstalacionLunes.setBounds(229, 8, 116, 20);
		}
		return cbInstalacionLunes;
	}

	private JComboBox getCbMonitorLunes() {
		if (cbMonitorLunes == null) {
			cbMonitorLunes = new JComboBox();
			cbMonitorLunes.setBounds(355, 8, 117, 20);
		}
		return cbMonitorLunes;
	}

	private JComboBox getCbInstalacionMartes() {
		if (cbInstalacionMartes == null) {
			cbInstalacionMartes = new JComboBox();
			cbInstalacionMartes.setBounds(229, 34, 116, 20);
		}
		return cbInstalacionMartes;
	}

	private JComboBox getCbInstalacionMiercoles() {
		if (cbInstalacionMiercoles == null) {
			cbInstalacionMiercoles = new JComboBox();
			cbInstalacionMiercoles.setBounds(229, 60, 116, 20);
		}
		return cbInstalacionMiercoles;
	}

	private JComboBox getCbInstalacionJueves() {
		if (cbInstalacionJueves == null) {
			cbInstalacionJueves = new JComboBox();
			cbInstalacionJueves.setBounds(229, 86, 116, 20);
		}
		return cbInstalacionJueves;
	}

	private JComboBox getCbInstalacionViernes() {
		if (cbInstalacionViernes == null) {
			cbInstalacionViernes = new JComboBox();
			cbInstalacionViernes.setBounds(229, 112, 116, 20);
		}
		return cbInstalacionViernes;
	}

	private JComboBox getCbInstalacionSabado() {
		if (cbInstalacionSabado == null) {
			cbInstalacionSabado = new JComboBox();
			cbInstalacionSabado.setBounds(229, 138, 116, 20);
		}
		return cbInstalacionSabado;
	}

	private JComboBox getCbInstalacionDomingo() {
		if (cbInstalacionDomingo == null) {
			cbInstalacionDomingo = new JComboBox();
			cbInstalacionDomingo.setBounds(229, 164, 116, 20);
		}
		return cbInstalacionDomingo;
	}

	private JComboBox getCbMonitorMartes() {
		if (cbMonitorMartes == null) {
			cbMonitorMartes = new JComboBox();
			cbMonitorMartes.setBounds(355, 34, 117, 22);
		}
		return cbMonitorMartes;
	}

	private JComboBox getCbMonitorMiercoles() {
		if (cbMonitorMiercoles == null) {
			cbMonitorMiercoles = new JComboBox();
			cbMonitorMiercoles.setBounds(355, 60, 117, 20);
		}
		return cbMonitorMiercoles;
	}

	private JComboBox getCbMonitorJueves() {
		if (cbMonitorJueves == null) {
			cbMonitorJueves = new JComboBox();
			cbMonitorJueves.setBounds(355, 86, 117, 20);
		}
		return cbMonitorJueves;
	}

	private JComboBox getCbMonitorViernes() {
		if (cbMonitorViernes == null) {
			cbMonitorViernes = new JComboBox();
			cbMonitorViernes.setBounds(355, 112, 117, 22);
		}
		return cbMonitorViernes;
	}

	private JComboBox getCbMonitorSabado() {
		if (cbMonitorSabado == null) {
			cbMonitorSabado = new JComboBox();
			cbMonitorSabado.setBounds(355, 138, 117, 20);
		}
		return cbMonitorSabado;
	}

	private JComboBox getCbMonitorDomingo() {
		if (cbMonitorDomingo == null) {
			cbMonitorDomingo = new JComboBox();
			cbMonitorDomingo.setBounds(355, 164, 117, 20);
		}
		return cbMonitorDomingo;
	}

	private JLabel getLblDiaDeLa() {
		if (lblDiaDeLa == null) {
			lblDiaDeLa = new JLabel("Dia de la semana");
			lblDiaDeLa.setBounds(20, 106, 105, 14);
		}
		return lblDiaDeLa;
	}

	private JLabel getLblInicio() {
		if (lblInicio == null) {
			lblInicio = new JLabel("Inicio");
			lblInicio.setHorizontalAlignment(SwingConstants.CENTER);
			lblInicio.setBounds(121, 106, 46, 14);
		}
		return lblInicio;
	}

	private JLabel getLblFin() {
		if (lblFin == null) {
			lblFin = new JLabel("Fin");
			lblFin.setHorizontalAlignment(SwingConstants.CENTER);
			lblFin.setBounds(177, 106, 46, 14);
		}
		return lblFin;
	}

	private JLabel getLblInstalacion() {
		if (lblInstalacion == null) {
			lblInstalacion = new JLabel("Instalacion");
			lblInstalacion.setHorizontalAlignment(SwingConstants.CENTER);
			lblInstalacion.setBounds(254, 106, 120, 14);
		}
		return lblInstalacion;
	}

	private JLabel getLblMonitor() {
		if (lblMonitor == null) {
			lblMonitor = new JLabel("Monitor");
			lblMonitor.setHorizontalAlignment(SwingConstants.CENTER);
			lblMonitor.setBounds(384, 106, 115, 14);
		}
		return lblMonitor;
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

	private void cargarModeloInstalaciones() {
		List<InstalacionDto> instalaciones = null;
		try {
			instalaciones = DatabaseLogic.getInstalaciones();
		} catch (SQLException e2) {
			JOptionPane.showMessageDialog(null, "Error en la conexion", "Error conexion", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e2) {
			JOptionPane.showMessageDialog(null, "Error en la configuracion", "Error configuracion",
					JOptionPane.ERROR_MESSAGE);
		}
		for (JComboBox cb : cbInstalaciones) {
			DefaultComboBoxModel modelo = new DefaultComboBoxModel();
			for (InstalacionDto i : instalaciones) {
				modelo.addElement(i);
			}
			cb.setModel(modelo);
		}
	}

	private void cargarModeloMonitores() {
		List<MonitorDto> monitores = null;
		try {
			monitores = DatabaseLogic.getMonitores();
		} catch (SQLException e2) {
			JOptionPane.showMessageDialog(null, "Error en la conexion", "Error conexion", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e2) {
			JOptionPane.showMessageDialog(null, "Error en la configuracion", "Error configuracion",
					JOptionPane.ERROR_MESSAGE);
		}
		for (JComboBox cb : cbMonitores) {
			DefaultComboBoxModel modelo = new DefaultComboBoxModel();
			for (MonitorDto m : monitores) {
				modelo.addElement(m);
			}
			cb.setModel(modelo);
		}
	}
}
