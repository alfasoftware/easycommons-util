<?xml version="1.0" encoding="UTF-8"?>
<!--
 Copyright (c) 2008-2012 EBM WebSourcing, 2012-2023 Linagora

 This program/library is free software: you can redistribute it and/or modify
 it under the terms of the New BSD License (3-clause license).

 This program/library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 FITNESS FOR A PARTICULAR PURPOSE. See the New BSD License (3-clause license)
 for more details.

 You should have received a copy of the New BSD License (3-clause license)
 along with this program/library; If not, see http://directory.fsf.org/wiki/License:BSD_3Clause/
 for the New BSD License (3-clause license).
-->
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" encoding="UTF-8" />
	<xsl:param name="indent-increment" select="'  '" />
	<xsl:template name="newline">
		<xsl:text disable-output-escaping="yes">
</xsl:text>
	</xsl:template>
	<xsl:template match="comment() | processing-instruction()">
		<xsl:param name="indent" select="''" />
		<xsl:call-template name="newline" />
		<xsl:value-of select="$indent" />
		<xsl:copy />
	</xsl:template>
	<xsl:template match="text()">
		<xsl:param name="indent" select="''" />
		<xsl:call-template name="newline" />
		<xsl:value-of select="$indent" />
		<xsl:value-of select="normalize-space(.)" />
	</xsl:template>
	<xsl:template match="text()[normalize-space(.)='']" />
	<xsl:template match="*">
		<xsl:param name="indent" select="''" />
		<xsl:call-template name="newline" />
		<xsl:value-of select="$indent" />
		<xsl:choose>
			<xsl:when test="count(child::*) > 0">
				<xsl:copy>
					<xsl:copy-of select="@*" />
					<xsl:apply-templates select="*|text()">
						<xsl:with-param name="indent"
							select="concat ($indent, $indent-increment)" />
					</xsl:apply-templates>
					<xsl:call-template name="newline" />
					<xsl:value-of select="$indent" />
				</xsl:copy>
			</xsl:when>
			<xsl:otherwise>
				<xsl:copy-of select="." />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>