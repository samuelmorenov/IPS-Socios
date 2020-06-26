package igu;

import java.awt.BorderLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import db.DatabaseLogic;
import logic.dto.ActividadDto;
import logic.dto.MonitorDto;
import logic.exception.MonitorOcupadoException;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.JComboBox;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class AsignarMonitor extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JLabel lblAsignarMonitor;
	private JLabel lblListaDeActividades;
	private JComboBox cbActividades;
	private JLabel lblListaDeMonitores;
	private JComboBox cbMonitores;
	private JButton btnCancelar;
	private JButton btnAsignar;
	
	public AsignarMonitor() {
		setResizable(false);
		setModal(true);
		setBounds(100, 100, 450, 300);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBackground(Color.WHITE);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		contentPanel.add(getLblAsignarMonitor());
		contentPanel.add(getLblListaDeActividades());
		contentPanel.add(getCbActividades());
		contentPanel.add(getLblListaDeMonitores());
		contentPanel.add(getCbMonitores());
		contentPanel.add(getBtnCancelar());
		contentPanel.add(getBtnAsignar());
	}
	private JLabel getLblAsignarMonitor() {
		if (lblAsignarMonitor == null) {
			lblAsignarMonitor = new JLabel("Asignar Monitor");
			lblAsignarMonitor.setFont(new Font("Tahoma", Font.BOLD, 24));
			lblAsignarMonitor.setHorizontalAlignment(SwingConstants.CENTER);
			lblAsignarMonitor.setBounds(0, 11, 434, 52);
		}
		return lblAsignarMonitor;
	}
	private JLabel getLblListaDeActividades() {
		if (lblListaDeActividades == null) {
			lblListaDeActividades = new JLabel("Lista de actividades");
			lblListaDeActividades.setBounds(20, 133, 140, 14);
		}
		return lblListaDeActividades;
	}
	private JComboBox getCbActividades() {
		if (cbActividades == null) {
			cbActividades = new JComboBox();
			cbActividades.setBounds(20, 158, 201, 20);
			try {
				cbActividades.setModel(new DefaultComboBoxModel(DatabaseLogic.getActividades().toArray()));
			} catch (SQLException e1) {
				JOptionPane.showMessageDialog(null, "Error en la conexion", "Error conexion",
						JOptionPane.ERROR_MESSAGE);
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(null, "Error en la configuracion", "Error configuracion",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		return cbActividades;
	}
	private JLabel getLblListaDeMonitores() {
		if (lblListaDeMonitores == null) {
			lblListaDeMonitores = new JLabel("Lista de monitores");
			lblListaDeMonitores.setBounds(231, 133, 203, 14);
		}
		return lblListaDeMonitores;
	}
	private JComboBox getCbMonitores() {
		if (cbMonitores == null) {
			cbMonitores = new JComboBox();
			cbMonitores.setBounds(231, 158, 173, 20);
			try {
				cbMonitores.setModel(new DefaultComboBoxModel(DatabaseLogic.getMonitores().toArray()));
			} catch (SQLException e1) {
				JOptionPane.showMessageDialog(null, "Error en la conexion", "Error conexion",
						JOptionPane.ERROR_MESSAGE);
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(null, "Error en la configuracion", "Error configuracion",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		return cbMonitores;
	}
	private JButton getBtnCancelar() {
		if (btnCancelar == null) {
			btnCancelar = new JButton("Cancelar");
			btnCancelar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					dispose();
				}
			});
			btnCancelar.setBounds(345, 237, 89, 23);
		}
		return btnCancelar;
	}
	private JButton getBtnAsignar() {
		if (btnAsignar == null) {
			btnAsignar = new JButton("Asignar");
			btnAsignar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ActividadDto actividad = (ActividadDto) getCbActividades().getSelectedItem();
					MonitorDto monitor = (MonitorDto) getCbMonitores().getSelectedItem();
					try {
						DatabaseLogic.asignarMonitor(monitor, actividad);;
						JOptionPane.showMessageDialog(null, "Monitor asignado", "",
								JOptionPane.DEFAULT_OPTION);
					} catch (SQLException e1) {
						JOptionPane.showMessageDialog(null, "Error en la conexion", "Error conexion",
								JOptionPane.ERROR_MESSAGE);
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(null, "Error en la configuracion", "Error configuracion",
								JOptionPane.ERROR_MESSAGE);
					} catch (MonitorOcupadoException e3) {
						JOptionPane.showMessageDialog(null, "Monitor ocupado", "Monitor ocupado",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			});
			btnAsignar.setBounds(231, 237, 89, 23);
		}
		return btnAsignar;
	}
}
