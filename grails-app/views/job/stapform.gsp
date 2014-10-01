<!DOCTYPE html
	PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	 "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en-US" xml:lang="en-US">
	<head>
		<title>STAP</title>

		<link rel="stylesheet" type="text/css" href="<g:createLinkTo dir='css' file='snazzy.css' /> " />
		<script type="text/javascript" src="<g:createLinkTo dir='javascripts' file='jquery-1.11.1.min.js' />"></script>
		<input type="hidden" name="tasktype" value="stap">
		<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
	</head>
	<body>
		<h1>STAP</h1>
		<g:form controller="job" action="submit" enctype="multipart/form-data" onsubmit="return validateForm()">
		
			<div class='header'> Enter your sample </div>
			
			<div class='panel'>
			Upload sequences in .fasta format
			<p><label><input type='file' name='files' id='files' multiple /></label>
			<div class = 'error'><label id = 'file_error'></label></div>
			Or enter fasta-formatted sequences
			<p><textarea rows="4" cols="50" name="sequences" id="sequences"></textarea>
			</div>
			
			<div class='header'> STAP options </div>
			
			<div class='panel'>
			<table class='options'>
			
					<tr>
						<td>Taxonomic database <img src='<g:createLinkTo dir='images' file='tooltip_icon.gif'/>' title ='Full database gives more reliable, but less informative results)
						Some text will be here.' id='mytooltip'></td>
						<td><select name='database'>
							<option selected='named isolates' value='named isolates'>named isolates</option>
							<option value='full'>full</option>
							</select>
						</td>
					</tr>
					<tr>
						<td>Maximum distance<br>between close taxons <img src='<g:createLinkTo dir='images' file='tooltip_icon.gif'/>' title ='
						Some text will be here.' id='mytooltip'></td>
						<td><input type='text' name='distance' id='distance' value='0,03' size='10' maxlength='30' />
						</td>
					</tr>
					<tr>
						<td colspan = '2'><div class = 'error'><label id = 'distance_error'></label></div></td>
					</tr>
						
			</table></div>
			<div class='header'>Submit your job </div>
			
			<div class='panel'>
			<label class ='collapsing-toggler'><input type='checkbox' name='checkbox_email' id='checkbox_email' value='ON' checked='checked' />Send results by E-mail</label>
			<p><div class='collapsing-panel'><input type='text' name='email' id='email' size='50' maxlength='80' /></div>
			<div class = 'error'><label id = 'email_error'></label></div><p>
			<p><input type='submit' name='submit' value='Submit' /></div>
		</g:form> 
		
		<script>
		$(document).ready(function(){
		
			$(".collapsing-toggler").change(function()
			{
					if($("#checkbox_email").prop("checked")){
					$(this).nextAll(".collapsing-panel:first").slideDown(400);
					}
					else {
					$(this).nextAll(".collapsing-panel:first").slideUp(400);
					document.getElementById("email_error").innerHTML = "";
					}
			});
			});
			

		
			function displayList(){
				document.getElementById("file_error").innerHTML = "";
				var files = document.getElementById("files").files || [];
				var fileTable = document.getElementById("fileTable");
				
				for(var i = fileTable.rows.length;i>0;i--) {
					fileTable.deleteRow(i-1);
				}
				
				var header  = fileTable.insertRow(0);
				var headerLabel = document.createElement("td");
				headerLabel.setAttribute("colspan", 2);
				headerLabel.innerHTML = "Select primer direction:";
				header.appendChild(headerLabel);
				
				var row  = fileTable.insertRow(1);
				
				var th1 = document.createElement("th");
				th1.innerHTML = "File name";
				row.appendChild(th1);
				
				var th2 = document.createElement("th");
				th2.innerHTML = "Forward primer";
				row.appendChild(th2);
				
		
				for	(var index = 0; index < files.length; index++) {
					var row = fileTable.insertRow(index+2);
					var filename = row.insertCell(0);
					filename.innerHTML = files[index].name;
					
					var isForward = row.insertCell(1);
					var newCheckBox = document.createElement("input");
					var checkBoxId = "checkbox" + index;
					
					var checked = check(files[index].name);
					
					newCheckBox.setAttribute("type","checkbox");
					newCheckBox.setAttribute("id",checkBoxId);
					newCheckBox.setAttribute("name",checkBoxId);
					
					if (checked){
						newCheckBox.setAttribute("checked", "true");
						newCheckBox.setAttribute("value","ON");
					}
					else {
						newCheckBox.setAttribute("value","OFF");
					}
					newCheckBox.setAttribute("onchange","changeDirection(this)");
					isForward.appendChild(newCheckBox);
		
					var newCheckBoxLabel = document.createElement("label");
					var checkBoxLabelId = "labelcheckbox" + index;
					newCheckBoxLabel.setAttribute("id",checkBoxLabelId);
					if (checked){
						newCheckBoxLabel.innerHTML = "forward";
					}
					else newCheckBoxLabel.innerHTML = "reverse";
					isForward.appendChild(newCheckBoxLabel);
		
				}
		
				
			}
			function changeDirection(c){
				var labelId = "label" + c.id;
				if(c.checked){
					document.getElementById(labelId).innerHTML="forward";
					c.setAttribute("value","ON");
				}
				else {
					document.getElementById(labelId).innerHTML="reverse";
					c.setAttribute("value","OFF");
				}
			}
			
			function check(str){
			var t = str.split(".")[0].split("_");
			console.log(t);
			var probablePrimer = t[t.length-1];
			console.log(probablePrimer);
		
			var primerHash = {	Un161: true,
								un161: true,
								Un162: false,
								un162: false,
								Un16sq: false,
								un16sq: false,
								"534R": false,
								"534r": false,
								"27F": true,
								"27f": true,
								"341F": true,
								"341f": true
								};
			if (probablePrimer in primerHash){
				console.log(primerHash[probablePrimer]);
				return primerHash[probablePrimer];
			}
			else {
				console.log("nope");
				return false;
			}
			}
			
			function validateForm() { 
			var passed = true;
			var x = document.getElementById("email").value;
			var test_email = true;
			var atpos = x.indexOf("@");
			var dotpos = x.lastIndexOf(".");
			if (atpos< 1 || dotpos<atpos+2 || dotpos+2>=x.length) {
				test_email = false;
			}
			if (!test_email & document.getElementById("checkbox_email").checked){
				document.getElementById("email_error").innerHTML = "Invalid e-mail";
				passed = false;
			}
			else {
				document.getElementById("email_error").innerHTML = "";
			}
			
			var files = document.getElementById("files").files || [];
			var files_test = true;
			if (document.getElementById("files").value === "" && document.getElementById("sequences").value === ""){
				document.getElementById("file_error").innerHTML = "Select at least one file or enter sequences in the field below";
				files_test = false;
			}
			else {
				for	(var index = 0; index < files.length; index++) {
					var myfile = files[index].name;
					console.log(myfile);
					console.log(myfile.substr(myfile.lastIndexOf("."), 4));
					if (myfile.substr(myfile.lastIndexOf("."), 4) !== ".fas"){
						document.getElementById("file_error").innerHTML = "Unsupported extension";
						files_test = false;
						break;
					}
				}
			}
			
			
				
			if (files_test === true){
				document.getElementById("file_error").innerHTML = "";
			}
			else {
				passed = false;
			}
			
			var dist_check = document.getElementById("distance").value.toString().replace(',', '.');
			if (!isNaN(dist_check) && dist_check.toString().indexOf('.') != -1 && parseFloat(dist_check) >= 0 && parseFloat(dist_check) <= 0.1 && document.getElementById("distance").value.indexOf(',') != -1 ){
				document.getElementById("distance_error").innerHTML = "";
			}
			else {
				document.getElementById("distance_error").innerHTML = "Enter a comma-separated number. Valid range is 0.. 0,1" + dist_check;
				passed = false;
			}
		
		
			return passed;
		} 
		

		
			
		</script>
	</body>
</html>























