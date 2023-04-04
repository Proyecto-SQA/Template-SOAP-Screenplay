package co.com.sqa.stepdefinitions.service.soap.co;


import org.apache.http.HttpStatus;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.Assertions;

import co.com.sqa.setup.ServiceSetup;
import co.com.sqa.util.service.soap.co.Patch;
import co.com.sqa.util.service.soap.co.Response;
import co.com.sqa.utill.BodyPetition;

import static co.com.sqa.questions.ResturnSoapServiceResponse.resturnSoapServiceResponse;
import static net.serenitybdd.screenplay.rest.questions.ResponseConsequence.seeThatResponse;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static co.com.sqa.tasks.DoPost.doPost;
import static org.hamcrest.CoreMatchers.containsString;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;

public class CodCapitalStepsDefinitions extends ServiceSetup {

	private static final Logger logger = LogManager.getLogger(CodCapitalStepsDefinitions.class);
	
	@When("el usuario ejecute la petición con código {string} para buscar una ciudad capital")
	public void elUsuarioEjecuteLaPeticionConCodigoParaBuscarUnaCiudadCapital(String isoCode) {

	      try {
	          super.setup();
	          actor.attemptsTo(
	                  doPost()
                      .withTheResource(RESOURCE)
                      .andTheHeaders(super.headers())
                      .andTheBodyRequest(BodyPetition.bodyRequest(
                              Patch.CIUDAD_COD_REQUEST.getValue(),isoCode))
      );
  }catch (Exception e){
      Assertions.fail(e.getMessage());
      logger.warn("error petición\n"+e.getMessage());
    }
}


	@Then("el usuario debería obtener el nombre de ciudad {string}")
	public void elUsuarioDeberíaObtenerElNombreDeCiudad(String response) {

	       try {
	           actor.should(
	                   seeThatResponse("El código de rspuesta HTTP debe ser: ",
	                           resp -> resp.statusCode(HttpStatus.SC_OK)),
	                   seeThat("La ciudad capital es: ",
	                           resturnSoapServiceResponse(),
	                           containsString(BodyPetition.bodyResponse(
	                           Response.CIUDAD_COD_REQUEST.getValue(),response)))
	           );
	       }catch (AssertionError e){
	           Assertions.fail(e.getMessage());
	           logger.warn("Error en la validación\n"+e);
	       }
	    }
	
		

	@Then("el sistema no encontrará ninguna ciudad y la respuesta será {string}")
	public void elSistemaNoEncontraraNingunaCiudadYLaRespuestaSera(String response) {	
		try { 
			
			actor.should(
					seeThatResponse("El código de respuesta HTTP debe ser: ",
							resp -> resp.statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)),
					seeThat("Respuesta del servidor: ",
							resturnSoapServiceResponse(),
							containsString(BodyPetition.bodyRequest(
							Response.ERROR_SERVER_RESPONSE.getValue(),response)))
					);					
			
		}catch(AssertionError e) {
			Assertions.fail(e.getMessage());
			logger.warn("Error en la validacio/n"+e);
			
		}
	}
		
		
}

	
