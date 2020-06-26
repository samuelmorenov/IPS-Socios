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
import logic.dto.UsuarioDto;
import logic.listado_cursillo.LogicaCursillo;
import logic.listado_reservas.LogicaReservas;

import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import javax.swing.border.TitledBorder;
import javax.swing.JCheckBox;
import javax.swing.ButtonGroup;

public class ListaReservasCursillo extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JLabel lblListaDeCursillos;
	private JButton btnCancelar;
	private JScrollPane scrollPane;
	private JList listaReservas;
	private DefaultListModel modeloActividades;
	private JPanel panel;
	private JComboBox cmbxSocio;
	private JLabel lblSocio;
	private LogicaCursillo lc = new LogicaCursillo();
	private JButton btnBuscarCursillos;
	private JPanel panel_1;
	private JCheckBox chckbxPasadas;
	private JCheckBox chckbxFuturas;
	private JCheckBox chckbxAnuladas;
	private JCheckBox chckbxTodas;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JButton btnCancelarInscripcion;

	/**
	 * Create the dialog.
	 */
	public ListaReservasCursillo() {
		modeloActividades = new DefaultListModel();
		setResizable(false);
		setModal(true);
		setBounds(100, 100, 631, 335);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBackground(Color.WHITE);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		contentPanel.add(getLblListaDeCursillos());
		contentPanel.add(getBtnCancelar());
		contentPanel.add(getScrollPane());
		contentPanel.add(getCmbxSocio());
		contentPanel.add(getLblSocio());
		contentPanel.add(getBtnBuscarCursillos());
		contentPanel.add(getPanel_1());
		contentPanel.add(getBtnCancelarInscripcion());
		cargarReservas(Long.valueOf(cmbxSocio.getSelectedItem().toString()));
	}

	private void cargarReservas(Long socio_id) {
		modeloActividades.clear();
		boolean pasadas = chckbxPasadas.isSelected();
		boolean futuras = chckbxFuturas.isSelected();
		boolean anuladas = chckbxAnuladas.isSelected();
		boolean todas = chckbxTodas.isSelected();
		List<CursilloDto> list = lc.processRs(DatabaseLogic.getCursilloFromSocio(socio_id));
		list = lc.checkBoolean(list, pasadas, futuras, anuladas, todas);
		for (CursilloDto aux : list) {
			if (!modeloActividades.contains(aux)) {
				modeloActividades.addElement(aux);
			}
		}
	}

	private JLabel getLblListaDeCursillos() {
		if (lblListaDeCursillos == null) {
			lblListaDeCursillos = new JLabel("Lista de Cursillos");
			lblListaDeCursillos.setHorizontalAlignment(SwingConstants.CENTER);
			lblListaDeCursillos.setFont(new Font("Tahoma", Font.BOLD, 24));
			lblListaDeCursillos.setBounds(10, 11, 424, 45);
		}
		return lblListaDeCursillos;
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
			btnCancelar.setBounds(451, 272, 89, 23);
		}
		return btnCancelar;
	}

	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setBounds(10, 104, 589, 157);
			scrollPane.setViewportView(getPanel());
		}
		return scrollPane;
	}

	private JList getListaReservas() {
		if (listaReservas == null) {
			listaReservas = new JList();
			listaReservas.setFont(new Font("Tahoma", Font.PLAIN, 13));
			listaReservas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			listaReservas.setModel(modeloActividades);
		}
		return listaReservas;
	}

	private JPanel getPanel() {
		if (panel == null) {
			panel = new JPanel();
			panel.setLayout(new BorderLayout(0, 0));
			panel.add(getListaReservas(), BorderLayout.CENTER);
		}
		return panel;
	}

	private JComboBox getCmbxSocio() {
		if (cmbxSocio == null) {
			cmbxSocio = new JComboBox();
			cmbxSocio.setFont(new Font("Tahoma", Font.PLAIN, 13));
			cmbxSocio.setModel(new DefaultComboBoxModel(DatabaseLogic.getNumerosSocios()));
			cmbxSocio.setBounds(77, 67, 184, 30);
		}
		return cmbxSocio;
	}

	private JLabel getLblSocio() {
		if (lblSocio == null) {
			lblSocio = new JLabel("Socio:");
			lblSocio.setFont(new Font("Tahoma", Font.PLAIN, 15));
			lblSocio.setHorizontalAlignment(SwingConstants.CENTER);
			lblSocio.setBounds(10, 66, 74, 30);
		}
		return lblSocio;
	}

	private JButton getBtnBuscarCursillos() {
		if (btnBuscarCursillos == null) {
			btnBuscarCursillos = new JButton("Buscar Cursillos");
			btnBuscarCursillos.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					modeloActividades.clear();
					cargarReservas(Long.valueOf(cmbxSocio.getSelectedItem().toString()));
				}
			});
			btnBuscarCursillos.setFont(new Font("Tahoma", Font.PLAIN, 13));
			btnBuscarCursillos.setBounds(97, 272, 144, 23);
		}
		return btnBuscarCursillos;
	}

	private JPanel getPanel_1() {
		if (panel_1 == null) {
			panel_1 = new JPanel();
			panel_1.setBorder(new TitledBorder(null, "Filtros", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			panel_1.setBounds(271, 52, 328, 45);
			panel_1.setLayout(null);
			panel_1.add(getChckbxPasadas());
			panel_1.add(getChckbxFuturas());
			panel_1.add(getChckbxAnuladas());
			panel_1.add(getChckbxTodas());
		}
		return panel_1;
	}

	private JCheckBox getChckbxPasadas() {
		if (chckbxPasadas == null) {
			chckbxPasadas = new JCheckBox("Pasadas");
			buttonGroup.add(chckbxPasadas);
			chckbxPasadas.setBounds(6, 15, 83, 23);
		}
		return chckbxPasadas;
	}

	private JCheckBox getChckbxFuturas() {
		if (chckbxFuturas == null) {
			chckbxFuturas = new JCheckBox("Proximas");
			buttonGroup.add(chckbxFuturas);
			chckbxFuturas.setBounds(91, 15, 81, 23);
		}
		return chckbxFuturas;
	}

	private JCheckBox getChckbxAnuladas() {
		if (chckbxAnuladas == null) {
			chckbxAnuladas = new JCheckBox("Anuladas");
			buttonGroup.add(chckbxAnuladas);
			chckbxAnuladas.setBounds(174, 15, 81, 23);
		}
		return chckbxAnuladas;
	}

	private JCheckBox getChckbxTodas() {
		if (chckbxTodas == null) {
			chckbxTodas = new JCheckBox("Todas");
			chckbxTodas.setSelected(true);
			buttonGroup.add(chckbxTodas);
			chckbxTodas.setBounds(257, 15, 65, 23);
		}
		return chckbxTodas;
	}

	private JButton getBtnCancelarInscripcion() {
		if (btnCancelarInscripcion == null) {
			btnCancelarInscripcion = new JButton("Cancelar inscripcion");
			btnCancelarInscripcion.setBounds(253, 271, 184, 25);
			btnCancelarInscripcion.addActionListener(actionCancelarInscripcion);
		}
		return btnCancelarInscripcion;
	}

	private ActionListener actionCancelarInscripcion = new ActionListener() {
		public void actionPerformed(ActionEvent actionEvent) {

			long socio = (Long.valueOf(cmbxSocio.getSelectedItem().toString()));
			CursilloDto cursillo = (CursilloDto) listaReservas.getSelectedValue();
			try {
				lc.deleteReserva(socio, cursillo);
				cargarReservas(Long.valueOf(cursillo.id_cursillo));
				String txt = "Eliminada la reserva del socio "+socio+" para el cursillo:\n"+cursillo;
				JOptionPane.showMessageDialog(null, txt, "Reserva realizada", JOptionPane.INFORMATION_MESSAGE);
				
			} catch (RuntimeException r) {
				JOptionPane.showMessageDialog(null, "Ya ha comenzado el cursillo", "Error en el borrado",
						JOptionPane.WARNING_MESSAGE);
			}
		}
	};

}
