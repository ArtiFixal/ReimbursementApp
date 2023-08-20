function getURL()
{
	const url=window.location.origin;
	let pathname=window.location.pathname;
	const trimPos=pathname.indexOf("/",1);
	pathname=pathname.substring(0,trimPos);
	return url+pathname;
}