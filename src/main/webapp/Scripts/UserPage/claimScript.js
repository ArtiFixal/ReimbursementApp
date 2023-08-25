const messageBox=document.getElementById("messageBox");
document.getElementById("addReceipt").addEventListener("click",function(){
	const receiptValueField=document.getElementById("receiptValue");
	if(validateField(receiptValueField))
	{
		if(floatGreaterThan(receiptValueField,0))
			addReceiptToTab(receiptValueField);
	}
});

document.getElementsByName("includeDailyAllowance")[0].addEventListener("click",function(){
	const allowanceBlock=document.getElementById("allowanceBlock");
	toggleShow(this,allowanceBlock);
});

document.getElementsByName("includeCarMileage")[0].addEventListener("click",function(){
	const mileageBlock=document.getElementById("mileageBlock");
	toggleShow(this,mileageBlock);
});

document.getElementById("dateFrom").addEventListener("change",function(){
	const dateTo=document.getElementById("dateTo");
	if(validateField(dateTo))
	{
		const from=new Date(this.value);
		const to=new Date(dateTo.value);
		if(to.valueOf()>=from.valueOf())
		{
			generateCalendar(from,to);
		}
	}
});

document.getElementById("dateTo").addEventListener("change",function(){
	const dateFrom=document.getElementById("dateFrom");
	if(validateField(dateFrom))
	{
		const from=new Date(dateFrom.value);
		const to=new Date(this.value);
		if(to.valueOf()>=from.valueOf())
		{
			generateCalendar(from,to);
		}
	}
});

document.getElementById("makeClaim").addEventListener("click",function(){
	createClaim();
});

document.getElementById("calculate").addEventListener("click",function(){
	calculateClaim();
});

function showBlock(block)
{
	if(!block.classList.contains("show"))
		block.classList.add("show");
}

function hideBlock(block)
{
	if(block.classList.contains("show"))
		block.classList.remove("show");
}

function toggleShow(checkbox,block)
{
	if(checkbox.checked)
		showBlock(block);
	else
		hideBlock(block);
}

function addNewCell(text,row,clazz)
{
	const cell=document.createElement("td");
	cell.textContent=text;
	cell.classList.add(clazz);
	row.appendChild(cell);
	return cell;
}

function addNewCellWithIdVal(text,row,clazz,id)
{
	const cell=addNewCell(text,row,clazz);
	cell.value=id;
}

function addReceiptToTab(receiptValueField)
{
	const tab=document.getElementById("userReceiptList"),
			selectReceiptType=document.getElementsByName("receiptsList")[0],
			row=document.createElement("tr");
	addNewCellWithIdVal(selectReceiptType.options[selectReceiptType.selectedIndex].
			textContent,row,"receiptName",
			selectReceiptType.options[selectReceiptType.selectedIndex].value);
	addNewCell(receiptValueField.value,row,"receiptValue");
	const deleteButton=document.createElement("input");
	deleteButton.type="button";
	deleteButton.value="Delete";
	deleteButton.addEventListener("click",function(){
		tab.removeChild(row);
	});
	const optionColumn=document.createElement("td");
	optionColumn.appendChild(deleteButton);
	row.appendChild(optionColumn);
	tab.appendChild(row);
	receiptValueField.value="";
}

function addYearBlock(year)
{
	const parent=document.getElementById("allowanceExcludeBlock"),
			yearBlock=document.createElement("div"),
			yearBlockHeader=document.createElement("div");
	yearBlock.classList.add("yearBlock");
	yearBlockHeader.textContent=year;
	yearBlockHeader.classList.add("yearHeader");
	yearBlock.appendChild(yearBlockHeader);
	const yearBody=document.createElement("div");
	yearBody.id="y"+year;
	yearBody.classList.add("yearBody");
	yearBlock.appendChild(yearBody);
	parent.appendChild(yearBlock);
	return yearBlock;
}

function addMonthBlock(year,month)
{
	const yearBlock=document.getElementById("y"+year),
			monthBlock=document.createElement("div"),
			monthHeader=document.createElement("div"),
			date=new Date(year,month);
	monthBlock.classList.add("monthBlock");
	monthHeader.textContent=date.toLocaleString("default",{month:"long"});
	monthHeader.classList.add("monthHeader");
	monthBlock.appendChild(monthHeader);
	const monthBody=document.createElement("div");
	monthBody.id="y"+year+"m"+month;
	monthBody.classList.add("monthBody");
	monthBlock.appendChild(monthBody);
	yearBlock.appendChild(monthBlock);
	return monthBlock;
}

function addDay(year,month,day)
{
	const parentMonth=document.getElementById("y"+year+"m"+month),
			dayBlock=document.createElement("div");
	dayBlock.classList.add("dayBlock");
	dayBlock.textContent=day;
	const checkbox=document.createElement("input");
	checkbox.type="checkbox";
	checkbox.checked=true;
	checkbox.id="y"+year+"m"+month+"d"+day;
	const label=document.createElement("label");
	label.classList.add("dayLabel");
	label.appendChild(checkbox);
	label.appendChild(dayBlock);
	parentMonth.append(label);
	return dayBlock;
}

