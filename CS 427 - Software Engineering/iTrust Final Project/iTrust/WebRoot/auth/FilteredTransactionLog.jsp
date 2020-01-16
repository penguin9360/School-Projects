<%@page import="edu.ncsu.csc.itrust.dao.DAOFactory"%>
<%@page import="java.util.List"%>
<%@page import="edu.ncsu.csc.itrust.beans.TransactionBean"%>
<%@page import="edu.ncsu.csc.itrust.enums.TransactionType"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@ page  import="org.jfree.chart.*" %>
<%@ page  import="org.jfree.chart.axis.*" %>
<%@ page  import="org.jfree.chart.entity.*" %>
<%@ page  import="org.jfree.chart.plot.*" %>
<%@ page  import="org.jfree.chart.renderer.category.*" %>
<%@ page  import="org.jfree.data.category.*" %>
<%@page import="java.util.Date"%>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.sql.Timestamp" %>
<%@ page import="java.util.*" %>
<%@page import="java.lang.String" %>
<%@ page  import="java.awt.*" %>
<%@ page  import="java.io.*" %>
<%@ page import="edu.ncsu.csc.itrust.exception.DBException" %>


<%@include file="/global.jsp"%>
<%@include file="/header.jsp"%>

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>jQuery UI Datepicker - Default functionality</title>
    <link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
    <link rel="stylesheet" href="/resources/demos/style.css">
    <script src="https://code.jquery.com/jquery-1.12.4.js"></script>
    <script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
    <script>
        $( function() {
            var dateFormat = "mm/dd/yy",
                from = $( "#startDate" )
                    .datepicker({
                        defaultDate: "+1w",
                        changeMonth: true,
                        changeYear: true,
                        numberOfMonths: 1,
                        maxDate: new Date()
                    })
                    .on( "change", function() {
                        to.datepicker( "option", "minDate", getDate( this ) );
                    }),
                to = $( "#endDate" ).datepicker({
                    defaultDate: "+1w",
                    changeMonth: true,
                    changeYear: true,
                    numberOfMonths: 1,
                    maxDate: new Date()
                })
                    .on( "change", function() {
                        from.datepicker( "option", "maxDate", getDate( this ) );
                    });
            function getDate( element ) {
                var date;
                try {
                    date = $.datepicker.parseDate( dateFormat, element.value );
                } catch( error ) {
                    date = null;
                }
                return date;
            }
        } );
    </script>
</head>

<h1>View Filtered Transaction Log</h1>

<%
    String [] RoleList = new String[]{"All", "PHA", "PR", "Admin", "ER", "HCP", "LT", "Patient", "Staff", "Tester", "UAP"};
    String [] SecondaryRoleList = new String[]{"PR", "Staff", "Tester", "Admin", "ER", "HCP", "LT", "Patient", "PHA", "UAP"};
    List<TransactionBean> TransactionList = null;
    try {
        TransactionList = DAOFactory.getProductionInstance().getTransactionDAO().getAllTransactions();
    } catch (DBException e) {
        e.printStackTrace();
    }

    double[] numRoles = new double[10];
    double[] numSecondaryRolls = new double[10];

    ArrayList<String> transactionTypeString = new ArrayList<String>();
    ArrayList<String> timeString = new ArrayList<String>();

    ArrayList<Double> numTransactionTypes = new ArrayList<Double>();
    ArrayList<Double> numTime = new ArrayList<Double>();
    String [] FormSelection = new String[5];
    Timestamp minTimeStamp = null;
    assert TransactionList != null;
    for (TransactionBean i : TransactionList) {
        if (minTimeStamp == null) {
            minTimeStamp = i.getTimeLogged();
        } else {
            if (minTimeStamp.after(i.getTimeLogged())) {
                minTimeStamp = i.getTimeLogged();
            }
        }
    }
    DateFormat dateForm = new SimpleDateFormat("MM/dd/yyyy");
    String selectedPastDate = dateForm.format(minTimeStamp);
    String dateOfToday = dateForm.format(new Date());
    if (request.getParameter("view") == null && request.getParameter("summarize") == null) {
        FormSelection[0] = "All";
        FormSelection[1] = "All";
        FormSelection[2] = "All";
        FormSelection[3] = selectedPastDate;
        FormSelection[4] = dateOfToday;
    }
    else{
        FormSelection[0] = request.getParameter("roles");
        FormSelection[1] = request.getParameter("2nd_roles");
        FormSelection[2] = request.getParameter("trans_type");
        FormSelection[3] = request.getParameter("startDate");
        FormSelection[4] = request.getParameter("endDate");
    }
