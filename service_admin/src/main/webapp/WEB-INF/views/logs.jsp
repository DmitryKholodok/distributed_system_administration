<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
    <head>

        <title>Admin</title>
        <style>
            <%@ include file="/resources/css/bootstrap.min.css"%>
            <%@ include file="/resources/css/table.css"%>
        </style>
    </head>
    <body>
        <c:choose>
            <%--// think!!!--%>
            <c:when test="${id == -1}">
                <div class="jumbotron">
                    <h1 class="text-center font-weight-bold" >No logs for current service</h1>
                </div>
            </c:when>
            <c:otherwise>
                <div class="jumbotron">
                    <h1 class="text-center font-weight-bold" >${serviceName} logs</h1>
                </div>
                <div class="container">
                    <div class="row">
                        <div class="scrolling">
                            <table class="table table-hover">
                                <thead>
                                <tr>
                                    <th class="col-lg-1" scope="col"></th>
                                    <th class="col-lg-11" scope="col">Log</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach items="${logList}" var="log">
                                    <tr>
                                        <th scope="row">
                                            <fmt:formatNumber value = "${logList.indexOf(log)+1}" type = "number"/>
                                        </th>
                                        <td><c:out value="${log}"/></td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                    <a href="/show" class="btn btn-info" role="button">Back</a>
                </div>
            </c:otherwise>
        </c:choose>
    </body>
</html>
