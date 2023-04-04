# Consulta de Ciudad por Código.

Se realiza la automatización de un servicio simulando el post de un servicio donde se ejecuta la consulta de una ciudad por su codigo ISO se utiliza [Gradle](https://gradle.org/), [Java](https://www.java.com/es/), [SerenityBDD](https://serenity-bdd.github.io/theserenitybook/latest/index.html), [Cucumber](https://cucumber.io/) y Screenplay.

## Estructura Codigo Fuente

La estructura del codigo fue realizada con Screenplay de la siguiente forma:
<table>
<tr>
  <th>Tasks</th>
  <td>
    <h6>Contiene todas las tareas que se ejecutaran en la automatizacion</h6>
  </td>
</tr>
  <tr>
  <th>Interactions</th>
  <td>
    <h6>Contiene todas las interaciones que se ejecutaran en la automatizacion</h6>
  </td>
</tr>
  <tr>
  <th>Models</th>
  <td>
    <h6>Contiene todos los modelos que se utilizaran para la construccion de la automatizacion</h6>
  </td>
</tr>
  <tr>
  <th>Utils</th>
  <td>
    <h6> Una que define un conjunto de métodos que realizan funciones, que ayudaran el la automatización</h6>
  </td>
  <tr>
  <th>Runners</th>
  <td>
    <h6>Contiene todos los ejecutores de las pruebas automatizadas</h6>
  </td>
</tr>
  <tr>
  <th>Steps Definitions</th>
  <td>
    <h6>Contiene todos los pasos de la ejecucion de cada prueba automatizada</h6>
  </td>
</tr>
  <tr>
  <th>Features</th>
  <td>
    <h6>Contiene todos los esenarios codificados en lenguaje Gherking</h6>
  </td>
</tr>
</table>

#### Tasks
Esta tarea llevarán a cabo la acción relacionada con la(s) historia(s) de usuario(s) planteada(s). Para ejemplo la tasks va contener una variables privaddas tipo String  y el uso del Map (utlizada para de valores para luego ser usada en stepdefinitions), esto va contener datos que posteriormente validararemos los headers.
Dentro de esta clase se hara la accion de validación del Post, para el envio del request, headers y el boydy de petición.

```java
public class DoPost implements Task {
    private String resource;
    private Map<String, Object> headers;
    private String bodyRequest;
    public DoPost withTheResource(String resource) {
        this.resource = resource;
        return this;
    }
    public DoPost andTheHeaders(Map<String, Object> headers) {
        this.headers = headers;
        return this;
    }
    public DoPost andTheBodyRequest(String bodyRequest) {
        this.bodyRequest = bodyRequest;
        return this;
    }
    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Post.to(resource)
                        .with(
                                requestSpecification -> requestSpecification.relaxedHTTPSValidation()
                                        .headers(headers)
                                        .body(bodyRequest)
                        )
        );
    }
    public static DoPost doPost(){
        return new DoPost();
    }
}
```
### Interactions
N/A
### Models
N/A
#### Usuario
N/A
### Utils
Esta es una clase de tipo enum basada en enumeración el cual nos va permitir  obtener información sobre algo especificico como una lista de datos, en este caso vamos declaramos un valor especifico y lo ubicamso en un directorio, para este ejemplo colocamos el request tipo xml de la petición que estamos validado, si tenemos un xml con varios tags a validar los podremos declarar acá y validarlos con el patch donde este ubicado.
#### Request
```java
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
```
Esta es una clase de tipo enum basada en enumeración el cual nos va permitir validar el response de la petición realizada, el cual podremos delcarara uno varios campos especificos de la respuesta.

#### Response
```java
public enum Response {
	CIUDAD_COD_REQUEST("<m:CapitalCityResult>%s</m:CapitalCityResult>"),
	ERROR_SERVER_RESPONSE("Server Error");
	private final String value;
    Response(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
```
#### ReadFile
Esta clase es el constructor para contener la información la lectura del pathReques y el Response de validación del xml y xsd.

```java
public class BodyPetition {

    public static String bodyRequest(String pathRequest, String code){
        return String.format(readFile(pathRequest),code);
    }
    public static String bodyResponse(String expectedResponse,String response){
        return String.format(expectedResponse,response);
    }
}
```
##### FileUtilis
Esta clase esta relacionada con la libreria log4j de Java por la Apache que permite validar los mensajes de salida o logs en tiempo de ejecución, esto aydara en el control de exepciones e identificar en pantalla alguna acción de error, guardando la hora y fecha con un mensaje especifico.
```java
public class FileUtilities {
    private static final Logger LOGGER= Logger.getLogger(FileUtilities.class);
    private FileUtilities() {
    }
    public static boolean verifyIfFileExist(String filePath){
        File file = new File(filePath);
        LOGGER.info("\n\r****Verificación de archivo:****\r");
        LOGGER.info("Ruta suministrada: " + filePath + "\r");
        LOGGER.info("¿Es un archivo o directorio existente?:" + file.exists() + "\n");
        LOGGER.info("¿Es un archivo?:" + file.isFile() + "\n\r");
        return file.isFile();
    }
    public static boolean deleteFileOrDirectory(String filePath) {
        File object = new File(filePath);
        LOGGER.info("\n\r****Verificación de eliminación de archivo o directorio:****\r");
        LOGGER.info("Ruta suministrada: " + filePath + "\r");
        LOGGER.info("¿Es un archivo o directorio existente?:" + object.exists() + "\n\r");
        boolean deleted = false;
        if(object.exists()) {
            deleted = object.delete();
        }
        LOGGER.info("¿Fue eliminado?:" + deleted + "\n\r");
        return deleted;
    }
    public static String readFile(String filePath) {
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath))) {
            while ((line = br.readLine()) != null)
                stringBuilder.append(line + "\n");
        } catch (IOException ioException) {
            LOGGER.info("\n\n****Hay problemas con la ruta especificada para la lectura de archivos****");
            LOGGER.info(ioException.getMessage() + "\r\n");
            LOGGER.info(ioException);
        }
        return stringBuilder.toString();
    }
}
```
### drivers
N/A
#### DriverRemoteBrowser
N/A
### Questions
Esta clase contiene el assert que va asegurar el cumplimiento de los parámetros de validacion de la petición, retornando un valor tipo String.

```java
    public class ReturnStringValue implements Question<String> {

    private String systemValue;

    public ReturnStringValue withSystemValue(String systemValue) {
        this.systemValue = systemValue;
        return this;
    }
    private ReturnStringValue() {
    }
    @Override
    public String answeredBy(Actor actor) {
        return systemValue;
    }
    public static ReturnStringValue returnStringValue(){
        return new ReturnStringValue();
    }
}
```
Esta clase va retornar la consulta del servicio y va a manterner la codifificacion UFT_8
```java
public class ResturnSoapServiceResponse implements Question<String> {

    @Override
    public String answeredBy(Actor actor) {
        return new String(LastResponse.received().answeredBy(actor).asByteArray(), StandardCharsets.UTF_8);
    }

    public static ResturnSoapServiceResponse resturnSoapServiceResponse(){
        return new ResturnSoapServiceResponse();
    }
}
```
### Runners
Ejecuta y llama los pasos asignados en el feature `capitalCiudad.feature` y busca los metodos correspondientes en el paquete de `CodCapitalStepsDefinitions` para realizar la ejecucion. Esta clase corre mediante el `@RunWith` de la clase `CucumberWithSerenity.class` y mediante el `@CucumberOptions` llama al feature correspondiente, el paquete que contiene los `Steps Definitions` y el `CamelCase`.

```java
@RunWith(CucumberWithSerenity.class)
@CucumberOptions(
        snippets = CucumberOptions.SnippetType.CAMELCASE,
        features = {"src/test/resources/features/services/soap/capitalCiudad.feature"},
        glue = {"co.com.sqa.runners.services.soap.co"}
)
public class ConsultaInfoTest {
}
```
### StepsDefinitions

Los steps definitions contienen todos los metodos llamados mediante el `Runner` al `Feature`. Los metodos ejecutan las `tasks`,`interactions` y `questions` mediante un `actor`.

####Codigo Capital
Esta clase estara contenida con la herencia de Kraken y  logging de seguimiento que colocamos en los stepsdefinitions para asegurarnos de que esta haya pasado por los flujos adecuados, usando "logger.warn" para imprimir el mensaje en consola.

```java
public class CodCapitalStepsDefinitions extends ServiceSetup {

	private static final Logger logger = LogManager.getLogger(CodCapitalStepsDefinitions.class);
	
	@When("el usuario ejecute la petición con código {string}  para buscar una ciudad capital")
	public void elUsuarioEjecuteLaPeticiónConCódigoParaBuscarUnaCiudadCapital(String isoCode) {

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
	public void elSistemaNoEncontraráNingunaCiudadYLaRespuestaSerá(String response) {
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
```
### Features
Contiene los escenarios exitosos y alternos de Buscar Tema y los datos a utilizar en cada escenario digitado en lenguaje Gherking. A su vez realizada el llamado del escenario de inicio de sesion sin el paso de `When`.

####capitalCiudad

```cucumber
@tagSmocktets
Feature: quiero buscar ciudad capital por código ISO del país
        Yo como usuario del servicio quiero consultar la capital de un país por su código ISO

  Scenario Outline: busqueda con con error en el servidor
    When el usuario ejecute la petición con código "<isoCoddeCiudad>"  para buscar una ciudad capital
    Then el usuario debería obtener el nombre de ciudad "<capitalCiudad>"

    Examples: 
      | isoCoddeCiudad | capitalCiudad                     |
      | BB             | Bridgetown                        |
      | BW             | Gaborone                          |
      | shghj56        | Country not found in the database |

  Scenario: busqueda sin coincidencias
    When el usuario ejecute la petición con código "&%_ddd"  para buscar una ciudad capital
    Then el sistema no encontrará ninguna ciudad y la respuesta será "Server Error"
```
## Ejecucion

Al momento de ejecutar el proyecto y obtener el reporte debemos ubicarnos en la carpeta del proyecto y abrir el `CMD` para ejecutar el siguiente comando

```yml
    gradle clean test aggregate
```

Este comando ejecutara todos los escenarios implementados en el proyecto

```cmd
    8 actionable tasks: 8 executed
```

Al finalizar debemos ingresar y abrir el archivo `index.html` que se encuentra en la siguiente ruta

```yml
  <ProyectoName>\target\site\serenity\index.html
```
