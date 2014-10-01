
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main"/>
		<title>Form</title>
	</head>
	<body>
        <g:form controller="job" action="save">
            <label>database: </label>
            <g:textField name="database"/><br/>

            <label>vocabulary: </label>
            <g:textField name="vocabulary"/><br/>

            <label>distance: </label>
            <g:textField name="distance"/><br/>

            <g:actionSubmit value="Save"/>
        </g:form>
	</body>
</html>