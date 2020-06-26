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
import logic.dto.UsuarioDto;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JComboBox;
import java.awt.SystemColor;
import javax.swing.JCheckBox;

public class AdminReservaCursilloIU extends JDialog {

	private ReservaCursillo reserva;
	private boolean hayPlazas = true;
	private boolean comprobado = false;

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

	private JPanel panelSocio;
	private JPanel panelNoSocio;
	private JCheckBox checkboxSocio;
	private JLabel etiquetaNombreNoSocio;
	private JTextField textNombreNoSocio;
	private JLabel etiquetaDNI;
	private JTextField textDNI;
	private JLabel etiquetaTelefono;
	private JTextField textTelefono;
	private JButton botonComprobarElDni;

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
			txtCobro.setText(Double.toString(cursillo.precio));
		}
	};

	private ActionListener actionCheckBoxEspera = new ActionListener() {
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
	private ActionListener actionCheckBoxSocio = new ActionListener() {
		public void actionPerformed(ActionEvent actionEvent) {
			AbstractButton abstractButton = (AbstractButton) actionEvent.getSource();
			boolean selected = abstractButton.getModel().isSelected();
			if (selected) {
				setSocio(true);
			} else {
				setSocio(false);
			}
		}
	};
	private ActionListener actionAceptar = new ActionListener() {
		public void actionPerformed(ActionEvent actionEvent) {
			// Comprobar condiciones
			CursilloDto cursillo = (CursilloDto) boxCursillos.getSelectedItem();
			if (!reserva.dentroDelHorario(cursillo, checkboxSocio.isSelected())) {
				JOptionPane.showMessageDialog(null, "El cursillo seleccionado no esta dentro del horario de reserva.",
						"Error al realizar la reserva", JOptionPane.ERROR_MESSAGE);
				return;
			}
			try{
				cursillo.precio=Double.valueOf(txtCobro.getText());
				if(cursillo.precio < 0) {
					throw new Exception();
				}
			}
			catch(Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "El precio establecido es incorrecto",
						"No existe Socio", JOptionPane.ERROR_MESSAGE);
				return;
			}

			// Pedir el id de usuario
			int idUsuario = 0;
			if (checkboxSocio.isSelected()) {

				try {
					idUsuario = Integer.valueOf(textIdSocio.getText());
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "El id de socio no tiene un formato correcto.",
							"No existe Socio", JOptionPane.ERROR_MESSAGE);
					return;
				}
			} else {
				String nombre = textNombreNoSocio.getText();
				String dni = textDNI.getText();
				String telefono = textTelefono.getText();
				if (nombre.length() < 3) {
					JOptionPane.showMessageDialog(null, "Nombre demasiado corto", "Error al realizar reserva",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (dni.length() < 6) {
					JOptionPane.showMessageDialog(null, "DNI demasiado corto", "Error al realizar reserva",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (telefono.length() < 9) {
					JOptionPane.showMessageDialog(null, "Telefono demasiado corto", "Error al realizar reserva",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				try {
					idUsuario = reserva.insertarNoSocio(nombre, dni, telefono);
				} catch (SQLException | IOException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "Error en la base de datos.", "Error al crear Reserva",
							JOptionPane.ERROR_MESSAGE);
				}

			}

			//Si hay 
			
			if (hayPlazas) {
				ArrayList<OcupacionesDto> ocupados = new ArrayList<OcupacionesDto>();

				try {
					reserva.reservar(idUsuario, cursillo, ocupados, chckbxPagar.isSelected()); //TODO

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

			} else {
				try {
					reserva.aniadirAListaEspera(idUsuario, cursillo);
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

	private ActionListener actionComprobar = new ActionListener() {
		public void actionPerformed(ActionEvent actionEvent) {

			try {
				// System.out.println("Comprobado"); //TODO
				String dni = textDNI.getText();
				UsuarioDto noSocio;
				noSocio = reserva.getNoSocio(dni);

				if (noSocio != null) {
					etiquetaAux.setText("El usuario ya existe");
					textDNI.setText(noSocio.dni);
					textNombreNoSocio.setText(noSocio.nombre);
					textTelefono.setText(noSocio.telefono);

				} else {
					etiquetaAux.setText("El usuario no existe");

				}
				comprobado = true;
				setSocio(false);
			} catch (SQLException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Error en la base de datos.", "Error al crear Reserva",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	};
	private JLabel etiquetaAux;
	private JCheckBox chckbxPagar;
	private JTextField txtCobro;
	private JLabel lblCobroARealizar;

	public AdminReservaCursilloIU() {

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
		setBounds(100, 100, 459, 715);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBackground(Color.WHITE);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		contentPanel.add(getBotonSeleccionarCursillo());

		contentPanel.add(getBotonAceptar());
		contentPanel.add(getBotonCancelar());
		contentPanel.add(getTextoDatosCursillo());
		contentPanel.add(getBoxCursillos());
		contentPanel.add(getCheckboxListaEspera());
		contentPanel.add(getEtiquetaReservaCursillos());
		contentPanel.add(getEtiquetaDatosCursillo());
		contentPanel.add(getPanelSocio());
		contentPanel.add(getPanelNoSocio());
		contentPanel.add(getCheckboxSocio());
		contentPanel.add(getEtiquetaAux());
		contentPanel.add(getTxtCobro());
		contentPanel.add(getLblCobroARealizar());
		setSocio(true);

	}

	private JCheckBox getCheckboxListaEspera() {
		if (checkboxListaEspera == null) {
			checkboxListaEspera = new JCheckBox("Deseo apuntarme a la lista de espera");
			checkboxListaEspera.setBackground(SystemColor.window);
			checkboxListaEspera.setEnabled(false);
			checkboxListaEspera.setSelected(false);
			checkboxListaEspera.addActionListener(actionCheckBoxEspera);
			checkboxListaEspera.setBounds(22, 318, 312, 25);
		}
		return checkboxListaEspera;
	}

	private JCheckBox getCheckboxSocio() {
		if (checkboxSocio == null) {
			checkboxSocio = new JCheckBox("Socio?");
			checkboxSocio.setSelected(true);
			checkboxSocio.addActionListener(actionCheckBoxSocio);
			checkboxSocio.setBounds(22, 343, 150, 25);
		}
		return checkboxSocio;
	}

	private JComboBox<CursilloDto> getBoxCursillos() {
		if (boxCursillos == null) {
			boxCursillos = new JComboBox<CursilloDto>();
			boxCursillos.setBounds(22, 66, 410, 22);
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

	private JButton getBotonComprobarElDni() {
		if (botonComprobarElDni == null) {
			botonComprobarElDni = new JButton("Comprobar el DNI");
			botonComprobarElDni.addActionListener(actionComprobar);
			botonComprobarElDni.setBounds(15, 77, 150, 25);
		}
		return botonComprobarElDni;
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
			botonAceptar.setBounds(181, 644, 150, 23);
			botonAceptar.setEnabled(false);
		}
		return botonAceptar;
	}

	private JButton getBotonCancelar() {
		if (botonCancelar == null) {
			botonCancelar = new JButton("Cancelar");
			botonCancelar.addActionListener(actionCancelar);
			botonCancelar.setBounds(343, 644, 89, 23);
		}
		return botonCancelar;
	}

	private JLabel getEtiquetaReservaCursillos() {
		if (etiquetaReservaCursillos == null) {
			etiquetaReservaCursillos = new JLabel("Reserva de Cursillos");
			etiquetaReservaCursillos.setHorizontalAlignment(SwingConstants.CENTER);
			etiquetaReservaCursillos.setFont(new Font("Tahoma", Font.BOLD, 24));
			etiquetaReservaCursillos.setBounds(22, 13, 410, 39);
		}
		return etiquetaReservaCursillos;
	}

	private JLabel getEtiquetaIDSocio() {
		if (etiquetaIDSocio == null) {
			etiquetaIDSocio = new JLabel("ID del Socio:");
			etiquetaIDSocio.setBounds(12, 13, 150, 16);
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
			textIdSocio.setBounds(12, 42, 150, 22);
			textIdSocio.setColumns(10);
		}
		return textIdSocio;
	}

	private JTextPane getTextoDatosCursillo() {
		if (textoDatosCursillo == null) {
			textoDatosCursillo = new JTextPane();
			textoDatosCursillo.setBackground(SystemColor.menu);
			textoDatosCursillo.setEditable(false);
			textoDatosCursillo.setBounds(22, 164, 410, 145);
		}
		return textoDatosCursillo;
	}

	private JPanel getPanelSocio() {
		if (panelSocio == null) {
			panelSocio = new JPanel();
			panelSocio.setBounds(22, 377, 186, 127);
			panelSocio.setLayout(null);
			panelSocio.add(getEtiquetaIDSocio());
			panelSocio.add(getTextIdSocio());
			panelSocio.add(getChckbxPagar());
		}
		return panelSocio;
	}

	private JPanel getPanelNoSocio() {
		if (panelNoSocio == null) {
			panelNoSocio = new JPanel();
			panelNoSocio.setBounds(246, 377, 186, 243);
			panelNoSocio.setLayout(null);
			panelNoSocio.add(getEtiquetaNombreNoSocio());
			panelNoSocio.add(getTextNombreNoSocio());
			panelNoSocio.add(getEtiquetaDNI());
			panelNoSocio.add(getTextDNI());
			panelNoSocio.add(getEtiquetaTelefono());
			panelNoSocio.add(getTextTelefono());
			panelNoSocio.add(getBotonComprobarElDni());
		}
		return panelNoSocio;
	}

	private JLabel getEtiquetaNombreNoSocio() {
		if (etiquetaNombreNoSocio == null) {
			etiquetaNombreNoSocio = new JLabel("Nombre del no socio:");
			etiquetaNombreNoSocio.setBounds(15, 115, 150, 16);
		}
		return etiquetaNombreNoSocio;
	}

	private JTextField getTextNombreNoSocio() {
		if (textNombreNoSocio == null) {
			textNombreNoSocio = new JTextField();
			textNombreNoSocio.setBounds(15, 144, 150, 22);
			textNombreNoSocio.setColumns(10);
		}
		return textNombreNoSocio;
	}

	private JLabel getEtiquetaDNI() {
		if (etiquetaDNI == null) {
			etiquetaDNI = new JLabel("DNI:");
			etiquetaDNI.setBounds(15, 13, 150, 16);
		}
		return etiquetaDNI;
	}

	private JTextField getTextDNI() {
		if (textDNI == null) {
			textDNI = new JTextField();
			textDNI.setBounds(15, 42, 150, 22);
			textDNI.setColumns(10);
		}
		return textDNI;
	}

	private JLabel getEtiquetaTelefono() {
		if (etiquetaTelefono == null) {
			etiquetaTelefono = new JLabel("Telefono:");
			etiquetaTelefono.setBounds(15, 179, 150, 16);
		}
		return etiquetaTelefono;
	}

	private JTextField getTextTelefono() {
		if (textTelefono == null) {
			textTelefono = new JTextField();
			textTelefono.setBounds(15, 208, 150, 22);
			textTelefono.setColumns(10);
		}
		return textTelefono;
	}

	private void setSocio(boolean socio) {

		etiquetaIDSocio.setEnabled(socio);
		textIdSocio.setEnabled(socio);

		etiquetaDNI.setEnabled(!socio);
		textDNI.setEnabled(!socio);

		botonComprobarElDni.setEnabled(!socio);
		etiquetaAux.setEnabled(!socio);

		etiquetaTelefono.setEnabled(!socio && comprobado);
		textTelefono.setEnabled(!socio && comprobado);

		etiquetaNombreNoSocio.setEnabled(!socio && comprobado);
		textNombreNoSocio.setEnabled(!socio && comprobado);
		
		chckbxPagar.setSelected(true);
		chckbxPagar.setEnabled(socio);

	}

	private JLabel getEtiquetaAux() {
		if (etiquetaAux == null) {
			etiquetaAux = new JLabel("");
			etiquetaAux.setBounds(22, 644, 150, 23);
		}
		return etiquetaAux;
	}
	private JCheckBox getChckbxPagar() {
		if (chckbxPagar == null) {
			chckbxPagar = new JCheckBox("Pagar en metalico");
			chckbxPagar.setSelected(true);
			chckbxPagar.setBounds(12, 73, 150, 23);
		}
		return chckbxPagar;
	}
	private JTextField getTxtCobro() {
		if (txtCobro == null) {
			txtCobro = new JTextField();
			txtCobro.setBounds(22, 546, 150, 22);
			txtCobro.setColumns(10);
		}
		return txtCobro;
	}
	private JLabel getLblCobroARealizar() {
		if (lblCobroARealizar == null) {
			lblCobroARealizar = new JLabel("Cobro a realizar");
			lblCobroARealizar.setBounds(22, 517, 150, 16);
		}
		return lblCobroARealizar;
	}
}
