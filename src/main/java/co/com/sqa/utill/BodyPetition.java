package co.com.sqa.utill;


import static co.com.sqa.utill.FileUtilities.readFile;

public class BodyPetition {

    public static String bodyRequest(String pathRequest, String code){
        return String.format(readFile(pathRequest),code);
    }
    public static String bodyResponse(String expectedResponse,String response){
        return String.format(expectedResponse,response);
    }
}
