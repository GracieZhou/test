<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="/">
<html>
  <head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<style>
		h1{text-align:center;}
		body {margin:20px;font: normal 11px auto "Trebuchet MS", Verdana, Arial, Helvetica, sans-serif;color: #4f6b72;background: #E6EAE9;}
		th{text-align:center;}
		td{border:solid #add9c0; border-width:0px 1px 1px 0px; padding:10px 0px;}
		table{margin:10px;width:400px;border:solid #add9c0; border-width:1px 0px 0px 1px;float :left;}
		.title{background-color:#9acd32}
		.error{background-color:#FF3333;font-weight:bolder;color:#000000;}
		.warning{font-weight:bolder;color:red;}
	</style>
  </head>
  <body>
    <h1>Full Diagnostics Result</h1>
	
	<xsl:for-each select="result/deviceinfo">
    <table>
		<tr class="title">
			<th colspan="2">Device Info</th>
		</tr>
		<tr>
			<td>Devices Name:</td>
			<td><xsl:value-of select="devicetype"/></td>
		</tr>
		<tr>
			<td>Diagnostics  Time:</td>
			<td><xsl:value-of select="time"/></td>
		</tr>
    </table>
	</xsl:for-each>
	
	<xsl:for-each select="result/disk">
	    <table>
		    <tr class="title">
		      <th colspan="2">Disk</th>
		    </tr>
		    <tr>
		      <td>PhyMemSize</td>
		      <td><xsl:value-of select="PhyMemSize"/></td>
		    </tr>			
		    <tr>
		      <td>StorageSize</td>
		      <td><xsl:value-of select="StorageSize"/></td>
		    </tr>			
	    </table>
	</xsl:for-each>
	
	<xsl:for-each select="result/cpumemeryinfo">	
	    <table>
		    <tr class="title">
		      <th  colspan="2">CpumeMeryInfo</th>
		    </tr>
		    <tr>
		      <td>Cpu_Usage</td>
		      <td><xsl:value-of select="cpu_usage"/></td>
		    </tr>	
		    <tr>
		      <td>Memery_Usage</td>
		      <td><xsl:value-of select="memery_usage"/></td>
		    </tr>		
		    <tr>
		      <td>MemoryToltal</td>
		      <td><xsl:value-of select="MemoryToltal"/></td>
		    </tr>
	    </table>
	</xsl:for-each>	
	
	<xsl:for-each select="result/ping">		
	    <table>
	    <tr class="title">
	      <th  colspan="2" >Ping</th>
	    </tr>
	    <tr>
	      <td>Host</td>
	      <td><xsl:value-of select="Host"/></td>
	    </tr>
	    <tr>
	      <td>DiagnosticsState</td>
	      <td><xsl:value-of select="DiagnosticsState"/></td>
	    </tr>	
	    <tr>
	      <td>MaximumResponseTime</td>
	      <td><xsl:value-of select="MaximumResponseTime"/></td>
	    </tr>
	    <tr>
	      <td>MinimumResponseTime</td>
	      <td><xsl:value-of select="MinimumResponseTime"/></td>
	    </tr>
	    <tr>
	      <td>AverageResponseTime</td>
	      <td><xsl:value-of select="AverageResponseTime"/></td>
	    </tr>
	    <tr>
	      <td>SuccessCount</td>
	      <td><xsl:value-of select="SuccessCount"/></td>
	    </tr>
	    <tr>
	      <td>FailureCount</td>
	      <td><xsl:value-of select="FailureCount"/></td>
	    </tr>
		
	    </table>
	</xsl:for-each>	
  </body>
</html>
</xsl:template>
</xsl:stylesheet>
