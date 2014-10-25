package org.apache.lucene.search.spans.checkers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.spans.MultiPayloadTermSpans;
import org.apache.lucene.search.spans.PayloadCheckerSpanQuery;

/**
 * Be aware checkers in different clauses are not matchable simultaneously.
 * 
 * @author Julien
 */
public class SwitchPayloadChecker extends AbstractPayloadChecker
{
    final static NullPayloadChecker nullChecker = new NullPayloadChecker();
    
    String field;
    
    ConcurrentHashMap<String,IPayloadChecker> clauses = new ConcurrentHashMap<>();
    
    boolean branch = false;
    
    public String getField()
    {
        return field;
    }

    public ConcurrentHashMap<String, IPayloadChecker> getClauses() {
        return clauses;
    }
    
    AbstractIPayloadCheckerCollectionChecker currentChecker = null;
    
    public SwitchPayloadChecker(AbstractIPayloadCheckerCollectionChecker collectionChecker,SwitchPayloadConditionClause... checkers)
    {
        this("_type",collectionChecker,checkers);
    }
    
    public SwitchPayloadChecker(String field, AbstractIPayloadCheckerCollectionChecker collectionChecker, SwitchPayloadConditionClause... checkers)
    {
        this.field = field;
        this.currentChecker = collectionChecker;
        for(int i=0;i<checkers.length;i++)
        {
            clauses.put(checkers[i].value,checkers[i].condition);
        }
    }
    
    /**
     * Default is to and-check all matching conditions on _type field.
     * @param checkers 
     */
    public SwitchPayloadChecker(SwitchPayloadConditionClause... checkers)
    {
        this("_type",new AndPayloadChecker(),checkers);
    }
    
    /**
     * * Default is to and-check all matching conditions.
     * @param field
     * @param checkers 
     */
    public SwitchPayloadChecker(String field, SwitchPayloadConditionClause... checkers)
    {
        this(field,new AndPayloadChecker(),checkers);
    }
    
    ArrayList<IPayloadChecker> checkers = new ArrayList<>();
    
    public void getBranch(MultiPayloadTermSpans subspan) throws IOException
    {
        checkers.clear();
        Document d = subspan.document();
        String[] types = d.getValues(field);
        for(int i=0;i<types.length;i++)
        {
            String type = types[i];
            
            IPayloadChecker clause = clauses.get(type);
            if (clause!=null && !checkers.contains(clause))
                checkers.add(clause);
        }
        if (checkers.isEmpty())
            checkers.add(nullChecker);
        currentChecker.setCheckers(checkers);
        branch = true;
    }
    
    @Override
    public boolean checkNextPayload(MultiPayloadTermSpans subspan) throws IOException
    {
        if (!branch)
        {
            getBranch(subspan);
        }
        return currentChecker.checkNextPayload(subspan);
    }

    @Override
    public boolean check()
    {
        return currentChecker.check();
    }

    @Override
    public void clear()
    {
        currentChecker.clear();
        branch = false;
    }
    
    @Override
    public String toString()
    {
        String res = "SWITCH[";
        res += field;
        res += "]";
        
        Enumeration<String> eKeys = clauses.keys();
        
        while(eKeys.hasMoreElements())
        {
            String key = eKeys.nextElement();
            
            res += "case "+key+": (";
            res += clauses.get(key).toString();
            res += ") break;";
        }
        return res;
    }

    @Override
    public void setQuery(PayloadCheckerSpanQuery query) {
        Enumeration<String> eKeys = clauses.keys();
        while(eKeys.hasMoreElements())
        {
            String key = eKeys.nextElement();
            clauses.get(key).setQuery(query);
        }
    }

    @Override
    public SwitchPayloadChecker clone()
    {
        ArrayList<SwitchPayloadConditionClause> clausesClone = new ArrayList<>();
        
        Enumeration<String> eKeys = clauses.keys();
        while(eKeys.hasMoreElements())
        {
            String key = eKeys.nextElement();
            SwitchPayloadConditionClause clause = new SwitchPayloadConditionClause(key, clauses.get(key).clone());
            clausesClone.add(clause);
        }
        
        SwitchPayloadChecker clone = new SwitchPayloadChecker(this.field,clausesClone.toArray(new SwitchPayloadConditionClause[clausesClone.size()]));
        return clone;
    }
}