/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ppol.jdonref.dao;

/**
 *
 * @author arochewi
 */
public class AbstractBeanWithNote
{
    private int note;

    protected AbstractBeanWithNote()
    {
        
    }

    /**
     * @return the note
     */
    public int getNote()
    {
        return note;
    }

    /**
     * @param note the note to set
     */
    public void setNote(int note)
    {
        this.note = note;
    }

    
}
