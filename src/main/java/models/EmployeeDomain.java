package models;

public enum EmployeeDomain {
    PHARMACIST("PH"),SUPPLIER("SP"),CUSTOMER("CU");

    private final String domain;
    EmployeeDomain(String domain) {
        this.domain=domain;
    }

    private String getDomain(){
        return domain;
    }
    public static EmployeeDomain getDomainFromString(String type) {
        for (EmployeeDomain employeeDomain : EmployeeDomain.values()) {
            if (employeeDomain.getDomain().equals(type)) {
                return employeeDomain;
            }
        }
        return null;
    }
}