%>

<div>
    <div align="center">
        <span style="font-size: 15pt; font-weight: bold;">Transaction Log Filter</span>
        <form method="post" action="FilteredTransactionLog.jsp">
            <table>
                <tr style="text-align: left;">
                    <td>
                        <label>Roles: </label>
                        <select name="roles">
                            <%
                                for (String r : RoleList){
                                    if (r.equals(FormSelection[0])){
                            %>
                            <option value="<%=r%>" selected><%=r%></option>
                            <%
                            }
                            else{
                            %>
                            <option value="<%=r%>"><%=r%></option>
                            <%
                                    }
                                }
                            %>
                        </select>
                    </td>
                    <td>
                        <label>Secondary Roles: </label>
                        <select name="2nd_roles">
                            <%
                                for (String r : RoleList){
                                    if (r.equals(FormSelection[1])){
                            %>
                            <option value="<%=r%>" selected><%=r%></option>
                            <%
                            }
                            else{
                            %>
                            <option value="<%=r%>"><%=r%></option>
                            <%
                                    }
                                }
                            %>
                        </select>
                    </td>
                    <td>
                        <label>Transaction Type: </label>
                        <select name="trans_type">
                            <option value="All">All</option>
                            <%
                                for(TransactionType type : TransactionType.values()){
                                    if (!FormSelection[2].equals("All") && type.getCode() == Integer.parseInt(FormSelection[2])){
                            %>
                            <option value="<%=type.getCode()%>" selected><%=type.name()%></option>
                            <%
                            }
                            else{
                            %>
                            <option value="<%=type.getCode()%>"><%=type.name()%></option>
                            <%
                                    }
                                }
                            %>
                        </select>
                        <input type="hidden" name="selectedValue" value=<%=FormSelection[2]%>/>
                    </td>
                    <td>
                        <label for="startDate">Start Date: </label>
                        <input type="text" name="startDate" id="startDate" value="<%= StringEscapeUtils.escapeHtml("" + (FormSelection[3] )) %>" size="25"/>
                    </td>
                    <td>
                        <label for="endDate">End Date: </label>
                        <input type="text" name="endDate" id="endDate" value="<%= StringEscapeUtils.escapeHtml("" + (FormSelection[4] )) %>" size="25"/>
                    </td>
                </tr>
            </table>
            <tr>
                <td align="center" colspan="4">
                    <input type="submit" name="view" value="View" />
                    <input type="submit" name="summarize" value="Summarize" />
                </td>
            </tr>
        </form>
    </div>
</div>
<br />



