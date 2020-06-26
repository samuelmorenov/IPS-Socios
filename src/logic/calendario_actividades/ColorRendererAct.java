package logic.calendario_actividades;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import java.awt.Color;
import java.awt.Component;


public class ColorRendererAct extends JLabel implements TableCellRenderer  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ColorRendererAct() {
		setOpaque(true); 
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row, int column) {

		String data = "";
		String comp = " - Plazas: 0";
		if(table.getValueAt(row, column) != null)
			data = table.getValueAt(row, column).toString();
		if (data.equals(""))
		{
			setBackground(Color.GREEN);
			setText(data);
		}
		else if (data.toLowerCase().contains(comp.toLowerCase()))
		{
			setBackground(Color.ORANGE);
			setText(data);
		}
		else
		{
			setBackground(Color.CYAN);
			setText(data);
		}
		return this;
	}
}
