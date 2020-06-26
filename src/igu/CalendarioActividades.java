package igu;

import java.awt.EventQueue;
import javax.swing.JFrame;
import com.toedter.calendar.*;

import db.DatabaseLogic;
import logic.calendario_actividades.CalendarLogicAct;
import logic.dto.ActividadDto;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Calendar;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.DefaultComboBoxModel;

public class CalendarioActividades{

	private CalendarLogicAct cl;
	
	//Lunes = 2; Domingo = 1
	private JFrame frame;
	private JPanel pnNorth;
	private JCalendar jC;
	private JPanel pnCenter;
	private JPanel pnSouth;
	private JButton btnBtnshowdate;
	private JButton btnCancel;
	private DefaultTableModel myData;
	private JTable tableFechas;
	private JScrollPane scrollPane;
	private JComboBox cbBoxSalas;

	/**
	 * Create the application.
	 */
	public CalendarioActividades() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		try {
			cl = new CalendarLogicAct();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		frame = new JFrame();
		frame.setBounds(100, 100, 1200, 550);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().add(getPnNorth(), BorderLayout.NORTH);
		frame.getContentPane().add(getPnCenter(), BorderLayout.CENTER);
		frame.getContentPane().add(getPnSouth(), BorderLayout.SOUTH);
	}

	private JPanel getPnNorth() {
		if (pnNorth == null) {
			pnNorth = new JPanel();
			pnNorth.add(addCalendar());
			pnNorth.add(getCbBoxSalas());
		}
		return pnNorth;
	}

	private Component addCalendar() {
		if( jC == null)
		{
			jC = new JCalendar();
		}
		return jC;
	}
	private JPanel getPnCenter() {
		if (pnCenter == null) {
			pnCenter = new JPanel();
			pnCenter.setLayout(new GridLayout(1, 0, 0, 0));
			pnCenter.add(getScrollPane());
		}
		return pnCenter;
	}
	private JPanel getPnSouth() {
		if (pnSouth == null) {
			pnSouth = new JPanel();
			pnSouth.add(getBtnBtnshowdate());
			pnSouth.add(getBtnCancel());
		}
		return pnSouth;
	}
	private JButton getBtnBtnshowdate() {
		if (btnBtnshowdate == null) {
			btnBtnshowdate = new JButton("Mostrar fechas");
			btnBtnshowdate.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					int day = jC.getCalendar().get(Calendar.DAY_OF_MONTH);
					int month = jC.getCalendar().get(Calendar.MONTH) + 1; //Devuelve valores entre 0 (Enero) y 11 (diciembre)
					int year = jC.getCalendar().get(Calendar.YEAR);
					int dayOfWeek = jC.getCalendar().get(Calendar.DAY_OF_WEEK);
					cl.printFollowingWeek(day, month, year, (ActividadDto) cbBoxSalas.getSelectedItem());
					refreshTable(dayOfWeek);
				}
			});
		}
		return btnBtnshowdate;
	}
	
	private void refreshTable(int dayOfWeek)
	{
		myData.getDataVector().removeAllElements();
		cl.showTable(tableFechas, dayOfWeek);
		myData.fireTableDataChanged();
	}
	
	private JButton getBtnCancel() {
		if (btnCancel == null) {
			btnCancel = new JButton("Cancelar");
			btnCancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					frame.dispose();
				}
			});
		}
		return btnCancel;
	}
	
	private JTable addTablaFechas() {
		if(tableFechas == null) {
			myData = new DefaultTableModel();
			tableFechas = new JTable(myData) {
				public boolean isCellEditable(int x, int y) {
					return false;
				}
			};
			tableFechas.getTableHeader().setReorderingAllowed(false);
		}
		return tableFechas;
	}
	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(addTablaFechas());
		}
		return scrollPane;
	}
	private JComboBox getCbBoxSalas() {
		if (cbBoxSalas == null) {
			cbBoxSalas = new JComboBox();
			cbBoxSalas.setModel(new DefaultComboBoxModel(DatabaseLogic.getNombreActividades()));
		}
		return cbBoxSalas;
	}
	
	public JFrame getFrame(){
		return frame;
	}
}
