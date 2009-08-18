package com.amalto.workbench.models;

import java.util.ArrayList;
import java.util.List;

import com.amalto.workbench.widgets.ComplexTableViewerColumn;

/**
 * The instance of complexTableViewer
 * @author aiming
 *
 */
public class Line implements Comparable<Object>, Cloneable{
	/**
	 * key is the TableColumn name, value is the  column's value
	 */
	public List<KeyValue> keyValues=new ArrayList<KeyValue>();
	public Line(ComplexTableViewerColumn[] keys,String[]values ){
		for(int i=0; i<keys.length && i<values.length; i++){
			keyValues.add(new KeyValue(keys[i].getName(),values[i]));
		}
	}
	public Line( List<KeyValue> keyValues){
		this.keyValues=keyValues;
	}
	
    public int compareTo(Object o)
    {
    	Line other = (Line)o;
    	int index = 0;
    	
    	for(KeyValue kv: this.keyValues)
    	{
    		int comp = kv.value.compareTo(other.keyValues.get(index).value);
    		if (comp != 0)
    		{
    			return comp;
    		}
    		index++;
    	}
    	
    	return 0;
    }
    

    public boolean equals(Object obj)
    {
    	if (!(obj instanceof Line))
    	{
    		return false;
    	}
    	
    	Line other = (Line)obj;
    	int index = 0;
    	
    	for(KeyValue kv: this.keyValues)
    	{
    		if (!kv.value.equals(other.keyValues.get(index).value))
    		{
    			return false;
    		}
    		index++;
    	}
    	
    	return true;
    }
    
    public int hashCode()
    {
    	int result = 53;
    	
    	for (KeyValue kv: keyValues)
    	{
    		result += 61 * result + kv.value.hashCode();
    	}
    	
    	return result;
    }
    
    public Line clone()
    {
    	List<KeyValue> copyKeyValues = new ArrayList<KeyValue>();
    	for (KeyValue kv: keyValues)
    	{
    		copyKeyValues.add(kv);
    	}
    	return new Line(copyKeyValues);
    }
}
