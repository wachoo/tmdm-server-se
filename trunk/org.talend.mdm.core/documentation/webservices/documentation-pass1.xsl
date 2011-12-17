<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
      xmlns:xs="http://www.w3.org/2001/XMLSchema"
      exclude-result-prefixes="xs"
      version="2.0"
      xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/"
      xmlns:xsd="http://www.w3.org/2001/XMLSchema"
    >

    <xsl:output encoding="UTF-8" method="xml" indent="yes" xml:space="preserve" />

    <xsl:template match="/" exclude-result-prefixes="#all">
        <documentation>
            <operations>
                <xsl:apply-templates select="//wsdl:portType/wsdl:operation"/>
            </operations>
            <types>
                <xsl:apply-templates select="wsdl:definitions/wsdl:types/xsd:schema"/>
            </types>
        </documentation>
    </xsl:template>


    <xsl:template match="wsdl:portType/wsdl:operation" exclude-result-prefixes="#all">
        <xsl:variable name="paramIn"><xsl:value-of select="substring(wsdl:input/@message,5)"/></xsl:variable>
        <xsl:variable name="objectIn"><xsl:value-of select="substring(/wsdl:definitions/wsdl:message[@name=$paramIn]/wsdl:part/@element,5)"/></xsl:variable>
        <xsl:variable name="paramOut"><xsl:value-of select="substring(wsdl:output/@message,5)"/></xsl:variable>
        <xsl:variable name="objectOut"><xsl:value-of select="substring(/wsdl:definitions/wsdl:message[@name=$paramOut]/wsdl:part/@element,5)"/></xsl:variable>

        <operation>
            <name><xsl:value-of select="@name"/></name>
            <documentation><xsl:value-of select="wsdl:documentation" xml:space=""/></documentation>
            <in><xsl:value-of select="$objectIn"/></in>
            <out><xsl:value-of select="$objectOut"/></out>
        </operation>

    </xsl:template>

    <xsl:template match="wsdl:definitions/wsdl:types/xsd:schema">
        <xsl:call-template name="xsd">
            <xsl:with-param name="doc" select="."/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template name="xsd">
        <xsl:param name="doc"/>

         <!-- Processing Element <xsl:value-of select="$doc/local-name()"/><br/> -->

        <!-- process element definitions -->
        <xsl:apply-templates select="$doc/xsd:complexType"/>

        <!-- process includes -->
        <xsl:apply-templates select="$doc/xsd:include"/>

        <!-- process imports -->
        <xsl:apply-templates select="$doc/xsd:import"/>

    </xsl:template>

    <xsl:template match="xsd:schema/xsd:import">
         <!-- Processing import <xsl:value-of select="@schemaLocation"/><br/> -->
        <xsl:variable name="importedDoc" select="document(@schemaLocation)"/>
        <xsl:call-template name="xsd">
            <xsl:with-param name="doc" select="$importedDoc/xsd:schema"/>
        </xsl:call-template>
    </xsl:template>


    <xsl:template match="xsd:include">
        <!-- Processing include <xsl:value-of select="@schemaLocation"/><br/> -->
        <xsl:variable name="includedDoc" select="document(@schemaLocation)"/>
        <xsl:call-template name="xsd">
            <xsl:with-param name="doc" select="$includedDoc/xsd:schema"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="xsd:schema/xsd:complexType" exclude-result-prefixes="#all">
        <type>
            <name><xsl:value-of select="@name"/></name>
            <documentation><xsl:value-of select="xsd:annotation/xsd:documentation" xml:space="preserve"/></documentation>
            <subtypes>
                <xsl:for-each select=".//xsd:element[starts-with(@type,'tns:')]">
                    <subtype><xsl:value-of select="substring(@type,5)"/></subtype>
                </xsl:for-each>
            </subtypes>
        </type>
    </xsl:template>


</xsl:stylesheet>