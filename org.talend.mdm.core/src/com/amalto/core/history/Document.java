package com.amalto.core.history;

/**
 *
 */
public interface Document {
    String getAsString();

    boolean isCreated();

    boolean isDeleted();

}
