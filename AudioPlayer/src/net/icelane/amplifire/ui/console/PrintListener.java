package net.icelane.amplifire.ui.console;

public interface PrintListener {
	
	/**
	 * Print a given text
	 * @param t Text to be printed
	 * @param n Is a new line
	 */
	public void print(String t, boolean n);
}
