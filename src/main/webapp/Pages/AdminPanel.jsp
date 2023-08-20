<%@page language="java" contentType="text/html;charset=UTF8" pageEncoding="UTF-8" %>
<html>
    <head>
        <title>Admin Panel</title>
		<meta charset="UTF-8">
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
    </head>
    <body>
		<jsp:include page="header.jspf"></jsp:include>
		<main>
			<div>
				<h2>Rates</h2>
				<jsp:useBean id="rates" class="artifixal.reimbursementcalculationapp.daos.RatesDAO"></jsp:useBean>
				<div>Daily allowance rate: <input id="allowanceRate" type="number" step="0.1" value="${rates.allowanceRate.rate}"></div>
				<input id="rateAllowanceButton" type="button" value="Update allowance rate" />
				<div>Car mileage rate: <input id="mileageRate" type="number" step="0.1" value="${rates.mileageRate.rate}"></div>
				<input id="rateMileageButton" type="button" value="Update mileage rate" />
			</div>
			<div>
				<h2>Limits</h2>
				<div>Daily allowance limit: <input id="allowanceLimit" type="number" step="0.1" value="${rates.allowanceRate.limit}"></div>
				<input id="allowanceLimitButton" type="button" value="update allowance limit" />
				<div>Car mileage limit: <input id="mileageLimit" type="number" step="0.1" value="${rates.mileageRate.limit}"></div>
				<input id="mileageLimitButton" type="button" value="update mileage limit" />
				<jsp:useBean id="limits" class="artifixal.reimbursementcalculationapp.daos.LimitDAO"></jsp:useBean>
				<div>Total reimbursement limit: <input id="totalReimbursementLimit" type="number" step="0.1" value="${limits.totalLimit.amount}"></div>
				<input id="totalReimbursementLimitButton" type="button" value="update total limit" />
				<div>Car mileage distance limit: <input id="distanceLimit" type="number" value="${limits.distanceLimit.amount}"></div>
				<input id="distanceLimitButton" type="button" value="update car distance limit" />
			</div>
			<a href="AdminPanel/Receipts.jsp">Receitps</a>
			<div id="messageBox"></div>
		</main>
		<script src="../Scripts/Common.js"></script>
		<script src="../Scripts/AdminPage/ratesAJAX.js"></script>
		<script src="../Scripts/AdminPage/limitsAJAX.js"></script>
    </body>
</html>

