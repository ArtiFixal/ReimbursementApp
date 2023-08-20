/**
 * Checks if given field exists.
 * 
 * @param {type} inputField Field to test.
 * @returns {Boolean} True if exists, false otherwise.
 */
function exists(inputField)
{
	return inputField!==null&&typeof inputField!=='undefined';
}

function isNotEmpty(textField)
{
	const text=textField.value;
	return text.trim().length>0;
}

/**
 * 
 * @param {type} numberField
 * @returns {Boolean}
 */
function isValidFloat(numberField)
{
	try{
		parseFloat(numberField.value);
		return true;
	}catch (e){
		return false;
	}
}

function isValidDate(dateField)
{
	const d=new Date(dateField.value);
	return ((d instanceof Date)&&(!isNaN(d.valueOf())));
}

function intGreaterThan(numberField,value)
{
	return parseInt(numberField.value)>value;
}

function floatGreaterThan(numberField,value)
{
	return parseFloat(numberField.value)>value;
}

/**
 * 
 * @param {type} inputField
 * @returns {Boolean|Number} True if field is ok, false if it contains errors 
 * and -1 if field type wasn't found.
 */
function validateField(inputField)
{
	if(!exists(inputField))
		return false;
	switch (inputField.type)
	{
		case "text":
			return isNotEmpty(inputField);
		case "number":
			return isValidFloat(inputField);
		case "date":
			return isValidDate(inputField);
		default:
			return -1;
	}
}

