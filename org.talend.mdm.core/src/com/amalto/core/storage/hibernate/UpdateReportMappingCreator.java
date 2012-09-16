package com.amalto.core.storage.hibernate;

import com.amalto.core.metadata.*;

import javax.xml.XMLConstants;
import java.util.Collections;

/**
 *
 */
public class UpdateReportMappingCreator extends DefaultMetadataVisitor<TypeMapping> {

    public MappingRepository mappings;

    private final ComplexTypeMetadata DATABASE_UPDATE_REPORT_TYPE;

    private final ComplexTypeMetadata USER_UPDATE_REPORT_TYPE;

    private final MetadataRepository repository;

    public UpdateReportMappingCreator(TypeMetadata updateReportType, MetadataRepository repository, MappingRepository mappings) {
        this.repository = repository;
        if(updateReportType == null) {
            throw new IllegalStateException("Update report type cannot be null.");
        }
        USER_UPDATE_REPORT_TYPE = (ComplexTypeMetadata) updateReportType;

        this.mappings = mappings;
        ComplexTypeMetadata databaseUpdateReportType = new ComplexTypeMetadataImpl("", "Update", true); //$NON-NLS-1$ //$NON-NLS-2$
        TypeMetadata stringType = new SimpleTypeMetadata(XMLConstants.W3C_XML_SCHEMA_NS_URI, "string"); //$NON-NLS-1$
        databaseUpdateReportType.addField(new SimpleTypeFieldMetadata(databaseUpdateReportType, false, false, false, "UserName", stringType, Collections.<String>emptyList(), Collections.<String>emptyList())); //$NON-NLS-1$
        databaseUpdateReportType.addField(new SimpleTypeFieldMetadata(databaseUpdateReportType, true, false, true, "Source", stringType, Collections.<String>emptyList(), Collections.<String>emptyList())); //$NON-NLS-1$
        databaseUpdateReportType.addField(new SimpleTypeFieldMetadata(databaseUpdateReportType, true, false, true, "TimeInMillis", stringType, Collections.<String>emptyList(), Collections.<String>emptyList())); //$NON-NLS-1$
        databaseUpdateReportType.addField(new SimpleTypeFieldMetadata(databaseUpdateReportType, false, false, false, "OperationType", stringType, Collections.<String>emptyList(), Collections.<String>emptyList())); //$NON-NLS-1$
        databaseUpdateReportType.addField(new SimpleTypeFieldMetadata(databaseUpdateReportType, false, false, false, "RevisionID", stringType, Collections.<String>emptyList(), Collections.<String>emptyList())); //$NON-NLS-1$
        databaseUpdateReportType.addField(new SimpleTypeFieldMetadata(databaseUpdateReportType, false, false, false, "DataCluster", stringType, Collections.<String>emptyList(), Collections.<String>emptyList())); //$NON-NLS-1$
        databaseUpdateReportType.addField(new SimpleTypeFieldMetadata(databaseUpdateReportType, false, false, false, "DataModel", stringType, Collections.<String>emptyList(), Collections.<String>emptyList())); //$NON-NLS-1$
        databaseUpdateReportType.addField(new SimpleTypeFieldMetadata(databaseUpdateReportType, false, false, false, "Concept", stringType, Collections.<String>emptyList(), Collections.<String>emptyList())); //$NON-NLS-1$
        databaseUpdateReportType.addField(new SimpleTypeFieldMetadata(databaseUpdateReportType, false, false, false, "Key", stringType, Collections.<String>emptyList(), Collections.<String>emptyList())); //$NON-NLS-1$
        SimpleTypeFieldMetadata items_xml = new SimpleTypeFieldMetadata(databaseUpdateReportType, false, false, false, "Items_xml", stringType, Collections.<String>emptyList(), Collections.<String>emptyList()); //$NON-NLS-1$
        items_xml.setData("SQL_TYPE", "text"); //$NON-NLS-1$ //$NON-NLS-2$
        databaseUpdateReportType.addField(items_xml);
        DATABASE_UPDATE_REPORT_TYPE = (ComplexTypeMetadata) databaseUpdateReportType.freeze();
    }

    @Override
    public TypeMapping visit(ComplexTypeMetadata complexType) {
        if(!"Update".equals(complexType.getName())) {
            throw new IllegalArgumentException("Expected a type 'Update' but got '" + complexType.getName() + "'.");
        }
        return new UpdateReportTypeMapping(USER_UPDATE_REPORT_TYPE, DATABASE_UPDATE_REPORT_TYPE, mappings, repository);
    }
}