function generateCalendar(dateFrom,dateTo)
{
	dateTo=new Date(dateTo.getTime()+1000*60*60*24);
	const parent=document.getElementById("allowanceExcludeBlock");
	if(parent.hasChildNodes())
		while(parent.firstChild)
			parent.removeChild(parent.firstChild);
	let length=dateTo.getFullYear();
	for(var i=dateFrom.getFullYear();i<length+1;i++)
	{
		addYearBlock(i);
	}
	let dateIterator=new Date(dateFrom);
	while(dateIterator.valueOf()!==dateTo.valueOf())
	{
		let year=dateIterator.getFullYear(),
				month=dateIterator.getMonth();
		// Create month if needed
		if(!exists(document.getElementById("y"+year+"m"+month)))
			addMonthBlock(year,month);
		addDay(dateIterator.getFullYear(),dateIterator.getMonth(),
		dateIterator.getDate());
		// Iterate by a day 
		dateIterator.setTime(dateIterator.getTime()+1000*60*60*24);
	}
}

function collectExcludedDays()
{
	const dateFrom=new Date(document.getElementById("dateFrom").value);
	let dateTo=new Date(document.getElementById("dateTo").value);
	// To include last day
	dateTo.setTime(dateTo.getTime()+3600000*24);
	let periodStart=null,
			started=false,
			dateIterator=new Date(dateFrom),
			days={days:[],periods:[]};
	while(dateIterator.valueOf()!==dateTo.valueOf())
	{
		const day=document.getElementById("y"+dateIterator.getFullYear()+
				"m"+dateIterator.getMonth()+"d"+dateIterator.getDate());
		if(!day.checked)
		{
			if(!started)
			{
				periodStart=new Date(dateIterator);
				started=true;
			}
		}
		else if(started&&day.checked)
		{
			const previousDay=new Date(dateIterator.getTime()-3600000*24);
			if(periodStart.valueOf()===previousDay.valueOf())
				days.days.push(periodStart);
			else
			{
				data={from:periodStart,to:previousDay};
				days.periods.push(data);
			}
			started=false;
		}
		dateIterator.setTime(dateIterator.getTime()+3600000*24);
	}
	dateTo.setTime(dateTo.getTime()-3600000*24);
	if(started)
	{
		if(periodStart.valueOf()===dateTo.valueOf())
			days.days.push(periodStart);
		else
		{
			data={from:periodStart,to:dateTo};
				days.periods.push(data);
		}
	}
        if(days.days.length===0&&days.periods.length===0)
        {
            delete days.days;
            delete days.periods;
            return null;
        }
        if(days.days.length===0)
            delete days.days;
        else
            delete days.periods;
	return days;
}

function collectReceipts()
{
	const names=document.getElementsByClassName("receiptName");
	const values=document.getElementsByClassName("receiptValue");
	let receipts=[];
	for(var i=0;i<values.length;i++)
	{
		receipts.push({id:names[i].value.trim(),name:names[i].textContent.trim(),
			value:values[i].textContent.trim()});
	}
	return receipts;
}

function prepareData(){
	const dateFrom=new Date(document.getElementById("dateFrom").value),
			dateTo=new Date(document.getElementById("dateTo").value);
	const data={"dateFrom":dateFrom,"dateTo":dateTo,receipts:collectReceipts()};
	if(document.getElementsByName("includeDailyAllowance")[0].checked)
	{
		const excludedDays=collectExcludedDays();
		data.excluded=excludedDays;
	}
	if(document.getElementsByName("includeCarMileage")[0].checked)
	{
		data.mileage=document.getElementById("mileageValue").value;
	}
	console.log(data);
	return data;
}

function createClaim()
{
	const insertRequest=new XMLHttpRequest(),
			url=getURL()+"/makeClaim",
			data=JSON.stringify(prepareData());
	insertRequest.addEventListener("load",function(){
		console.log(insertRequest.status+" "+insertRequest.responseText);
		if (insertRequest.status === 200){
			messageBox.textContent="Claim created successfuly";
		}
	});
	insertRequest.open("PUT",url,true);
	insertRequest.send(data);
}

function calculateClaim()
{
	const valueOutput=document.getElementById("claimValue");
	const calculateRequest=new XMLHttpRequest(),
			url=getURL()+"/calculateClaim",
			data=JSON.stringify(prepareData());
	calculateRequest.responseType="text";
	calculateRequest.addEventListener("load",function(){
		if (calculateRequest.status === 200){
			valueOutput.value=calculateRequest.response;
		}
	});
	calculateRequest.open("POST",url,true);
	calculateRequest.send(data);
}