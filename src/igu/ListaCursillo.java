package igu;

import java.awt.BorderLayout;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import db.DatabaseLogic;
import logic.dto.ActividadDto;
import logic.dto.CursilloDto;
import logic.dto.ReservaCursilloDto;
import logic.dto.UsuarioDto;
import logic.lista_espera.LogicaListaEspera;
import logic.listado_reservas.LogicaReservas;

import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import javax.swing.border.TitledBorder;

import org.joda.time.DateTime;

import javax.swing.JCheckBox;
import javax.swing.ButtonGroup;
import javax.swing.JTextField;
import javax.swing.UIManager;

public class ListaCursillo extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JLabel lblListaDeApuntados;
	private JButton btnCancelar;
	private JScrollPane scrollPane;
	private JList listaEspera;
	private DefaultListModel modeloCursillos;
	private JPanel panel;
	private JComboBox cmbxCursillo;
	private JLabel lblCursillo;
	private JButton btnBuscarLista;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private LogicaListaEspera le = new LogicaListaEspera();
	private JButton btnDelete;
	private JButton btnCancelarCursillo;
	private JTextField txtDevolucion;
	private ListaCursillo window;

	/**
	 * Create the dialog.
	 */
	public ListaCursillo() {
		window = this;
		modeloCursillos = new DefaultListModel();
		setResizable(false);
		setModal(true);
		setBounds(100, 100, 705, 500);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBackground(Color.WHITE);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		contentPanel.add(getLblListaDeApuntados());
		contentPanel.add(getBtnCancelar());
		contentPanel.add(getScrollPane());
		contentPanel.add(getCmbxCursillo());
		contentPanel.add(getLblCursillo());
		contentPanel.add(getBtnBuscarLista());
		contentPanel.add(getBtnDelete());
		contentPanel.add(getBtnCancelarCursillo());
		contentPanel.add(getTxtDevolucion());
		CursilloDto var = (CursilloDto) cmbxCursillo.getSelectedItem();
		cargarReservas(Long.valueOf(var.id_cursillo));
	}

	private void cargarReservas(Long cursilloId) {
		modeloCursillos.clear();
		List<UsuarioDto> list = le.processRs(DatabaseLogic.getListaApuntados(cursilloId));
		for (UsuarioDto user : list) {
			if (!modeloCursillos.contains(user)) {
				modeloCursillos.addElement(user);
			}
		}
	}

	private JLabel getLblListaDeApuntados() {
		if (lblListaDeApuntados == null) {
			lblListaDeApuntados = new JLabel("Lista de Apuntados");
			lblListaDeApuntados.setHorizontalAlignment(SwingConstants.CENTER);
			lblListaDeApuntados.setFont(new Font("Tahoma", Font.BOLD, 24));
			lblListaDeApuntados.setBounds(10, 11, 374, 45);
		}
		return lblListaDeApuntados;
	}

	private JButton getBtnCancelar() {
		if (btnCancelar == null) {
			btnCancelar = new JButton("Cancelar");
			btnCancelar.setFont(new Font("Tahoma", Font.PLAIN, 13));
			btnCancelar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
			btnCancelar.setBounds(600, 437, 89, 23);
		}
		return btnCancelar;
	}

	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setBounds(10, 108, 679, 318);
			scrollPane.setViewportView(getPanel());
		}
		return scrollPane;
	}

	private JList getListaEspera() {
		if (listaEspera == null) {
			listaEspera = new JList();
			listaEspera.setFont(new Font("Tahoma", Font.PLAIN, 13));
			listaEspera.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			listaEspera.setModel(modeloCursillos);
		}
		return listaEspera;
	}

	private JPanel getPanel() {
		if (panel == null) {
			panel = new JPanel();
			panel.setLayout(new BorderLayout(0, 0));
			panel.add(getListaEspera(), BorderLayout.CENTER);
		}
		return panel;
	}

	private JComboBox getCmbxCursillo() {
		if (cmbxCursillo == null) {
			cmbxCursillo = new JComboBox();
			cmbxCursillo.setFont(new Font("Tahoma", Font.PLAIN, 13));
			cmbxCursillo.setModel(new DefaultComboBoxModel(DatabaseLogic.getListaCursillos()));
			cmbxCursillo.setBounds(77, 60, 307, 37);
		}
		return cmbxCursillo;
	}

	private JLabel getLblCursillo() {
		if (lblCursillo == null) {
			lblCursillo = new JLabel("Cursillo:");
			lblCursillo.setFont(new Font("Tahoma", Font.PLAIN, 15));
			lblCursillo.setHorizontalAlignment(SwingConstants.CENTER);
			lblCursillo.setBounds(10, 59, 74, 38);
		}
		return lblCursillo;
	}

	private JButton getBtnBuscarLista() {
		if (btnBuscarLista == null) {
			btnBuscarLista = new JButton("Buscar Lista");
			btnBuscarLista.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					modeloCursillos.clear();
					CursilloDto var = (CursilloDto) cmbxCursillo.getSelectedItem();
					cargarReservas(Long.valueOf(var.id_cursillo));
				}
			});
			btnBuscarLista.setFont(new Font("Tahoma", Font.PLAIN, 13));
			btnBuscarLista.setBounds(315, 437, 144, 23);
		}
		return btnBuscarLista;
	}

	private JButton getBtnDelete() {
		if (btnDelete == null) {
			btnDelete = new JButton("Desapuntar");
			btnDelete.setFont(new Font("Tahoma", Font.PLAIN, 13));
			btnDelete.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					UsuarioDto seleccionado = (UsuarioDto) listaEspera.getSelectedValue();
					CursilloDto cursillo = (CursilloDto) cmbxCursillo.getSelectedItem();
					if (seleccionado == null) {
						JOptionPane.showMessageDialog(null, "Ningun usuario seleccionado", "Error en seleccion",
								JOptionPane.WARNING_MESSAGE);
					} else {
						Long idsocio = Long.valueOf(seleccionado.id_usuario);
						try {
							le.deleteReserva(cursillo, idsocio);
							cargarReservas(Long.valueOf(cursillo.id_cursillo));
						} catch (RuntimeException r) {
							JOptionPane.showMessageDialog(null, "Ya ha comenzado el cursillo",
									"Error en el borrado", JOptionPane.WARNING_MESSAGE);
						}
					}
				}
			});
			btnDelete.setBounds(467, 437, 123, 23);
		}
		return btnDelete;
	}
	private JButton getBtnCancelarCursillo() {
		if (btnCancelarCursillo == null) {
			btnCancelarCursillo = new JButton("Cancelar Cursillo");
			btnCancelarCursillo.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					CursilloDto cursillo = (CursilloDto) cmbxCursillo.getSelectedItem();
					if (cursillo == null) {
						JOptionPane.showMessageDialog(null, "Ningun cursillo seleccionado", "Error en seleccion",
								JOptionPane.WARNING_MESSAGE);
					} else {
						le.deleteCursillo(cursillo, DateTime.now(), Integer.valueOf(txtDevolucion.getText())*(cursillo.precio / 100) );
						window.dispose();
					}
				}
			});
			btnCancelarCursillo.setBounds(550, 61, 139, 37);
		}
		return btnCancelarCursillo;
	}
	private JTextField getTxtDevolucion() {
		if (txtDevolucion == null) {
			txtDevolucion = new JTextField();
			txtDevolucion.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Devolucion(%)", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
			txtDevolucion.setText("0");
			txtDevolucion.setBounds(397, 60, 143, 37);
			txtDevolucion.setColumns(10);
		}
		return txtDevolucion;
	}
}
