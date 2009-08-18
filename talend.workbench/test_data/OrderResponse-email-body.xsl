<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="text" omit-xml-declaration="yes"/>
    
    <xsl:variable name="response"><xsl:value-of select="/OrderResponse/OrderResponseHeader/ResponseType/ResponseTypeCoded"/></xsl:variable>
    
    <xsl:template match="/">
        <xsl:text>Supplier </xsl:text>
        <xsl:value-of select='OrderResponse/OrderResponseHeader/SellerParty/Party/NameAddress/Name1'/>
        <xsl:text> has </xsl:text>
        <xsl:choose>
            <xsl:when test="$response='Accepted'"><xsl:text>ACCEPTED</xsl:text></xsl:when>
            <xsl:when test="$response='NotAccepted'"><xsl:text>REJECTED</xsl:text></xsl:when>
            <xsl:when test="$response='AcceptedWithAmendmentInDetailSection'"><xsl:text>ACCEPTED WITH AMENDMENT</xsl:text></xsl:when>
            <xsl:otherwise><xsl:text>HUUH: </xsl:text><xsl:value-of select="$response"/></xsl:otherwise>
         </xsl:choose>
         <xsl:text> you order number </xsl:text>
        <xsl:value-of select="OrderResponse/OrderResponseHeader/OrderReference/Reference/RefNum"/>
    </xsl:template>
    
</xsl:stylesheet>