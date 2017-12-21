package by.kholodok.coursework.service_a.controller.error;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Created by dmitrykholodok on 12/15/17
 */

@Component
@PropertySource("classpath:error.properties")
public class ErrorMsg {

    @Value("${error.msg.connection_error}")
    private String connectionErrorMsg;

    @Value("${error.msg.no_service_found}")
    private String noServiceFoundMsg;

    @Value("${error.msg.serialize_error}")
    private String serializeErrorMsg;

    public String getConnectionErrorMsg() {
        return connectionErrorMsg;
    }

    public String getNoServiceFoundMsg() {
        return noServiceFoundMsg;
    }

    public String getSerializeErrorMsg() {
        return serializeErrorMsg;
    }
}
