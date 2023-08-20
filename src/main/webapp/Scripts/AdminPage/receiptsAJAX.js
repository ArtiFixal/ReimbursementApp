const messageBox=document.getElementById("messageBox");
document.getElementById("addReceipt").addEventListener("click",function(){
	addNewReceipt();
});
document.getElementById("renameReceipt").addEventListener("click",function(){
	updateReceipt();
});
document.getElementById("deleteReceipt").addEventListener("click",function(){
	deleteReceipt();
});

function validateFields()
{
	const nameField=document.getElementById("name");
	const limitField=document.getElementById("limit");
	var ok=true;
	if(exists(nameField))
	{
		if(!validateField(nameField))
		{
			ok=false;
			messageBox.textContent="Name can't be empty";
		}
	}
	else
	{
		ok=false;
		messageBox.textContent="Name input field not found";
	}
	if(exists(limitField))
	{
		if(!validateField(limitField))
		{
			ok=false;
			messageBox.textContent="Limit can't be empty";
		}
		if(!intGreaterThan(limitField,0))
		{
			ok=false;
			messageBox.textContent="Limit have to be positive number";
		}
	}
	else
	{
		ok=false;
		messageBox.textContent="Limit input field not found";
	}
	return ok;
}

function addNewReceipt()
{
	if(validateFields())
	{
		// Fields
		const nameField=document.getElementById("name");
		const limitField=document.getElementById("limit");
		// Request
		const addReceiptRequest=new XMLHttpRequest();
		addReceiptRequest.responseType="json";
		const url=getURL()+"/addReceipt";
		const data=JSON.stringify({name:nameField.value,limit:limitField.value});
		addReceiptRequest.addEventListener("load",function(){
			console.log(addReceiptRequest.status);
			if(addReceiptRequest.status===200){
				nameField.value='';
				limitField.value='';
				messageBox.textContent="Succesfully added new receipt";
			}
		});
		addReceiptRequest.open("PUT",url,true);
		addReceiptRequest.send(data);
	}
}

function validateSelect(selectField)
{
	if(exists(selectField))
	{
		
	}
	return false;
}

function deleteReceipt()
{
	const selectField=document.getElementById('receiptList');
	const deleteRequest=new XMLHttpRequest();
	deleteRequest.responseType="json";
	const url=getURL()+"/deleteReceipt";
	const data=JSON.stringify({id:selectField.value});
	deleteRequest.addEventListener("load",function(){
		if(deleteRequest.status===200){
			messageBox.textContent="Receipt deleted succesfully";
			selectField.remove(selectField.selectedIndex);
		}
	});
	console.log(url);
	deleteRequest.open("DELETE",url,true);
	deleteRequest.send(data);
}

function updateReceipt()
{
	// Fields
	const selectField=document.getElementById('receiptList');
	const nameField=document.getElementById("name");
	const limitField=document.getElementById("limit");
	const url=getURL()+"/updateReceipt";
	let data={id:selectField.value};
	// Check what changed
	if(selectField.textContent!==nameField.value)
		data.name=nameField.value;
	if(selectField.getAttribute("limit")!==limitField)
		data.limit=limitField.value;
	const updateRequest=new XMLHttpRequest();
	updateRequest.addEventListener("load",function(){
		if (updateRequest.status === 200){
			messageBox.textContent="Receipt updated succesfully";
			selectField.options[selectField.selectedIndex].textContent=nameField.value;
			selectField.options[selectField.selectedIndex].setAttribute("limit",limitField.value);
		}
	});
	data=JSON.stringify(data);
	updateRequest.open("POST",url,true);
	updateRequest.send(data);
}

