package es.vargontoc.incident.domain;

public class IncidentEvent {
    private String serviceName;
    private String stacktrace;

    public IncidentEvent() {
    }

    public IncidentEvent(String serviceName, String stacktrace) {
        this.serviceName = serviceName;
        this.stacktrace = stacktrace;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getStacktrace() {
        return stacktrace;
    }

    public void setStacktrace(String stacktrace) {
        this.stacktrace = stacktrace;
    }
}
