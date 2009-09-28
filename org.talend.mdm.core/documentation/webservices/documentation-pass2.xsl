<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
      xmlns:xs="http://www.w3.org/2001/XMLSchema"
      exclude-result-prefixes="xs"
      version="2.0"
      xmlns:xsd="http://www.w3.org/2001/XMLSchema"
    >

    <xsl:output encoding="UTF-8" method="html"/>

    <xsl:template match="/" exclude-result-prefixes="#all">


        <html>
            <head>

                <style type="text/css">
                    * {
                        font-family: tahoma, verdana, helvetica, sans-serif;
                    }

                    .operation-name {
                        font-weight: bold;
                        padding-bottom:5px;
                    }

                    .operation-description {
                        font-size: 0.85em;
                        font-style: italic;
                        padding-bottom:5px;
                    }

                    .params {
                        border-style: dotted;
                        border-width: 1px;
                        vertical-align: top;
                        padding: 5px;
                    }

                    .param-direction {
                        font-size: 0.85em;
                        font-style: italic;
                        vertical-align: top;
                        padding: 5px;
                    }


                </style>
            </head>
            <body>
                <h1>Open MDM Webservices</h1>

                <h3>Index</h3>
                <xsl:for-each select="//operation/name">
                    <xsl:element name="a">
                        <xsl:attribute name="href"><xsl:value-of select="concat('#',./text())"></xsl:value-of></xsl:attribute>
                        <xsl:value-of select="."/>
                    </xsl:element>: <xsl:value-of select="tokenize(../documentation/text(),'&#x0A;')[1]"></xsl:value-of><br/>
                </xsl:for-each>

                <h3>Operations</h3>
                <xsl:apply-templates select="//operation"/>

                <h3>Types</h3>
                <xsl:variable name="top" select="/documentation"/>
                <!--
                <xsl:for-each select="
                    for $vals in distinct-values($top//type/name)
                    return $vals
                    ">
                -->
                <xsl:for-each select="distinct-values($top/types/type/name)">
                    <xsl:variable name="val2" select="."/>
                    <xsl:apply-templates select="$top/types/type[name/text()=$val2][1]"/>
                </xsl:for-each>

            </body>
        </html>
    </xsl:template>


    <xsl:template match="operation">
        <xsl:variable name="in"><xsl:value-of select="in/text()"/></xsl:variable>
        <xsl:variable name="out"><xsl:value-of select="out/text()"/></xsl:variable>

        <span>
            <xsl:element name="a">
                <xsl:attribute name="class">operation-name</xsl:attribute>
                <xsl:attribute name="name"><xsl:value-of select="name/text()"></xsl:value-of></xsl:attribute>
                <xsl:value-of select="name/text()"></xsl:value-of>
            </xsl:element>
            <xsl:text> </xsl:text>
            <xsl:element name="span">
                <xsl:attribute name="class">operation-description</xsl:attribute>
                <xsl:call-template name="break">
                    <xsl:with-param name="text">
                        <xsl:value-of select="replace(replace(documentation,'^\s+',''),'\s+$','')" xml:space="preserve"/>
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:element>
        </span>
        <table>
            <tr>
                <td class="param-direction">in</td>
                <td class="params"><xsl:value-of select="$in"/></td>
                <td width="100%" class="params">
                    <xsl:call-template name="break">
                        <xsl:with-param name="text">
                            <xsl:value-of select="replace(replace(/documentation/types/type[name=$in][1]/documentation/text(),'^\s+',''),'\s+$','')" xml:space="preserve"/>
                        </xsl:with-param>
                    </xsl:call-template>
                    <xsl:apply-templates select="/documentation/types/type[name=$in][1]/subtypes"/>
                </td>
            </tr>
            <tr class="params">
                <td class="param-direction">out</td>
                <td class="params"><xsl:value-of select="$out"/></td>
                <td width="100%" class="params">
                    <xsl:call-template name="break">
                        <xsl:with-param name="text">
                            <xsl:value-of select="replace(replace(/documentation/types/type[name=$out][1]/documentation/text(),'^\s+',''),'\s+$','')" xml:space="preserve"/>
                        </xsl:with-param>
                    </xsl:call-template>
                    <xsl:apply-templates select="/documentation/types/type[name=$out][1]/subtypes"/>
                </td>
            </tr>
        </table>
        <tr><td><br/></td></tr>
    </xsl:template>

    <xsl:template match="/documentation/types/type">
        <br/><span>
            <xsl:element name="a">
                <xsl:attribute name="class">operation-name</xsl:attribute>
                <xsl:attribute name="name"><xsl:value-of select="name/text()"></xsl:value-of></xsl:attribute>
                <xsl:value-of select="name/text()"></xsl:value-of>
            </xsl:element>
        </span>
        <table>
            <tr>
                <td width="100%" class="params">
                    <xsl:call-template name="break">
                        <xsl:with-param name="text">
                            <xsl:value-of select="replace(replace(./documentation/text(),'^\s+',''),'\s+$','')" xml:space="preserve"/>
                        </xsl:with-param>
                    </xsl:call-template>
                    <xsl:apply-templates select="./subtypes"/>
                </td>
            </tr>
        </table>
    </xsl:template>

    <xsl:template match="subtypes">
        <xsl:if test="./subtype">
            <p>see:<xsl:text> </xsl:text>
                <xsl:for-each select="./subtype">
                    <xsl:element name="a">
                        <xsl:attribute name="href"><xsl:value-of select="concat('#',./text())"/></xsl:attribute>
                        <xsl:value-of select="."/>
                    </xsl:element><xsl:text> </xsl:text>
                </xsl:for-each>
            </p>
        </xsl:if>
    </xsl:template>

    <xsl:template name="break">
        <xsl:param name="text" select="."/>
        <xsl:choose>
            <xsl:when test="contains($text, '&#xA;')">
                <xsl:value-of select="normalize-space(substring-before($text, '&#xA;'))"/><ul>
                <xsl:call-template name="li">
                    <xsl:with-param name="text" select="substring-after($text,'&#xA;')"/>
                </xsl:call-template>
                </ul>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="normalize-space($text)"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="li">
        <xsl:param name="text" select="."/>
        <xsl:choose>
            <xsl:when test="contains($text, '&#xA;')">
                <li><xsl:value-of select="normalize-space(substring-before($text, '&#xA;'))"/></li>
                <xsl:call-template name="li">
                    <xsl:with-param name="text" select="substring-after($text,'&#xA;')"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <li><xsl:value-of select="normalize-space($text)"/></li>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>


</xsl:stylesheet>