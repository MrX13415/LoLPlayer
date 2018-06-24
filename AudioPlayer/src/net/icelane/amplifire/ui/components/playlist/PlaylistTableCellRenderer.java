package net.icelane.amplifire.ui.components.playlist;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class PlaylistTableCellRenderer extends DefaultTableCellRenderer{
	 /**
	 * 
	 */
	private static final long serialVersionUID = -5478855574845832369L;

	@Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//		PlaylistTableModel model = (PlaylistTableModel) table.getModel();
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//        Color col = model.getRowColour(row);
        return c;
    }
}
