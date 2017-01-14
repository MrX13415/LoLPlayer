package audioplayer.gui.components.frame;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

import javax.swing.JPanel;

public class TitleFramePane extends JPanel{

    /**
	 * 
	 */
	private static final long serialVersionUID = -8026557460138809730L;
	
	private JPanel titleFrame;
    private JPanel mainPane;
    
    private Insets invisborder = new Insets(5,5,5,5);
    private Insets frameBorder = new Insets(20,5,0,0);
    private Dimension frameBorderSize = new Dimension(400 - 30 + 5, 400 - 30 + 20);
    
	public TitleFramePane(JPanel tF, JPanel mP) {
		this.titleFrame = tF;  
		this.mainPane = mP;
	    
		this.add(titleFrame);
		this.add(mainPane);
		this.setComponentZOrder(titleFrame, 1);
		this.setComponentZOrder(mainPane, 0);    
		
		this.setLayout(new LayoutManager() {
				
			@Override
			public void removeLayoutComponent(Component arg0) {}
	
			@Override
			public void addLayoutComponent(String arg0, Component arg1) {}
			
			@Override
			public Dimension preferredLayoutSize(Container p) {
				return minimumLayoutSize(p);
			}
			
			@Override
			public Dimension minimumLayoutSize(Container p) {
				int h = 0;
				int w = 0;
				
				for (int i = 0; i < p.getComponentCount(); i++) {
					Component c = p.getComponent(i);
				
					if (c.equals(mainPane)){
						h = c.getPreferredSize().height
								+ invisborder.top
								+ invisborder.bottom
								+ frameBorder.top
								+ frameBorder.bottom;
						
						w = c.getPreferredSize().width
								+ invisborder.left
								+ invisborder.right
								+ frameBorder.left
								+ frameBorder.right;
					}
				}
				
				return new Dimension(w, h);
			}
			
			@Override
			public void layoutContainer(Container p) {
				for (int i = 0; i < p.getComponentCount(); i++) {
					Component c = p.getComponent(i);
				
					if (c.equals(titleFrame)){
//						
//						int h = mainPane.getSize().height
//								+ frameBorder.bottom
//								- 30;
//						
//						int w = mainPane.getSize().width
//								+ frameBorder.right
//								- 30;
						
//						h = h == 0 ? c.getPreferredSize().height: h;
//						w = w == 0 ? c.getPreferredSize().width : w;
						
						int x = invisborder.left;
						int y = invisborder.top;
						
						c.setBounds(x, y, frameBorderSize.width, frameBorderSize.height);
					}
					
					if (c.equals(mainPane)){
						int h = p.getHeight()
								- invisborder.top
								- invisborder.bottom
								- frameBorder.top
								- frameBorder.bottom;
						
						int w = p.getWidth()
								- invisborder.left
								- invisborder.right
								- frameBorder.left
								- frameBorder.right;
						
						h = h == 0 ? c.getPreferredSize().height: h;
						w = w == 0 ? c.getPreferredSize().width : w;
						
						int x = invisborder.left + frameBorder.left;
						int y = invisborder.top + frameBorder.top;
						
						c.setBounds(x, y, w, h);
					}
	
				}
			}
			
		});
	}
}
