package co.com.sqa.util.service.soap.co;

public enum Response {

	CIUDAD_COD_REQUEST("<web:CountryISOCodeResult>Bridgetown</web:CountryISOCodeResult>"),
	ERROR_SERVER_RESPONSE("Server Error");
	private final String value;

    Response(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
	
}
