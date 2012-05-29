package net.mabako.zwickau.autohaendler;

/**
 * Hilfsklasse zur Darstellung von JComboBox-en
 * 
 * @author Marcus Bauer (mabako@gmail.com)
 */
public class TableViewLink
{
	private int id;
	private String value;
	
	public TableViewLink(int id, String value)
	{
		this.id = id;
		this.value = value;
	}
	
	public int getID()
	{
		return id;
	}
	
	@Override
	public String toString()
	{
		return value;
	}
}
