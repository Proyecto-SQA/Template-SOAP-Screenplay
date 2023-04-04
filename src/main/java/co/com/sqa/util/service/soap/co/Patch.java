package co.com.sqa.util.service.soap.co;


public enum Patch {

    CIUDAD_COD_REQUEST(System.getProperty("user.dir")
            + "/src/test/resources/file/services/soap/co/request/capitalcod.xml");


    private final String value;

    Patch(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
