/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package audioplayer.gui.components.playlist;

import audioplayer.player.AudioPlaylist;
import javax.swing.table.AbstractTableModel;

/**
 *  LoLPlayer II - Audio-Player Project
 * 
 * @author Oliver Daus
 * 
 */
public class PlaylistTableModel extends AbstractTableModel{

    /**
	 * 
	 */
	private static final long serialVersionUID = 5029454889039282590L;
	private String[] columnNames = {" No.", " Title", " Length"};
    private Object[][] data = {};

    public void setContent(AudioPlaylist apl){
        Object[][] ndata = new Object[apl.size()][3];
        
        for (int i = 0; i < apl.size(); i++) {
            ndata[i] = new Object[] {i + 1, 
            		apl.get(i).getAuthor().equals("Unknow") ? 
    				apl.get(i).getTitle() :
    				String.format("%s - %s", apl.get(i).getAuthor(), apl.get(i).getTitle()),
    				apl.get(i).getFormatedLength()};
        }
        
        data = ndata;
        fireTableDataChanged();
    }
    
    public void add(){
        
    }
            
    public void remove(){
        
    }
            
    @Override
    public int getRowCount() {
        return data.length;
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    
    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
        return data[row][col];
    }
    
}
