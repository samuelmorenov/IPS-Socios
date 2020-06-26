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

public class ListaEspera extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JLabel lblListaDeEspera;
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

	/**
	 * Create the dialog.
	 */
	public ListaEspera() {
		modeloCursillos = new DefaultListModel();
		setResizable(false);
		setModal(true);
		setBounds(100, 100, 400, 500);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBackground(Color.WHITE);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		contentPanel.add(getLblListaDeEspera());
		contentPanel.add(getBtnCancelar());
		contentPanel.add(getScrollPane());
		contentPanel.add(getCmbxSocio());
		contentPanel.add(getLblCursillo());
		contentPanel.add(getBtnBuscarLista());
		CursilloDto var = (CursilloDto)cmbxCursillo.getSelectedItem();
		cargarReservas(Long.valueOf(var.id_cursillo));
	}

	private void cargarReservas(Long cursilloId) {
		modeloCursillos.clear();
		List<UsuarioDto> list = le.processRs(DatabaseLogic.getListaEspera(cursilloId));
		for (UsuarioDto user :  list) {
			if (!modeloCursillos.contains(user)) {
				modeloCursillos.addElement(user);
			}
		}
	}

	private JLabel getLblListaDeEspera() {
		if (lblListaDeEspera == null) {
			lblListaDeEspera = new JLabel("Lista de Espera");
			lblListaDeEspera.setHorizontalAlignment(SwingConstants.CENTER);
			lblListaDeEspera.setFont(new Font("Tahoma", Font.BOLD, 24));
			lblListaDeEspera.setBounds(10, 11, 374, 45);
		}
		return lblListaDeEspera;
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
			btnCancelar.setBounds(241, 437, 89, 23);
		}
		return btnCancelar;
	}

	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setBounds(10, 108, 374, 318);
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

	private JComboBox getCmbxSocio() {
		if (cmbxCursillo == null) {
			cmbxCursillo = new JComboBox();
			cmbxCursillo.setFont(new Font("Tahoma", Font.PLAIN, 13));
			cmbxCursillo.setModel(new DefaultComboBoxModel(DatabaseLogic.getListaCursillos()));
			cmbxCursillo.setBounds(77, 67, 307, 30);
		}
		return cmbxCursillo;
	}

	private JLabel getLblCursillo() {
		if (lblCursillo == null) {
			lblCursillo = new JLabel("Cursillo:");
			lblCursillo.setFont(new Font("Tahoma", Font.PLAIN, 15));
			lblCursillo.setHorizontalAlignment(SwingConstants.CENTER);
			lblCursillo.setBounds(10, 66, 74, 30);
		}
		return lblCursillo;
	}

	private JButton getBtnBuscarLista() {
		if (btnBuscarLista == null) {
			btnBuscarLista = new JButton("Buscar Lista");
			btnBuscarLista.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					modeloCursillos.clear();
					CursilloDto var = (CursilloDto)cmbxCursillo.getSelectedItem();
					cargarReservas(Long.valueOf(var.id_cursillo));
				}
			});
			btnBuscarLista.setFont(new Font("Tahoma", Font.PLAIN, 13));
			btnBuscarLista.setBounds(36, 437, 144, 23);
		}
		return btnBuscarLista;
	}
}
