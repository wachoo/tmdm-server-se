package com.amalto.core.server.api;

import com.amalto.core.objects.customform.ejb.CustomFormPOJO;
import com.amalto.core.objects.customform.ejb.CustomFormPOJOPK;
import com.amalto.core.util.XtentisException;

import java.util.Collection;

/**
 *
 */
public interface CustomForm {
    CustomFormPOJOPK putCustomForm(CustomFormPOJO customForm) throws XtentisException;

    CustomFormPOJO getCustomForm(CustomFormPOJOPK pk) throws XtentisException;

    CustomFormPOJO getUserCustomForm(CustomFormPOJOPK cpk) throws XtentisException;

    CustomFormPOJO existsCustomForm(CustomFormPOJOPK pk) throws XtentisException;

    CustomFormPOJOPK removeCustomForm(CustomFormPOJOPK pk) throws XtentisException;

    Collection<CustomFormPOJOPK> getCustomFormPKs(String regex) throws XtentisException;
}
