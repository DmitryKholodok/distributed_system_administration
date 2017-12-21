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
        <div class="jumbotron">
            <h1 class="text-center font-weight-bold" >Distributed system administration</h1>
        </div>
        <div class="container">
            <div class="row">
                <c:choose>
                    <c:when test="${sessionScope.get('serviceDataList') == null}">
                        <h1 class="text-center">No service found!</h1>
                    </c:when>
                    <c:otherwise>
                        <div class="scrolling">
                            <table class="table table-hover">
                                <thead>
                                <tr>
                                    <th class="col-lg-1" scope="col"></th>
                                    <th class="col-lg-2" scope="col">Service name</th>
                                    <th class="col-lg-3" scope="col">ZNode path</th>
                                    <th class="col-lg-1" scope="col">Host</th>
                                    <th class="col-lg-1" scope="col">Port</th>
                                    <th class="col-lg-4" scope="col" colspan="2">Action</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach items="${sessionScope.get('serviceDataList')}" var="serv">
                                    <tr>
                                        <th scope="row">
                                            <fmt:formatNumber value = "${sessionScope.get('serviceDataList').indexOf(serv)+1}"
                                                              type = "number"/>
                                        </th>
                                        <td><c:out value="${serv.serviceName}"/></td>
                                        <td><c:out value="${serv.ZNodePath}"/></td>
                                        <td><c:out value="${serv.host}"/></td>
                                        <td><c:out value="${serv.port}"/></td>
                                        <td>
                                            <a href="/logs/${sessionScope.get('serviceDataList').indexOf(serv)}" class="badge badge-light">Logs</a>
                                        </td>
                                        <td>
                                            <a href="/shutdown/${sessionScope.get('serviceDataList').indexOf(serv)}" class="badge badge-light">Shutdown</a>
                                        </td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </body>
</html>
