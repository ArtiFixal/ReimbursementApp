<%@page language="java" contentType="text/html;charset=UTF8" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
    <head>
		<title>Admin Panel - Receipts</title>
        <meta charset="UTF-8">
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
    </head>
    <body>
		<jsp:include page="../header.jspf"></jsp:include>
		<main>
			<select id="receiptList">
				<jsp:useBean id="receipts" class="artifixal.reimbursementcalculationapp.daos.ReceiptTypeDAO"></jsp:useBean>
				<c:forEach items="${receipts.allReceipts}" var="receipt">
					<option value="${receipt.id}" limit="${receipt.limit}">
						${receipt.name}
					</option>
				</c:forEach>
				${receipts.close()}
			</select>
			<div>
				Name: <input type="text" id="name">
				Limit: <input type="number" id="limit" step="0.1">
			</div>
			<div>
				<input type="button" id="addReceipt" value="Add new"/>
				<input type="button" id="renameReceipt" value="Rename selected"/>
				<input type="button" id="deleteReceipt" value="Delete selected"/>
			</div>
			<div id="messageBox"></div>
			<a href="../AdminPanel.jsp">Return to Admin panel</a>
		</main>
		<script src="../../Scripts/Validator.js"></script>
		<script src="../../Scripts/Common.js"></script>
		<script src="../../Scripts/AdminPage/receiptsAJAX.js"></script>
    </body>
</html>
