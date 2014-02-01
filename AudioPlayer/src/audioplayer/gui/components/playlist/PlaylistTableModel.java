/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package audioplayer.gui.components.playlist;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import audioplayer.player.AudioFile;
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

    private List<Color> rowColours = Arrays.asList(Color.red);

    
    public void setRowColour(int row, Color c) {
        rowColours.set(row, c);
        fireTableRowsUpdated(row, row);
    }
    
    public Color getRowColour(int row) {
        if (row < rowColours.size()) return rowColours.get(row);
        return null;
    }
    
    public void update(int index, AudioFile file){
    	if (data.length <= 0)
    		data = new Object[1][3];

    	if (index >= data.length){
    		 Object[][] ndata = new Object[data.length + 1][3];
    	        
    		for (int i = 0; i < data.length; i++) {
            	ndata[i] = data[i];
            }
    		ndata[data.length] = createDataObject(data.length, file);
    		data = ndata;
    	}else
    		data[index] = createDataObject(index, file);
    	fireTableDataChanged();
    }
    
    private Object[] createDataObject(int index, AudioFile af){
    	return new Object[] {index + 1, 
    			af.isTitleEmpty() ? 
				af.getName() :
				af.isAuthorEmpty() ?
				af.getTitle() :
				String.format("%s - %s", af.getAuthor(), af.getTitle()),
				af.getFormatedLength()};
    }
    
    public void setContent(AudioPlaylist apl){
        Object[][] ndata = new Object[apl.size()][3];
        
        for (int i = 0; i < apl.size(); i++) {
        	try {
        		ndata[i] = createDataObject(i, apl.get(i));
			} catch (Exception e) {
				System.out.println(e);
			}
        }
        
        data = ndata;
        fireTableDataChanged();
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
