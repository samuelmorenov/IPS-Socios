package igu;

import java.awt.BorderLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import db.DatabaseLogic;
import logic.dto.ActividadDto;

import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ListaActividades extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JLabel lblListaDeActividades;
	private JButton btnCancelar;
	private JButton btnVerInformacion;
	private JScrollPane scrollPane;
	private JList listaActividades;
	private DefaultListModel modeloActividades;
	private JPanel panel;
	private JButton btnEliminar;

	/**
	 * Create the dialog.
	 */
	public ListaActividades() {
		modeloActividades = new DefaultListModel();
		cargarActividades();
		setResizable(false);
		setModal(true);
		setBounds(100, 100, 598, 431);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBackground(Color.WHITE);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		contentPanel.add(getLblListaDeActividades());
		contentPanel.add(getBtnCancelar());
		contentPanel.add(getBtnVerInformacion());
		contentPanel.add(getScrollPane());
		contentPanel.add(getBtnEliminar());
	}
	
	private void cargarActividades() {
		try {
			for (ActividadDto actividad : DatabaseLogic.getActividades()) {
				if (!modeloActividades.contains(actividad))
					modeloActividades.addElement(actividad);
			}
		} catch (SQLException e1) {
			JOptionPane.showMessageDialog(null, "Error en la conexion", "Error conexion",
					JOptionPane.ERROR_MESSAGE);
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(null, "Error en la configuracion", "Error configuracion",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private JLabel getLblListaDeActividades() {
		if (lblListaDeActividades == null) {
			lblListaDeActividades = new JLabel("Lista de Actividades");
			lblListaDeActividades.setHorizontalAlignment(SwingConstants.CENTER);
			lblListaDeActividades.setFont(new Font("Tahoma", Font.BOLD, 24));
			lblListaDeActividades.setBounds(20, 11, 562, 45);
		}
		return lblListaDeActividades;
	}
	private JButton getBtnCancelar() {
		if (btnCancelar == null) {
			btnCancelar = new JButton("Volver");
			btnCancelar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
			btnCancelar.setBounds(493, 368, 89, 23);
		}
		return btnCancelar;
	}
	private JButton getBtnVerInformacion() {
		if (btnVerInformacion == null) {
			btnVerInformacion = new JButton("Ver informacion");
			btnVerInformacion.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					InfoActividad infoWindow = new InfoActividad((ActividadDto) modeloActividades.getElementAt(getListaActividades().getSelectedIndex()));
					infoWindow.setVisible(true);
				}
			});
			btnVerInformacion.setBounds(352, 368, 131, 23);
		}
		return btnVerInformacion;
	}
	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setBounds(20, 67, 562, 290);
			scrollPane.setViewportView(getPanel());
		}
		return scrollPane;
	}
	private JList getListaActividades() {
		if (listaActividades == null) {
			listaActividades = new JList();
			listaActividades.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			listaActividades.setModel(modeloActividades);
		}
		return listaActividades;
	}
	private JPanel getPanel() {
		if (panel == null) {
			panel = new JPanel();
			panel.setLayout(new BorderLayout(0, 0));
			panel.add(getListaActividades(), BorderLayout.CENTER);
		}
		return panel;
	}
	private JButton getBtnEliminar() {
		if (btnEliminar == null) {
			btnEliminar = new JButton("Eliminar");
			btnEliminar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					try {
						DatabaseLogic.eliminarAsignacion((ActividadDto) getListaActividades().getSelectedValue());
						JOptionPane.showMessageDialog(null, "Actividad eliminada", "",
								JOptionPane.DEFAULT_OPTION);
						resetList();
					} catch (SQLException e1) {
						JOptionPane.showMessageDialog(null, "Error en la conexion", "Error conexion",
								JOptionPane.ERROR_MESSAGE);
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(null, "Error en la configuracion", "Error configuracion",
								JOptionPane.ERROR_MESSAGE);
					} catch (NullPointerException e1) {
						JOptionPane.showMessageDialog(null, "Actividad no seleccionada", "Actividad no seleccionada",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			});
			btnEliminar.setBounds(253, 368, 89, 23);
		}
		return btnEliminar;
	}
	
	private void resetList() {
		modeloActividades.removeAllElements();
		cargarActividades();
	}
}
