<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: dmitrykholodok
  Date: 12/11/17
  Time: 12:58 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>${serviceEntityList}</title>
    </head>
    <body>
        <c:forEach items="${serviceEntityList}" var="serviceEntity">
            Service name : <c:out value="${serviceEntity.serviceName}"/> <br>
            ZNode path   : <c:out value="${serviceEntity.ZNodePath}"/> <br>
            Host-port    : <c:out value="${serviceEntity.hostPort}"/> <br>
        </c:forEach>
    </body>
</html>
