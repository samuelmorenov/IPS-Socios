package igu;

import java.awt.Color;
import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.awt.event.ActionEvent;

import javax.swing.AbstractButton;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import logic.comboBox_data.ComboBoxData;
import logic.cursillo_crear_reservar.ReservaCursillo;
import logic.cursillo_crear_reservar.TratamientoArchivos;
import logic.dto.CursilloDto;
import logic.dto.OcupacionesDto;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JComboBox;
import java.awt.SystemColor;
import javax.swing.JCheckBox;

public class SocioReservaCursilloIU extends JDialog {

	private ReservaCursillo reserva;
	private boolean hayPlazas = true;

	private static final long serialVersionUID = 403333508228349888L;
	private final JPanel contentPanel = new JPanel();

	private JButton botonCancelar;
	private JButton botonAceptar;
	private JLabel etiquetaReservaCursillos;
	private JLabel etiquetaIDSocio;
	private JTextField textIdSocio;
	private JTextPane textoDatosCursillo;
	private JComboBox<CursilloDto> boxCursillos;
	private JButton botonSeleccionarCursillo;
	private JLabel etiquetaDatosCursillo;
	private JCheckBox checkboxListaEspera;

	public SocioReservaCursilloIU() {

		try {
			reserva = new ReservaCursillo();
		} catch (IOException | SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error al abrir la base de datos", "Error en la base de datos",
					JOptionPane.ERROR_MESSAGE);
			dispose();
			return;
		}
		setResizable(false);
		setModal(true);
		setBounds(100, 100, 440, 550);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBackground(Color.WHITE);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		contentPanel.add(getBotonSeleccionarCursillo());

