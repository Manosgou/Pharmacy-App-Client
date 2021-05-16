package models;

public enum UserDomain {
    PHARMACIST("PH"),SUPPLIER("SP"),CUSTOMER("CU");

    private final String domain;
    UserDomain(String domain) {
        this.domain=domain;
    }

    private String getDomain(){
        return domain;
    }
    public static UserDomain getDomainFromString(String type) {
        for (UserDomain userDomain : UserDomain.values()) {
            if (userDomain.getDomain().equals(type)) {
                return userDomain;
            }
        }
        return null;
    }
}
