<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:android='http://schemas.android.com/apk/res/android'>

    <xsl:output method="xml" indent="yes"/>

    <xsl:param name="increment" select="1" />

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()" />
        </xsl:copy>
    </xsl:template>

    <xsl:template match="/manifest/@android:versionCode">
        <xsl:attribute name="android:versionCode"><xsl:value-of select="$increment + ." /></xsl:attribute>
    </xsl:template>

</xsl:stylesheet>