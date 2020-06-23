//Performs filtering of projects based on class name

filterSelection("all")

function filterSelection(c) {
	
	rewardAction("FILTER_PROJECTS");
	
	var x, i;
	x = document.getElementsByClassName("filterDiv");
	if (c == "all") c = "";
	for (i = 0; i < x.length; i++) {
	   if (x[i].className.indexOf(c) > -1) x[i].style.display = "inline-block";
	   else x[i].style.display = "none";
	}
}
