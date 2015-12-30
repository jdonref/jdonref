package org.apache.lucene.search.spans.checkers;

/**
 *
 * @author akchana
 */
public class SwitchPayloadConditionClause
{
    public String value;
    
    public IPayloadChecker condition;
    
    public SwitchPayloadConditionClause(String value,IPayloadChecker condition)
    {
        this.value = value;
        this.condition = condition;
    }
}
