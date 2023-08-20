document.getElementById('rateAllowanceButton').addEventListener("click",function(){
	const el=document.getElementById("allowanceRate");
	updateRate(el,1,true);
});
document.getElementById('rateMileageButton').addEventListener("click",function(){
	const el=document.getElementById("mileageRate");
	updateRate(el,2,true);
});
document.getElementById('allowanceLimitButton').addEventListener("click",function(){
	const el=document.getElementById("allowanceLimit");
	updateRate(el,1,false);
});
document.getElementById('mileageLimitButton').addEventListener("click",function(){
	const el=document.getElementById("mileageLimit");
	updateRate(el,2,false);
});

function updateRate(field,id,isRate)
{
	const updateRequest=new XMLHttpRequest();
	const url=getURL()+"/updateRate";
	let data={'id':id};
	if(isRate)
		data.amount=field.value;
	else
		data.limit=field.value;
	data=JSON.stringify(data);
	updateRequest.addEventListener("load",function(){
		if(updateRequest.status===200){
			if(isRate)
				messageBox.textContent="Succesfully updated rate";
			else
				messageBox.textContent="Succesfully updated limit";
		}
	});
	updateRequest.open("POST",url,true);
	updateRequest.send(data);
}