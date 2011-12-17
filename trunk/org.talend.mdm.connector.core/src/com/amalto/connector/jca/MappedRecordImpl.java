/*
 * 
 *
 */
package com.amalto.connector.jca;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.resource.cci.MappedRecord;

/**
 * @author bgrieder
 */
public class MappedRecordImpl implements MappedRecord {
	
    
    private String recordName;
    private String recordShortDescription;
    private HashMap map = new HashMap();
       
    public MappedRecordImpl() {
        super();
//        org.apache.log4j.Category.getInstance(this.getClass()).debug("MappedRecordImpl() ");
    }

    
    /*
     *  (non-Javadoc)
     * @see javax.resource.cci.Record#setRecordName(java.lang.String)
     */
    public void setRecordName(String name) {
        this.recordName = name;
    }
    /* (non-Javadoc)
     * @see javax.resource.cci.Record#getRecordName()
     */
    public String getRecordName() {
        return this.recordName;
    }

    /* (non-Javadoc)
     * @see javax.resource.cci.Record#getRecordShortDescription()
     */
    public String getRecordShortDescription() {
        return this.recordShortDescription;
    }

    /* (non-Javadoc)
     * @see javax.resource.cci.Record#setRecordShortDescription(java.lang.String)
     */
    public void setRecordShortDescription(String description) {
        this.recordShortDescription = description;
    }

    /* (non-Javadoc)
     * @see java.util.Map#size()
     */
    public int size() {
        return map.size();
    }

    /* (non-Javadoc)
     * @see java.util.Map#clear()
     */
    public void clear() {
        map.clear();
    }

    /* (non-Javadoc)
     * @see java.util.Map#isEmpty()
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /* (non-Javadoc)
     * @see java.util.Map#containsKey(java.lang.Object)
     */
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    /* (non-Javadoc)
     * @see java.util.Map#containsValue(java.lang.Object)
     */
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    /* (non-Javadoc)
     * @see java.util.Map#values()
     */
    public Collection values() {
        return map.values();
    }

    /* (non-Javadoc)
     * @see java.util.Map#putAll(java.util.Map)
     */
    public void putAll(Map t) {
        //TODO: limit the possible keys
        map.putAll(t);
    }

    /* (non-Javadoc)
     * @see java.util.Map#entrySet()
     */
    public Set entrySet() {
        return map.entrySet();
    }

    /* (non-Javadoc)
     * @see java.util.Map#keySet()
     */
    public Set keySet() {
        return map.keySet();
    }

    /* (non-Javadoc)
     * @see java.util.Map#get(java.lang.Object)
     */
    public Object get(Object key) {
        return map.get(key);
    }

    /* (non-Javadoc)
     * @see java.util.Map#remove(java.lang.Object)
     */
    public Object remove(Object key) {
        //TODO: make it impossible to remove?
        return map.remove(key);
    }

    /* (non-Javadoc)
     * @see java.util.Map#put(java.lang.Object, java.lang.Object)
     */
    public Object put(Object key, Object value) {
        // TODO limit the record names
        return map.put(key, value);
    }

    /*
     *  (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    public Object clone() throws CloneNotSupportedException {
        MappedRecordImpl clone = new MappedRecordImpl(); 
        clone.setRecordName(this.recordName);
        clone.setRecordShortDescription(this.getRecordShortDescription());
        clone.putAll(this.map);
        return clone;
    }

    
}
