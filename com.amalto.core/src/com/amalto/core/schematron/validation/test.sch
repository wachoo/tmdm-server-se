<?xml version="1.0"?>
<schema xmlns="http://www.ascc.net/xml/schematron">
    <title>AB Example</title>
    
    <pattern name="Our Only Pattern" id="pattern1">
        <rule context="/" id="sum100">
            <assert test=" (value of select='/A') + (value of select='/B') = 100">
                The sum of the values of A and B must be 100.
            </assert>
        </rule>
    </pattern>
</schema>
