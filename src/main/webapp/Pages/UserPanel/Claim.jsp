<%@page language="java" contentType="text/html;charset=UTF8" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
	<head>
		<title>User panel - make claim</title>
		<meta charset="UTF-8">
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<link rel="stylesheet" href="../../Styles/style.css"/>
	</head>
	<body>
		<jsp:include page="../header.jspf"></jsp:include>
		<main>
			<div>
				Trip date:
				<div>
					<label>From: <input id="dateFrom" type="date"></label>
					<label>To: <input id="dateTo" type="date"></label>
				</div>
			</div>
			<table>
				<thead>
					<tr>
						<th>Receipt type</th>
						<th>Value</th>
						<th>Options</th>
					</tr>
				</thead>
				<tbody id="userReceiptList">
					<tr>
						<td></td>
						<td></td>
					</tr>
				</tbody>
			</table>
			<jsp:useBean id="receipts" class="artifixal.reimbursementcalculationapp.daos.ReceiptTypeDAO"></jsp:useBean>
			<select name="receiptsList">
			<c:forEach var="receipt" items="${receipts.allReceiptsMinimal}">
				<option value="${receipt.id}">
					${receipt.name}
				</option>
			</c:forEach>
			</select>
			<div>Receipt value: <input id="receiptValue" type="number" min="0" name="receiptValue"></div>
			<input id="addReceipt" type="button" value="Add receipt"/>
			<div>
				<label>
					Claim for daily allowance <input type="checkbox" name="includeDailyAllowance" value="Daily allowance"/>
				</label>
				<div id="allowanceBlock">
					Claim for those days:
					<div id="allowanceExcludeBlock">
					</div>
				</div>
			</div>
			<div>
				<label>
					Personal car was used <input type="checkbox" name="includeCarMileage"/>
				</label>
				<div id="mileageBlock">
					KM gained: <input id="mileageValue" type="number" min="0" name="carMileage">
				</div>
			</div>
			<input id="makeClaim" type="button" value="Make claim" />
			<input id="calculate" type="button" value="Calculate total amount" />
			<a href="../UserPanel.jsp">Back to User panel</a>
			<div>
				Claim value: <input id="claimValue" disabled/>
			</div>
			<div id="messageBox"></div>
		</main>
		<script src="../../Scripts/Validator.js"></script>
		<script src="../../Scripts/Common.js"></script>
		<script src="../../Scripts/UserPage/claimScript.js"></script>
	</body>
</html>