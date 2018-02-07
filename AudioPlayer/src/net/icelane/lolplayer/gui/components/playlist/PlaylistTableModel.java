/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.icelane.lolplayer.gui.components.playlist;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import net.icelane.lolplayer.player.AudioFile;
import net.icelane.lolplayer.player.AudioPlaylist;

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
    
    public void insertData(AudioFile file){
		Object[][] ndata = new Object[data.length + 1][3];		
		
		if (data.length > 0)
			System.arraycopy(data, 0, ndata, 0, data.length);
		
		ndata[data.length] = createDataObject(data.length, file);		
		
		data = ndata; //apply changes ...
		fireTableRowsInserted(data.length - 1, data.length - 1);
    }
    
    public void removeData(int index){
    	try {
	    	if (data.length <= 0)
	    		data = new Object[1][3];
	
			Object[][] ndata = new Object[data.length - 1][3];		
			System.arraycopy(data, 0, ndata, 0, index);
			System.arraycopy(data, index+1, ndata, index, data.length - index - 1);
			data = ndata; //apply changes ...
			fireTableRowsDeleted(index, index);
			updateIndexCol(index, data.length);
    	} catch (Exception e) {}
    }
    
    public void updateIndexCol(int index, int length){
    	try {
	    	for (int i = index; i < length; i++) {
				data[i][0] = i + 1;
				fireTableCellUpdated(i, 0);
	    	}
    	} catch (Exception e) {}
    }
    
    public void updateData(int index, AudioFile file){
    	try {
        	if (index >= data.length) insertData(file);
        	
        	data[index] = createDataObject(index, file);
        	fireTableRowsUpdated(index, index);
		} catch (Exception e) {}
    }
    
    public void updateData(AudioFile file){
    	updateData(data.length, file);
    }
    
    /** 
     * Creates an data Object (actually an object array) which contains all information to display in the gui-list<br/>
     * <br/>
     * data[0] : the index in the list as int <br/>
     * data[1] : the name to display<br/>
     * data[2] : the length<br/>
     * 
     * 
     * @param index	the index in the list
     * @param af	an <code>AudioFile</code> object 
     * @return An object array (Object[]) 
     */
    public static Object[] createDataObject(int index, AudioFile af){
    	return new Object[] {index + 1
			    			, 
			    			af.isTitleEmpty() ? 
							af.getName() :
							af.isAuthorEmpty() ?
							af.getTitle() :
							String.format("%s - %s", af.getAuthor(), af.getTitle())
							,
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
