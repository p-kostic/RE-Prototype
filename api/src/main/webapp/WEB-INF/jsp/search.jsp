<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--<jsp:useBean id="it" class="nl.reprototyping.model.ResultsModel" scope="request"/>--%>
<html>
<link rel="stylesheet" href="/css/style.css"/>
<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js" integrity="sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49"
        crossorigin="anonymous"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js" integrity="sha384-ChfqqxuZUCnJSK3+MXmPNIyE6ZbWh2IMqE241rYiqJxyMiZ6OW/JmZQ5stwEULTy"
        crossorigin="anonymous"></script>
<head>
    <title>Title</title>
</head>
<body class="${model.theme.toString()}">
<div class="container d-flex justify-content-center h-75">
    <div class="align-self-center col">
        <div class="text-center">
            <img alt="image" class="img-fluid p-5 p-lg-0" width="350"
                 src="/img/innovation${model.theme.toString().equals("dark")? "-dark" : ""}.png"
            />
        </div>
        <div class="align-self-center">
            <form class="container" action="${pageContext.request.contextPath}/results" method="get">
                <div class="row align-items-end justify-content-between">
                    <div class="col align-self-center">
                        <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/plugin">Test our chrome plugin</a>
                    </div>
                    <div class="mb-3 mt-3 btn-group btn-group-toggle col text-right d-block">
                        <div class="btn-group">
                            <button id="btn--light" type="radio" name="theme" value="light" class="btn btn-primary"
                                    <c:if test="${param.theme.equals(\"light\")}">checked="checked"</c:if>> Light
                            </button>
                            <button id="btn--dark" type="radio" name="theme" value="dark" class="btn btn-outline-primary"
                                    <c:if test="${param.theme.equals(\"dark\")}">checked="checked"</c:if>>Dark
                            </button>
                        </div>
                    </div>
                </div>
                <div class="form-group">
                    <div class="input-group">
                        <input class="form-control" type="text" name="query" placeholder="Zoeken">
                        <div class="input-group-append">
                            <button type="submit" class="btn btn-primary">Search</button>
                        </div>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>
</body>
</html>
