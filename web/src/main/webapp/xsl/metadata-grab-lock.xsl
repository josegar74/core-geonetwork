<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:include href="modal.xsl"/>
    <xsl:variable name="profile"  select="/root/gui/session/profile"/>

    <xsl:template match="/" mode="pageTitle" priority="10">
        <xsl:value-of select="/root/gui/strings/grab.lock"/>
    </xsl:template>

    <xsl:template name="content">
        <xsl:call-template name="formLayout">
            <xsl:with-param name="title" select="/root/gui/strings/grab.lock"/>
            <xsl:with-param name="content">

                <xsl:variable name="lang" select="/root/gui/language"/>
                <xsl:variable name="disabled" select="(/root/response/owner='false')"/>

                <div id="users">
                    <input name="id" id="id" type="hidden" value="{/root/request/id}"/>

                    <p>
                        <strong><xsl:value-of select="/root/gui/strings/currentlockowner"/> <xsl:value-of select="/root/request/currentLockOwner"/></strong>
                    </p>

                    <p>
                        <xsl:value-of select="/root/gui/strings/selectlockowner"/>
                    </p>

                    <div style="padding-left: 20px;max-height:400px;overflow:auto">
                    <table class="wet-boew-zebra rowzebra rowhover">
                        <tr>
                            <th class="padded"><xsl:value-of select="/root/gui/strings/username"/></th>
                            <th class="padded"><xsl:value-of select="/root/gui/strings/surName"/></th>
                            <th class="padded"><xsl:value-of select="/root/gui/strings/firstName"/></th>
                            <th class="padded"><xsl:value-of select="/root/gui/strings/profile"/></th>
                            <th class="padded">&#160;</th>

                        </tr>

                        <xsl:for-each select="/root/response/record">
                            <xsl:variable name="profileId"><xsl:value-of select="profile"/></xsl:variable>
                            <xsl:if test="$profileId = 'Administrator' or $profileId='UserAdmin' or $profileId='Reviewer' or $profileId='Editor'">
                            <tr>
                                <td class="padded"><xsl:value-of select="username"/></td>
                                <td class="padded"><xsl:value-of select="surname"/></td>
                                <td class="padded"><xsl:value-of select="name"/></td>
                                <td class="padded"><xsl:value-of select="/root/gui/strings/profileChoice[@value=$profileId]"/></td>
                                <td class="padded">
                                    <xsl:choose>
                                        <xsl:when test="username = /root/request/currentLockOwner">
                                            <input type="radio" name="userId" value="{id}" id="u{id}" disabled="disabled"/>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <input type="radio" name="userId" value="{id}" id="u{id}"/>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </td>
                            </tr>
                            </xsl:if>
                        </xsl:for-each>

                    </table>
                    </div> <xsl:if test="not($disabled)">
                    <p>
                    <input type="button" class="content" onclick="radioModalUpdate('users','metadata.grab.lock');" value="{/root/gui/strings/submit}"/>
                    </p> </xsl:if>
                </div>
            </xsl:with-param>
        </xsl:call-template>
    </xsl:template>

</xsl:stylesheet>