package com.amalto.core.objects.role.ejb.local;

/**
 *
 */
public interface Role {
    /**
     * Creates or updates a Role
     * @throws XtentisException
     */
    com.amalto.core.objects.role.ejb.RolePOJOPK putRole(com.amalto.core.objects.role.ejb.RolePOJO role) throws com.amalto.core.util.XtentisException;

    /**
     * Get Role
     * @throws XtentisException
     */
    com.amalto.core.objects.role.ejb.RolePOJO getRole(com.amalto.core.objects.role.ejb.RolePOJOPK pk) throws com.amalto.core.util.XtentisException;

    /**
     * Get a Role - no exception is thrown: returns null if not found
     * @throws XtentisException
     */
    com.amalto.core.objects.role.ejb.RolePOJO existsRole(com.amalto.core.objects.role.ejb.RolePOJOPK pk) throws com.amalto.core.util.XtentisException;

    /**
     * Remove an item
     * @throws XtentisException
     */
    com.amalto.core.objects.role.ejb.RolePOJOPK removeRole(com.amalto.core.objects.role.ejb.RolePOJOPK pk) throws com.amalto.core.util.XtentisException;

    /**
     * Retrieve all Role PKS
     * @throws XtentisException
     */
    java.util.Collection getRolePKs(String regex) throws com.amalto.core.util.XtentisException;
}
