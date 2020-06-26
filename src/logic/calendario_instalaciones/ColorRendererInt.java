package logic.calendario_instalaciones;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import java.awt.Color;
import java.awt.Component;


public class ColorRendererInt extends JLabel implements TableCellRenderer  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ColorRendererInt() {
		setOpaque(true); 
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row, int column) {

		String data = "";
		if(table.getValueAt(row, column) != null)
			data = table.getValueAt(row, column).toString();
		if (data.equals(""))
		{
			setBackground(Color.GREEN);
			setText(data);
		}
		else
		{
			setBackground(Color.RED);
			setText(data);
		}
		return this;
	}
}