		contentPanel.add(getBotonAceptar());
		contentPanel.add(getBotonCancelar());
		contentPanel.add(getTextIdSocio());
		contentPanel.add(getTextoDatosCursillo());
		contentPanel.add(getBoxCursillos());
		contentPanel.add(getCheckboxListaEspera());
		contentPanel.add(getEtiquetaReservaCursillos());
		contentPanel.add(getEtiquetaIDSocio());
		contentPanel.add(getEtiquetaDatosCursillo());
		contentPanel.add(getChckbxPagar());

	}

	private ActionListener actionSeleccionar = new ActionListener() {
		public void actionPerformed(ActionEvent actionEvent) {
			CursilloDto cursillo = (CursilloDto) boxCursillos.getSelectedItem();
			int plazasLibres = cursillo.plazas - cursillo.plazas_ocupadas;
			String mes = ComboBoxData.getMeses()[cursillo.fecha1.getMonthOfYear() - 1];
			int anio = cursillo.fecha1.getYear();
			String dia1 = ComboBoxData.getDiasSemana()[cursillo.diaSemana1];
			String dia2 = ComboBoxData.getDiasSemana()[cursillo.diaSemana2];

			String texto = "";
			texto = texto + "Cursillo seleccionado: " + cursillo.nombre;
			texto = texto + "\n" + "Con actividades el mes: " + mes + " de " + anio;
			texto = texto + "\n" + "Los " + dia1 + " y " + dia2 + " de todo el mes";
			texto = texto + "\n" + "Con horario de " + cursillo.horaInicio + ":00 a " + cursillo.horaFin + ":00";
			texto = texto + "\n\n" + "Plazas libres: " + plazasLibres + "/" + cursillo.plazas;
			textoDatosCursillo.setText(texto);
			if (plazasLibres <= 0) {
				hayPlazas = false;
				checkboxListaEspera.setEnabled(true);
				botonAceptar.setText("Apuntarse");
				botonAceptar.setEnabled(false);
				chckbxPagar.setEnabled(false);
			} else {
				hayPlazas = true;
				checkboxListaEspera.setEnabled(false);
				botonAceptar.setText("Reservar plaza");
				botonAceptar.setEnabled(true);
				chckbxPagar.setEnabled(true);
			}
		}
	};

	private ActionListener actionCheckBox = new ActionListener() {
		public void actionPerformed(ActionEvent actionEvent) {
			AbstractButton abstractButton = (AbstractButton) actionEvent.getSource();
			boolean selected = abstractButton.getModel().isSelected();
			if (selected) {
				botonAceptar.setEnabled(true);
			} else {
				botonAceptar.setEnabled(false);
			}
		}
	};

	private ActionListener actionAceptar = new ActionListener() {
		public void actionPerformed(ActionEvent actionEvent) {
			// Pedir el id de usuario
			int idSocio = 0;
			try {
				idSocio = Integer.valueOf(textIdSocio.getText());
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "El id de socio no tiene un formato correcto.", "No existe Socio",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			// Comprobar condiciones
			CursilloDto cursillo = (CursilloDto) boxCursillos.getSelectedItem();
			if (!reserva.dentroDelHorario(cursillo, true)) {
				JOptionPane.showMessageDialog(null, "El cursillo seleccionado no esta dentro del horario de reserva.",
						"Error al realizar la reserva", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (hayPlazas) {
				ArrayList<OcupacionesDto> ocupados = new ArrayList<OcupacionesDto>();

				try {
					reserva.reservar(idSocio, cursillo, ocupados, chckbxPagar.isSelected());

					//Si hay coincidencias las imprimimos
					if (!ocupados.isEmpty()) {
						TratamientoArchivos archivo = new TratamientoArchivos();
						String e = archivo.crearDocumento(ocupados, "Coincidencias encontradas al apuntarse a un cursillo");
						int confirmado = JOptionPane.showConfirmDialog(null, e, "Error al crear Cursillo", JOptionPane.YES_OPTION, JOptionPane.DEFAULT_OPTION);
						 if (JOptionPane.OK_OPTION == confirmado) {
							 archivo.abrirElUltimoArchivo();
						 }
					} 
					else {

						JOptionPane.showMessageDialog(null, "Reserva creada con exito.", "Cursillo creado",
								JOptionPane.INFORMATION_MESSAGE);
						dispose();
					}

				} catch (SQLException | IOException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "Error en la base de datos.", "Error al crear Reserva",
							JOptionPane.ERROR_MESSAGE);
				}

			}
			else {
				try {
					reserva.aniadirAListaEspera(idSocio, cursillo);
					JOptionPane.showMessageDialog(null, "Añadido a la lista de espera con exito.", "Cursillo creado",
							JOptionPane.INFORMATION_MESSAGE);
					dispose();
					
				} catch (SQLException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "Ya esta en esa lista de espera.", "Error al crear Reserva",
							JOptionPane.ERROR_MESSAGE);
				} catch (IOException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "Error en la base de datos.", "Error al crear Reserva",
							JOptionPane.ERROR_MESSAGE);
				}
			}

		}
	};

	private ActionListener actionCancelar = new ActionListener() {
		public void actionPerformed(ActionEvent actionEvent) {
			dispose();
		}
	};
	private JCheckBox chckbxPagar;

	private JComboBox<CursilloDto> getBoxCursillos() {
		if (boxCursillos == null) {
			boxCursillos = new JComboBox<CursilloDto>();
			boxCursillos.setBounds(22, 66, 387, 22);
			try {
				boxCursillos.setModel(new DefaultComboBoxModel<CursilloDto>(reserva.getCursillos()));
			} catch (SQLException | IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Error al obtener las actividades", "Error en asignacion",
						JOptionPane.ERROR_MESSAGE);
				dispose();
			}
		}
		return boxCursillos;
	}

	private JButton getBotonSeleccionarCursillo() {
		if (botonSeleccionarCursillo == null) {
			botonSeleccionarCursillo = new JButton("Seleccionar Cursillo");
			botonSeleccionarCursillo.addActionListener(actionSeleccionar);
			botonSeleccionarCursillo.setBounds(22, 101, 150, 25);
		}
		return botonSeleccionarCursillo;
	}

	private JButton getBotonAceptar() {
		if (botonAceptar == null) {
			botonAceptar = new JButton("Aceptar");
			botonAceptar.addActionListener(actionAceptar);
			botonAceptar.setBounds(191, 479, 117, 23);
			botonAceptar.setEnabled(false);
		}
		return botonAceptar;
	}

	private JButton getBotonCancelar() {
		if (botonCancelar == null) {
			botonCancelar = new JButton("Cancelar");
			botonCancelar.addActionListener(actionCancelar);
			botonCancelar.setBounds(320, 479, 89, 23);
		}
		return botonCancelar;
	}

	private JLabel getEtiquetaReservaCursillos() {
		if (etiquetaReservaCursillos == null) {
			etiquetaReservaCursillos = new JLabel("Reserva de Cursillos");
			etiquetaReservaCursillos.setHorizontalAlignment(SwingConstants.CENTER);
			etiquetaReservaCursillos.setFont(new Font("Tahoma", Font.BOLD, 24));
			etiquetaReservaCursillos.setBounds(22, 13, 312, 39);
		}
		return etiquetaReservaCursillos;
	}

	private JLabel getEtiquetaIDSocio() {
		if (etiquetaIDSocio == null) {
			etiquetaIDSocio = new JLabel("ID del Socio:");
			etiquetaIDSocio.setBounds(22, 405, 150, 16);
		}
		return etiquetaIDSocio;
	}

	private JLabel getEtiquetaDatosCursillo() {
		if (etiquetaDatosCursillo == null) {
			etiquetaDatosCursillo = new JLabel("Datos del cursillo seleccionado:");
			etiquetaDatosCursillo.setBounds(22, 139, 312, 16);
		}
		return etiquetaDatosCursillo;
	}

	private JTextField getTextIdSocio() {
		if (textIdSocio == null) {
			textIdSocio = new JTextField();
			textIdSocio.setBounds(22, 434, 150, 22);
			textIdSocio.setColumns(10);
		}
		return textIdSocio;
	}

	private JTextPane getTextoDatosCursillo() {
		if (textoDatosCursillo == null) {
			textoDatosCursillo = new JTextPane();
			textoDatosCursillo.setBackground(SystemColor.menu);
			textoDatosCursillo.setEditable(false);
			textoDatosCursillo.setBounds(22, 164, 387, 198);
		}
		return textoDatosCursillo;
	}

	private JCheckBox getCheckboxListaEspera() {
		if (checkboxListaEspera == null) {
			checkboxListaEspera = new JCheckBox("Deseo apuntarme a la lista de espera");
			checkboxListaEspera.setBackground(SystemColor.window);
			checkboxListaEspera.setEnabled(false);
			checkboxListaEspera.addActionListener(actionCheckBox);
			checkboxListaEspera.setBounds(22, 371, 312, 25);
		}
		return checkboxListaEspera;
	}
	private JCheckBox getChckbxPagar() {
		if (chckbxPagar == null) {
			chckbxPagar = new JCheckBox("Pagar en metalico");
			chckbxPagar.setSelected(true);
			chckbxPagar.setBounds(22, 479, 163, 23);
		}
		return chckbxPagar;
	}
}