<%
    if ((request.getParameter("view") != null)){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        FormSelection[3] = dateFormat.format(new Date(FormSelection[3]));
        FormSelection[4] = dateFormat.format(new Date(FormSelection[4]+" 23:59:59"));
%>
<table border=1>
    <tr>
        <th>ID></th>
        <th>Time Logged</th>
        <th>Type</th>
        <th>Code</th>
        <th>Description</th>
        <th>Logged in User MID</th>
        <th>Secondary MID</th>
        <th>Extra Info</th>
    </tr>
    <%
        List<TransactionBean> tempTransactionList = null;
        try {
            tempTransactionList = DAOFactory.getProductionInstance().getTransactionDAO().getFilteredTransactions(FormSelection);
        } catch (DBException e) {
            e.printStackTrace();
        }
        assert tempTransactionList != null;
        for (TransactionBean t : tempTransactionList) {
    %>
    <tr>
        <td><%= StringEscapeUtils.escapeHtml("" + (t.getTransactionID())) %></td>
        <td><%= StringEscapeUtils.escapeHtml("" + (t.getTimeLogged())) %></td>
        <td><%= StringEscapeUtils.escapeHtml("" + (t.getTransactionType().name())) %></td>
        <td><%= StringEscapeUtils.escapeHtml("" + (t.getTransactionType().getCode())) %></td>
        <td><%= StringEscapeUtils.escapeHtml("" + (t.getTransactionType().getDescription())) %></td>
        <td><%= StringEscapeUtils.escapeHtml("" + (t.getLoggedInMID())) %></td>
        <td><%= StringEscapeUtils.escapeHtml("" + (t.getSecondaryMID())) %></td>
        <td><%= StringEscapeUtils.escapeHtml("" + (t.getAddedInfo())) %></td>
    </tr>
    <%
        }
    %>
</table>
<%
}

