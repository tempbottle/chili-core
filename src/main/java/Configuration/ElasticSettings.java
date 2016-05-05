package Configuration;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Created by Robin on 2016-05-05.
 */
public class ElasticSettings {
    private String remote;
    private String index;
    private Integer port;
    private JsonNode template;

    public String getRemote() {
        return remote;
    }

    protected void setRemote(String remote) {
        this.remote = remote;
    }

    public String getIndex() {
        return index;
    }

    protected void setIndex(String index) {
        this.index = index;
    }

    public Integer getPort() {
        return port;
    }

    protected void setPort(Integer port) {
        this.port = port;
    }

    public JsonNode getTemplate() {
        return template;
    }

    protected void setTemplate(JsonNode template) {
        this.template = template;
    }
}