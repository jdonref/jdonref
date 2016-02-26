package org.elasticsearch.common.lucene.search.jdonrefv4;

import org.elasticsearch.common.lucene.search.LimitFilter;

/**
 * toString only purposes
 * @author Julien
 */
public class MyLimitFilter extends LimitFilter
{
    protected int size;
        public MyLimitFilter(int size)
    {
        super(size);
        this.size = size; // limit is private field
    }
    
    @Override
    public String toString()
    {
        return "count <="+size;
    }
}