// summarize button clicked
else if(request.getParameter("summarize") != null){
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    for (int i = 0; i < SecondaryRoleList.length; i++) {
        FormSelection[0] = SecondaryRoleList[i];
        FormSelection[1] = "All";
        FormSelection[2] = "All";
        FormSelection[3] = dateFormat.format(new Date(selectedPastDate));
        FormSelection[4] = dateFormat.format(new Date(dateOfToday + " 23:59:59"));
        List<TransactionBean> tempTransactionList = null;
        try {
            tempTransactionList = DAOFactory.getProductionInstance().getTransactionDAO().getFilteredTransactions(FormSelection);
        } catch (DBException e) {
            e.printStackTrace();
        }
        numRoles[i] = tempTransactionList.size();
    }

    for (int i = 0; i < SecondaryRoleList.length; i++) {
        FormSelection[0] = "All";
        FormSelection[1] = SecondaryRoleList[i];
        FormSelection[2] = "All";
        FormSelection[3] = dateFormat.format(new Date(selectedPastDate));
        FormSelection[4] = dateFormat.format(new Date(dateOfToday + " 23:59:59"));
        List<TransactionBean> tempTransactionList = null;
        try {
            tempTransactionList = DAOFactory.getProductionInstance().getTransactionDAO().getFilteredTransactions(FormSelection);
        } catch (DBException e) {
            e.printStackTrace();
        }
        numSecondaryRolls[i] = tempTransactionList.size();
    }

    for (TransactionType type : TransactionType.values()) {
        FormSelection[0] = "All";
        FormSelection[1] = "All";
        FormSelection[2] = ""+type.getCode();
        FormSelection[3] = dateFormat.format(new Date(selectedPastDate));
        FormSelection[4] = dateFormat.format(new Date(dateOfToday + " 23:59:59"));
        List<TransactionBean> tempTransactionList = null;
        try {
            tempTransactionList = DAOFactory.getProductionInstance().getTransactionDAO().getFilteredTransactions(FormSelection);
        } catch (DBException e) {
            e.printStackTrace();
        }
        if(tempTransactionList.size()!=0) {
            transactionTypeString.add(""+type.getCode());
            numTransactionTypes.add((double) tempTransactionList.size());
        }
    }

    Date startDate = new Date(selectedPastDate);
    Date endDate = new Date(dateOfToday);
    int StartYear = startDate.getYear();
    int EndYear = endDate.getYear();
    int month;
    int year = StartYear;

    while(year <= EndYear) {
        for(month = 0; month < 12; month++){
            Calendar tempCalendar = new GregorianCalendar(year, month, 1);
            int daysInMonth = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            FormSelection[0] = "All";
            FormSelection[1] = "All";
            FormSelection[2] = "All";
            String dateform1 = dateForm.format(new Date(year, month, 1));
            FormSelection[3] = dateFormat.format(new Date(dateform1));
            String dateform2 = dateForm.format(new Date(year, month, daysInMonth));
            FormSelection[4] = dateFormat.format(new Date(dateform2 + " 23:59:59"));
            List<TransactionBean> tempTransactionList = null;
            try {
                tempTransactionList = DAOFactory.getProductionInstance().getTransactionDAO().getFilteredTransactions(FormSelection);
            } catch (DBException e) {
                e.printStackTrace();
            }
            if(tempTransactionList.size()!=0) {
                int monthA = month + 1;
                int yearA = year + 1900;
                timeString.add(monthA + "/" + yearA);
                numTime.add((double) tempTransactionList.size());
            }
        }
        year++;
    }

    JFreeChart RoleChart;
    JFreeChart SecondaryRoleChart;
    JFreeChart TransactionTypeChart;
    JFreeChart TimeChart;

    BarRenderer RoleRenderer;
    BarRenderer SecondaryRoleRenderer;
    BarRenderer TransactionTypeRenderer;
    BarRenderer TimeRenderer;

    CategoryPlot RolePlot;
    CategoryPlot SecondaryRolePlot;
    CategoryPlot TransactionTypePlot;
    CategoryPlot TimePlot;

    DefaultCategoryDataset SecondaryRoleDataset = new DefaultCategoryDataset();
    for (int i = 0; i < SecondaryRoleList.length; i++) {
        SecondaryRoleDataset.addValue(numRoles[i], "role", SecondaryRoleList[i]);
    }
    final CategoryAxis ROLE_X = new CategoryAxis("Roles");
    final ValueAxis ROLE_Y = new NumberAxis("Number of Transactions");
    RoleRenderer = new BarRenderer();
    RoleRenderer.setItemMargin(-2);
    RolePlot = new CategoryPlot(SecondaryRoleDataset, ROLE_X, ROLE_Y,
            RoleRenderer);
    RolePlot.setOrientation(PlotOrientation.VERTICAL);
    RoleChart = new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT,
            RolePlot, true);
    RoleRenderer.setSeriesPaint(0, Color.red);
    RolePlot.setRenderer(RoleRenderer);
    String RolePath;
    try {
        final ChartRenderingInfo info = new ChartRenderingInfo
                (new StandardEntityCollection());
        RolePath = getServletConfig().getServletContext().getRealPath("/auth/Role_Chart.png");
        File RoleFile = new File(RolePath);

        ChartUtilities.saveChartAsPNG(RoleFile, RoleChart, 1100, 500, info);
    } catch (Exception e) {
        out.println(e);
    }

    DefaultCategoryDataset SecondaryDataset = new DefaultCategoryDataset();
    for (int i = 0; i < SecondaryRoleList.length; i++) {
        SecondaryDataset.addValue(numSecondaryRolls[i], "role", SecondaryRoleList[i]);
    }
    final CategoryAxis Secondary_X = new CategoryAxis("Secondary Roles");
    final ValueAxis Secondary_Y = new NumberAxis("Number of Transactions");
    SecondaryRoleRenderer = new BarRenderer();
    SecondaryRolePlot = new CategoryPlot(SecondaryDataset, Secondary_X, Secondary_Y,
            SecondaryRoleRenderer);
    SecondaryRoleRenderer.setItemMargin(-2);
    SecondaryRolePlot.setOrientation(PlotOrientation.VERTICAL);
    SecondaryRoleChart = new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT,
            SecondaryRolePlot, true);

    SecondaryRoleRenderer.setSeriesPaint(0, new Color(255, 102, 0));
    SecondaryRolePlot.setRenderer(SecondaryRoleRenderer);
    String SecondaryRolePath;
    try {
        final ChartRenderingInfo info = new ChartRenderingInfo
                (new StandardEntityCollection());
        SecondaryRolePath = getServletConfig().getServletContext().getRealPath("/auth/Secondary_Chart.png");
        File SecondaryRoleFile = new File(SecondaryRolePath);

        ChartUtilities.saveChartAsPNG(SecondaryRoleFile, SecondaryRoleChart, 1100, 500, info);
    } catch (Exception e) {
        out.println(e);
    }


    DefaultCategoryDataset TransactionTypeDataset = new DefaultCategoryDataset();
    for (int i = 0; i < transactionTypeString.size(); i++) {
        TransactionTypeDataset.addValue(numTransactionTypes.get(i).doubleValue(), "Type", transactionTypeString.get(i));
    }
    final CategoryAxis Transaction_X = new CategoryAxis("Transaction Type");
    final ValueAxis Transaction_Y = new NumberAxis("Number of Transactions");
    TransactionTypeRenderer = new BarRenderer();
    TransactionTypePlot = new CategoryPlot(TransactionTypeDataset, Transaction_X, Transaction_Y,
            TransactionTypeRenderer);
    SecondaryRoleRenderer.setItemMargin(-2);
    TransactionTypePlot.setOrientation(PlotOrientation.VERTICAL);
    TransactionTypeChart = new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT,
            TransactionTypePlot, true);

    TransactionTypeRenderer.setSeriesPaint(0, new Color(0, 255, 204));
    TransactionTypePlot.setRenderer(TransactionTypeRenderer);
    String TransactionTypePath;
    try {
        final ChartRenderingInfo info = new ChartRenderingInfo
                (new StandardEntityCollection());
        TransactionTypePath = getServletConfig().getServletContext().getRealPath("/auth/Trans_Type_Chart.png");
        File TransactionTypeFile = new File(TransactionTypePath);

        ChartUtilities.saveChartAsPNG(TransactionTypeFile, TransactionTypeChart, 1100, 500, info);
    } catch (Exception e) {
        out.println(e);
    }

    DefaultCategoryDataset TimeDataset = new DefaultCategoryDataset();
    for (int i = 0; i < timeString.size(); i++) {
        TimeDataset.addValue(numTime.get(i).doubleValue(), "Time", timeString.get(i));
    }
    final CategoryAxis Time_X = new CategoryAxis("Time");
    final ValueAxis Time_Y = new NumberAxis("Number of Transactions");
    TimeRenderer = new BarRenderer();
    TimePlot = new CategoryPlot(TimeDataset, Time_X, Time_Y,
            TimeRenderer);
    TimeRenderer.setItemMargin(-2);
    TimePlot.setOrientation(PlotOrientation.VERTICAL);
    TimeChart = new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT,
            TimePlot, true);

    TimeRenderer.setSeriesPaint(0, Color.green);
    TimePlot.setRenderer(TimeRenderer);
    String TimePath;
    try {
        final ChartRenderingInfo info = new ChartRenderingInfo
                (new StandardEntityCollection());
        TimePath = getServletConfig().getServletContext().getRealPath("/auth/Time_Chart.png");
        File TimeFile = new File(TimePath);

        ChartUtilities.saveChartAsPNG(TimeFile, TimeChart, 1100, 500, info);
    } catch (Exception e) {
        out.println(e);
    }
%>

<IMG SRC="Role_Chart.png" WIDTH="1100"
     HEIGHT="500" BORDER="0" USEMAP="#chart">
<IMG SRC="Secondary_Chart.png" WIDTH="1100"
     HEIGHT="500" BORDER="0" USEMAP="#chart">
<IMG SRC="Trans_Type_Chart.png" WIDTH="1100"
     HEIGHT="500" BORDER="0" USEMAP="#chart">
<IMG SRC="Time_Chart.png" WIDTH="1100"
     HEIGHT="500" BORDER="0" USEMAP="#chart">

<%
    }
%>
<%@include file="/footer.jsp" %>