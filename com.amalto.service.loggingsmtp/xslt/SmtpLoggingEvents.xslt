<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="xml" indent="yes" omit-xml-declaration="yes" />
    
        
    <xsl:template  match="/logging_event">
        
        <html>
            
            <body>
                
                <table style="border: 1px solid #ccc; border-collapse: collapse;">
                    <tr>
                        <td style="border: 1px solid #ccc; padding: 0.5em; vertical-align: top; font-weight: bold;">Time</td>
                        <td style="border: 1px solid #ccc; padding: 0.5em; vertical-align: top;"><xsl:value-of select="time/text()"/></td>
                    </tr>
                    <tr>
                        <td style="border: 1px solid #ccc; padding: 0.5em; vertical-align: top; font-weight: bold;">Level</td>
                        <td style="border: 1px solid #ccc; padding: 0.5em; vertical-align: top;"><xsl:value-of select="level/text()"/></td>
                    </tr>
                    <tr>
                        <td style="border: 1px solid #ccc; padding: 0.5em; vertical-align: top; font-weight: bold;">Logger</td>
                        <td style="border: 1px solid #ccc; padding: 0.5em; vertical-align: top;"><xsl:value-of select="logger/text()"/></td>
                    </tr>
                    <tr>
                        <td style="border: 1px solid #ccc; padding: 0.5em; vertical-align: top; font-weight: bold;">Message</td>
                        <td style="border: 1px solid #ccc; padding: 0.5em; vertical-align: top;"><pre><xsl:value-of select="message/text()"/></pre></td>
                    </tr>
                    <tr>
                        <td style="border: 1px solid #ccc; padding: 0.5em; vertical-align: top; font-weight: bold;">NDC</td>
                        <td style="border: 1px solid #ccc; padding: 0.5em; vertical-align: top;"><xsl:value-of select="ndc/text()"/></td>
                    </tr>
                    <tr>
                        <td style="border: 1px solid #ccc; padding: 0.5em; vertical-align: top; font-weight: bold;">Thread</td>
                        <td style="border: 1px solid #ccc; padding: 0.5em; vertical-align: top;"><xsl:value-of select="thread/text()"/></td>
                    </tr>
                    <tr>
                        <td style="border: 1px solid #ccc; padding: 0.5em; vertical-align: top; font-weight: bold;">Throwable</td>
                        <td style="border: 1px solid #ccc; padding: 0.5em; vertical-align: top;"><pre><xsl:value-of select="throwable/text()"/></pre></td>
                    </tr>
                    
                </table>
                
            </body>
        </html>
    </xsl:template>
    
    
    
</xsl:stylesheet>
