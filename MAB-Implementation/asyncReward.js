//Records asynchronous rewards that do not redirect to another page

function rewardAction(action){
	var xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function() {};
	xhttp.open("GET", "Rewarder?rewardType=" + action + "&version=" + getPageVersion(), true);
	xhttp.send();
}

function getPageVersion(){
	var path = window.location.pathname;
	var page = path.split("/").pop();
	var num = page.replace(/[^0-9]/g,'');
	return num;
}