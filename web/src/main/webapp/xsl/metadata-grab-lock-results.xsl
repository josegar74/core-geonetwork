<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:include href="modal.xsl"/>
	
	<xsl:template name="content">
	</xsl:template>

	<xsl:template mode="title" match="/" priority="20">
			<xsl:value-of select='/root/gui/info/resultstitle'/>
	</xsl:template>

</xsl:stylesheet>