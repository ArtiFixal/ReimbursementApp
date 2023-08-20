document.getElementById("totalReimbursementLimitButton").addEventListener("click",
function(){
	const field=document.getElementById("totalReimbursementLimit");
	updateLimit(field,1);
});

document.getElementById("distanceLimitButton").addEventListener("click",function(){
	const field=document.getElementById("distanceLimit");
	updateLimit(field,2);
});

function updateLimit(field,id)
{
	const updateRequest=new XMLHttpRequest();
	const url=getURL()+"/updateLimit";
	const data=JSON.stringify({'id':id,amount:field.value});
	updateRequest.addEventListener("load",function(){
		if(updateRequest.status===200){
			messageBox.textContent="Succesfully updated limit";
		}
	});
	updateRequest.open("POST",url,true);
	updateRequest.send(data);
}