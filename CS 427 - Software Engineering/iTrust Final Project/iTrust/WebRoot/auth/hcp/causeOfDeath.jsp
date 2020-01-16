<%--
  Created by IntelliJ IDEA.
  User: botaoh2
  Date: 11/3/19
  Time: 3:04 PM
  To change this template use File | Settings | File Templates.
--%>
<%@page errorPage="/auth/exceptionHandler.jsp"%>

<%@page import="edu.ncsu.csc.itrust.dao.DAOFactory"%>
<%@page import="edu.ncsu.csc.itrust.dao.mysql.CauseOfDeathTrendsDAO"%>
<%@page import="edu.ncsu.csc.itrust.beans.CauseOfDeathBean"%>
<%@page import="edu.ncsu.csc.itrust.enums.Gender"%>
<%@ page import="java.util.regex.Pattern" %>
<%@ page import="edu.ncsu.csc.itrust.action.CauseOfDeathAction" %>
<%@ page import="java.util.List" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="edu.ncsu.csc.itrust.exception.FormValidationException" %>

<%@include file="/global.jsp" %>

<%
    pageTitle = "iTrust - Cause of Death Page (UC20)";
%>

<%@include file="/header.jsp" %>

<%
    String startYear = request.getParameter("START_YEAR");
    String endYear = request.getParameter("END_YEAR");
    String genderStr = request.getParameter("GENDER");
    boolean formIsFilled = "true".equals(request.getParameter("formIsFilled"));
    if(startYear == null) {
        startYear = "";
    }
    if(endYear == null) {
        endYear = "";
    }
    if(genderStr == null) {
        genderStr = "Not Specified";
    }
    Gender gender;
    if("Male".equals(genderStr)) {
        gender = Gender.Male;
    }else if("Female".equals(genderStr)) {
        gender = Gender.Female;
    }else {
        gender = Gender.NotSpecified;
    }
%>

<table align=center>
    <tr> </tr>
    <form id="userSearchForm" action="causeOfDeath.jsp" method="post">
        <input type="hidden" name="formIsFilled" value="true"><br />
        <tr>
            <td><b>Start Year:</b></td>
            <td style="width: 150px; border: 1px solid Gray;">
                <input name="START_YEAR" type="text" value="<%= StringEscapeUtils.escapeHtml("" + ( startYear )) %>" />
            </td>
            <td><b>End Year:</b></td>
            <td style="width: 150px; border: 1px solid Gray;">
                <input name="END_YEAR" type="text" value="<%= StringEscapeUtils.escapeHtml("" + ( endYear )) %>" />
            </td>
            <td><b>Gender:</b></td>
            <td style="width: 150px; border: 1px solid Gray;">
                <select name="GENDER">
                    <option><%=Gender.Male.toString()%></option>
                    <option><%=Gender.Female.toString()%></option>
                    <option><%=Gender.NotSpecified.toString()%></option>
                </select>
                <script type="text/javascript">
                    $('select[name="GENDER"] option:contains("<%=gender.toString()%>")').prop('selected',true);
                </script>
            </td>
            <td align="center" colspan="2">
                <input type="submit" id="searchCauseOfDeathBtn" value="User Search" />
            </td>
        </tr>
    </form>
</table>

<br>

<%
    if(formIsFilled) {
        try {
            String yearPattern = "[1|2][0-9]{3}";
            if(!Pattern.matches(yearPattern, startYear) && !Pattern.matches(yearPattern, endYear)) {
                throw new FormValidationException("Start Year", "End Year");
            }
            if(!Pattern.matches(yearPattern, startYear)) {
                throw new FormValidationException("Start Year");
            }
            if(!Pattern.matches(yearPattern, endYear)) {
                throw new FormValidationException("End Year");
            }
            int startYearInt = Integer.parseInt(startYear);
            int endYearInt = Integer.parseInt(endYear);
            if (startYearInt > endYearInt) {
                throw new Exception("Start Year must be less or equal to End Year!");
            }
            CauseOfDeathAction action = new CauseOfDeathAction(prodDAO, loggedInMID);
            List<CauseOfDeathBean> list = action.topTwoCommon(startYearInt, endYearInt, gender);
%>

<table align=center class="fTable" style="width:60%">
    <tr> </tr>
    <tr>
        <th>Code</th>
        <th>Name</th>
        <th>Count</th>
    </tr>
    <%
        for (CauseOfDeathBean cod : list) {
            out.println("<tr>");
            out.println("<td>" + cod.getCode() + "</td>");
            out.println("<td>" + cod.getCauseOfDeath() + "</td>");
            out.println("<td>" + cod.getCount() + "</td>");
            out.println("<tr>");
        }
    %>
</table>

<%
        }catch (Exception e) {
%>
<div align=center>
    <span class="iTrustError"><%=StringEscapeUtils.escapeHtml(e.getMessage()) %></span>
</div>
<%
        }
    }%>


<%@include file="/footer.jsp" %>