<xsl:stylesheet xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"><xsl:template match="body"><fo:root><fo:layout-master-set><fo:simple-page-master master-name="only"><fo:region-body region-name="xsl-region-body" margin="0" padding="0"/><fo:region-before region-name="xsl-region-before" extent="0.7in"/><fo:region-after region-name="xsl-region-after" extent="0.7in"/></fo:simple-page-master></fo:layout-master-set><xsl:apply-templates select="section"/></fo:root></xsl:template><xsl:template match="section"><fo:page-sequence master-reference="only"><fo:flow flow-name="xsl-region-body"><fo:block><xsl:copy-of select="*"/></fo:block></fo:flow></fo:page-sequence></xsl:template></xsl:stylesheet>