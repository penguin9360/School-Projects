<%@page errorPage="/auth/exceptionHandler.jsp" %>

<%@page import="edu.ncsu.csc.itrust.dao.mysql.EpidemicDAO"%>
<%@page import="java.text.SimpleDateFormat"%>

<%@page import="java.util.Date"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Locale"%>

<%@include file="/global.jsp" %>

<%
    pageTitle = "iTrust - Request Biosurveillance Page (UC 14)";
%>

<%@include file="/header.jsp" %>

<%
    //Get the submitted parameters to search by
    String ICD = request.getParameter("ICDCode");
    String zip = request.getParameter("ZipCode");
    String date = request.getParameter("Date");
    String option = request.getParameter("Option");

    ICD = (ICD == null) ? "487.00" : ICD;
    zip = (zip == null) ? "27601" : zip;
    date = (date == null) ? (new SimpleDateFormat("MM/dd/yyyy").format(new Date())) : date;
    option = (option == null) ? "Epidemics" : option;
%>

<form>
    <table class="fTable" align="center">
        <tr>
            <th colspan="4" style="text-align: center">Request Biosurveillance</th>
        </tr>
        <tr class="subHeader">
            <td>ICD Code</td>
            <td>Zip Code</td>
            <td>Date</td>
            <td>Option</td>
        </tr>
        <tr>
            <td>
                <label>
                    <input name="ICDCode" value="<%=StringEscapeUtils.escapeHtml("")%>" type="text"/>
                </label>
            </td>

            <td>
                <label>
                    <input name="ZipCode" value="<%=StringEscapeUtils.escapeHtml("")%>" type="text"/>
                </label>
            </td>

            <td>
                <label>
                    <input type="text" name="Date" value="<%= StringEscapeUtils.escapeHtml("") %>"
                    />
                </label><input type="button" value="Select Date" onclick="displayDatePicker('Date');" />
            </td>

            <td>
                <label>
                    <select name="Option">
                        <option value="Epidemics" <%if(option.equalsIgnoreCase("Epidemics")){%>selected<%}%>>Epidemics</option>
                        <option value="Diagnoses" <%if(option.equalsIgnoreCase("Diagnoses")){%>selected<%}%>>Diagnoses</option>
                    </select>
                </label>
                <input id = "submitButton" type="submit" value="Submit" name="submit"/>
            </td>
        </tr>
    </table>
</form>

<br/>

<div class="col-sm-12">
    <div class="panel panel-primary panel-notification">
        <div style="padding-bottom: 10px" class="panel-heading"><h3 class="panel-title">Result</h3></div>
        <div class="panel-body">
            <ul> <%
			EpidemicDAO epidemicDAO = new EpidemicDAO(DAOFactory.getProductionInstance());
			//Convert the data to proper format
			float ICDCode = Float.parseFloat(ICD);
			Date formatedDate = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH).parse(date);
			java.sql.Date sqlDate = new java.sql.Date(formatedDate.getTime());

			//Check if the zipCode from input is valid
			if (!epidemicDAO.checkIfZipCodeExists(zip)) {
				out.print("The zip code is invalid. Please try again.");
			} else {
				//Check if the diagnosis code from input is valid
				if (!epidemicDAO.checkIfDiagnosisCodeExists(ICD)) {
					out.print("The ICD code is invalid. Please try again.");
				} else {
					//Check if the date from input is valid
					Date currentDate = new Date();
					String monthStr = date.substring(0, 2);
					String dayStr = date.substring(3, 5);
					int month = Integer.parseInt(monthStr);
					int day = Integer.parseInt(dayStr);
					if ((formatedDate.compareTo(currentDate) > 0)//The date input is a day in the future
							|| (day <= 0) || (day > 31) //Check range of date
							|| (month <= 0) || (month > 12) //Check range of month
							|| ((month == 2) && (day > 29)) //Special case for February
					) { out.print("The date is invalid. Please try again.");
					} else {
						if (option.equals("Epidemics")) {
							if (ICDCode == 487.0) {//Influenza
								boolean result = epidemicDAO.isInfluenzaEpidemic(zip, sqlDate);
								out.print("Influenza diagnosis: " + result);
							} else if (ICDCode == 84.5) {//Malaria
								boolean result = epidemicDAO.isMalariaEpidemic(zip, sqlDate);
								out.print("Malaria diagnosis: " + result);
							} else {
								out.print("The diagnosis code is invalid. Please try again.");
							}
						}
						if (option.equals("Diagnoses")) {//show diagram %>
                <head>
                    <title>Bar Chart</title>
                    <script src="/iTrust/js/chart.js" type="text/javascript"></script>
                </head>
                <div style="width: 50%">
                    <canvas id="canvas" height="450" width="700"></canvas>
                </div>

                <script>
                    var randomScalingFactor = function(){ return Math.round(Math.random()*100)};
                    var barChartData = {
                        labels : ["Week 1","Week 2","Week 3","Week 4","Week 5","Week 6","Week 7","Week 8"],
                        datasets : [
                            {
                                fillColor : "rgba(22,115,220,0.5)",
                                strokeColor : "rgba(220,220,220,0.8)",
                                highlightFill: "rgba(0,17,220,0.75)",
                                highlightStroke: "rgba(220,220,220,1)",
                                data : [<%
                                    List<Integer> regionList = epidemicDAO.diagnosisRegion(zip, sqlDate, ICD);
                                    for(int i = 0; (i < regionList.size() - 1); i++){
                                        out.print(regionList.get(i) +", " );
                                    }
                                    out.print(regionList.get(regionList.size() - 1));
                                %>]
                            },
                            {
                                fillColor : "rgba(108,205,11,0.5)",
                                strokeColor : "rgba(151,187,205,0.8)",
                                highlightFill : "rgba(33,205,0,0.75)",
                                highlightStroke : "rgba(151,187,205,1)",
                                data : [<%
                                    List<Integer> stateList = epidemicDAO.diagnosisState(zip, sqlDate, ICD);
                                    for(int i = 0; (i < stateList.size() - 1); i++){
                                        out.print(stateList.get(i) +", " );
                                    }
                                    out.print(stateList.get(stateList.size() - 1));
					            %>]
                            },
                            {
                                fillColor : "rgba(220,33,190,0.5)",
                                strokeColor : "rgba(220,220,220,0.8)",
                                highlightFill: "rgba(220,0,196,0.75)",
                                highlightStroke: "rgba(220,220,220,1)",
                                data : [<%
                                    List<Integer> allList = epidemicDAO.diagnosisAll(sqlDate, ICD);
                                    for(int i = 0; (i < allList.size() - 1); i++){
                                        out.print(allList.get(i) +", " );
                                    }
                                    out.print(allList.get(allList.size() - 1));
                                %>]
                            }
                        ]
                    };
                    window.onload = function(){
                        var ctx = document.getElementById("canvas").getContext("2d");
                        window.myBar = new Chart(ctx).Bar(barChartData, {
                            responsive : true
                        });
                    }
                </script>
            <%
 			}
 					}//Diagnoses code checking
 				}//Zip code checking
 			}//date checking
 		%>
        </div>
    </div>


<%@include file="/footer.jsp" %>